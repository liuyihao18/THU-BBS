package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityEditBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.ImageGroup;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyImageView;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.lib.GlideEngine;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;
    private final ArrayList<String> mImageUriList = new ArrayList<>();
    private ArrayList<LocalMedia> mSelectedImageData = new ArrayList<>();
    private String mAudioUri = null;
    private String mVideoUri = null;
    private String mLocation = null;
    private MediaController mMediaController = null;
    private MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
        initController();
        initListener();
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            resetPlayer();
        }
        if (binding.videoView.isPlaying()) {
            binding.videoView.stopPlayback();
        }
        mMediaController.hide();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.LOCATION_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Alert.info(this, "获取位置信息失败");
                    return;
                }
            }
            selectLocation();
        }
    }

    private void initView() {
        binding.imageGroup.bindImageUriList(mImageUriList)
                .setEditable(true)
                .refresh();
    }

    private void initListener() {
        binding.cancel.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.question_cancel_edit)
                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                }))
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> finish())
                .create().show());
        binding.addLocationButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.question_add_location)
                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                }))
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> selectLocation())
                .create().show());
        binding.imageGroup.registerImageGroupListener(new ImageGroup.ImageGroupListener() {
            @Override
            public void onClickImage(MyImageView myImageView, int index) {

            }

            @Override
            public void onClickAddImage(MyImageView myImageView, int index) {
                if (index == mImageUriList.size()) {
                    selectImage();
                }
            }

            @Override
            public void onClickCloseButton(View view, int index) {
                removeImage(index);
            }
        });
        binding.audioPlayButton.setOnClickListener(view -> {
            if (mMediaPlayer == null) {
                initPlayer();
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                    binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_stop);
                }
            } else {
                resetPlayer();
            }

        });
        binding.audioCloseButton.setOnClickListener(view -> {
            removeAudio();
            refresh();
        });
        binding.videoView.setOnPreparedListener(mediaPlayer -> {
            DisplayMetrics dm = new DisplayMetrics();
            getDisplay().getRealMetrics(dm);
            int maxWidth = dm.widthPixels - 2 * getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
            int maxHeight = getResources().getDimensionPixelSize(R.dimen.max_video_height);
            int videoWith = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();
            double ratio = videoHeight * 1.0 / videoWith; // 高宽比
            ViewGroup.LayoutParams layoutParams = binding.videoView.getLayoutParams();
            if (maxWidth * ratio < maxHeight) {
                layoutParams.width = maxWidth;
                layoutParams.height = (int) (maxWidth * ratio);
            } else {
                layoutParams.width = (int) (maxHeight / ratio);
                layoutParams.height = maxHeight;
            }
            binding.videoView.setLayoutParams(layoutParams);
        });
        binding.videoCloseButton.setOnClickListener(view -> {
            removeVideo();
            refresh();
        });
    }

    private void initPlayer() {
        if (mAudioUri == null) {
            Alert.error(this, R.string.unknown_error);
            return;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(mAudioUri));
            mMediaPlayer.setOnCompletionListener(view -> {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
                binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
            });
            mMediaPlayer.prepare();
        } catch (IOException ioe) {
            Alert.error(this, R.string.unknown_error);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void resetPlayer() {
        try {
            mMediaPlayer.stop();
        } catch (Exception e) {
            Alert.error(this, R.string.unknown_error);
        }
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
    }

    private void initController() {
        mMediaController = new MediaController(this);
        binding.videoView.setMediaController(mMediaController);
    }

    private void refresh() {
        if (mAudioUri != null) {
            binding.imageGroup.setVisibility(View.GONE);
            binding.audioGroup.setVisibility(View.VISIBLE);
            binding.videoGroup.setVisibility(View.GONE);
            if (binding.videoView.isPlaying()) {
                binding.videoView.stopPlayback();
            }
            mMediaController.hide();
        } else if (mVideoUri != null) {
            binding.imageGroup.setVisibility(View.GONE);
            binding.audioGroup.setVisibility(View.GONE);
            if (mMediaPlayer != null) {
                resetPlayer();
            }
            binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
            binding.videoGroup.setVisibility(View.VISIBLE);
            binding.videoView.setVideoPath(mVideoUri);
        } else {
            binding.imageGroup.setVisibility(View.VISIBLE);
            binding.audioGroup.setVisibility(View.GONE);
            if (mMediaPlayer != null) {
                resetPlayer();
            }
            binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
            binding.videoGroup.setVisibility(View.GONE);
            if (binding.videoView.isPlaying()) {
                binding.videoView.stopPlayback();
            }
            mMediaController.hide();
        }
        setImageButton();
        setAudioButton();
        setVideoButton();
    }

    private void setImageButtonEnabledStyle(boolean enabled) {
        if (enabled) {
            binding.addImageText.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.addImageIcon.setImageResource(R.drawable.ic_baseline_image_enabled_24dp);
        } else {
            binding.addImageText.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.addImageIcon.setImageResource(R.drawable.ic_baseline_image_disabled_24dp);
        }
    }

    private void setAudioButtonEnabledStyle(boolean enabled) {
        if (enabled) {
            binding.addAudioText.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.addAudioIcon.setImageResource(R.drawable.ic_baseline_volume_up_enabled_24dp);
        } else {
            binding.addAudioText.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.addAudioIcon.setImageResource(R.drawable.ic_baseline_volume_up_disabled_24dp);
        }
    }

    private void setVideoButtonEnabledStyle(boolean enabled) {
        if (enabled) {
            binding.addVideoText.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.addVideoIcon.setImageResource(R.drawable.ic_baseline_videocam_enabled_24dp);
        } else {
            binding.addVideoText.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.addVideoIcon.setImageResource(R.drawable.ic_baseline_videocam_disabled_24dp);
        }
    }

    private void setImageButton() {
        if (mAudioUri != null || mVideoUri != null) {
            binding.addImageButton.setEnabled(false);
            setImageButtonEnabledStyle(false);
        } else {
            binding.addImageButton.setEnabled(true);
            if (mImageUriList.size() < Constant.MAX_IMAGE_COUNT) {
                setImageButtonEnabledStyle(true);
                binding.addImageButton.setOnClickListener(view -> selectImage());
            } else {
                setImageButtonEnabledStyle(false);
                binding.addImageButton.setOnClickListener(view -> Alert.info(this, R.string.max_image_hint));
            }
        }
    }

    private void setAudioButton() {
        if (mVideoUri != null || mImageUriList.size() > 0) {
            binding.addAudioButton.setEnabled(false);
            setAudioButtonEnabledStyle(false);
        } else {
            binding.addAudioButton.setEnabled(true);
            if (mAudioUri == null) {
                setAudioButtonEnabledStyle(true);
                binding.addAudioButton.setOnClickListener(view -> selectAudio());
            } else {
                setAudioButtonEnabledStyle(false);
                binding.addAudioButton.setOnClickListener(view -> Alert.info(this, R.string.max_audio_hint));
            }
        }
    }

    private void setVideoButton() {
        if (mAudioUri != null || mImageUriList.size() > 0) {
            binding.addVideoButton.setEnabled(false);
            setVideoButtonEnabledStyle(false);
        } else {
            binding.addVideoButton.setEnabled(true);
            if (mVideoUri == null) {
                setVideoButtonEnabledStyle(true);
                binding.addVideoButton.setOnClickListener(view -> selectVideo());
            } else {
                setVideoButtonEnabledStyle(false);
                binding.addVideoButton.setOnClickListener(view -> Alert.info(this, R.string.max_video_hint));
            }
        }
    }

    private void selectLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.LOCATION_PERMISSION);
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        if (bestLocation == null) {
            mLocation = "(0°E, 0°N)";
            binding.addLocationText.setText(mLocation);
            Alert.info(this, "获取位置信息失败");
            return;
        }
        mLocation = Util.FormatLocation(bestLocation);
        binding.addLocationText.setText(mLocation);
        Alert.info(this, "获取位置信息成功");
    }

    private void selectImage() {
        PictureSelector
                .create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCameraInterceptListener((fragment, cameraMode, requestCode) -> {
                    if (cameraMode != SelectMimeType.ofImage()) {
                        Alert.error(this, R.string.unknown_error);
                        return;
                    }
                    SimpleCameraX camera = SimpleCameraX.of();
                    camera.setCameraMode(cameraMode);
                    camera.setOutputPathDir(getDir(Constant.TMP_DIR, MODE_PRIVATE).getPath());
                    camera.setImageEngine((context, url, imageView) -> Glide.with(context).load(url).into(imageView));
                    if (fragment.getActivity() == null) {
                        Alert.error(this, R.string.unknown_error);
                        return;
                    }
                    camera.start(fragment.getActivity(), fragment, requestCode);
                })
                .setMaxSelectNum(Constant.MAX_IMAGE_COUNT)
                .setSelectedData(mSelectedImageData)
                .setLanguage(86)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        mSelectedImageData = result;
                        mImageUriList.clear();
                        for (LocalMedia media : mSelectedImageData) {
                            mImageUriList.add(media.getPath());
                        }
                        binding.imageGroup.refresh();
                        refresh();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void removeImage(int i) {
        if (i >= mSelectedImageData.size() || i >= mImageUriList.size()) {
            return;
        }
        mSelectedImageData.remove(i);
        mImageUriList.remove(i);
        binding.imageGroup.refresh();
        refresh();
    }

    private void selectAudio() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofAudio())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setLanguage(86)
                .setMaxSelectNum(Constant.MAX_AUDIO_COUNT)
                .setRecordAudioInterceptListener((fragment, requestCode) -> {
                    if (fragment.getActivity() == null) {
                        Alert.error(this, R.string.unknown_error);
                        return;
                    }
                    fragment.startActivityForResult(new Intent(fragment.getActivity(), RecordActivity.class),
                            requestCode);
                })
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result.size() == 0) {
                            Alert.error(EditActivity.this, R.string.unknown_error);
                        } else {
                            mAudioUri = result.get(0).getPath();
                        }
                        refresh();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void removeAudio() {
        mAudioUri = null;
    }

    private void selectVideo() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setLanguage(86)
                .setMaxSelectNum(Constant.MAX_VIDEO_COUNT)
                .setCameraInterceptListener((fragment, cameraMode, requestCode) -> {
                    if (cameraMode != SelectMimeType.ofVideo()) {
                        Alert.error(this, R.string.unknown_error);
                        return;
                    }
                    SimpleCameraX camera = SimpleCameraX.of();
                    camera.setCameraMode(cameraMode);
                    camera.setOutputPathDir(getDir(Constant.TMP_DIR, MODE_PRIVATE).getPath());
                    camera.setImageEngine((context, url, imageView) -> Glide.with(context).load(url).into(imageView));
                    if (fragment.getActivity() == null) {
                        Alert.error(this, R.string.unknown_error);
                        return;
                    }
                    camera.start(fragment.getActivity(), fragment, requestCode);
                })
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result.size() == 0) {
                            Alert.error(EditActivity.this, R.string.unknown_error);
                        } else {
                            mVideoUri = result.get(0).getPath();
                        }
                        refresh();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void removeVideo() {
        mVideoUri = null;
    }
}