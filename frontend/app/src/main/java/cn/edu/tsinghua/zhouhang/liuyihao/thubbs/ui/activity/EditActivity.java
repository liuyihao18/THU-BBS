package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.TweetAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityEditBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Draft;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.ImageGroup;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyImageView;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.lib.GlideEngine;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class EditActivity extends AppCompatActivity {
    private static final int POST_OK = 1;
    private static final int DRAFT_OK = 2;

    private ActivityEditBinding binding;
    private final ArrayList<String> mImageUriList = new ArrayList<>();
    private ArrayList<LocalMedia> mSelectedImageData = new ArrayList<>();
    private String mAudioUri = null;
    private String mVideoUri = null;
    private String mLocation = null;
    private MediaController mMediaController = null;
    private MediaPlayer mMediaPlayer = null;
    private int mTweetId = -1;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case POST_OK:
                Alert.info(this, R.string.post_success);
                setResult(RESULT_OK);
                finish();
                break;
            case DRAFT_OK:
                Alert.info(this, R.string.draft_success);
                break;
            case APIConstant.REQUEST_ERROR:
                Alert.error(this, (String) msg.obj);
                break;
            case APIConstant.NETWORK_ERROR:
                Alert.error(this, R.string.network_error);
                break;
            case APIConstant.SERVER_ERROR:
                Alert.error(this, R.string.server_error);
                break;
        }
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(Constant.EDIT_FROM_DRAFT)) {
            Draft draft = (Draft) intent.getSerializableExtra(Constant.EXTRA_DRAFT);
            mTweetId = draft.getTweetId();
            binding.title.setText(draft.getTitle());
            binding.content.setText(draft.getContent());
            mLocation = draft.getLocation();
        }
        initView();
        initController();
        initListener();
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 停止播放音频
        if (mMediaPlayer != null) {
            resetPlayer();
        }
        // 停止播放视频
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

    @Override
    public void onBackPressed() {
        if (binding.title.getText().toString().isEmpty() &&
                binding.content.getText().toString().isEmpty() &&
                mAudioUri == null && mVideoUri == null && mImageUriList.size() == 0) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.question_cancel_edit)
                    .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                    }))
                    .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> super.onBackPressed())
                    .create().show();
        }
    }

    private void initView() {
        // 九宫格
        binding.imageGroup.bindImageUriList(mImageUriList)
                .setEditable(true)
                .refresh();
    }

    private void initListener() {
        // 取消按钮
        binding.cancel.setOnClickListener(view -> onBackPressed());
        // 添加位置
        binding.addLocationButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.question_add_location)
                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                }))
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> selectLocation())
                .create().show());
        // 九宫格
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
        // 音频播放按钮
        binding.audioPlayButton.setOnClickListener(view -> {
            if (mMediaPlayer == null) {
                initPlayer();
            } else {
                resetPlayer();
            }
        });
        // 音频删除按钮
        binding.audioCloseButton.setOnClickListener(view -> {
            removeAudio();
            refresh();
        });
        // 视频大小控制
        binding.videoView.setOnPreparedListener(mediaPlayer -> {
            DisplayMetrics dm = new DisplayMetrics();
            DisplayManager displayManager = (DisplayManager) getSystemService(
                    Context.DISPLAY_SERVICE);
            displayManager.getDisplay(Display.DEFAULT_DISPLAY).getRealMetrics(dm);
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
        // 视频删除按钮
        binding.videoCloseButton.setOnClickListener(view -> {
            removeVideo();
            refresh();
        });
        // 发布按钮
        binding.post.setOnClickListener(view -> {
            if (binding.title.getText().toString().isEmpty()) {
                Alert.info(this, R.string.title_required);
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle(R.string.question_post)
                    .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                    }))
                    .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> post(false)).create().show();
        });
        // 保存草稿按钮
        binding.saveDraft.setOnClickListener(view -> {
            if (binding.title.getText().toString().isEmpty()) {
                Alert.info(this, R.string.title_required);
                return;
            }
            post(true);
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
            mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mMediaPlayer.start();
                binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_stop);
            });
            mMediaPlayer.prepareAsync();
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

    // 刷新九宫格和下方按钮
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
            mLocation = null;
            binding.addLocationText.setText(R.string.add_location);
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

    private void post(boolean isDraft) {
        int type = Tweet.TYPE_TEXT;
        if (!isDraft) {
            if (mImageUriList.size() > 0) {
                type = Tweet.TYPE_IMAGE;
            } else if (mAudioUri != null) {
                type = Tweet.TYPE_AUDIO;
            } else if (mVideoUri != null) {
                type = Tweet.TYPE_VIDEO;
            }
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (mTweetId > 0) {
            builder.addFormDataPart(TweetAPI.tweetId, String.valueOf(mTweetId));
        }
        builder.addFormDataPart(TweetAPI.type, String.valueOf(type));
        builder.addFormDataPart(TweetAPI.isDraft, String.valueOf(isDraft));
        builder.addFormDataPart(TweetAPI.title, binding.title.getText().toString());
        builder.addFormDataPart(TweetAPI.content, binding.content.getText().toString());
        if (mLocation != null) {
            builder.addFormDataPart(TweetAPI.location, mLocation);
        }
        if (!isDraft) {
            File file;
            String path;
            switch (type) {
                case Tweet.TYPE_IMAGE:
                    builder.addFormDataPart(TweetAPI.imageCount, String.valueOf(mImageUriList.size()));
                    for (int i = 0; i < Math.min(mImageUriList.size(), Constant.MAX_IMAGE_COUNT); i++) {
                        path = Util.getPathFromUri(this, Uri.parse(mImageUriList.get(i)));
                        if (path == null) {
                            file = new File(mImageUriList.get(i));
                        } else {
                            file = new File(path);
                        }
                        builder.addFormDataPart(TweetAPI.image + i,
                                file.getName(),
                                RequestBody.create(file, MediaType.parse("multipart/form-data"))
                        );
                    }
                    break;
                case Tweet.TYPE_AUDIO:
                    path = Util.getPathFromUri(this, Uri.parse(mAudioUri));
                    if (path == null) {
                        file = new File(mAudioUri);
                    } else {
                        file = new File(path);
                    }
                    builder.addFormDataPart(TweetAPI.audio,
                            file.getName(),
                            RequestBody.create(file, MediaType.parse("multipart/form-data"))
                    );
                    break;
                case Tweet.TYPE_VIDEO:
                    path = Util.getPathFromUri(this, Uri.parse(mVideoUri));
                    if (path == null) {
                        file = new File(mVideoUri);
                    } else {
                        file = new File(path);
                    }
                    builder.addFormDataPart(TweetAPI.video,
                            file.getName(),
                            RequestBody.create(file, MediaType.parse("multipart/form-data"))
                    );
                    break;
            }
        }
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message msg = new Message();
                msg.what = APIConstant.NETWORK_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                Message msg = new Message();
                if (body == null) {
                    msg.what = APIConstant.SERVER_ERROR;
                    handler.sendMessage(msg);
                    return;
                }
                try {
                    JSONObject data = new JSONObject(body.string());
                    int errCode = data.getInt(APIConstant.ERR_CODE);
                    if (errCode == 0) {
                        if (isDraft) {
                            msg.what = DRAFT_OK;
                            if (mTweetId <= 0) {
                                mTweetId = data.getInt(TweetAPI.tweetId);
                            }
                        } else {
                            msg.what = POST_OK;
                        }
                    } else {
                        msg.what = APIConstant.REQUEST_ERROR;
                        msg.obj = data.getString(APIConstant.ERR_MSG);
                    }
                    handler.sendMessage(msg);
                } catch (JSONException je) {
                    System.err.println("Bad response format.");
                } finally {
                    body.close();
                }
            }
        };
        if (mTweetId > 0) {
            TweetAPI.editTweet(builder.build(), callback);
        } else {
            TweetAPI.createTweet(builder.build(), callback);
        }
    }
}