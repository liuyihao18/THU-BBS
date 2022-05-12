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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyCircleImageView;

public class CommentListAdapter extends
        RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {
    private final LinkedList<CommentItemContent> mCommentItemList;
    private final Context mContext;
    static class CommentViewHolder extends RecyclerView.ViewHolder{
        MyCircleImageView comment_headshot;
        TextView comment_title, comment_date, comment_content, comment_tweet_content;
        public CommentViewHolder(View view) {
            super(view);
            comment_headshot = view.findViewById(R.id.comment_headshot);
            comment_content = view.findViewById(R.id.comment_content);
            comment_title = view.findViewById(R.id.comment_title);
            comment_date = view.findViewById(R.id.comment_date);
            comment_tweet_content = view.findViewById(R.id.comment_tweet_content);
        }
    };

    public CommentListAdapter(Context context, LinkedList<CommentItemContent> commentItemContents) {
        mCommentItemList= commentItemContents;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(CommentListAdapter.CommentViewHolder holder, int position) {
        holder.comment_headshot.setImageUrl(mCommentItemList.get(position).getHeadshotURL());
        holder.comment_title.setText(String.format(mContext.getString(R.string.like_your_tweet),mCommentItemList.get(position).getCommentUserName()));
        holder.comment_date.setText(mCommentItemList.get(position).getCommentDate());
        holder.comment_tweet_content.setText(mContext.getString(R.string.test_like_content));
        holder.comment_content.setText(mCommentItemList.get(position).getCommentContent());
    }

    @NonNull
    @Override
    public CommentListAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notification_comment_item, parent, false);
        return new CommentViewHolder(mItemView);
    }

    @Override
    public int getItemCount() {
        return mCommentItemList.size();
    }
}
