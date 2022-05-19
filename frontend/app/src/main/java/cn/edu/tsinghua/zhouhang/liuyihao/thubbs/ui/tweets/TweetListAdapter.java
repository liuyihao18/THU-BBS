package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.DetailActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.MediaResource;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.TweetUtil;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.TweetViewHolder> {
    private final LinkedList<Tweet> mTweetList;
    private final Context mContext;
    private final TweetsFragment mParent;

    class TweetViewHolder extends RecyclerView.ViewHolder {
        TweetItemBinding binding;
        Tweet mTweet;
        private final MediaResource mediaResource = new MediaResource();

        public TweetViewHolder(@NonNull TweetItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // 每个动态的背景和位置参数
            binding.getRoot().setBackgroundResource(R.drawable.my_white_rounded_corner_8dp);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.getRoot().getLayoutParams();
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            binding.getRoot().setLayoutParams(layoutParams);
            // 初始化
            initListener();
        }

        private void initListener() {
            View.OnClickListener onClickListener = view -> {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.setAction(Constant.DETAIL_HAVE_DATA);
                intent.putExtra(Constant.EXTRA_TWEET, mTweet);
                mParent.setOnDetailReturnListener((result) -> {
                    // 屏蔽用户返回
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mParent.notifyRefresh();
                    }
                    // 正常返回
                    else {
                        Intent resultIntent = result.getData();
                        if (resultIntent != null) {
                            mTweet = (Tweet) resultIntent.getSerializableExtra(Constant.EXTRA_TWEET);
                        }
                        refresh();
                    }
                }).goDetail(intent);
            };
            // 点击内容或评论都打开详情界面
            binding.contentLayout.setOnClickListener(onClickListener);
            binding.commentButton.setOnClickListener(onClickListener);
        }

        public TweetViewHolder setTweet(Tweet tweet) {
            mTweet = tweet;
            return this;
        }

        public void refresh() {
            // 绑定动态
            TweetUtil.bind(mContext, binding, mTweet, mParent.getType(), mediaResource,
                    // 这里的屏蔽按钮实际上是不看该条动态
                    view -> {
                        int index = mTweetList.indexOf(mTweet);
                        if (index < 0) {
                            Alert.error(mContext, R.string.unknown_error);
                        } else {
                            mTweetList.remove(index);
                            notifyItemRemoved(index);
                        }
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

    @Override
    public void onViewDetachedFromWindow(@NonNull TweetViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // 终止不在屏幕内的播放
        if (holder.mediaResource.mediaPlayer != null) {
            holder.mediaResource.resetPlayer(mContext);
        }
    }
}
