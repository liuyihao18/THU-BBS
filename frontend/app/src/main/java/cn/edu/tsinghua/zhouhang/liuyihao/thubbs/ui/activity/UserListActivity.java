package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityUserListBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class UserListActivity extends AppCompatActivity {

    private ActivityUserListBinding binding;
    private int mListType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 隐藏APP顶部栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 获取数据（列表类别）
        Intent intent = getIntent();
        mListType = intent.getIntExtra(Constant.USER_LIST_TYPE, Constant.EMPTY_LIST);
        initView();
    }

    private void initView() {
        switch (mListType) {
            case Constant.FOLLOW_LIST:
                ((TextView) findViewById(R.id.header_title)).setText(R.string.follow_list);
                break;
            case Constant.FAN_LIST:
                ((TextView) findViewById(R.id.header_title)).setText(R.string.follower_list);
                break;
            case Constant.BLACK_LIST:
                ((TextView) findViewById(R.id.header_title)).setText(R.string.blacklist);
                break;
        }
    }
}