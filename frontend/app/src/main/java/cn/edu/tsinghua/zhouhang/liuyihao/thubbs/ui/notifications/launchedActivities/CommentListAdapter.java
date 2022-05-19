package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.CommentItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.NotificationCommentItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.NotificationLikeItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyCircleImageView;

public class CommentListAdapter extends
        RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {
    private final LinkedList<CommentItemContent> mCommentItemList;
    private final Context mContext;
    class CommentViewHolder extends RecyclerView.ViewHolder{
        NotificationCommentItemBinding binding;
        CommentItemContent commentItemContent;
        public CommentViewHolder(NotificationCommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public CommentViewHolder setContent(CommentItemContent content) {
            commentItemContent = content;
            return this;
        }

        public void refresh() {
            binding.commentHeadshot.setImageUrl(commentItemContent.getHeadshotURL());
            binding.commentContent.setText(commentItemContent.getCommentContent());
            binding.commentDate.setText(commentItemContent.getCommentDate());
            binding.commentTitle.setText(String.format(
                    mContext.getString(R.string.comment_your_tweet),
                    commentItemContent.getCommentUserName()
            ));
            binding.commentTweetContent.setText(commentItemContent.getTweetContent());
        }
    };

    public CommentListAdapter(Context context, LinkedList<CommentItemContent> commentItemContents) {
        mCommentItemList = commentItemContents;
        mContext = context;
    }


    @Override
    public void onBindViewHolder(CommentListAdapter.CommentViewHolder holder, int position) {
        holder.setContent(mCommentItemList.get(position)).refresh();
    }

    @NonNull
    @Override
    public CommentListAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        NotificationCommentItemBinding binding = NotificationCommentItemBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
        );
        return new CommentViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mCommentItemList.size();
    }
}
