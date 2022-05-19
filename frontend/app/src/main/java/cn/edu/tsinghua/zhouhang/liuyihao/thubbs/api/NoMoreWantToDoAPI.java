package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.TweetUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NoMoreWantToDoAPI {

    public interface OnCMDSuccessListener {
        void onCMDSuccess();
    }

    private static final int CMD_FOLLOW = 1;
    private static final int CMD_UNFOLLOW = 2;
    private static final int CMD_BLACK = 3;
    private static final int CMD_WHITE = 4;
    private static final int CMD_LIKE_TWEET = 5;
    private static final int CMD_CANCEL_LIKE_TWEET = 6;

    public static void follow(Context context, int userid, OnCMDSuccessListener onCMDSuccessListener) {
        doCMD(context, userid, CMD_FOLLOW, onCMDSuccessListener);
    }

    public static void unfollow(Context context, int userid, OnCMDSuccessListener onCMDSuccessListener) {
        doCMD(context, userid, CMD_UNFOLLOW, onCMDSuccessListener);
    }

    public static void likeTweet(Context context, int tweet_id, OnCMDSuccessListener onCMDSuccessListener) {
        doCMD(context, tweet_id, CMD_LIKE_TWEET, onCMDSuccessListener);
    }

    private static void doCMD(Context context, int id, int cmd, OnCMDSuccessListener onCMDSuccessListener) {
        Handler handler = new Handler(Looper.myLooper(), msg -> {
            switch (msg.what) {
                case APIConstant.REQUEST_OK:
                case APIConstant.REQUEST_ERROR:
                    if (onCMDSuccessListener != null) {
                        onCMDSuccessListener.onCMDSuccess();
                    }
                    break;
                case APIConstant.NETWORK_ERROR:
                    Alert.error(context, R.string.network_error);
                    break;
                case APIConstant.SERVER_ERROR:
                    Alert.error(context, R.string.server_error);
                    break;
            }
            return true;
        });
        JSONObject data = new JSONObject();
        try {
            switch (cmd) {
                case CMD_FOLLOW:
                case CMD_UNFOLLOW:
                    data.put(RelationAPI.followId, id);
                    break;
                case CMD_LIKE_TWEET:
                    data.put(TweetAPI.tweetId, id);
                    break;
            }
            Callback callback = new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Message msg = new Message();
                    msg.what = APIConstant.NETWORK_ERROR;
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    ResponseBody body = response.body();
                    Message msg = new Message();
                    if (body == null) {
                        msg.what = APIConstant.SERVER_ERROR;
                        handler.sendMessage(msg);
                        return;
                    }
                    try {
                        JSONObject data = new JSONObject(body.string());
                        int errCode = data.getInt(APIConstant.ERR_CODE);
                        if (errCode == 0) {
                            msg.what = APIConstant.REQUEST_OK;
                        } else {
                            msg.what = APIConstant.REQUEST_ERROR;
                            msg.obj = data.getString(APIConstant.ERR_MSG);
                        }
                        handler.sendMessage(msg);
                    } catch (JSONException je) {
                        System.err.println("Bad response format.");
                    } finally {
                        body.close();
                    }
                }
            };
            switch (cmd) {
                case CMD_FOLLOW:
                    RelationAPI.follow(data, callback);
                    break;
                case CMD_UNFOLLOW:
                    RelationAPI.unfollow(data, callback);
                    break;
                case CMD_BLACK:
                    break;
                case CMD_WHITE:
                    break;
                case CMD_LIKE_TWEET:
                    TweetAPI.likeTweet(data, callback);
                    break;
            }
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }
}
