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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.NotificationLikeItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.LikeItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyCircleImageView;

public class LikeListAdapter extends
        RecyclerView.Adapter<LikeListAdapter.LikeViewHolder> {
    private final LinkedList<LikeItemContent> mLikeItemList;
    private final Context mContext;
    class LikeViewHolder extends RecyclerView.ViewHolder{
        NotificationLikeItemBinding binding;
        LikeItemContent likeItemContent;
        public LikeViewHolder(NotificationLikeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public LikeViewHolder setContent(LikeItemContent content) {
            likeItemContent = content;
            return this;
        }

        public void refresh() {
            binding.likeHeadshot.setImageUrl(likeItemContent.getHeadshotURL());
            binding.likeTitle.setText(String.format(mContext.getString(R.string.like_your_tweet),likeItemContent.getLikeUserName()));
            binding.likeDate.setText(likeItemContent.getLikeDate());
            binding.likeTweetContent.setText(likeItemContent.getTweetContent());
        }
    };

    public LikeListAdapter(Context context, LinkedList<LikeItemContent> likeItemContents) {
        mLikeItemList = likeItemContents;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(LikeListAdapter.LikeViewHolder holder, int position) {
        holder.setContent(mLikeItemList.get(position)).refresh();
    }

    @NonNull
    @Override
    public LikeListAdapter.LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationLikeItemBinding binding = NotificationLikeItemBinding.inflate(
                LayoutInflater.from(mContext),
                parent, false);
        return new LikeViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mLikeItemList.size();
    }
}
