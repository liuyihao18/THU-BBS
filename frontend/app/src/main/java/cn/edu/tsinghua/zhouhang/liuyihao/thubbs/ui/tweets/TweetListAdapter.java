package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
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
            binding.getRoot().setBackgroundResource(R.drawable.my_white_rounded_corner_8dp);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.getRoot().getLayoutParams();
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            binding.getRoot().setLayoutParams(layoutParams);
            initView();
            initListener();
        }

        private void initView() {

        }

        private void initListener() {
            View.OnClickListener onClickListener = view -> {
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
            };
            binding.contentLayout.setOnClickListener(onClickListener);
            binding.commentButton.setOnClickListener(onClickListener);
        }

        public TweetViewHolder setTweet(Tweet tweet) {
            mTweet = tweet;
            return this;
        }

        public void refresh() {
            TweetUtil.bind(mContext, binding, mTweet, mParent.getType(), mediaResource,
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
        if (holder.mediaResource.mediaPlayer != null) {
            holder.mediaResource.resetPlayer(mContext);
        }
    }
}
