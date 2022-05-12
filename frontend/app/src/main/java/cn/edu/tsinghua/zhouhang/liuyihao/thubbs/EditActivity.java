package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luck.lib.camerax.CameraImageEngine;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.util.ArrayList;
import java.util.List;

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
    private String mLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        initView();
        initListener();
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
        } else if (requestCode == Constant.STORAGE_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Alert.info(this, "获取存储权限失败");
                    return;
                }
            }
            selectImage();
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
    }

    private void initListener() {
        binding.cancel.setOnClickListener(view -> finish());
        binding.locationButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.question_add_location)
                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                }))
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> selectLocation()).
                create().show()
        );
        binding.imageButton.setOnClickListener(view -> selectImage());
        binding.audioButton.setOnClickListener(view -> selectAudio());
        binding.videoButton.setOnClickListener(view -> selectVideo());
    }

    private void refresh() {
        if (mImageUrlList.size() > 0) {
            setImageButtonEnabled(mImageUrlList.size() != Constant.MAX_IMAGE_COUNT);
            setAudioButtonEnabled(false);
            setVideoButtonEnabled(false);
        } else {
            setImageButtonEnabled(true);
            setAudioButtonEnabled(true);
            setVideoButtonEnabled(true);
        }
    }

    private void setAudioButtonEnabled(boolean enabled) {
        binding.audioButton.setEnabled(enabled);
        if (enabled) {
            binding.addAudio.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.audioIcon.setImageResource(R.drawable.ic_baseline_volume_up_enabled_24dp);
        } else {
            binding.addAudio.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.audioIcon.setImageResource(R.drawable.ic_baseline_volume_up_disabled_24dp);
        }
    }

    private void setImageButtonEnabled(boolean enabled) {
        binding.imageButton.setEnabled(enabled);
        if (enabled) {
            binding.addImage.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.imageIcon.setImageResource(R.drawable.ic_baseline_image_enabled_24dp);
        } else {
            binding.addImage.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.imageIcon.setImageResource(R.drawable.ic_baseline_image_disabled_24dp);
        }
    }

    private void setVideoButtonEnabled(boolean enabled) {
        binding.videoButton.setEnabled(enabled);
        if (enabled) {
            binding.addVideo.setTextColor(getResources().getColor(R.color.button_enabled, null));
            binding.videoIcon.setImageResource(R.drawable.ic_baseline_videocam_enabled_24dp);
        } else {
            binding.addVideo.setTextColor(getResources().getColor(R.color.button_disabled, null));
            binding.videoIcon.setImageResource(R.drawable.ic_baseline_videocam_disabled_24dp);
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

    public void selectImage() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constant.STORAGE_PERMISSION);
            return;
        }
        PictureSelector
                .create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCameraInterceptListener((fragment, cameraMode, requestCode) -> {
                    if (cameraMode == SelectMimeType.ofAudio()) {
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

    public void removeImage(int i) {
        if (i >= mSelectedImageData.size() || i >= mImageUrlList.size()) {
            return;
        }
        mSelectedImageData.remove(i);
        mImageUrlList.remove(i);
        binding.imageGroup.refresh();
        refresh();
    }

    public void selectAudio() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofAudio())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setLanguage(86)
                .setMaxSelectNum(Constant.MAX_AUDIO_COUNT)
                .setRecordAudioInterceptListener((fragment, requestCode) -> {
                    Alert.info(this, "录制音频");
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

    public void selectVideo() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setLanguage(86)
                .setMaxSelectNum(Constant.MAX_VIDEO_COUNT)
                .setCameraInterceptListener((fragment, cameraMode, requestCode) -> {
                    if (cameraMode == SelectMimeType.ofAudio()) {
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

                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
}