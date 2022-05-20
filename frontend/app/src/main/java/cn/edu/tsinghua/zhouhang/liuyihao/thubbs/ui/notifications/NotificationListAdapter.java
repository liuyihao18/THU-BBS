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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.NotificationItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.mMessage;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyImageView;

public class NotificationListAdapter extends
        RecyclerView.Adapter<NotificationListAdapter.MessageViewHolder> {

    private final LinkedList<mMessage> messageList;
    private final Context mContext;
    class MessageViewHolder extends RecyclerView.ViewHolder{
        NotificationItemBinding binding;
        mMessage message;
        public MessageViewHolder(NotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public MessageViewHolder setContent(mMessage message) {
            this.message = message;
            return this;
        }

        public  void refresh() {
            binding.notificationContent.setText(message.getContent());
            binding.notificationHeadshot.setImageUrl(message.getHeadshot());
            binding.notificationTitle.setText(message.getTitle());
            binding.notificationTime.setText(message.getMessageTime());
        }
    };

    public NotificationListAdapter(Context context, LinkedList<mMessage> messageList) {
        this.messageList = messageList;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(NotificationListAdapter.MessageViewHolder holder, int position) {
        holder.setContent(messageList.get(position)).refresh();
    }

    @NonNull
    @Override
    public NotificationListAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        NotificationItemBinding binding = NotificationItemBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
        );
        return new MessageViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
