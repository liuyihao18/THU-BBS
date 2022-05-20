package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class Draft {
    private final int tweetId;
    private final String title;
    private final String content;
    private final String location;
    private final String lastModified;

    public Draft(int tweetId, String title, String content, String location, String lastModified) {
        this.tweetId = tweetId;
        this.title = title;
        this.content = content;
        this.location = location;
        this.lastModified = lastModified;
    }

    public int getTweetId() {
        return tweetId;
    }

    public String getTitle() {
        return title;
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
}
