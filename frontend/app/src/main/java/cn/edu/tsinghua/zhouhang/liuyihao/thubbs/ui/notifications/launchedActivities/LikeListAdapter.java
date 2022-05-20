package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.NotificationLikeItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.LikeItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.GoUserSpaceInterface;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyCircleImageView;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LikeListAdapter extends
        RecyclerView.Adapter<LikeListAdapter.LikeViewHolder> {
    private final LinkedList<LikeItemContent> mLikeItemList;
    private final Context mContext;
    private final GoUserSpaceInterface mParent;
    private final Handler handler;
    class LikeViewHolder extends RecyclerView.ViewHolder{
        NotificationLikeItemBinding binding;
        LikeItemContent likeItemContent;
        public LikeViewHolder(NotificationLikeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.likeHeadshot.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, likeItemContent.getLikeUserID());
                mParent.goUserSpace(intent);
            });
            binding.closeButton.setOnClickListener(view -> {
                close();
            });
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

        public void close() {
            JSONObject data = new JSONObject();
            try {
                data.put(NotificationAPI.notification_id, likeItemContent.getNotificationID());
                NotificationAPI.delete_like_notification(data, new Callback() {
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
                                    int index = mLikeItemList.indexOf(likeItemContent);
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
    };

    public LikeListAdapter(Context context, LinkedList<LikeItemContent> likeItemContents, GoUserSpaceInterface parent) {
        mLikeItemList = likeItemContents;
        mContext = context;
        mParent = parent;
        handler = new Handler(Looper.myLooper(), msg -> {
            switch (msg.what){
                case APIConstant.REQUEST_OK:
                    mLikeItemList.remove(msg.arg1);
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
