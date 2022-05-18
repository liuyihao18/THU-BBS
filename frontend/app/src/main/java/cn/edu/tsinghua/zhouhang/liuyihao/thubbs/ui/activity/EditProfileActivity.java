package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
        initListener();
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.edit_profile);
        binding.nickname.setText(State.getState().user.nickname);
        if (State.getState().user.description.isEmpty()) {
            binding.description.setHint(R.string.description_empty);
        } else {
            binding.description.setText(State.getState().user.description);
        }
    }

    private void initListener() {
        findViewById(R.id.back_button).setOnClickListener(view -> {
            onBackPressed();
        });
    }


}