package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityDraftBoxBinding;

public class DraftBoxActivity extends AppCompatActivity implements GoEditInterface {

    private ActivityDraftBoxBinding binding;
    private ActivityResultLauncher<Intent> mEditLauncher;
    private OnEditReturnListener onEditReturnListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDraftBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 隐藏APP顶部栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initLauncher();
        initView();
        initListener();
        getDraftList(true);
    }

    private void initLauncher() {
        mEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            getDraftList(true);
            if (onEditReturnListener != null) {
                onEditReturnListener.onEditReturn(result);
            }
        });
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.draft_box);
    }

    private void initListener() {
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void registerOnEditReturnListener(OnEditReturnListener onEditReturnListener) {
        this.onEditReturnListener = onEditReturnListener;
    }

    @Override
    public void goEdit(Intent intent) {
        mEditLauncher.launch(intent);
    }

    private void getDraftList(boolean isDraft) {

    }
}