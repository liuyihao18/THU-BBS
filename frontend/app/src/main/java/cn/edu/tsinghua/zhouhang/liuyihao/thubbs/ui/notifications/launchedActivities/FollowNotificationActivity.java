package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityFollowNotificationBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityLikeBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.FollowItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.LikeItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.GoUserSpaceInterface;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FollowNotificationActivity extends AppCompatActivity implements GoUserSpaceInterface {

    private ActivityFollowNotificationBinding binding;
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;
    private OnUserSpaceReturnListener onUserSpaceReturnListener;
    private final LinkedList<FollowItemContent> followItemContents = new LinkedList<FollowItemContent>();
    private FollowListAdapter mAdapter;

    private int mBlock = 0;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what){
            case APIConstant.REQUEST_OK:
                // 加载
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, followItemContents.size() - msg.arg1);
                    if (followItemContents.size() - msg.arg1 > 0) {
                        String loadStr = getString(R.string.new_like_notification);
                        Alert.info(this, String.format(loadStr, followItemContents.size() - msg.arg1));
                    } else {
                        Alert.info(this, R.string.no_more_notification);
                    }
                }
                // 刷新
                else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, followItemContents.size());
                }
                refresh();
                break;
            case APIConstant.SERVER_ERROR:{
                Alert.error(this, R.string.server_error);
                break;
            }
            case APIConstant.NETWORK_ERROR:{
                Alert.error(this, R.string.network_error);
                break;
            }
        }
        binding.followSwipeRefreshLayout.setRefreshing(false);
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initLauncher();
        initView();
        initRecyclerView();
        getFollowNotificationList(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initLauncher() {
        mUserSpaceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            getFollowNotificationList(true);
            if(onUserSpaceReturnListener != null) {
                onUserSpaceReturnListener.onUserSpaceReturn(result);
            }
        });
    }

    public void initView() {
        if (State.getState().isLogin) {
            binding.followSwipeRefreshLayout.setVisibility(View.VISIBLE);
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        }
        else {
            binding.followSwipeRefreshLayout.setVisibility(View.GONE);
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.activityFollowList;
        mAdapter = new FollowListAdapter(this, followItemContents, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(State.getState().isLogin) {
            binding.followSwipeRefreshLayout.setOnRefreshListener(() -> getFollowNotificationList(true));
            binding.activityFollowList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if(!recyclerView.canScrollVertically(-1)) {
                            Util.doNothing();
                        }
                        else if (!recyclerView.canScrollVertically(1)) {
                            getFollowNotificationList(false);
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

    private void refresh() {
        if(followItemContents.size() > 0) {
            binding.noNotificationLayout.setVisibility(View.GONE);
        } else{
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getFollowNotificationList(boolean isRefresh) {
        if(!binding.followSwipeRefreshLayout.isRefreshing()) {
            binding.followSwipeRefreshLayout.setRefreshing(true);
        }
        try{
            JSONObject data = new JSONObject();
            Log.d("isRefresh", String.valueOf(isRefresh));
            if(isRefresh) {
                mBlock = 0;
            } else{
                mBlock++;
            }
            data.put(NotificationAPI.block, mBlock);
            NotificationAPI.get_follow_notification_list(data, new Callback() {
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
                                msg.arg2 = followItemContents.size();
                                followItemContents.clear();
                            } else {
                                msg.arg1 = followItemContents.size();
                                msg.arg2 = -1;
                            }
                            JSONArray notificationList = data.getJSONArray(NotificationAPI.notification_list);
                            for (int i = 0; i < notificationList.length(); i++) {
                                FollowItemContent followItemContent = JSONUtil.createFollowFromJSON(notificationList.getJSONObject(i));
                                if (followItemContent == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                followItemContents.add(followItemContent);
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