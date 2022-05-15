package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Tweet implements Serializable {
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

    public Tweet(int tweetID, int userID, int type, String content, @Nullable String location,
                 String lastModified, int commentCount, int likeCount,
                 @Nullable ArrayList<String> imageList, @Nullable String audioUrl, @Nullable String videoUrl) {
        this.tweetID = tweetID;
        this.userID = userID;
        this.type = type;
        this.content = content;
        this.location = location;
        this.lastModified = lastModified;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.imageList = imageList;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
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

    @Nullable
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
        if (imageList == null) {
            return 0;
        }
        return imageList.size();
    }

    @Nullable
    public String getImageAt(int i) {
        if (imageList == null) {
            return null;
        }
        return imageList.get(i);
    }

    @Nullable
    public ArrayList<String> getImageList() {
        return imageList;
    }

    @Nullable
    public String getAudioUrl() {
        return audioUrl;
    }

    @Nullable
    public String getVideoUrl() {
        return videoUrl;
    }
}
