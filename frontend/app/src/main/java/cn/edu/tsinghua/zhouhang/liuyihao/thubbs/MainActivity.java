package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.UserAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityMainBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                break;
            case APIConstant.REQUEST_ERROR:
                Alert.error(this, (String) msg.obj);
                break;
            case APIConstant.NETWORK_ERROR:
                Alert.error(this, R.string.network_error);
                break;
            case APIConstant.SERVER_ERROR:
                Alert.error(this, R.string.server_error);
                break;
        }
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(Constant.JWT) && preferences.contains(Constant.USER_ID)) {
            State.getState().jwt = preferences.getString(Constant.JWT, "");
            State.getState().userId = preferences.getInt(Constant.USER_ID, 0);
            State.getState().isLogin = true;
        }
        getMyProfile();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        final AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_account, R.id.navigation_notifications)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getMyProfile() {
        try {
            JSONObject data = new JSONObject();
            data.put(UserAPI.userid, State.getState().userId);
            UserAPI.getProfile(data, new Callback() {
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
                            User user = JSONUtil.createUserFromJSON(data);
                            if (user == null) {
                                msg.what = APIConstant.REQUEST_ERROR;
                                msg.obj = getResources().getString(R.string.server_error);
                            } else {
                                State.getState().user = user;
                                msg.what = APIConstant.REQUEST_OK;
                            }
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
            });
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }
}