package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityCommentNotificationBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.GoUserSpaceInterface;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CommentNotificationActivity extends AppCompatActivity implements GoUserSpaceInterface {

    private ActivityCommentNotificationBinding binding;

    private final LinkedList<CommentItemContent> commentItemContents = new LinkedList<>();

    private CommentListAdapter mAdapter;

    private int mBlock = 0;
    private OnUserSpaceReturnListener onUserSpaceReturnListener;
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                // 加载
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, commentItemContents.size() - msg.arg1);
                    if (commentItemContents.size() - msg.arg1 > 0) {
                        String loadStr = getString(R.string.new_like_notification);
                        Alert.info(this, String.format(loadStr, commentItemContents.size() - msg.arg1));
                    } else {
                        Alert.info(this, R.string.no_more_notification);
                    }
                }
                // 刷新
                else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, commentItemContents.size());
                    // String loadStr = getString(R.string.load_some_notification);
                    // Alert.info(this, String.format(loadStr, commentItemContents.size()));
                }
                refresh();
                break;
            case APIConstant.SERVER_ERROR: {
                Alert.error(this, R.string.server_error);
                break;
            }
            case APIConstant.NETWORK_ERROR: {
                Alert.error(this, R.string.network_error);
                break;
            }
        }
        binding.commentSwipeRefreshLayout.setRefreshing(false);
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initLauncher();
        initView();
        initRecyclerView();
        getCommentNotificationList(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        if (commentItemContents.size() > 0) {
            binding.noNotificationLayout.setVisibility(View.GONE);
        } else {
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    public void initView() {
        if (State.getState().isLogin) {
            binding.commentSwipeRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            binding.commentSwipeRefreshLayout.setVisibility(View.GONE);
        }
        binding.noNotificationLayout.setVisibility(View.VISIBLE);
    }

    public void initLauncher() {
        mUserSpaceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            getCommentNotificationList(true);
            if (onUserSpaceReturnListener != null) {
                onUserSpaceReturnListener.onUserSpaceReturn(result);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.commentNotificationList;
        mAdapter = new CommentListAdapter(this, commentItemContents, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (State.getState().isLogin) {
            binding.commentSwipeRefreshLayout.setOnRefreshListener(() -> getCommentNotificationList(true));
            binding.commentNotificationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            Util.doNothing();
                        } else if (!recyclerView.canScrollVertically(1)) {
                            getCommentNotificationList(false);
                        }
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

        }
    }

    private void getCommentNotificationList(boolean isRefresh) {
        if (!State.getState().isLogin) {
            return;
        }
        if (!binding.commentSwipeRefreshLayout.isRefreshing()) {
            binding.commentSwipeRefreshLayout.setRefreshing(true);
        }
        try {
            JSONObject data = new JSONObject();
            if (isRefresh) {
                mBlock = 0;
            } else {
                mBlock++;
            }
            data.put(NotificationAPI.block, mBlock);
            NotificationAPI.get_comment_notification_list(data, new Callback() {
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
                                msg.arg2 = commentItemContents.size();
                                commentItemContents.clear();
                            } else {
                                msg.arg1 = commentItemContents.size();
                                msg.arg2 = -1;
                            }
                            JSONArray notificationList = data.getJSONArray(NotificationAPI.notification_list);
                            for (int i = 0; i < notificationList.length(); i++) {
                                CommentItemContent commentItemContent = JSONUtil.createCommentNotificationFromJson(
                                        notificationList.getJSONObject(i)
                                );
                                if (commentItemContent == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                commentItemContents.add(commentItemContent);
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

    @Override
    public void registerOnUserSpaceReturnListener(OnUserSpaceReturnListener onUserSpaceReturnListener) {
        this.onUserSpaceReturnListener = onUserSpaceReturnListener;
    }

    @Override
    public void goUserSpace(Intent intent) {
        mUserSpaceLauncher.launch(intent);
    }
}