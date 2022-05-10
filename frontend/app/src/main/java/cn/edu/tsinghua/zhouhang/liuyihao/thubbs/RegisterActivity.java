package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListener();
    }

    private void initListener() {
        binding.cancelButton.setOnClickListener(view -> finish());
        binding.okButton.setOnClickListener(view -> {
            // TODO: 注册
        });
    }
}