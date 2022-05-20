package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityEditPasswordBinding;

public class EditPasswordActivity extends AppCompatActivity {

    private ActivityEditPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
        initListener();
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.edit_password);
    }

    private void initListener() {
        // 返回
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
    }

}