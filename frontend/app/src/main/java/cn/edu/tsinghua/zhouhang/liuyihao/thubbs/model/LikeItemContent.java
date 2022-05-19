package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class LikeItemContent {
    private final String HeadshotURL;
    private final String LikeUserName;
    private final String LikeDate;
    private final int TweetID;
    private final String TweetContent;
    private final int LikeUserID;
    private final int NotificationID;
    public LikeItemContent(String headshotURL, String likeUserName, String likeDate, int tweetID, String tweetContent,
                           int likeUserID, int notificationID) {
        HeadshotURL = headshotURL;
        LikeUserName = likeUserName;
        LikeDate = likeDate;
        TweetID = tweetID;
        TweetContent = tweetContent;
        LikeUserID = likeUserID;
        NotificationID = notificationID;
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

    public String getTweetContent() {
        return TweetContent;
    }

    public int getLikeUserID() {
        return LikeUserID;
    }

    public int getNotificationID() {
        return NotificationID;
    }
}
