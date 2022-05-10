package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

public class State {
    private static final State mState = new State();
    public boolean isLogin;
    public String jwt;

    private State() {
        isLogin = false;
        jwt = null;
    }

    public static State getState() {
        return mState;
    }
}
