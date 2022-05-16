package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class CommentItemContent {
    private final String HeadshotURL;
    private final String CommentUserName;
    private final String CommentDate;
    private final String CommentContent;
    private final int TweetID;

    public CommentItemContent(String headshotURL, String commentUserName, String commentDate, int tweetID, String commentContent) {
        HeadshotURL = headshotURL;
        CommentUserName = commentUserName;
        CommentDate = commentDate;
        CommentContent = commentContent;
        TweetID = tweetID;
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
}
