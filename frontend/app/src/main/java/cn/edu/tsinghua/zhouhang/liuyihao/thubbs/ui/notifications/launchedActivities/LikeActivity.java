package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityLikeBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.LikeItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities.GoUserSpaceInterface;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LikeActivity extends AppCompatActivity implements GoUserSpaceInterface {

    private ActivityLikeBinding binding;
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;
    private OnUserSpaceReturnListener onUserSpaceReturnListener;
    private final LinkedList<LikeItemContent> likeItemContents = new LinkedList<>();
    private LikeListAdapter mAdapter;

    private int mBlock = 0;

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                // 加载
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, likeItemContents.size() - msg.arg1);
                    if (likeItemContents.size() - msg.arg1 > 0) {
                        String loadStr = getString(R.string.new_like_notification);
                        Alert.info(this, String.format(loadStr, likeItemContents.size() - msg.arg1));
                    } else {
                        Alert.info(this, R.string.no_more_notification);
                    }
                }
                // 刷新
                else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, likeItemContents.size());
//                    String loadStr = getString(R.string.load_some_notification);
//                    Alert.info(this, String.format(loadStr, likeItemContents.size()));
//                    binding.activityLikeList.smoothScrollBy(0, 0);
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
        binding.likeSwipeRefreshLayout.setRefreshing(false);
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLikeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initLauncher();
        initView();
        initRecyclerView();
        getLikeNotificationList(true);
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
            getLikeNotificationList(true);
            if (onUserSpaceReturnListener != null) {
                onUserSpaceReturnListener.onUserSpaceReturn(result);
            }
        });
    }

    public void initView() {
        if (State.getState().isLogin) {
            binding.likeSwipeRefreshLayout.setVisibility(View.VISIBLE);
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        } else {
            binding.likeSwipeRefreshLayout.setVisibility(View.GONE);
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.activityLikeList;
        mAdapter = new LikeListAdapter(this, likeItemContents, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (State.getState().isLogin) {
            binding.likeSwipeRefreshLayout.setOnRefreshListener(() -> getLikeNotificationList(true));
            binding.activityLikeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            Util.doNothing();
                        } else if (!recyclerView.canScrollVertically(1)) {
                            getLikeNotificationList(false);
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
        if (likeItemContents.size() > 0) {
            binding.noNotificationLayout.setVisibility(View.GONE);
        } else {
            binding.noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getLikeNotificationList(boolean isRefresh) {
        if (!binding.likeSwipeRefreshLayout.isRefreshing()) {
            binding.likeSwipeRefreshLayout.setRefreshing(true);
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
            NotificationAPI.get_like_notification_list(data, new Callback() {
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
                                msg.arg2 = likeItemContents.size();
                                likeItemContents.clear();
                            } else {
                                msg.arg1 = likeItemContents.size();
                                msg.arg2 = -1;
                            }
                            JSONArray notificationList = data.getJSONArray(NotificationAPI.notification_list);
                            for (int i = 0; i < notificationList.length(); i++) {
                                LikeItemContent likeItemContent = JSONUtil.createLikeFromJSON(notificationList.getJSONObject(i));
                                if (likeItemContent == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                likeItemContents.add(likeItemContent);
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