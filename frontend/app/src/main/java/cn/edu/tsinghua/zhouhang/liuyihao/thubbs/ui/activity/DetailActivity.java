package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityDetailBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.CommentItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Comment;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.MediaResource;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.TweetUtil;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private TweetItemBinding tweetItemBinding;
    private final MediaResource mediaResource = new MediaResource();
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;
    private Tweet mTweet;

    private final LinkedList<Comment> mCommentList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        // 动态项绑定
        tweetItemBinding = TweetItemBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        binding.tweet.addView(tweetItemBinding.getRoot());
        setContentView(binding.getRoot());
        // 隐藏APP顶部栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 获取数据
        Intent intent = getIntent();
        String action = intent.getAction();
        initLauncher();
        initView();
        // 携带数据打开的
        if (action.equals(Constant.DETAIL_HAVE_DATA)) {
            mTweet = (Tweet) intent.getSerializableExtra(Constant.EXTRA_TWEET);
            bindTweet();
        }
        // 不携带数据打开的
        else if (action.equals(Constant.DETAIL_NO_DATA)) {
            // TODO: 获取动态数据
        }
        /* 测试数据开始 */
        mCommentList.add(new Comment(1, 1, "用户1", State.getState().user.headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(2, 2, "用户1", State.getState().user.headshot,
                "不也挺好的", "2022-05-16"));
        mCommentList.add(new Comment(3, 3, "用户2", State.getState().user.headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(4, 4, "用户3", State.getState().user.headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(5, 1, "用户1", State.getState().user.headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(6, 2, "用户4", State.getState().user.headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(7, 3, "用户2", State.getState().user.headshot,
                "真棒~", "2022-05-16"));
        mCommentList.add(new Comment(1, 4, "用户8", State.getState().user.headshot,
                "真棒~", "2022-05-16"));
        /* 测试数据结束 */
        bindComment();
        initListener();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_TWEET, mTweet);
        super.onBackPressed();
    }

    private void initLauncher() {
        mUserSpaceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // 级联返回（从个人主页返回详情）
            if (result.getResultCode() == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.detail);
        if (State.getState().user != null) {
            binding.commentHeadshot.setImageUrl(State.getState().user.headshot);
        } else {
            binding.commentHeadshot.setImageUrl(Constant.DEFAULT_HEADSHOT);
            State.getState().refreshMyProfile(this, null);
        }
    }

    private void initListener() {
        // 返回
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
    }

    private void bindTweet() {
        // 绑定动态
        TweetUtil.bind(this, tweetItemBinding, mTweet, Constant.TWEETS_DETAIL, mediaResource,
                // 屏蔽用户
                view ->
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.question_black)
                                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                                }))
                                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                                    setResult(RESULT_OK);
                                    finish();
                                }).create().show(),
                // 级联返回（从个人主页返回详情）
                view -> {
                    Intent intent = new Intent(this, UserSpaceActivity.class);
                    intent.putExtra(Constant.EXTRA_USER_ID, mTweet.getUserID());
                    mUserSpaceLauncher.launch(intent);
                });
    }

    void bindComment() {
        for (int i = binding.commentGroup.getChildCount(); i < mCommentList.size(); i++) {
            CommentItemBinding commentItemBinding = CommentItemBinding.inflate(getLayoutInflater(), binding.commentGroup, false);
            commentItemBinding.headshot.setImageUrl(mCommentList.get(i).getHeadshot());
            commentItemBinding.nickname.setText(mCommentList.get(i).getNickname());
            commentItemBinding.commentTime.setText(mCommentList.get(i).getCommentTime());
            commentItemBinding.content.setText(mCommentList.get(i).getContent());
            if (mCommentList.get(i).getUserId() == State.getState().userId) {
                commentItemBinding.closeButton.setVisibility(View.VISIBLE);
            } else {
                commentItemBinding.closeButton.setVisibility(View.GONE);
            }
            int finalI = i;
            commentItemBinding.headshot.setOnClickListener(view -> {
                Intent intent = new Intent(this, UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, mCommentList.get(finalI).getUserId());
                this.startActivity(intent);
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            View view = commentItemBinding.getRoot();
            view.setLayoutParams(layoutParams);
            binding.commentGroup.addView(view);
        }
    }
}