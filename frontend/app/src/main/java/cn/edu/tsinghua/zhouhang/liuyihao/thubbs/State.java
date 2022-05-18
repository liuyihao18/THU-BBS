package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.LoginActivity;

public class State {
    private static final State mState = new State();
    public boolean isLogin;
    public String jwt;
    public int userId;
    public User user;
    public ActivityResultLauncher<Intent> mLoginLauncher;
    public OnLoginListener onLoginListener;

    public interface OnLoginListener {
        void onLogin();
    }

    private State() {
        isLogin = false;
        jwt = null;
        userId = 0;
        user = null;
    }

    public static State getState() {
        return mState;
    }

    public State setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
        return this;
    }

    public void login(Context context) {
        mLoginLauncher.launch(new Intent(context, LoginActivity.class));
    }

    @NonNull
    public String toString() {
        String result = "isLogin: " + isLogin + "\n" +
                "userId: " + userId + "\n";
        if (user != null) {
            result += "nickname: " + user.nickname + "\n" +
                    "description: " + user.description;
        }
        return result;
    }
}
