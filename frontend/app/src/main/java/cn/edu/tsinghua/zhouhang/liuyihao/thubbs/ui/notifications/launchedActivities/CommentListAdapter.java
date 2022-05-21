package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.NotificationCommentItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.GoUserSpaceInterface;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.DetailActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CommentListAdapter extends
        RecyclerView.Adapter<CommentListAdapter.CommentViewHolder> {
    private final LinkedList<CommentItemContent> mCommentItemList;
    private final Context mContext;
    private final GoUserSpaceInterface mParent;
    private final Handler handler;
    class CommentViewHolder extends RecyclerView.ViewHolder{
        NotificationCommentItemBinding binding;
        CommentItemContent commentItemContent;
        public CommentViewHolder(NotificationCommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.commentHeadshot.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, commentItemContent.getCommentUserID());
                mParent.goUserSpace(intent);
            });
            binding.closeButton.setOnClickListener(view -> close());
            binding.getRoot().setOnClickListener(view -> {
                if(commentItemContent.getTweetID() > 0) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constant.EXTRA_TWEET_ID, commentItemContent.getTweetID());
                    mContext.startActivity(intent);
                }
            });
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

        public void close() {
            JSONObject data = new JSONObject();
            try {
                data.put(NotificationAPI.notification_id, commentItemContent.getNotificationID());
                NotificationAPI.delete_comment_notification(data, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Message message = new Message();
                        message.what = APIConstant.NETWORK_ERROR;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if(body == null) {
                            Message message = new Message();
                            message.what = APIConstant.SERVER_ERROR;
                            handler.sendMessage(message);
                        } else {
                            try {
                                JSONObject data = new JSONObject(body.string());
                                int errCode = data.getInt(APIConstant.ERR_CODE);
                                if (errCode == 0) {
                                    int index = mCommentItemList.indexOf(commentItemContent);
                                    if(index >= 0) {
                                        Message message = new Message();
                                        message.what = APIConstant.REQUEST_OK;
                                        message.arg1 = index;
                                        handler.sendMessage(message);
                                    }
                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }
                    }
                });
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }

    public CommentListAdapter(Context context,
                              LinkedList<CommentItemContent> commentItemContents,
                              GoUserSpaceInterface parent) {
        mCommentItemList = commentItemContents;
        mContext = context;
        mParent = parent;
        handler = new android.os.Handler(Looper.myLooper(), msg -> {
            switch (msg.what){
                case APIConstant.REQUEST_OK:
                    mCommentItemList.remove(msg.arg1);
                    notifyItemRemoved(msg.arg1);
                    break;
                case APIConstant.SERVER_ERROR:{
                    Alert.error(mContext, R.string.server_error);
                    break;
                }
                case APIConstant.NETWORK_ERROR:{
                    Alert.error(mContext, R.string.network_error);
                    break;
                }
            }
            return true;
        });
    }


    @Override
    public void onBindViewHolder(CommentListAdapter.CommentViewHolder holder, int position) {
        holder.setContent(mCommentItemList.get(position)).refresh();
    }

    @NonNull
    @Override
    public CommentListAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
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
