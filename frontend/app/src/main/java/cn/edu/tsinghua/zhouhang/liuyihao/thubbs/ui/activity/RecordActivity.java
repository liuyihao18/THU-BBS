package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.luck.lib.camerax.CustomCameraConfig;
import com.luck.lib.camerax.listener.CaptureListener;
import com.luck.lib.camerax.utils.DensityUtil;
import com.luck.lib.camerax.widget.CaptureButton;
import com.luck.lib.camerax.widget.TypeButton;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityRecordBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class RecordActivity extends AppCompatActivity {

    private ActivityRecordBinding binding;
    private int layout_width;
    private CaptureButton btn_capture;
    private TypeButton btn_confirm;
    private TypeButton btn_cancel;

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
        layout_width = DensityUtil.getScreenWidth(this);
        initView();
        initListener();
        atBefore();
    }

    private void initView() {
        // 参考： https://github.com/LuckSiege/PictureSelector
        int button_size = (int) (layout_width / 4.5f);
        btn_capture = new CaptureButton(this, button_size);
        ConstraintLayout.LayoutParams btn_capture_param = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        btn_capture_param.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_capture_param.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_capture_param.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_capture_param.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_capture.setLayoutParams(btn_capture_param);
        btn_capture.setProgressColor(getColor(R.color.button_pushed));
        btn_capture.setMinDuration(CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO);
        btn_capture.setMaxDuration(CustomCameraConfig.DEFAULT_MAX_RECORD_VIDEO);
        btn_capture.setButtonFeatures(CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER);

        btn_cancel = new TypeButton(this, TypeButton.TYPE_CANCEL, button_size);
        final ConstraintLayout.LayoutParams btn_cancel_param = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        btn_cancel_param.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_cancel_param.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_cancel_param.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_cancel_param.leftMargin = (layout_width / 4) - button_size / 2;
        btn_cancel.setLayoutParams(btn_cancel_param);


        btn_confirm = new TypeButton(this, TypeButton.TYPE_CONFIRM, button_size);
        final ConstraintLayout.LayoutParams btn_confirm_param = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        btn_confirm_param.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_confirm_param.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_confirm_param.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        btn_confirm_param.rightMargin = (layout_width / 4) - button_size / 2;
        btn_confirm.setLayoutParams(btn_confirm_param);

        binding.recordLayout.addView(btn_capture);
        binding.recordLayout.addView(btn_cancel);
        binding.recordLayout.addView(btn_confirm);
    }

    private void initListener() {
        btn_capture.setCaptureListener(new CaptureListener() {
            @Override
            public void takePictures() {

            }

            @Override
            public void recordShort(long time) {
                Alert.info(RecordActivity.this, R.string.audio_recording_time_is_short);
            }

            @Override
            public void recordStart() {
                atBetween();
            }

            @Override
            public void recordEnd(long time) {
                Alert.info(RecordActivity.this, R.string.audio_recording_over);
                atAfter();
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
            }
        });
        btn_cancel.setOnClickListener(view -> {
            atBefore();
        });
        btn_confirm.setOnClickListener(view -> {

        });
        binding.backButton.setOnClickListener(view -> finish());
    }

    private void atBefore() {
        btn_capture.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.GONE);
        binding.tip.setText(R.string.audio_recording);
    }

    private void atBetween() {
        binding.tip.setText(null);
    }

    private void atAfter() {
        // 参考： https://github.com/LuckSiege/PictureSelector
        btn_capture.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.VISIBLE);
        btn_cancel.setClickable(false);
        btn_confirm.setClickable(false);
        ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btn_cancel, "translationX", layout_width / 4.0f, 0);
        ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btn_confirm, "translationX", -layout_width / 4.0f, 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator_cancel, animator_confirm);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_cancel.setClickable(true);
                btn_confirm.setClickable(true);
            }
        });
        set.setDuration(500);
        set.start();
    }
}