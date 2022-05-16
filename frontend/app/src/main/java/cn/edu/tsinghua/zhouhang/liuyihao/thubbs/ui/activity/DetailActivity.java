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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Comment;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    MediaPlayer mMediaPlayer;
    boolean loaded = false;
    private Tweet mTweet;

    private final LinkedList<Comment> mCommentList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
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
        binding.followButton.setOnClickListener(view -> {
            if (mTweet.isFollow) {
                mTweet.isFollow = false;
                binding.followButton.setText(R.string.follow);
                binding.followButton.setBackgroundColor(this.getColor(R.color.pink));
            } else {
                mTweet.isFollow = true;
                binding.followButton.setText(R.string.button_unfollow);
                binding.followButton.setBackgroundColor(this.getColor(R.color.button_disabled));
            }
        });
        binding.likeButton.setOnClickListener(view -> {
            if (mTweet.isLike) {
                mTweet.isLike = false;
                binding.likeButtonIcon.setImageResource(R.drawable.ic_like_24dp);
                mTweet.likeCount--;
            } else {
                mTweet.isLike = true;
                binding.likeButtonIcon.setImageResource(R.drawable.ic_like_pink_24dp);
                mTweet.likeCount++;
            }
            binding.likeButtonText.setText(String.valueOf(mTweet.getLikeCount()));
        });
    }

    private void initPlayer(@NotNull String audioUri) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(audioUri);
            mMediaPlayer.setOnCompletionListener(view -> {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
                binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
            });
            mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mMediaPlayer.start();
                binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_stop);
            });
            mMediaPlayer.prepareAsync();
        } catch (IOException ioe) {
            Alert.error(this, R.string.unknown_error);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void resetPlayer() {
        try {
            mMediaPlayer.stop();
        } catch (Exception e) {
            Alert.error(this, R.string.unknown_error);
        }
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
    }

    private void bindTweet() {
        /* 初始化 */
        loaded = false;
        binding.locationLayout.setVisibility(View.GONE);
        binding.imageGroup.setVisibility(View.GONE);
        binding.imageView.setVisibility(View.GONE);
        binding.audioPlayButton.setVisibility(View.GONE);
        binding.videoView.setVisibility(View.GONE);
        binding.videoPlayButton.setVisibility(View.GONE);
        /* 加载数据 */
        binding.authorHeadshot.setImageUrl(mTweet.getHeadshot());
        binding.authorName.setText(mTweet.getNickname());
        binding.contentText.setText(mTweet.getContent());
        binding.lastModified.setText(mTweet.getLastModified());
        if (mTweet.getLocation() != null) {
            binding.locationLayout.setVisibility(View.VISIBLE);
            binding.locationText.setText(mTweet.getLocation());
        }
        switch (mTweet.getType()) {
            case Tweet.TYPE_TEXT:
                break;
            case Tweet.TYPE_IMAGE:
                if (mTweet.getImageCount() == 0) {
                    Alert.error(this, R.string.unknown_error);
                } else if (mTweet.getImageCount() == 1) {
                    binding.imageView.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(mTweet.getImageAt(0))
                            .placeholder(R.drawable.ic_loading_spinner_black_24dp)
                            .into(binding.imageView);
                } else if (mTweet.getImageCount() <= Constant.MAX_IMAGE_COUNT) {
                    if (mTweet.getImageList() == null) {
                        Alert.error(this, R.string.unknown_error);
                    } else {
                        binding.imageGroup.setVisibility(View.VISIBLE);
                        binding.imageGroup.bindImageUriList(mTweet.getImageList())
                                .setEditable(false)
                                .refresh();
                    }
                } else {
                    Alert.error(this, R.string.unknown_error);
                }
                break;
            case Tweet.TYPE_AUDIO:
                if (mTweet.getAudioUrl() == null) {
                    Alert.error(this, R.string.unknown_error);
                } else {
                    binding.audioPlayButton.setVisibility(View.VISIBLE);
                    binding.audioPlayButton.setOnClickListener(view -> {
                        if (mMediaPlayer == null) {
                            initPlayer(mTweet.getAudioUrl());
                        } else {
                            resetPlayer();
                        }
                    });
                }
                break;
            case Tweet.TYPE_VIDEO:
                if (mTweet.getVideoUrl() == null) {
                    Alert.error(this, R.string.unknown_error);
                } else {
                    binding.videoView.setVisibility(View.VISIBLE);
                    binding.videoView.setOnPreparedListener(mediaPlayer -> {
                        DisplayMetrics dm = new DisplayMetrics();
                        this.getDisplay().getRealMetrics(dm);
                        int maxWidth = dm.widthPixels - 4 * this.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                        int maxHeight = this.getResources().getDimensionPixelSize(R.dimen.max_video_height);
                        int videoWith = mediaPlayer.getVideoWidth();
                        int videoHeight = mediaPlayer.getVideoHeight();
                        double ratio = videoHeight * 1.0 / videoWith; // 高宽比
                        ViewGroup.LayoutParams layoutParams = binding.videoView.getLayoutParams();
                        if (maxWidth * ratio < maxHeight) {
                            layoutParams.width = maxWidth;
                            layoutParams.height = (int) (maxWidth * ratio);
                        } else {
                            layoutParams.width = (int) (maxHeight / ratio);
                            layoutParams.height = maxHeight;
                        }
                        binding.videoView.setLayoutParams(layoutParams);
                    });
                    binding.videoView.setOnErrorListener((mediaPlayer, i, i1) -> {
                        Alert.error(this, R.string.network_error);
                        loaded = false;
                        binding.videoPlayButton.setVisibility(View.VISIBLE);
                        return true;
                    });
                    binding.videoView.setOnCompletionListener(mediaPlayer -> binding.videoPlayButton.setVisibility(View.VISIBLE));
                    binding.videoView.setOnClickListener(view -> {
                        binding.videoView.pause();
                        binding.videoPlayButton.setVisibility(View.VISIBLE);
                    });
                    binding.videoPlayButton.setVisibility(View.VISIBLE);
                    binding.videoPlayButton.setOnClickListener(view -> {
                        if (!loaded) {
                            binding.videoView.setVideoPath(mTweet.getVideoUrl());
                            loaded = true;
                        }
                        binding.videoView.start();
                        binding.videoPlayButton.setVisibility(View.GONE);
                    });
                }
        }
        binding.commentButtonText.setText(String.valueOf(mTweet.getCommentCount()));
        binding.likeButtonText.setText(String.valueOf(mTweet.getLikeCount()));
        if (mTweet.isFollow) {
            binding.followButton.setText(R.string.button_unfollow);
            binding.followButton.setBackgroundColor(this.getColor(R.color.button_disabled));
        } else {
            binding.followButton.setText(R.string.follow);
            binding.followButton.setBackgroundColor(this.getColor(R.color.pink));
        }
        if (mTweet.isLike) {
            binding.likeButtonIcon.setImageResource(R.drawable.ic_like_pink_24dp);
        } else {
            binding.likeButtonIcon.setImageResource(R.drawable.ic_like_24dp);
        }
    }

    void bindComment() {
        System.out.println(mCommentList.size());
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