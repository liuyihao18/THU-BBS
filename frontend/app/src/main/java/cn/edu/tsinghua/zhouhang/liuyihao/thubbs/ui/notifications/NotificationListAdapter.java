package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyImageView;

/**
 * Shows how to implement a simple Adapter for a RecyclerView.
 * Demonstrates how to add a click handler for each item in the ViewHolder.
 */
public class NotificationListAdapter extends
        RecyclerView.Adapter<NotificationListAdapter.MomentsViewHolder> {

    private final LinkedList<String> mTitleList, mContentList;
    private final LinkedList<String> mImageList;
    private Context mContext;
    static class MomentsViewHolder extends RecyclerView.ViewHolder{
        TextView notification_title, notification_content;
        MyImageView notification_headshot;
        public MomentsViewHolder(View view) {
            super(view);
            notification_title = view.findViewById(R.id.notification_title);
            notification_content = view.findViewById(R.id.notification_content);
//            notification_content.setMovementMethod(ScrollingMovementMethod.getInstance());
            notification_headshot = view.findViewById(R.id.notification_headshot);
        }
    };

    public NotificationListAdapter(Context context, LinkedList<String> titleList,LinkedList<String> contentList, LinkedList<String> imageList) {
        mTitleList = titleList;
        mContentList = contentList;
        mImageList = imageList;
    }

    @Override
    public void onBindViewHolder(NotificationListAdapter.MomentsViewHolder holder, int position) {
        holder.notification_title.setText(mTitleList.get(position));
        holder.notification_content.setText(mContentList.get(position));
        holder.notification_headshot.setImageUrl(mImageList.get(position));
    }

    @NonNull
    @Override
    public NotificationListAdapter.MomentsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notification_item, parent, false);
        return new MomentsViewHolder(mItemView);
    }

    @Override
    public int getItemCount() {
        return mTitleList.size();
    }
}
