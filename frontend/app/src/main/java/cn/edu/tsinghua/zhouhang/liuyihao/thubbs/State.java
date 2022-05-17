package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;

public class State {
    private static final State mState = new State();
    public boolean isLogin;
    public String jwt;
    public int userID;
    public String nickname;
    public String headshot;
    public String description;
    public int tweetCount;
    public int followCount; // 关注
    public int followerCount; // 粉丝

    private State() {
        isLogin = false;
        jwt = null;
        userID = 0;
        nickname = "用户名";
        headshot = Static.HeadShot.getHeadShotUrl("default_headshot.jpg");
        description = "这个人很懒，什么都没留下~";
        tweetCount = 0;
        followCount = 0;
        followerCount = 0;
    }

    public static State getState() {
        return mState;
    }
}
