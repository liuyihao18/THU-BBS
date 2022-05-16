package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;

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
        headshot = Static.HeadShot.getHeadShotUrl("default_headshot.jpg");
    }

    public static State getState() {
        return mState;
    }
}
