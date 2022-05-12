package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class LikeItemContent {
    private final String HeadshotURL;
    private final String LikeUserName;
    private final String LikeDate;
    private final int TweetID;
    public LikeItemContent(String headshotURL, String likeUserName, String likeDate, int tweetID) {
        HeadshotURL = headshotURL;
        LikeUserName = likeUserName;
        LikeDate = likeDate;
        TweetID = tweetID;
    }

    public int getTweetID() {
        return TweetID;
    }

    public String getHeadshotURL() {
        return HeadshotURL;
    }

    public String getLikeUserName() {
        return LikeUserName;
    }

    public String getLikeDate() {
        return LikeDate;
    }
}
