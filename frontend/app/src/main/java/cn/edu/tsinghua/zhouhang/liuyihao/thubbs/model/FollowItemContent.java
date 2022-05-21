package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class FollowItemContent {
    private final String HeadshotURL;
    private final String FolloweeUserName;
    private final String NotificationDate;
    private final int TweetID;
    private final String TweetContent;
    private final int FolloweeUserID;
    private final int NotificationID;
    public FollowItemContent(String headshotURL, String followeeUserName, String notificationDate, int tweetID, String tweetContent,
                           int followeeUserID, int notificationID) {
        HeadshotURL = headshotURL;
        FolloweeUserName = followeeUserName;
        NotificationDate = notificationDate;
        TweetID = tweetID;
        TweetContent = tweetContent;
        FolloweeUserID = followeeUserID;
        NotificationID = notificationID;
    }

    public int getTweetID() {
        return TweetID;
    }

    public String getHeadshotURL() {
        return HeadshotURL;
    }

    public String getFolloweeUserName() {
        return FolloweeUserName;
    }

    public String getNotificationDate() {
        return NotificationDate;
    }

    public String getTweetContent() {
        return TweetContent;
    }

    public int getFolloweeUserID() {
        return FolloweeUserID;
    }

    public int getNotificationID() {
        return NotificationID;
    }
}
