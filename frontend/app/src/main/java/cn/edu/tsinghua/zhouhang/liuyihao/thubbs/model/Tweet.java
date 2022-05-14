package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;

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
    private final int commentCount;
    private final int likeCount;
    private final ArrayList<String> imageList;
    private final String audioUrl;
    private final String videoUrl;

    public Tweet(int tweetID, int userID, int type, String content, String location,
                 String lastModified, int commentNum, int likeNum,
                 ArrayList<String> imageList, String audioUrl, String videoUrl) {
        this.tweetID = tweetID;
        this.userID = userID;
        this.type = type;
        this.content = content;
        this.location = location;
        this.lastModified = lastModified;
        this.commentCount = commentNum;
        this.likeCount = likeNum;
        this.imageList = imageList;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
    }

    public static Tweet createFromJSON(@NonNull JSONObject data) {
        return null;
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

    public int getCommentCount() {
        return commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getImageCount() {
        return imageList.size();
    }

    public String getImageAt(int i) {
        return imageList.get(i);
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
