package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class Tweet {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;

    private final int tweetID;
    private final int userID;
    private final int type;
    private final String content;
    private final String location;
    private final String lastModified;
    private final int commentNum;
    private final int likeNum;

    public Tweet(int tweetID, int userID, int type, String content, String location,
                 String lastModified, int commentNum, int likeNum) {
        this.tweetID = tweetID;
        this.userID = userID;
        this.type = type;
        this.content = content;
        this.location = location;
        this.lastModified = lastModified;
        this.commentNum = commentNum;
        this.likeNum = likeNum;
    }

    public int getTweetID() {
        return tweetID;
    }

    public int getUserID() {
        return userID;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getLocation() {
        return location;
    }

    public String getLastModified() {
        return lastModified;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public int getLikeNum() {
        return likeNum;
    }
}
