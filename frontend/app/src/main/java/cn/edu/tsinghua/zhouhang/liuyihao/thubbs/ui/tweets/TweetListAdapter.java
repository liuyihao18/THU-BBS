package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.DetailActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.ImagePreviewActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.ImageGroup;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyImageView;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.lib.GlideEngine;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.MediaResource;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.TweetUtil;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.TweetViewHolder> {
    private final LinkedList<Tweet> mTweetList;
    private final Context mContext;
    private final TweetsFragment mParent;

    class TweetViewHolder extends RecyclerView.ViewHolder {
        TweetItemBinding binding;
        MediaResource mediaResource = new MediaResource();
        Tweet mTweet;

        public TweetViewHolder(@NonNull TweetItemBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setBackgroundResource(R.drawable.my_white_rounded_corner_8dp);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.getRoot().getLayoutParams();
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            binding.getRoot().setLayoutParams(layoutParams);
            this.binding = binding;
            initListener();
        }

        private void initListener() {
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

        public TweetViewHolder setTweet(Tweet tweet) {
            mTweet = tweet;
            return this;
        }

        public void refresh() {
            TweetUtil.bind(mContext, binding, mTweet, mediaResource);
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
