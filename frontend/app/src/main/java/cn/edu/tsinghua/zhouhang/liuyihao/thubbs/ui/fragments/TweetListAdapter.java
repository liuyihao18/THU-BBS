package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.TweetViewHolder> {
    private final LinkedList<Tweet> mTweetList;
    private final Context mContext;


    class TweetViewHolder extends RecyclerView.ViewHolder {
        TweetItemBinding binding;

        public TweetViewHolder(@NonNull TweetItemBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setBackgroundResource(R.drawable.my_white_rounded_corner_8dp);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.getRoot().getLayoutParams();
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            binding.getRoot().setLayoutParams(layoutParams);
            this.binding = binding;
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
        bind(holder.binding, tweet);
    }

    @Override
    public int getItemCount() {
        return mTweetList.size();
    }

    private void bind(TweetItemBinding binding, Tweet tweet) {
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
                    binding.imageView.setImageUrl(tweet.getImageAt(0));
                } else if (tweet.getImageCount() < 9) {
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
                }
                break;

        }
        binding.commentButtonText.setText(String.valueOf(tweet.getCommentCount()));
        binding.likeButtonText.setText(String.valueOf(tweet.getLikeCount()));
    }

}
