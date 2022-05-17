package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.UserAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityMainBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(Constant.JWT) && preferences.contains(Constant.USER_ID)) {
            State.getState().jwt = preferences.getString(Constant.JWT, "");
            State.getState().userID = preferences.getInt(Constant.USER_ID, 0);
            State.getState().isLogin = true;
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
            data.put(UserAPI.userid, State.getState().userID);
            UserAPI.getProfile(data, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }
}