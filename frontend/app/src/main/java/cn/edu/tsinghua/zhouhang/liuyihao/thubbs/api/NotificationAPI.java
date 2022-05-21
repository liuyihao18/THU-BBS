package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import okhttp3.Callback;

public class NotificationAPI {
    public static final String block = "block";
    public static final String notification_id = "notification_id";
    public static final String userid = "userid";
    public static final String tweet_id = "tweet_id";
    public static final String like_time = "like_time";
    public static final String tweet_content = "tweet_content";
    public static final String like_user_name = "like_user_name";
    public static final String comment_time = "comment_time";
    public static final String content = "content";
    public static final String comment_user_name = "comment_user_name";
    public static final String headshot = "headshot";
    public static final String notification_time = "notification_time";
    public static final String followee_user_name = "followee_user_name";

    private static final String prefix = "/notification";

    public static String notification_list = "notification_list";

    private static String getUrl(String suffix) {
        return Config.BASE_URL + APIConstant.API_PREFIX + prefix + suffix;
    }

    public static void get_like_notification_list(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_like_notification_list"), data, callback);
    }

    public static void get_comment_notification_list(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_comment_notification_list"), data, callback);
    }

    public static void get_follow_notification_list(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_follow_notification_list"), data, callback);
    }

    public static void get_message_list(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_message_list"), data, callback);
    }

    public static void delete_like_notification(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/delete_like_notification"), data, callback);
    }

    public static void delete_comment_notification(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/delete_comment_notification"), data, callback);
    }

    public static void delete_follow_notification(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/delete_follow_notification"), data, callback);
    }
}
