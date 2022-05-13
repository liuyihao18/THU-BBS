package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.User;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityLoginBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Security;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ActivityResultLauncher<Intent> mLauncher;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        try {
            switch (msg.what) {
                case APIConstant.REQUEST_OK:
                    JSONObject data = (JSONObject) msg.obj;
                    int errCode = data.getInt(APIConstant.ERR_CODE);
                    if (errCode == 0) {
                        setResult(RESULT_OK);
                        State.getState().jwt = data.getString(Constant.JWT);
                        // State.getState().userID = data.getInt(Constant.USER_ID);
                        State.getState().isLogin = true;
                        SharedPreferences preferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);
                        preferences.edit().putString(Constant.JWT, State.getState().jwt).apply();
                        preferences.edit().putInt(Constant.USER_ID, State.getState().userID).apply();
                        Alert.info(this, R.string.login_success);
                        finish();
                    } else {
                        Alert.error(this, data.getString(APIConstant.ERR_MSG));
                    }
                    break;
                case APIConstant.NETWORK_ERROR:
                    Alert.error(this, R.string.network_error);
                    break;
                case APIConstant.SERVER_ERROR:
                    Alert.error(this, R.string.server_error);
                    break;
            }
        } catch (JSONException je) {
            System.err.println("Bad response format.");
            State.getState().jwt = null;
            State.getState().userID = 0;
            State.getState().isLogin = false;
        }
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLauncher();
        initListener();
    }

    private void initLauncher() {
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    String email = intent.getStringExtra(Constant.EXTRA_EMAIL);
                    binding.emailInput.setText(email);
                }
            }
        });
    }

    private void initListener() {
        binding.cancelButton.setOnClickListener(view -> finish());
        binding.okButton.setOnClickListener(view -> {
            String email = binding.emailInput.getText().toString();
            String password = binding.passwordInput.getText().toString();
            if (email.isEmpty()) {
                Alert.info(this, R.string.email_required);
                return;
            }
            if (password.isEmpty()) {
                Alert.info(this, R.string.password_required);
                return;
            }
            String regEx = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
                    "(tsinghua.edu.cn|mail.tsinghua.edu.cn|mails.tsinghua.edu.cn)$";
            Matcher matcher = Pattern.compile(regEx).matcher(email);
            if (!matcher.matches()) {
                Alert.info(this, R.string.valid_email_required);
                return;
            }
            password = Security.encode(password);
            JSONObject data = new JSONObject();
            try {
                data.put(User.email, email);
                data.put(User.password, password);
                User.login(data, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Message msg = new Message();
                        msg.what = APIConstant.NETWORK_ERROR;
                        e.printStackTrace();
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
                            msg.what = APIConstant.REQUEST_OK;
                            msg.obj = data;
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
        });
        binding.forget.setOnClickListener(view -> Alert.info(this, R.string.forget_label));
        binding.register.setOnClickListener(view -> mLauncher.launch(new Intent(this, RegisterActivity.class)));
    }

}