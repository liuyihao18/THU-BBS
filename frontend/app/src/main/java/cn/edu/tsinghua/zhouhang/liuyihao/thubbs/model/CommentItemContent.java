package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;

public class CommentItemContent {
    private final String HeadshotURL;
    private final String CommentUserName;
    private final String CommentDate;
    private final String CommentContent;
    private final int TweetID;
    private final String TweetContent;
    private final int CommentUserID;
    private final int NotificationID;

    public CommentItemContent(String headshotURL, String commentUserName, String commentDate, int tweetID, String commentContent,
                              int commentUserID, int notificationID, String tweetContent) {
        HeadshotURL = headshotURL;
        CommentUserName = commentUserName;
        CommentDate = commentDate;
        CommentContent = commentContent;
        TweetID = tweetID;
        CommentUserID = commentUserID;
        NotificationID = notificationID;
        TweetContent = tweetContent;
    }

    public int getTweetID() {
        return TweetID;
    }

    public String getHeadshotURL() {
        return HeadshotURL;
    }

    public String getCommentContent() {
        return CommentContent;
    }

    public String getCommentDate() {
        return CommentDate;
    }

    public String getCommentUserName() {
        return CommentUserName;
    }

    public int getNotificationID() {
        return NotificationID;
    }

    public int getCommentUserID() {
        return CommentUserID;
    }

    public String getTweetContent() {
        return TweetContent;
    }
}
