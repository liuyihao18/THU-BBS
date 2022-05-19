package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import okhttp3.Callback;


public class RelationAPI {
    public static final String followId = "follow_id";

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
}
