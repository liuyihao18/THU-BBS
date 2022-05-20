package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import okhttp3.Callback;


public class RelationAPI {
    public static final String followId = "follow_id";
    public static final String userId = "userid";
    public static final String nickname = "nickname";
    public static final String headshot = "headshot";
    public static final String description = "description";
    public static final String followList = "follow_list";
    public static final String blackList = "black_list";
    public static final String fanList = "fan_list";
    public static final String blackId = "black_id";
    public static final String whiteId = "white_id";

    private static final String prefix = "/relation";

    private static String getUrl(String suffix) {
        return Config.BASE_URL + APIConstant.API_PREFIX + prefix + suffix;
    }

    public static void follow(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/follow"), data, callback);
    }

    public static void unfollow(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/unfollow"), data, callback);
    }

    public static void black(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/black"), data, callback);
    }

    public static void white(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/white"), data, callback);
    }

    public static void getFollowList(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_follow_list"), data, callback);
    }

    public static void getBlackList(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_black_list"), data, callback);
    }

    public static void getFanList(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_fan_list"), data, callback);
    }
}
