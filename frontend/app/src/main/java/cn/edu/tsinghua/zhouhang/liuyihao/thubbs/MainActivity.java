package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.CharsetEncoder;

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
                if (State.getState().onLoginListener != null) {
                    State.getState().onLoginListener.onLogin();
                }
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
        State.getState().mLoginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getMyProfile();
            }
        });
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

    public void getMyProfile() {
        State.getState().refreshMyProfile(this, handler);
    }
}