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

import java.io.IOException;
import java.util.Calendar;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityRecordBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class RecordActivity extends AppCompatActivity {

    private ActivityRecordBinding binding;
    private int layoutWidth;
    private CaptureButton btnCapture;
    private TypeButton btnConfirm;
    private TypeButton btnCancel;
    private MediaRecorder mMediaRecorder;
    private String _mAudioUrl = null;
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
        initPermission();
        initView();
        initListener();
        atBefore();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.RECORD_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Alert.info(this, "获取录音权限失败");
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initPermission() {
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
        binding.backButton.setOnClickListener(view -> {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
            finish();
        });
    }

    private void initRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
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
        if (mMediaRecorder != null) {
            Alert.error(this, R.string.unknown_error);
            return;
        }
        initRecorder();
        _mAudioUrl = getDir(Constant.TMP_DIR, MODE_PRIVATE).getPath() +
                "/AUD_" + Calendar.getInstance().getTime().getTime() + ".aac";
        mMediaRecorder.setOutputFile(_mAudioUrl);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException ioe) {
            Alert.error(this, R.string.unknown_error);
        }
    }

    private void stopRecord() {
        if (mMediaRecorder == null) {
            Alert.error(this, R.string.unknown_error);
            return;
        }
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mAudioUrl = _mAudioUrl;
    }
}