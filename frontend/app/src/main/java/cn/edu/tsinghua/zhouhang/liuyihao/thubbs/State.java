package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

public class State {
    private static final State mState = new State();
    public boolean isLogin;
    public String jwt;
    public int userID;

    private State() {
        isLogin = false;
        jwt = null;
        userID = 0;
    }

    public static State getState() {
        return mState;
    }
}
