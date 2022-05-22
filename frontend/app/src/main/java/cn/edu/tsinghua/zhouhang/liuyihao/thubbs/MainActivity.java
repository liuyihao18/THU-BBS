package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.baidu.mapapi.SDKInitializer;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityMainBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case Constant.LOGIN_OK:
                // 此时已经登录成功，保证State.getState().user != null
                if (State.getState().onLoginListener != null) {
                    // 处理回调
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
        SDKInitializer.initialize(getApplicationContext());
        SharedPreferences preferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        // 加载登录基础信息
        if (preferences.contains(Constant.JWT) && preferences.contains(Constant.USER_ID)) {
            State.getState().jwt = preferences.getString(Constant.JWT, "");
            State.getState().userId = preferences.getInt(Constant.USER_ID, 0);
            State.getState().isLogin = true;
        }
        // 挂载登录接口
        State.getState().mLoginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getMyProfile();
            }
        });
        // 获取用户信息
        if (State.getState().isLogin) {
            getMyProfile();
        }
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

    public void getMyProfile() {
        State.getState().refreshMyProfile(handler);
    }
}