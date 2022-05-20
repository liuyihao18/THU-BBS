package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NoErrorAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.UserAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityUserSpaceBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UserSpaceActivity extends AppCompatActivity {
    private ActivityUserSpaceBinding binding;
    private int mUserId = 0;
    private User mUser;
    private boolean firstStart = true;

    private ActivityResultLauncher<Intent> mEditProfileLauncher;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                refresh();
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
        binding = ActivityUserSpaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent = getIntent();
        mUserId = intent.getIntExtra(Constant.EXTRA_USER_ID, 0);
        if (mUserId <= 0) {
            Alert.error(this, R.string.unknown_error);
            finish();
        }
        initLauncher();
        initView();
        initListener();
        getProfile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 只加载一次Container（不能放在onCreate里面，此时Container还没创建）
        if (firstStart) {
            final NavController navController = Navigation.findNavController(this, R.id.fragment_tweets_container);
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.TWEETS_TYPE, Constant.TWEETS_USER);
            bundle.putInt(Constant.EXTRA_USER_ID, mUserId);
            navController.setGraph(R.navigation.tweets_navigation, bundle);
            firstStart = false;
        }
    }

    private void initLauncher() {
        // 编辑资料Activity
        mEditProfileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                State.getState().refreshMyProfile(null);
                getProfile();
            }
        });
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.user_space);
        // 自己
        if (mUserId == State.getState().userId) {
            binding.editProfileButton.setVisibility(View.VISIBLE);
            binding.followButton.setVisibility(View.GONE);
            binding.blackButton.setVisibility(View.GONE);
        }
        // 其他人
        else {
            binding.editProfileButton.setVisibility(View.GONE);
            binding.followButton.setVisibility(View.VISIBLE);
            binding.blackButton.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        // 返回
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
        // 关注按钮
        binding.followButton.setOnClickListener(view -> {
            if (mUser.isFollow) {
                NoErrorAPI.unfollow(this, mUserId, () -> {
                    mUser.isFollow = false;
                    binding.followButton.setText(R.string.follow);
                    binding.followButton.setBackgroundColor(getColor(R.color.pink));
                });
            } else {
                NoErrorAPI.follow(this, mUserId, () -> {
                    mUser.isFollow = true;
                    binding.followButton.setText(R.string.button_unfollow);
                    binding.followButton.setBackgroundColor(getColor(R.color.button_disabled));
                });
            }
        });
        // 屏蔽按钮
        binding.blackButton.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.question_black)
                        .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                        }))
                        .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                            NoErrorAPI.black(this, mUserId, () -> {
                                Alert.info(this, R.string.black_success);
                                setResult(RESULT_OK);
                                finish();
                            });
                        })
                        .create().show());
        // 编辑资料按钮
        binding.editProfileButton.setOnClickListener(view -> mEditProfileLauncher.launch(new Intent(this, EditProfileActivity.class)));
        // 头像
        binding.headshot.setOnClickListener(view -> {
            if (mUser != null) {
                Intent intent = new Intent(this, ImagePreviewActivity.class);
                intent.putExtra(Constant.EXTRA_IMAGE_URL, mUser.headshot);
                startActivity(intent);
            }
        });
    }

    private void refresh() {
        binding.headshot.setImageUrl(mUser.headshot);
        binding.nickname.setText(mUser.nickname);
        if (mUser.description.isEmpty()) {
            binding.description.setText(R.string.description_empty);
        } else {
            binding.description.setText(mUser.description);
        }
        binding.tweetCount.setText(String.valueOf(mUser.tweetCount));
        binding.followCount.setText(String.valueOf(mUser.followCount));
        binding.followerCount.setText(String.valueOf(mUser.followerCount));
        if (mUser.isFollow) {
            binding.followButton.setText(R.string.button_unfollow);
            binding.followButton.setBackgroundColor(getColor(R.color.button_disabled));
        } else {
            binding.followButton.setText(R.string.follow);
            binding.followButton.setBackgroundColor(getColor(R.color.pink));
        }
    }

    private void getProfile() {
        try {
            JSONObject data = new JSONObject();
            data.put(UserAPI.userid, mUserId);
            UserAPI.getProfile(data, new Callback() {
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
                            User user = JSONUtil.createUserFromJSON(data);
                            if (user == null) {
                                msg.what = APIConstant.SERVER_ERROR;
                            } else {
                                mUser = user;
                                msg.what = APIConstant.REQUEST_OK;
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
            });
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }
}