package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

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

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.UserAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityEditProfileBinding;
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

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private String mHeadshotUri = null;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                Alert.info(this, R.string.edit_success);
                setResult(RESULT_OK);
                finish();
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
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
        initListener();
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.edit_profile);
        if (State.getState().user != null) {
            binding.nickname.setText(State.getState().user.nickname);
            if (State.getState().user.description.isEmpty()) {
                binding.description.setHint(R.string.description_empty);
            } else {
                binding.description.setText(State.getState().user.description);
            }
        }
    }

    private void initListener() {
        // 返回
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
        // 头像添加
        binding.headshot.setOnClickListener(view -> {
            if (mHeadshotUri == null) {
                selectImage();
            }
        });
        // 头像删除
        binding.closeButton.setOnClickListener(view -> {
            mHeadshotUri = null;
            refresh();
        });
        // 确定
        binding.button.setOnClickListener(view -> editProfile());
    }

    private void refresh() {
        if (mHeadshotUri != null) {
            binding.headshot.setImageUrl(mHeadshotUri);
            binding.closeButton.setVisibility(View.VISIBLE);
        } else {
            binding.headshot.setImageResource(R.drawable.ic_add_image_gray_24dp);
            binding.closeButton.setVisibility(View.GONE);
        }
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
                .setMaxSelectNum(1)
                .setLanguage(86)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        if (result.size() == 0) {
                            Alert.error(EditProfileActivity.this, R.string.unknown_error);
                        } else {
                            mHeadshotUri = result.get(0).getPath();
                        }
                        refresh();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void editProfile() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart(UserAPI.nickname, binding.nickname.getText().toString());
        builder.addFormDataPart(UserAPI.description, binding.description.getText().toString());
        if (mHeadshotUri != null) {
            String path = Util.getPathFromUri(this, Uri.parse(mHeadshotUri));
            File file;
            if (path == null) {
                file = new File(mHeadshotUri);
            } else {
                file = new File(path);
            }
            builder.addFormDataPart(UserAPI.headshot,
                    file.getName(),
                    RequestBody.create(file, MediaType.parse("multipart/form-data"))
            );
        }
        UserAPI.editProfile(builder.build(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message msg = new Message();
                msg.what = APIConstant.NETWORK_ERROR;
                handler.sendMessage(msg);
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
                        msg.what = APIConstant.REQUEST_OK;
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
        });
    }
}