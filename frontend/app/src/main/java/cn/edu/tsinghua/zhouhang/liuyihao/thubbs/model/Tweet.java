package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class Tweet implements Serializable {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;

    private final int tweetId;
    private final int userId;
    private final int type;
    private final String title;
    private final String content;
    private final String location;
    private final String lastModified;
    private final ArrayList<String> imageList;
    private final String audioUrl;
    private final String videoUrl;
    private final String nickname;
    private final String headshot;
    public int commentCount;
    public int likeCount;
    public boolean isFollow;
    public boolean isLike;


    public Tweet(int tweetID, int userID, int type, String title, String content,
                 @Nullable String location, String lastModified, int commentCount, int likeCount,
                 @Nullable ArrayList<String> imageList, @Nullable String audioUrl, @Nullable String videoUrl,
                 String nickname, String headshot, boolean isFollow, boolean isLike) {
        this.tweetId = tweetID;
        this.userId = userID;
        this.type = type;
        this.title = title;
        this.content = content;
        this.location = location;
        this.lastModified = lastModified;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.imageList = imageList;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
        this.nickname = nickname;
        this.headshot = headshot;
        this.isFollow = isFollow;
        this.isLike = isLike;
    }

    public int getTweetId() {
        return tweetId;
    }

    public int getUserId() {
        return userId;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
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

    public String getNickname() {
        return nickname;
    }

    public String getHeadshot() {
        return headshot;
    }
}
