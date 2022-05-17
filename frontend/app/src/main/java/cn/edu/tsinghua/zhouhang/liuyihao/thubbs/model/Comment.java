package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class Comment {
    private final int commentId;
    private final int userId;
    private final String nickname;
    private final String headshot;
    private final String content;
    private final String commentTime;

    public Comment(int commentId, int userId, String nickname, String headshot,
                   String content, String commentTime) {
        this.commentId = commentId;
        this.userId = userId;
        this.nickname = nickname;
        this.headshot = headshot;
        this.content = content;
        this.commentTime = commentTime;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getHeadshot() {
        return headshot;
    }

    public String getContent() {
        return content;
    }

    public String getCommentTime() {
        return commentTime;
    }
}
