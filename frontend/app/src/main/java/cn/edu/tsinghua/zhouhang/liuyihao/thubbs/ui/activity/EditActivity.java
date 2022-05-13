package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.bumptech.glide.Glide;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

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
    private final ArrayList<String> mImageUrlList = new ArrayList<>();
    private ArrayList<LocalMedia> mSelectedImageData = new ArrayList<>();
    private String mAudioUrl = null;
    private String mVideoUrl = null;
    private String mLocation = null;
    private MediaController mMediaController = null;

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
        initListener();
        refresh();
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
        binding.imageGroup.bindImageUrlList(mImageUrlList)
                .setEditable(true)
                .registerImageGroupListener(new ImageGroup.ImageGroupListener() {
                    @Override
                    public void onClickImage(MyImageView myImageView, int index) {

                    }

                    @Override
                    public void onClickAddImage(MyImageView myImageView, int index) {
                        if (index == mImageUrlList.size()) {
                            selectImage();
                        }
                    }

                    @Override
                    public void onClickCloseButton(View view, int index) {
                        removeImage(index);
                    }
                })
                .refresh();
        mMediaController = new MediaController(this);
        binding.videoView.setMediaController(mMediaController);
    }

    private void initListener() {
        binding.cancel.setOnClickListener(view -> finish());
        binding.locationButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.question_add_location)
                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                }))
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> selectLocation()).
                create().show());
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

    private void refresh() {
        if (mAudioUrl != null) {
            binding.imageGroup.setVisibility(View.GONE);
            binding.videoGroup.setVisibility(View.GONE);
            binding.videoView.stopPlayback();
            mMediaController.hide();
        } else if (mVideoUrl != null) {
            binding.imageGroup.setVisibility(View.GONE);
            binding.videoGroup.setVisibility(View.VISIBLE);
            binding.videoView.setVideoPath(mVideoUrl);
        } else {
            binding.imageGroup.setVisibility(View.VISIBLE);
            binding.videoGroup.setVisibility(View.GONE);
            binding.videoView.stopPlayback();
            mMediaController.hide();
        }
        setImageButton();
        setAudioButton();
        setVideoButton();
    }

    private void setImageButtonEnabledStyle(boolean enabled) {
        if (enabled) {
            binding.addImage.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.imageIcon.setImageResource(R.drawable.ic_baseline_image_enabled_24dp);
        } else {
            binding.addImage.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.imageIcon.setImageResource(R.drawable.ic_baseline_image_disabled_24dp);
        }
    }

    private void setAudioButtonEnabledStyle(boolean enabled) {
        if (enabled) {
            binding.addAudio.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.audioIcon.setImageResource(R.drawable.ic_baseline_volume_up_enabled_24dp);
        } else {
            binding.addAudio.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.audioIcon.setImageResource(R.drawable.ic_baseline_volume_up_disabled_24dp);
        }
    }

    private void setVideoButtonEnabledStyle(boolean enabled) {
        if (enabled) {
            binding.addVideo.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.videoIcon.setImageResource(R.drawable.ic_baseline_videocam_enabled_24dp);
        } else {
            binding.addVideo.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.videoIcon.setImageResource(R.drawable.ic_baseline_videocam_disabled_24dp);
        }
    }

    private void setImageButton() {
        if (mAudioUrl != null || mVideoUrl != null) {
            binding.imageButton.setEnabled(false);
            setImageButtonEnabledStyle(false);
        } else {
            binding.imageButton.setEnabled(true);
            if (mImageUrlList.size() < Constant.MAX_IMAGE_COUNT) {
                setImageButtonEnabledStyle(true);
                binding.imageButton.setOnClickListener(view -> selectImage());
            } else {
                setImageButtonEnabledStyle(false);
                binding.imageButton.setOnClickListener(view -> Alert.info(this, R.string.max_image_hint));
            }
        }
    }

    private void setAudioButton() {
        if (mVideoUrl != null || mImageUrlList.size() > 0) {
            binding.audioButton.setEnabled(false);
            setAudioButtonEnabledStyle(false);
        } else {
            binding.audioButton.setEnabled(true);
            if (mAudioUrl == null) {
                setAudioButtonEnabledStyle(true);
                binding.audioButton.setOnClickListener(view -> selectAudio());
            } else {
                setAudioButtonEnabledStyle(false);
                binding.audioButton.setOnClickListener(view -> Alert.info(this, R.string.max_audio_hint));
            }
        }
    }

    private void setVideoButton() {
        if (mAudioUrl != null || mImageUrlList.size() > 0) {
            binding.videoButton.setEnabled(false);
            setVideoButtonEnabledStyle(false);
        } else {
            binding.videoButton.setEnabled(true);
            if (mVideoUrl == null) {
                setVideoButtonEnabledStyle(true);
                binding.videoButton.setOnClickListener(view -> selectVideo());
            } else {
                setVideoButtonEnabledStyle(false);
                binding.videoButton.setOnClickListener(view -> Alert.info(this, R.string.max_video_hint));
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
            binding.addLocation.setText(mLocation);
            Alert.info(this, "获取位置信息失败");
            return;
        }
        mLocation = Util.FormatLocation(bestLocation);
        binding.addLocation.setText(mLocation);
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
                    camera.setImageEngine((context, url, imageView) -> {
                        Glide.with(context).load(url).into(imageView);
                    });
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
                        mImageUrlList.clear();
                        for (LocalMedia media : mSelectedImageData) {
                            mImageUrlList.add(media.getPath());
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
        if (i >= mSelectedImageData.size() || i >= mImageUrlList.size()) {
            return;
        }
        mSelectedImageData.remove(i);
        mImageUrlList.remove(i);
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

                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void removeAudio() {
        mAudioUrl = null;
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
                    camera.setImageEngine((context, url, imageView) -> {
                        Glide.with(context).load(url).into(imageView);
                    });
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
                            mVideoUrl = result.get(0).getPath();
                        }
                        refresh();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void removeVideo() {
        mVideoUrl = null;
    }
}