package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import okhttp3.Callback;

public class UserAPI {
    public static final String email = "email";
    public static final String password = "password";
    public static final String userid = "userid";
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
}
