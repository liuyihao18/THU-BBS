package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.LinkedList;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.NotificationFollowItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.FollowItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.GoUserSpaceInterface;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.DetailActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FollowListAdapter extends
        RecyclerView.Adapter<FollowListAdapter.FollowViewHolder> {
    private final LinkedList<FollowItemContent> mFollowItemList;
    private final Context mContext;
    private final GoUserSpaceInterface mParent;
    private final Handler handler;
    class FollowViewHolder extends RecyclerView.ViewHolder{
        NotificationFollowItemBinding binding;
        FollowItemContent followItemContent;
        public FollowViewHolder(NotificationFollowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.followHeadshot.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, followItemContent.getFolloweeUserID());
                mParent.goUserSpace(intent);
            });
            binding.closeButton.setOnClickListener(view -> {
                close();
            });
            binding.getRoot().setOnClickListener(view -> {
                if(followItemContent.getTweetID() > 0) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constant.EXTRA_TWEET_ID, followItemContent.getTweetID());
                    mContext.startActivity(intent);
                }
            });
        }

        public FollowViewHolder setContent(FollowItemContent content) {
            followItemContent = content;
            return this;
        }

        public void refresh() {
            binding.followHeadshot.setImageUrl(followItemContent.getHeadshotURL());
            binding.followTweetContent.setText(followItemContent.getTweetContent());
            binding.followDate.setText(followItemContent.getNotificationDate());
            binding.followTitle.setText(String.format(
                    mContext.getString(R.string.follow_new_tweet),
                    followItemContent.getFolloweeUserName()
            ));
        }

        public void close() {
            JSONObject data = new JSONObject();
            try {
                data.put(NotificationAPI.notification_id, followItemContent.getNotificationID());
                NotificationAPI.delete_follow_notification(data, new Callback() {
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
                                    int index = mFollowItemList.indexOf(followItemContent);
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

    public FollowListAdapter(Context context,
                              LinkedList<FollowItemContent> followItemContents,
                              GoUserSpaceInterface parent) {
        mFollowItemList = followItemContents;
        mContext = context;
        mParent = parent;
        handler = new android.os.Handler(Looper.myLooper(), msg -> {
            switch (msg.what){
                case APIConstant.REQUEST_OK:
                    mFollowItemList.remove(msg.arg1);
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
    public void onBindViewHolder(FollowViewHolder holder, int position) {
        holder.setContent(mFollowItemList.get(position)).refresh();
    }

    @NonNull
    @Override
    public FollowListAdapter.FollowViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        NotificationFollowItemBinding binding = NotificationFollowItemBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
        );
        return new FollowViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mFollowItemList.size();
    }
}
