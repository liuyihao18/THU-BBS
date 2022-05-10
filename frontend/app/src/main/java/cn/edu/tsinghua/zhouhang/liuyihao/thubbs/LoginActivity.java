package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityLoginBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ActivityResultLauncher<Intent> mLauncher;

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

            }
        });
    }

    private void initListener() {
        binding.cancelButton.setOnClickListener(view -> {
            finish();
        });
        binding.okButton.setOnClickListener(view -> {
            // TODO: 登录
        });
        binding.forget.setOnClickListener(view -> {
            Alert.info(this, R.string.forget_label);
        });
        binding.register.setOnClickListener(view -> {
            mLauncher.launch(new Intent(this, RegisterActivity.class));
        });
    }

}