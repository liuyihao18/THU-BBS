package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

public class State {
    private static final State mState = new State();
    public boolean isLogin;
    public String jwt;
    public int userID;
    public String headshot;

    private State() {
        isLogin = false;
        jwt = null;
        userID = 0;
        headshot = "default_headshot.jpg";
    }

    public static State getState() {
        return mState;
    }
}
