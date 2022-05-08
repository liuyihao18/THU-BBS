package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BaseRequest {
    public static final String ERR_CODE = "errCode";
    public static final String ERR_MSG = "errMsg";

    public static final int REQUEST_OK = 0;
    public static final int REQUEST_ERR = -1;

    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static void post(String url, JSONObject data, Callback callback) {
        RequestBody body = RequestBody.create(data.toString(), JSON_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    public static void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }
}
