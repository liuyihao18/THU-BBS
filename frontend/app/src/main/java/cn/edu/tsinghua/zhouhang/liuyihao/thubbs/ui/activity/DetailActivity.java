package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityDetailBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.CommentItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Comment;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.MediaResource;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.TweetUtil;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private TweetItemBinding tweetItemBinding;
    private MediaResource mediaResource = new MediaResource();
    private Tweet mTweet;

    private final LinkedList<Comment> mCommentList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        tweetItemBinding = TweetItemBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        binding.tweet.addView(tweetItemBinding.getRoot());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        initView();
        if (action.equals(Constant.DETAIL_HAVE_DATA)) {
            mTweet = (Tweet) intent.getSerializableExtra(Constant.EXTRA_TWEET);
            bindTweet();
        } else {
            // TODO: 获取推特数据
        }
        /* 测试数据开始 */
        mCommentList.add(new Comment(1, 1, "用户1", State.getState().headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(2, 1, "用户1", State.getState().headshot,
                "不也挺好的", "2022-05-16"));
        mCommentList.add(new Comment(3, 1, "用户2", State.getState().headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(4, 1, "用户3", State.getState().headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(5, 1, "用户1", State.getState().headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(6, 1, "用户4", State.getState().headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(7, 1, "用户2", State.getState().headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(1, 1, "用户8", State.getState().headshot,
                "真棒~", "2022-05-16"));
        /* 测试数据结束 */
        bindComment();
        initListener();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_TWEET, mTweet);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void initView() {
        binding.commentHeadshot.setImageUrl(State.getState().headshot);
    }

    private void initListener() {
        findViewById(R.id.back_button).setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA_TWEET, mTweet);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void bindTweet() {
        TweetUtil.bind(this, tweetItemBinding, mTweet, mediaResource);
    }

    void bindComment() {
        for (int i = binding.commentGroup.getChildCount(); i < mCommentList.size(); i++) {
            CommentItemBinding commentItemBinding = CommentItemBinding.inflate(getLayoutInflater(), binding.commentGroup, false);
            commentItemBinding.headshot.setImageUrl(mCommentList.get(i).getHeadshot());
            commentItemBinding.nickname.setText(mCommentList.get(i).getNickname());
            commentItemBinding.commentTime.setText(mCommentList.get(i).getCommentTime());
            commentItemBinding.content.setText(mCommentList.get(i).getContent());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            View view = commentItemBinding.getRoot();
            view.setLayoutParams(layoutParams);
            binding.commentGroup.addView(view);
        }
    }
}