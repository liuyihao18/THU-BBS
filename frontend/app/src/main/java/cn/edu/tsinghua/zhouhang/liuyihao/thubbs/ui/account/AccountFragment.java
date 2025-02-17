package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentAccountBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.DraftBoxActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.EditPasswordActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.EditProfileActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.UserListActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private ActivityResultLauncher<Intent> mEditProfileLauncher;
    private ActivityResultLauncher<Intent> mEditPasswordLauncher;
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;
    private ActivityResultLauncher<Intent> mUserListLauncher;
    private ActivityResultLauncher<Intent> mDraftBoxLauncher;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        if (getContext() == null) {
            return true;
        }
        switch (msg.what) {
            case Constant.LOGIN_OK:
                refresh();
                break;
            case APIConstant.REQUEST_ERROR:
                Alert.error(getContext(), (String) msg.obj);
                refresh();
                break;
            case APIConstant.NETWORK_ERROR:
                Alert.error(getContext(), R.string.network_error);
                break;
            case APIConstant.SERVER_ERROR:
                Alert.error(getContext(), R.string.server_error);
                break;
        }
        return true;
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initLauncher();
        initView();
        initListener();
        if (State.getState().isLogin && State.getState().user == null) {
            State.getState().refreshMyProfile(handler);
        }
        return root;
    }

    private void initLauncher() {
        mEditProfileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                State.getState().refreshMyProfile(handler);
            }
        });
        mEditPasswordLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                logout();
                Alert.info(getContext(), R.string.again_login_required);
                refresh();
            }
        });
        mUserSpaceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> State.getState().refreshMyProfile(handler));
        mUserListLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> State.getState().refreshMyProfile(handler));
        mDraftBoxLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> State.getState().refreshMyProfile(handler));
    }

    private void initView() {
        refresh();
    }

    private void initListener() {
        // 头像
        binding.accountHeadshot.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                Intent intent = new Intent(getContext(), UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, State.getState().userId);
                mUserSpaceLauncher.launch(intent);
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
        // 登录按钮
        binding.loginButton.setOnClickListener(view -> State.getState()
                .setOnLoginListener(this::refresh)
                .login(getContext()));
        // 退出按钮
        binding.logoutButton.setOnClickListener(view -> new AlertDialog.Builder(getContext())
                .setTitle(R.string.question_logout)
                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                }))
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                    logout();
                    Alert.info(getContext(), R.string.logout_success);
                    refresh();
                }).
                create().show());
        // 编辑资料
        binding.editProfileButton.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                mEditProfileLauncher.launch(new Intent(getContext(), EditProfileActivity.class));
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
        // 修改密码
        binding.editPasswordButton.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                mEditPasswordLauncher.launch(new Intent(getContext(), EditPasswordActivity.class));
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
        // 动态
        binding.tweetButton.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                Intent intent = new Intent(getContext(), UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, State.getState().userId);
                mUserSpaceLauncher.launch(intent);
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
        // 关注
        binding.followButton.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                Intent intent = new Intent(getContext(), UserListActivity.class);
                intent.putExtra(Constant.USER_LIST_TYPE, Constant.FOLLOW_LIST);
                mUserListLauncher.launch(intent);
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
        // 粉丝
        binding.followerButton.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                Intent intent = new Intent(getContext(), UserListActivity.class);
                intent.putExtra(Constant.USER_LIST_TYPE, Constant.FAN_LIST);
                mUserListLauncher.launch(intent);
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
        // 黑名单
        binding.blacklistButton.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                Intent intent = new Intent(getContext(), UserListActivity.class);
                intent.putExtra(Constant.USER_LIST_TYPE, Constant.BLACK_LIST);
                mUserListLauncher.launch(intent);
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
        // 草稿箱
        binding.draftButton.setOnClickListener(view -> {
            if (State.getState().isLogin) {
                Intent intent = new Intent(getContext(), DraftBoxActivity.class);
                intent.putExtra(Constant.USER_LIST_TYPE, Constant.BLACK_LIST);
                mDraftBoxLauncher.launch(intent);
            } else {
                State.getState().setOnLoginListener(this::refresh)
                        .login(getContext());
            }
        });
    }

    public void refresh() {
        if (State.getState().isLogin && State.getState().user != null) {
            binding.accountHeadshot.setImageUrl(State.getState().user.headshot);
            binding.tweetCount.setText(String.valueOf(State.getState().user.tweetCount));
            binding.followCount.setText(String.valueOf(State.getState().user.followCount));
            binding.followerCount.setText(String.valueOf(State.getState().user.followerCount));
            binding.accountUserName.setText(State.getState().user.nickname);
            binding.accountUserName.setVisibility(View.VISIBLE);
            binding.loginButton.setVisibility(View.GONE);
        } else {
            binding.accountHeadshot.setImageUrl(Constant.DEFAULT_HEADSHOT);
            binding.tweetCount.setText(String.valueOf(0));
            binding.followCount.setText(String.valueOf(0));
            binding.followerCount.setText(String.valueOf(0));
            binding.accountUserName.setVisibility(View.GONE);
            binding.loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void logout() {
        State.getState().jwt = null;
        State.getState().userId = 0;
        State.getState().isLogin = false;
        Activity activity = getActivity();
        if (activity == null) {
            Alert.error(getContext(), R.string.unknown_error);
            return;
        }
        SharedPreferences preferences = activity.getSharedPreferences(Constant.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        preferences.edit().remove(Constant.JWT).apply();
        preferences.edit().remove(Constant.USER_ID).apply();
    }
}