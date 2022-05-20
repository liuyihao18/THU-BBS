package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model;

public class mMessage {
    private final String Title;
    private final String Content;
    private final String MessageTime;
    private final int MessageID;
    private final String Headshot;

    public mMessage(String title, String content, String messageTime, int messageID, String headshot) {
        Title = title;
        Content = content;
        MessageTime = messageTime;
        MessageID = messageID;
        Headshot = headshot;
    }

    public int getMessageID() {
        return MessageID;
    }

    public String getContent() {
        return Content;
    }

    public String getMessageTime() {
        return MessageTime;
    }

    public String getTitle() {
        return Title;
    }

    public String getHeadshot() {
        return Headshot;
    }
}
