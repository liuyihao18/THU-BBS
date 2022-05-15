package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityDetailBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    MediaPlayer mMediaPlayer;
    boolean loaded = false;
    private Tweet mTweet;

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
        if (action.equals(Constant.DETAIL_HAVE_DATA)) {
            mTweet = (Tweet) intent.getSerializableExtra(Constant.EXTRA_TWEET);
            initView();
        } else {
            // TODO: 获取推特数据
        }
        initListener();
    }

    private void initView() {
        /* 初始化 */
        loaded = false;
        binding.locationLayout.setVisibility(View.GONE);
        binding.imageGroup.setVisibility(View.GONE);
        binding.imageView.setVisibility(View.GONE);
        binding.audioPlayButton.setVisibility(View.GONE);
        binding.videoView.setVisibility(View.GONE);
        binding.videoPlayButton.setVisibility(View.GONE);
        /* 加载数据 */
        binding.authorHeadshot.setImageUrl(Static.HeadShot.getHeadShotUrl("default_headshot.jpg"));
        binding.authorName.setText("かみ");
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
                        int maxWidth = dm.widthPixels - 2 * this.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
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
    }

    private void initListener() {
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
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
}