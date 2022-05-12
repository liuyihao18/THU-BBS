package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityCommentNotificationBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;

public class CommentNotificationActivity extends AppCompatActivity {

    private ActivityCommentNotificationBinding binding;

    private LinkedList<CommentItemContent> commentItemContents = new LinkedList<CommentItemContent>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        commentItemContents.add(
                new CommentItemContent(
                        getString(R.string.default_headshot_url),
                        "xxx",
                        "2022=05-12",
                        1,
                        "测试用评论内容"));

        initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.commentNotificationList;
        recyclerView.setAdapter(new CommentListAdapter(this, commentItemContents));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}