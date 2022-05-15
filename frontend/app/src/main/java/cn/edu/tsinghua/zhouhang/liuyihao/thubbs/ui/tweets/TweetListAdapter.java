package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.DetailActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.TweetViewHolder> {
    private final LinkedList<Tweet> mTweetList;
    private final Context mContext;
    private final TweetsFragment mParent;

    class TweetViewHolder extends RecyclerView.ViewHolder {
        TweetItemBinding binding;
        MediaPlayer mMediaPlayer;
        boolean loaded = false;
        Tweet mTweet;

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

        public TweetViewHolder setTweet(Tweet tweet) {
            mTweet = tweet;
            return this;
        }

        public void refresh() {
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
                        Alert.error(mContext, R.string.unknown_error);
                    } else if (mTweet.getImageCount() == 1) {
                        binding.imageView.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .load(mTweet.getImageAt(0))
                                .placeholder(R.drawable.ic_loading_spinner_black_24dp)
                                .into(binding.imageView);
                    } else if (mTweet.getImageCount() <= Constant.MAX_IMAGE_COUNT) {
                        if (mTweet.getImageList() == null) {
                            Alert.error(mContext, R.string.unknown_error);
                        } else {
                            binding.imageGroup.setVisibility(View.VISIBLE);
                            binding.imageGroup.bindImageUriList(mTweet.getImageList())
                                    .setEditable(false)
                                    .refresh();
                        }
                    } else {
                        Alert.error(mContext, R.string.unknown_error);
                    }
                    break;
                case Tweet.TYPE_AUDIO:
                    if (mTweet.getAudioUrl() == null) {
                        Alert.error(mContext, R.string.unknown_error);
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
                binding.followButton.setText(R.string.unfollow);
                binding.followButton.setBackgroundColor(mContext.getColor(R.color.button_disabled));
            } else {
                binding.followButton.setText(R.string.follow);
                binding.followButton.setBackgroundColor(mContext.getColor(R.color.pink));
            }
            if (mTweet.isLike) {
                binding.likeButtonIcon.setImageResource(R.drawable.ic_like_pink_24dp);
            } else {
                binding.likeButtonIcon.setImageResource(R.drawable.ic_like_24dp);
            }
            /* 设置监听器 */
            binding.followButton.setOnClickListener(view -> {
                if (mTweet.isFollow) {
                    mTweet.isFollow = false;
                    binding.followButton.setText(R.string.follow);
                    binding.followButton.setBackgroundColor(mContext.getColor(R.color.pink));
                } else {
                    mTweet.isFollow = true;
                    binding.followButton.setText(R.string.unfollow);
                    binding.followButton.setBackgroundColor(mContext.getColor(R.color.button_disabled));
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
            binding.contentText.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.setAction(Constant.DETAIL_HAVE_DATA);
                intent.putExtra(Constant.EXTRA_TWEET, mTweet);
                mParent.setOnDetailReturnListener((result) -> {
                    Intent resultIntent = result.getData();
                    if (resultIntent != null) {
                        mTweet = (Tweet) resultIntent.getSerializableExtra(Constant.EXTRA_TWEET);
                    }
                    refresh();
                }).goDetail(intent);
            });
            binding.commentButton.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.setAction(Constant.DETAIL_HAVE_DATA);
                intent.putExtra(Constant.EXTRA_TWEET, mTweet);
                mParent.setOnDetailReturnListener((result) -> {
                    Intent resultIntent = result.getData();
                    if (resultIntent != null) {
                        mTweet = (Tweet) resultIntent.getSerializableExtra(Constant.EXTRA_TWEET);
                    }
                    refresh();
                }).goDetail(intent);
            });
        }
    }

    public TweetListAdapter(Context context, LinkedList<Tweet> tweetList, TweetsFragment parent) {
        mContext = context;
        mTweetList = tweetList;
        mParent = parent;
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
        holder.setTweet(tweet).refresh();
    }

    @Override
    public int getItemCount() {
        return mTweetList.size();
    }

}
