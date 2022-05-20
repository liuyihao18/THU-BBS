package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class UserItemContent {
    private final int userId;
    private final String nickname;
    private final String headshot;

    public UserItemContent(int userId, String nickname, String headshot) {
        this.userId = userId;
        this.nickname = nickname;
        this.headshot = headshot;
    }

    public int getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getHeadshot() {
        return headshot;
    }
}
