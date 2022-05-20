package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class UserListItem {
    private final int userId;
    private final String nickname;
    private final String headshot;
    public boolean isFollow = true;

    public UserListItem(int userId, String nickname, String headshot) {
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
