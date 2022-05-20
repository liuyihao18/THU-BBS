package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class UserListItem {
    private final int userId;
    private final String nickname;
    private final String headshot;
    private final String description;
    public boolean isFollow = true;

    public UserListItem(int userId, String nickname, String headshot, String description) {
        this.userId = userId;
        this.nickname = nickname;
        this.headshot = headshot;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
