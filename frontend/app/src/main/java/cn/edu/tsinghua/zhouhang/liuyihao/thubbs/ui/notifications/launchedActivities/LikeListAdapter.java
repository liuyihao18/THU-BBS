package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.LikeItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyCircleImageView;

public class LikeListAdapter extends
        RecyclerView.Adapter<LikeListAdapter.LikeViewHolder> {
    private final LinkedList<LikeItemContent> mLikeItemList;
    private final Context mContext;
    static class LikeViewHolder extends RecyclerView.ViewHolder{
        MyCircleImageView like_headshot;
        TextView like_title, like_date, like_tweet_content;
        public LikeViewHolder(View view) {
            super(view);
            like_headshot = view.findViewById(R.id.like_headshot);
            like_title = view.findViewById(R.id.like_title);
            like_date = view.findViewById(R.id.like_date);
            like_tweet_content = view.findViewById(R.id.like_tweet_content);
        }
    };

    public LikeListAdapter(Context context, LinkedList<LikeItemContent> likeItemContents) {
        mLikeItemList = likeItemContents;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(LikeListAdapter.LikeViewHolder holder, int position) {
        holder.like_headshot.setImageUrl(mLikeItemList.get(position).getHeadshotURL());
        holder.like_title.setText(String.format(mContext.getString(R.string.like_your_tweet),mLikeItemList.get(position).getLikeUserName()));
        holder.like_date.setText(mLikeItemList.get(position).getLikeDate());
        holder.like_tweet_content.setText(mContext.getString(R.string.test_like_content));
    }

    @NonNull
    @Override
    public LikeListAdapter.LikeViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notification_like_item, parent, false);
        return new LikeViewHolder(mItemView);
    }

    @Override
    public int getItemCount() {
        return mLikeItemList.size();
    }
}
