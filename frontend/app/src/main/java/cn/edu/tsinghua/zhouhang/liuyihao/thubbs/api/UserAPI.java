package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MultipartBody;

public class UserAPI {
    public static final String email = "email";
    public static final String password = "password";
    public static final String userid = "userid";
    public static final String nickname = "nickname";
    public static final String headshot = "headshot";
    public static final String description = "description";
    public static final String tweetCount = "tweet_count";
    public static final String followCount = "follow_count";
    public static final String followerCount = "follower_count";
    public static final String isFollow = "is_follow";
    public static final String oldPassword = "old_password";
    public static final String newPassword = "new_password";

    private static final String prefix = "/user";

    private static String getUrl(String suffix) {
        return Config.BASE_URL + APIConstant.API_PREFIX + prefix + suffix;
    }

    public static void register(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/register"), data, callback);
    }

    public static void login(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/login"), data, callback);
    }

    public static void getProfile(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_profile"), data, callback);
    }

    public static void editProfile(MultipartBody body, Callback callback) {
        BaseRequest.post(getUrl("/edit_profile"), body, callback);
    }

    public static void editPassword(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/edit_password"), data, callback);
    }
}
