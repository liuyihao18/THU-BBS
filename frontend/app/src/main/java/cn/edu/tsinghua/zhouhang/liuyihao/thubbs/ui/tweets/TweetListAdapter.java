package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NoErrorAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.DetailActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
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
                intent.putExtra(Constant.EXTRA_TWEET_ID, mTweet.getTweetId());
                mParent.setOnDetailReturnListener((result) -> {
                    // 屏蔽用户返回
                    if (result.getResultCode() == RESULT_OK) {
                        // 级联返回（从详情返回个人主页）
                        if (mParent.getType() == Constant.TWEETS_USER) {
                            Activity activity = mParent.getActivity();
                            if (activity != null) {
                                activity.setResult(RESULT_OK);
                                activity.finish();
                            }
                        }
                        // 级联返回（从详情返回广场）
                        else {
                            mParent.notifyRefresh();
                        }
                    }
                    // 正常返回
                    else {
                        Intent resultIntent = result.getData();
                        if (resultIntent != null) {
                            Tweet tweet = (Tweet) resultIntent.getSerializableExtra(Constant.EXTRA_TWEET);
                            if (tweet != null) {
                                mTweet = tweet;
                                refresh();
                            }
                        }
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
                    },
                    // 点击头像
                    view -> {
                        if (mParent.getType() != Constant.TWEETS_USER) {
                            Intent intent = new Intent(mContext, UserSpaceActivity.class);
                            intent.putExtra(Constant.EXTRA_USER_ID, mTweet.getUserId());
                            mParent.setOnOnUserSpaceReturnListener(result -> {
                                // 级联返回（从个人主页返回广场）
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    mParent.notifyRefresh();
                                }
                            }).goUserSpace(intent);
                        }
                    },
                    // 点击删除
                    mParent.getType() == Constant.TWEETS_USER ?
                            view -> new AlertDialog.Builder(mContext)
                                    .setTitle(R.string.question_delete_tweet)
                                    .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                                    }))
                                    .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                                        int index = mTweetList.indexOf(mTweet);
                                        if (index < 0) {
                                            Alert.error(mContext, R.string.unknown_error);
                                        } else {
                                            NoErrorAPI.deleteTweet(mContext, mTweet.getTweetId(), () -> {
                                                Alert.info(mContext, R.string.delete_tweet_success);
                                                mTweetList.remove(index);
                                                notifyItemRemoved(index);
                                            });
                                        }
                                    }).
                                    create().show() : null
            );
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
