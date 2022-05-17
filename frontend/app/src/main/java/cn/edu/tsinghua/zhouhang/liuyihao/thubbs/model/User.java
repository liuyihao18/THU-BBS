package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class User {
    public String nickname;
    public String headshot;
    public String description;
    public int tweetCount;
    public int followCount; // 关注
    public int followerCount; // 粉丝

    public User(String nickname, String headshot, String description,
                int tweetCount, int followCount, int followerCount) {
        this.nickname = nickname;
        this.headshot = headshot;
        this.description = description;
        this.tweetCount = tweetCount;
        this.followCount = followCount;
        this.followerCount = followerCount;
    }
}
