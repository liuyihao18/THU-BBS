package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.luck.lib.camerax.CustomCameraConfig;
import com.luck.lib.camerax.listener.CaptureListener;
import com.luck.lib.camerax.utils.DensityUtil;
import com.luck.lib.camerax.widget.CaptureButton;
import com.luck.lib.camerax.widget.TypeButton;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityRecordBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class RecordActivity extends AppCompatActivity {
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    private final static int AUDIO_SAMPLE_RATE = 44100;
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO; // 单声道
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private ActivityRecordBinding binding;
    private int layoutWidth;
    private CaptureButton btnCapture;
    private TypeButton btnConfirm;
    private TypeButton btnCancel;
    private AudioRecord mAudioRecorder;
    private int mBufferSize = 0;
    private boolean isRecording = false;
    private String mAudioUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setStatusBarColor(getColor(R.color.background_dark));
        layoutWidth = DensityUtil.getScreenWidth(this);
        initView();
        initListener();
        initRecorder();
        atBefore();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.RECORD_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Alert.info(this, "获取录音权限失败");
                    return;
                }
            }
            initRecorder();
        }
    }

    private void initView() {
        // 参考： https://github.com/LuckSiege/PictureSelector
        int button_size = (int) (layoutWidth / 4.5f);

        btnCapture = new CaptureButton(this, button_size);
        ConstraintLayout.LayoutParams btnCaptureParam = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        btnCaptureParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        btnCaptureParam.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btnCaptureParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        btnCaptureParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        btnCapture.setLayoutParams(btnCaptureParam);
        btnCapture.setProgressColor(getColor(R.color.button_pushed));
        btnCapture.setMinDuration(0);
        btnCapture.setMaxDuration(CustomCameraConfig.DEFAULT_MAX_RECORD_VIDEO);
        btnCapture.setButtonFeatures(CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER);

        btnCancel = new TypeButton(this, TypeButton.TYPE_CANCEL, button_size);
        final ConstraintLayout.LayoutParams btnCancelParam = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        btnCancelParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        btnCancelParam.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btnCancelParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        btnCancelParam.leftMargin = (layoutWidth / 4) - button_size / 2;
        btnCancel.setLayoutParams(btnCancelParam);

        btnConfirm = new TypeButton(this, TypeButton.TYPE_CONFIRM, button_size);
        final ConstraintLayout.LayoutParams btnConfirmParam = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        btnConfirmParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        btnConfirmParam.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btnConfirmParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        btnConfirmParam.rightMargin = (layoutWidth / 4) - button_size / 2;
        btnConfirm.setLayoutParams(btnConfirmParam);

        binding.recordLayout.addView(btnCapture);
        binding.recordLayout.addView(btnCancel);
        binding.recordLayout.addView(btnConfirm);
    }

    private void initListener() {
        btnCapture.setCaptureListener(new CaptureListener() {
            @Override
            public void takePictures() {

            }

            @Override
            public void recordShort(long time) {
                Alert.info(RecordActivity.this, R.string.audio_recording_time_is_short);
                stopRecord();
            }

            @Override
            public void recordStart() {
                inBetween();
                startRecord();
            }

            @Override
            public void recordEnd(long time) {
                Alert.info(RecordActivity.this, R.string.audio_recording_over);
                atAfter();
                stopRecord();
            }

            @Override
            public void changeTime(long duration) {

            }

            @Override
            public void recordZoom(float zoom) {

            }

            @Override
            public void recordError() {
                Alert.info(RecordActivity.this, R.string.unknown_error);
                stopRecord();
            }
        });
        btnCancel.setOnClickListener(view -> atBefore());
        btnConfirm.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(mAudioUrl));
            setResult(RESULT_OK, intent);
            finish();
        });
        binding.backButton.setOnClickListener(view -> finish());
    }

    private void initRecorder() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO},
                    Constant.RECORD_PERMISSION);
            return;
        }
        mBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        mAudioRecorder = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, mBufferSize);
    }

    private void atBefore() {
        btnCapture.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        binding.tip.setText(R.string.audio_recording);
    }

    private void inBetween() {
        binding.tip.setText(null);
    }

    private void atAfter() {
        // 参考： https://github.com/LuckSiege/PictureSelector
        btnCapture.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);
        btnCancel.setClickable(false);
        btnConfirm.setClickable(false);
        ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btnCancel, "translationX", layoutWidth / 4.0f, 0);
        ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btnConfirm, "translationX", -layoutWidth / 4.0f, 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator_cancel, animator_confirm);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btnCancel.setClickable(true);
                btnConfirm.setClickable(true);
            }
        });
        set.setDuration(500);
        set.start();
    }

    private void startRecord() {
        String dirUrl = getDir(Constant.TMP_DIR, MODE_PRIVATE).getPath();
        String fileUrl = dirUrl + "/" + Constant.RAW_WAV;
        String outputFileUrl = dirUrl + "/AUD_" + Calendar.getInstance().getTime().getTime() + ".wav";
        new Thread(() -> {
            File file = new File(fileUrl);
            if (file.exists()) {
                if (!file.delete()) {
                    Alert.error(this, R.string.unknown_error);
                    return;
                }
            }
            try {
                if (!file.createNewFile()) {
                    Alert.error(this, R.string.unknown_error);
                    return;
                }
            } catch (IOException ioe) {
                Alert.error(this, R.string.unknown_error);
                return;
            }
            try {
                OutputStream os = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                byte[] buffer = new byte[mBufferSize];
                mAudioRecorder.startRecording();
                isRecording = true;
                while (isRecording) {
                    int res = mAudioRecorder.read(buffer, 0, mBufferSize);
                    if (res > 0) {
                        bos.write(buffer, 0, mBufferSize);
                    }
                }
                mAudioRecorder.stop();
                bos.close();
                os.close();
            } catch (IOException ioe) {
                Alert.error(this, R.string.unknown_error);
                return;
            }
            try {
                FileInputStream in = new FileInputStream(file);
                FileOutputStream out = new FileOutputStream(outputFileUrl);
                long totalFileLen = in.getChannel().size();
                long totalDataLen = totalFileLen + 44 - 8;
                writeWavHeader(out, totalFileLen, totalDataLen);
                byte[] buffer = new byte[mBufferSize];
                while (in.read(buffer) > 0) {
                    out.write(buffer);
                }
                in.close();
                out.close();
            } catch (IOException ioe) {
                Alert.error(this, R.string.unknown_error);
                return;
            }
            mAudioUrl = outputFileUrl;
        }).start();
    }

    private void stopRecord() {
        isRecording = false;
    }

    private void writeWavHeader(OutputStream out, long totalFileLen, long totalDataLen) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF / WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // WAV type format = 1
        header[21] = 0;
        header[22] = (byte) 1; // 指示是单声道还是双声道
        header[23] = 0;
        header[24] = (byte) (AUDIO_SAMPLE_RATE & 0xff); // 采样频率
        header[25] = (byte) ((AUDIO_SAMPLE_RATE >> 8) & 0xff);
        header[26] = (byte) ((AUDIO_SAMPLE_RATE >> 16) & 0xff);
        header[27] = (byte) ((AUDIO_SAMPLE_RATE >> 24) & 0xff);
        header[28] = (byte) (AUDIO_SAMPLE_RATE & 0xff); // 每分钟录到的字节数
        header[29] = (byte) ((AUDIO_SAMPLE_RATE >> 8) & 0xff);
        header[30] = (byte) ((AUDIO_SAMPLE_RATE >> 16) & 0xff);
        header[31] = (byte) ((AUDIO_SAMPLE_RATE >> 24) & 0xff);
        header[32] = (byte) (header[22] * 16 / 8); // block align（声道数 * 量化位数 / 8）
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalFileLen & 0xff); //真实数据的长度
        header[41] = (byte) ((totalFileLen >> 8) & 0xff);
        header[42] = (byte) ((totalFileLen >> 16) & 0xff);
        header[43] = (byte) ((totalFileLen >> 24) & 0xff);
        // 把 header 写入 wav 文件
        out.write(header, 0, 44);
    }
}