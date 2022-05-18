package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BaseRequest {
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * HTTP GET
     *
     * @param url      地址
     * @param callback 回调
     */
    public static void get(String url, Callback callback) {
        String jwt = State.getState().jwt == null ? "" : State.getState().jwt;
        Request request = new Request.Builder()
                .url(url)
                .addHeader(APIConstant.AUTHORIZATION, jwt)
                .get()
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    /**
     * HTTP POST
     *
     * @param url      地址
     * @param data     数据
     * @param callback 回调
     */
    public static void post(String url, JSONObject data, Callback callback) {
        RequestBody body = RequestBody.create(data.toString(), JSON_TYPE);
        String jwt = State.getState().jwt == null ? "" : State.getState().jwt;
        Request request = new Request.Builder()
                .url(url)
                .addHeader(APIConstant.AUTHORIZATION, jwt)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    /**
     * HTTP POST
     * @param url 地址
     * @param body 请求体
     * @param callback 回调
     */
    public static void post(String url, MultipartBody body, Callback callback) {
        String jwt = State.getState().jwt == null ? "" : State.getState().jwt;
        Request request = new Request.Builder()
                .url(url)
                .addHeader(APIConstant.AUTHORIZATION, jwt)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }


}
