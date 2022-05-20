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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NoErrorAPI {

    public interface OnSuccessListener {
        void onSuccess();
    }

    private static final int CMD_FOLLOW = 1;
    private static final int CMD_UNFOLLOW = 2;
    private static final int CMD_BLACK = 3;
    private static final int CMD_WHITE = 4;
    private static final int CMD_LIKE_TWEET = 5;
    private static final int CMD_CANCEL_LIKE_TWEET = 6;

    public static void follow(Context context, int userid, OnSuccessListener onSuccessListener) {
        doCMD(context, userid, CMD_FOLLOW, onSuccessListener);
    }

    public static void unfollow(Context context, int userid, OnSuccessListener onSuccessListener) {
        doCMD(context, userid, CMD_UNFOLLOW, onSuccessListener);
    }

    public static void likeTweet(Context context, int tweet_id, OnSuccessListener onSuccessListener) {
        doCMD(context, tweet_id, CMD_LIKE_TWEET, onSuccessListener);
    }

    public static void cancelLikeTweet(Context context, int tweed_id, OnSuccessListener onSuccessListener) {
        doCMD(context, tweed_id, CMD_CANCEL_LIKE_TWEET, onSuccessListener);
    }

    public static void black(Context context, int black_id, OnSuccessListener onSuccessListener) {
        doCMD(context, black_id, CMD_BLACK, onSuccessListener);
    }

    public static void white(Context context, int white_id, OnSuccessListener onSuccessListener) {
        doCMD(context, white_id, CMD_WHITE, onSuccessListener);
    }

    private static void doCMD(Context context, int id, int cmd, OnSuccessListener onSuccessListener) {
        Handler handler = new Handler(Looper.myLooper(), msg -> {
            switch (msg.what) {
                case APIConstant.REQUEST_OK:
                case APIConstant.REQUEST_ERROR:
                    if (onSuccessListener != null) {
                        onSuccessListener.onSuccess();
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
                case CMD_CANCEL_LIKE_TWEET:
                    data.put(TweetAPI.tweetId, id);
                    break;
                case CMD_BLACK:
                    data.put(RelationAPI.blackId, id);
                    break;
                case CMD_WHITE:
                    data.put(RelationAPI.whiteId, id);
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
                    RelationAPI.black(data, callback);
                    break;
                case CMD_WHITE:
                    RelationAPI.white(data, callback);
                    break;
                case CMD_LIKE_TWEET:
                    TweetAPI.likeTweet(data, callback);
                    break;
                case CMD_CANCEL_LIKE_TWEET:
                    TweetAPI.cancelLikeTweet(data, callback);
                    break;
            }
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }
}
