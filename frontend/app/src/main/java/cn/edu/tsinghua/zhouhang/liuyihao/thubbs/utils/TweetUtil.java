package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.Objects;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.ImagePreviewActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.ImageGroup;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyImageView;

public class TweetUtil {

    public static void bind(Context context, TweetItemBinding binding, Tweet tweet, int tweetsType,
                            MediaResource mediaResource, View.OnClickListener onClickBlackButtonListener) {
        /* 初始化 */
        mediaResource.loaded = false;
        binding.locationLayout.setVisibility(View.GONE);
        binding.imageGroup.setVisibility(View.GONE);
        binding.imageView.setVisibility(View.GONE);
        binding.audioPlayButton.setVisibility(View.GONE);
        binding.videoView.setVisibility(View.GONE);
        binding.videoPlayButton.setVisibility(View.GONE);
        /* 加载数据 */
        binding.authorHeadshot.setImageUrl(tweet.getHeadshot());
        binding.authorName.setText(tweet.getNickname());
        binding.title.setText(tweet.getTitle());
        binding.contentText.setText(tweet.getContent());
        binding.lastModified.setText(tweet.getLastModified());
        if (tweet.getLocation() != null && !Objects.equals(tweet.getLocation(), "null")) {
            binding.locationLayout.setVisibility(View.VISIBLE);
            binding.locationText.setText(tweet.getLocation());
        }
        switch (tweet.getType()) {
            case Tweet.TYPE_TEXT:
                break;
            case Tweet.TYPE_IMAGE:
                if (tweet.getImageCount() == 0) {
                    Alert.error(context, R.string.unknown_error);
                } else if (tweet.getImageCount() == 1) {
                    binding.imageView.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(tweet.getImageAt(0))
                            .placeholder(R.drawable.ic_loading_spinner_black_24dp)
                            .into(binding.imageView);
                } else if (tweet.getImageCount() <= Constant.MAX_IMAGE_COUNT) {
                    if (tweet.getImageList() == null) {
                        Alert.error(context, R.string.unknown_error);
                    } else {
                        binding.imageGroup.setVisibility(View.VISIBLE);
                        binding.imageGroup.bindImageUriList(tweet.getImageList())
                                .setEditable(false)
                                .refresh();
                    }
                } else {
                    Alert.error(context, R.string.unknown_error);
                }
                break;
            case Tweet.TYPE_AUDIO:
                if (tweet.getAudioUrl() == null) {
                    Alert.error(context, R.string.unknown_error);
                } else {
                    binding.audioPlayButton.setVisibility(View.VISIBLE);
                    binding.audioPlayButton.setOnClickListener(view -> {
                        if (mediaResource.mediaPlayer == null) {
                            mediaResource.initPlayer(context, tweet.getAudioUrl());
                        } else {
                            mediaResource.resetPlayer(context);
                        }
                    });
                }
                break;
            case Tweet.TYPE_VIDEO:
                if (tweet.getVideoUrl() == null) {
                    Alert.error(context, R.string.unknown_error);
                } else {
                    binding.videoView.setVisibility(View.VISIBLE);
                    binding.videoView.setOnPreparedListener(mediaPlayer -> {
                        DisplayMetrics dm = new DisplayMetrics();
                        DisplayManager displayManager = (DisplayManager) context.getSystemService(
                                Context.DISPLAY_SERVICE);
                        displayManager.getDisplay(Display.DEFAULT_DISPLAY).getRealMetrics(dm);
                        int maxWidth = dm.widthPixels - 4 * context.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                        int maxHeight = context.getResources().getDimensionPixelSize(R.dimen.max_video_height);
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
                        Alert.error(context, R.string.network_error);
                        mediaResource.loaded = false;
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
                        if (!mediaResource.loaded) {
                            binding.videoView.setVideoPath(tweet.getVideoUrl());
                            mediaResource.loaded = true;
                        }
                        binding.videoView.start();
                        binding.videoPlayButton.setVisibility(View.GONE);
                    });
                }
        }
        binding.commentButtonText.setText(String.valueOf(tweet.getCommentCount()));
        binding.likeButtonText.setText(String.valueOf(tweet.getLikeCount()));
        if (tweet.isFollow) {
            binding.followButton.setText(R.string.button_unfollow);
            binding.followButton.setBackgroundColor(context.getColor(R.color.button_disabled));
        } else {
            binding.followButton.setText(R.string.follow);
            binding.followButton.setBackgroundColor(context.getColor(R.color.pink));
        }
        if (tweet.isLike) {
            binding.likeButtonIcon.setImageResource(R.drawable.ic_like_pink_24dp);
        } else {
            binding.likeButtonIcon.setImageResource(R.drawable.ic_like_24dp);
        }
        if (tweetsType == Constant.TWEETS_USER) {
            binding.followButton.setVisibility(View.GONE);
            binding.blackButton.setVisibility(View.GONE);
        } else {
            binding.followButton.setVisibility(View.VISIBLE);
            if (State.getState().userId == tweet.getUserID()) {
                binding.followButton.setText("我自己");
                binding.blackButton.setVisibility(View.GONE);
            } else {
                binding.blackButton.setVisibility(View.VISIBLE);
            }
        }
        /* 监听器 */
        mediaResource.registerMediaResourceListener(new MediaResource.MediaResourceListener() {
            @Override
            public void onInit() {
                binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_stop);
            }

            @Override
            public void onReset() {
                binding.audioPlayButton.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_audio_play);
            }
        });
        if (tweetsType != Constant.TWEETS_USER && State.getState().userId != tweet.getUserID()) {
            binding.followButton.setOnClickListener(view -> {
                if (tweet.isFollow) {
                    tweet.isFollow = false;
                    binding.followButton.setText(R.string.follow);
                    binding.followButton.setBackgroundColor(context.getColor(R.color.pink));
                } else {
                    tweet.isFollow = true;
                    binding.followButton.setText(R.string.button_unfollow);
                    binding.followButton.setBackgroundColor(context.getColor(R.color.button_disabled));
                }
            });
            binding.blackButton.setOnClickListener(view -> {
                if (onClickBlackButtonListener != null) {
                    onClickBlackButtonListener.onClick(view);
                }
            });
        } else {
            binding.followButton.setOnClickListener(null);
            binding.blackButton.setOnClickListener(null);
        }
        binding.imageGroup.registerImageGroupListener(new ImageGroup.ImageGroupListener() {
            @Override
            public void onClickImage(MyImageView myImageView, int index) {
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putExtra(Constant.EXTRA_IMAGE_URL, tweet.getImageAt(index));
                context.startActivity(intent);
            }

            @Override
            public void onClickAddImage(MyImageView myImageView, int index) {

            }

            @Override
            public void onClickCloseButton(View view, int index) {

            }
        });
        binding.imageView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImagePreviewActivity.class);
            intent.putExtra(Constant.EXTRA_IMAGE_URL, tweet.getImageAt(0));
            context.startActivity(intent);
        });
        binding.likeButton.setOnClickListener(view -> {
            if (tweet.isLike) {
                tweet.isLike = false;
                binding.likeButtonIcon.setImageResource(R.drawable.ic_like_24dp);
                tweet.likeCount--;
            } else {
                tweet.isLike = true;
                binding.likeButtonIcon.setImageResource(R.drawable.ic_like_pink_24dp);
                tweet.likeCount++;
            }
            binding.likeButtonText.setText(String.valueOf(tweet.getLikeCount()));
        });
        binding.authorHeadshot.setOnClickListener(view -> {
            if (tweetsType != Constant.TWEETS_USER) {
                Intent intent = new Intent(context, UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, tweet.getUserID());
                context.startActivity(intent);
            }
        });
    }

}
