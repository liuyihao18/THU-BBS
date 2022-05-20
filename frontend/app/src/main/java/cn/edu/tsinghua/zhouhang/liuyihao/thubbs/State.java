package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.UserAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.LoginActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class State {
    private static final State mState = new State();
    public boolean isLogin;
    public String jwt;
    public int userId;
    public User user;
    public ActivityResultLauncher<Intent> mLoginLauncher;
    public OnLoginListener onLoginListener;

    // 登录成功后的回调接口
    public interface OnLoginListener {
        void onLogin();
    }

    private State() {
        isLogin = false;
        jwt = null;
        userId = 0;
        user = null;
    }

    public static State getState() {
        return mState;
    }

    public State setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
        return this;
    }

    // 通用登录接口
    public void login(Context context) {
        mLoginLauncher.launch(new Intent(context, LoginActivity.class));
    }

    /**
     * 通用刷新用户信息接口
     *
     * @param handler 消息处理句柄
     *                有可能发出以下信息：
     *                1. 成功：msg.what = Constant.LOGIN_OK = 0, msg.obj = null
     *                2. 失败：msg.what = APIConstant.NETWORK_ERROR, msg.obj = null
     *                3. 失败：msg.what = APIConstant.SERVER_ERROR, msg.obj = null
     *                4. 失败：msg.what = APIConstant.REQUEST_ERROR, msg.obj = ERR_MSG
     */
    public void refreshMyProfile(@Nullable Handler handler) {
        try {
            JSONObject data = new JSONObject();
            data.put(UserAPI.userid, State.getState().userId);
            UserAPI.getProfile(data, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Message msg = new Message();
                    msg.what = APIConstant.NETWORK_ERROR;
                    if (handler != null) {
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    ResponseBody body = response.body();
                    Message msg = new Message();
                    if (body == null) {
                        msg.what = APIConstant.SERVER_ERROR;
                        if (handler != null) {
                            handler.sendMessage(msg);
                        }
                        return;
                    }
                    try {
                        JSONObject data = new JSONObject(body.string());
                        int errCode = data.getInt(APIConstant.ERR_CODE);
                        if (errCode == 0) {
                            User user = JSONUtil.createUserFromJSON(data);
                            if (user == null) {
                                msg.what = APIConstant.SERVER_ERROR;
                            } else {
                                getState().user = user;
                                msg.what = Constant.LOGIN_OK;
                            }
                        } else {
                            msg.what = APIConstant.REQUEST_ERROR;
                            msg.obj = data.getString(APIConstant.ERR_MSG);
                        }
                        if (handler != null) {
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException je) {
                        System.err.println("Bad response format.");
                    } finally {
                        body.close();
                    }
                }
            });
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }

    @NonNull
    public String toString() {
        String result = "isLogin: " + isLogin + "\n" +
                "userId: " + userId + "\n";
        if (user != null) {
            result += "nickname: " + user.nickname + "\n" +
                    "description: " + user.description;
        }
        return result;
    }
}
