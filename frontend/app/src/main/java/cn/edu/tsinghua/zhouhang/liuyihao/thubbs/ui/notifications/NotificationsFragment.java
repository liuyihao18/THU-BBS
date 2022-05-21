package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.mMessage;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities.CommentNotificationActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities.FollowNotificationActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities.LikeActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentNotificationsBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private final LinkedList<mMessage> messageList = new LinkedList<>();

    private ActivityResultLauncher<Intent> mLikeLauncher;
    private ActivityResultLauncher<Intent> mFollowLauncher;
    private ActivityResultLauncher<Intent> mCommentLauncher;

    private NotificationListAdapter mAdapter;
    private int mBlock = 0;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        if (getContext() == null) {
            return true;
        }
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                // 加载
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, messageList.size() - msg.arg1);
                    if (messageList.size() - msg.arg1 > 0) {
                        String loadStr = getString(R.string.new_like_notification);
                        Alert.info(getContext(), String.format(loadStr, messageList.size() - msg.arg1));
                    } else {
                        Alert.info(getContext(), R.string.no_more_notification);
                    }
                }
                // 刷新
                else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, messageList.size());
                    // String loadStr = getString(R.string.load_some_notification);
                    // Alert.info(getContext(), String.format(loadStr, messageList.size()));
                }
                refresh();
                break;
            case APIConstant.SERVER_ERROR: {
                Alert.error(getContext(), R.string.server_error);
                break;
            }
            case APIConstant.NETWORK_ERROR: {
                Alert.error(getContext(), R.string.network_error);
                break;
            }
        }
        binding.messageSwipeRefreshLayout.setRefreshing(false);
        return true;
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initLauncher();
        initView();
        initRecycleView();
        initListener();
        getMessageList(true);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initLauncher() {
        mLikeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });

        mFollowLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });

        mCommentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });
    }

    private void initListener() {
        LinearLayout likeLayout = binding.notificationLikeLayout;
        likeLayout.setOnClickListener(view -> mLikeLauncher.launch(new Intent(getActivity(), LikeActivity.class)));

        LinearLayout followLayout = binding.notificationFollowLayout;
        followLayout.setOnClickListener(view -> mFollowLauncher.launch(new Intent(getActivity(), FollowNotificationActivity.class)));

        LinearLayout commentLayout = binding.notificationCommentLayout;
        commentLayout.setOnClickListener(view -> mCommentLauncher.launch(new Intent(getActivity(), CommentNotificationActivity.class)));
    }

    public void initView() {
        if (State.getState().isLogin) {
            binding.messageSwipeRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            binding.messageSwipeRefreshLayout.setVisibility(View.GONE);
        }
        binding.noNotificationLayout.setVisibility(View.VISIBLE);
    }

    private void initRecycleView() {
        RecyclerView recyclerView = binding.messages;
        mAdapter = new NotificationListAdapter(getActivity(), messageList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.messageSwipeRefreshLayout.setOnRefreshListener(() -> getMessageList(true));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        Util.doNothing();
                    } else if (!recyclerView.canScrollVertically(1)) {
                        getMessageList(false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void refresh() {
        if (messageList.size() > 0) {
            binding.noNotificationLayout.setVisibility(View.GONE);
        } else {
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getMessageList(boolean isRefresh) {
        if (!binding.messageSwipeRefreshLayout.isRefreshing()) {
            binding.messageSwipeRefreshLayout.setRefreshing(true);
        }
        try {
            JSONObject data = new JSONObject();
            Log.d("isRefresh", String.valueOf(isRefresh));
            if (isRefresh) {
                mBlock = 0;
            } else {
                mBlock++;
            }
            data.put(NotificationAPI.block, mBlock);
            NotificationAPI.get_message_list(data, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Message msg = new Message();
                    msg.what = APIConstant.NETWORK_ERROR;
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    ResponseBody body = response.body();
                    Message msg = new Message();
                    if (body == null) {
                        msg.what = APIConstant.SERVER_ERROR;
                        handler.sendMessage(msg);
                        return;
                    }
                    try {
                        JSONObject data = new JSONObject(body.string());
                        int errCode = data.getInt(APIConstant.ERR_CODE);
                        if (errCode == 0) {
                            msg.what = APIConstant.REQUEST_OK;
                            if (isRefresh) {
                                msg.arg1 = 0;
                                msg.arg2 = messageList.size();
                                messageList.clear();
                            } else {
                                msg.arg1 = messageList.size();
                                msg.arg2 = -1;
                            }
                            JSONArray notificationList = data.getJSONArray(NotificationAPI.notification_list);
                            for (int i = 0; i < notificationList.length(); i++) {
                                mMessage message = JSONUtil.createMessageFromJson(notificationList.getJSONObject(i));
                                if (message == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                messageList.add(message);
                            }
                        } else {
                            msg.what = APIConstant.REQUEST_ERROR;
                            msg.obj = data.getString(APIConstant.ERR_MSG);
                        }
                        handler.sendMessage(msg);
                    } catch (JSONException je) {
                        System.err.println("Bad response format.");
                    } finally {
                        body.close();
                    }
                }
            });
        } catch (JSONException jsonException) {
            System.err.println("Bad request err");
        }
    }
}