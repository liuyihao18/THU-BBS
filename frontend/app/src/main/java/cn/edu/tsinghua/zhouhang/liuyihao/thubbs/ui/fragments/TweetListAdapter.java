package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.lib.GlideEngine;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.TweetViewHolder> {
    private final LinkedList<Tweet> mTweetList;
    private final Context mContext;

    class TweetViewHolder extends RecyclerView.ViewHolder {
        TweetItemBinding binding;
        MediaPlayer mMediaPlayer;
        boolean loaded = false;

        public TweetViewHolder(@NonNull TweetItemBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setBackgroundResource(R.drawable.my_white_rounded_corner_8dp);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.getRoot().getLayoutParams();
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            binding.getRoot().setLayoutParams(layoutParams);
            this.binding = binding;
        }

        private void initPlayer(@NotNull String audioUri) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(mContext, Uri.parse(audioUri));
                mMediaPlayer.setOnCompletionListener(view -> {
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
                });
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_stop);
            } catch (IOException ioe) {
                Alert.error(mContext, R.string.unknown_error);
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        private void resetPlayer() {
            try {
                mMediaPlayer.stop();
            } catch (Exception e) {
                Alert.error(mContext, R.string.unknown_error);
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
        }

    }

    public TweetListAdapter(Context context, LinkedList<Tweet> tweetList) {
        mContext = context;
        mTweetList = tweetList;
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TweetItemBinding binding = TweetItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new TweetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        Tweet tweet = mTweetList.get(position);
        TweetItemBinding binding = holder.binding;
        /* 初始化 */
        holder.loaded = false;
        binding.locationLayout.setVisibility(View.GONE);
        binding.imageGroup.setVisibility(View.GONE);
        binding.imageView.setVisibility(View.GONE);
        binding.audioPlayButton.setVisibility(View.GONE);
        binding.videoView.setVisibility(View.GONE);
        binding.videoPlayButton.setVisibility(View.GONE);
        /* 加载数据 */
        binding.authorHeadshot.setImageUrl(Static.HeadShot.getHeadShotUrl("default_headshot.jpg"));
        binding.authorName.setText("かみ");
        binding.contentText.setText(tweet.getContent());
        binding.lastModified.setText(tweet.getLastModified());
        if (tweet.getLocation() != null) {
            binding.locationLayout.setVisibility(View.VISIBLE);
            binding.locationText.setText(tweet.getLocation());
        }
        switch (tweet.getType()) {
            case Tweet.TYPE_TEXT:
                break;
            case Tweet.TYPE_IMAGE:
                if (tweet.getImageCount() == 0) {
                    Alert.error(mContext, R.string.unknown_error);
                } else if (tweet.getImageCount() == 1) {
                    binding.imageView.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(tweet.getImageAt(0))
                            .placeholder(R.drawable.ic_loading_spinner_black_24dp)
                            .into(binding.imageView);
                } else if (tweet.getImageCount() <= Constant.MAX_IMAGE_COUNT) {
                    if (tweet.getImageList() == null) {
                        Alert.error(mContext, R.string.unknown_error);
                    } else {
                        binding.imageGroup.setVisibility(View.VISIBLE);
                        binding.imageGroup.bindImageUriList(tweet.getImageList())
                                .setEditable(false)
                                .refresh();
                    }
                } else {
                    Alert.error(mContext, R.string.unknown_error);
                }
                break;
            case Tweet.TYPE_AUDIO:
                if (tweet.getAudioUrl() == null) {
                    Alert.error(mContext, R.string.unknown_error);
                } else {
                    binding.audioPlayButton.setVisibility(View.VISIBLE);
                    binding.audioPlayButton.setOnClickListener(view -> {
                        if (holder.mMediaPlayer == null) {
                            holder.initPlayer(tweet.getAudioUrl());
                        } else {
                            holder.resetPlayer();
                        }
                    });
                }
                break;
            case Tweet.TYPE_VIDEO:
                if (tweet.getVideoUrl() == null) {
                    Alert.error(mContext, R.string.unknown_error);
                } else {
                    binding.videoView.setVisibility(View.VISIBLE);
                    binding.videoView.setOnPreparedListener(mediaPlayer -> {
                        DisplayMetrics dm = new DisplayMetrics();
                        mContext.getDisplay().getRealMetrics(dm);
                        int maxWidth = dm.widthPixels - 4 * mContext.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                        int maxHeight = mContext.getResources().getDimensionPixelSize(R.dimen.max_video_height);
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
                        Alert.error(mContext, R.string.network_error);
                        holder.loaded = false;
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
                        if (!holder.loaded) {
                            binding.videoView.setVideoPath(tweet.getVideoUrl());
                            holder.loaded = true;
                        }
                        binding.videoView.start();
                        binding.videoPlayButton.setVisibility(View.GONE);
                    });
                }
        }
        binding.commentButtonText.setText(String.valueOf(tweet.getCommentCount()));
        binding.likeButtonText.setText(String.valueOf(tweet.getLikeCount()));
    }

    @Override
    public int getItemCount() {
        return mTweetList.size();
    }

}
