package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import androidx.annotation.NonNull;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;

public class State {
    private static final State mState = new State();
    public boolean isLogin;
    public String jwt;
    public int userId;
    public User user;

    private State() {
        isLogin = false;
        jwt = null;
        userId = 0;
        user = null;
    }

    public static State getState() {
        return mState;
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
