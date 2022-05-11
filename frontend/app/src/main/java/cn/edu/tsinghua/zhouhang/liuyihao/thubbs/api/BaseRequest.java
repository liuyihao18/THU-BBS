package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BaseRequest {
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static void post(String url, JSONObject data, Callback callback) {
        RequestBody body = RequestBody.create(data.toString(), JSON_TYPE);
        String jwt = State.getState().jwt == null ? "" : State.getState().jwt;
        Request request = new Request.Builder()
                .url(url)
                .addHeader(Constant.JWT, jwt)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    public static void get(String url, Callback callback) {
        String jwt = State.getState().jwt == null ? "" : State.getState().jwt;
        Request request = new Request.Builder()
                .url(url)
                .addHeader(Constant.JWT, jwt)
                .get()
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }
}
