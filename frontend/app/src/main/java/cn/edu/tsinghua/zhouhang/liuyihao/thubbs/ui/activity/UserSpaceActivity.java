package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityUserSpaceBinding;

public class UserSpaceActivity extends AppCompatActivity {
    private ActivityUserSpaceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSpaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final NavController navController = Navigation.findNavController(this, R.id.fragment_tweets_container);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.TWEETS_TYPE, Constant.TWEETS_USER);
        navController.setGraph(R.navigation.tweets_navigation, bundle);
    }

    private void initView() {

    }

    private void initListener() {
        findViewById(R.id.back_button).setOnClickListener(view -> {
            finish();
        });
    }
}