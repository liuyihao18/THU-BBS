package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.RelationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.TweetAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityUserListBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.UserListItem;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets.TweetListAdapter;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UserListActivity extends AppCompatActivity implements GoUserSpaceInterface {

    private ActivityUserListBinding binding;
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;
    private UserListAdapter mAdapter;
    private OnUserSpaceReturnListener onUserSpaceReturnListener;
    private int mListType;
    private int mBlock = -1;
    private boolean isLoading = false;

    private final LinkedList<UserListItem> mUserList = new LinkedList<>();

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                // 加载
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, mUserList.size() - msg.arg1);
                }
                // 刷新
                else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, mUserList.size());
                }
                break;
            case APIConstant.REQUEST_ERROR:
                Alert.error(this, (String) msg.obj);
                break;
            case APIConstant.NETWORK_ERROR:
                Alert.error(this, R.string.network_error);
                break;
            case APIConstant.SERVER_ERROR:
                Alert.error(this, R.string.server_error);
                break;
        }
        isLoading = false;
        binding.swipeRefreshLayout.setRefreshing(false);
        return true;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 隐藏APP顶部栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 获取数据（列表类别）
        Intent intent = getIntent();
        mListType = intent.getIntExtra(Constant.USER_LIST_TYPE, Constant.EMPTY_LIST);
        initLauncher();
        initAdapter();
        initView();
        getUserList(true);
    }

    private void initLauncher() {
        mUserSpaceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            getUserList(true);
            if (onUserSpaceReturnListener != null) {
                onUserSpaceReturnListener.onUserSpaceReturn(result);
            }
        });
    }

    private void initView() {
        switch (mListType) {
            case Constant.FOLLOW_LIST:
                ((TextView) findViewById(R.id.header_title)).setText(R.string.follow_list);
                break;
            case Constant.FAN_LIST:
                ((TextView) findViewById(R.id.header_title)).setText(R.string.follower_list);
                break;
            case Constant.BLACK_LIST:
                ((TextView) findViewById(R.id.header_title)).setText(R.string.blacklist);
                break;
        }
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initAdapter() {
        mAdapter = new UserListAdapter(this, mUserList, mListType, this);
    }

    private void getUserList(boolean isRefresh) {
        if (isLoading) {
            return;
        } else {
            isLoading = true;
        }
        if (!binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        try {
            JSONObject data = new JSONObject();
            if (isRefresh) {
                mBlock = 0;
            } else {
                mBlock++;
            }
            data.put(TweetAPI.block, mBlock);
            Callback callback = new Callback() {
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
                                msg.arg2 = mUserList.size();
                                mUserList.clear();
                            } else {
                                msg.arg1 = mUserList.size();
                                msg.arg2 = -1;
                            }
                            JSONArray userList;
                            switch (mListType) {
                                case Constant.FOLLOW_LIST:
                                    userList = data.getJSONArray(RelationAPI.followList);
                                    break;
                                case Constant.FAN_LIST:
                                    userList = data.getJSONArray(RelationAPI.fanList);
                                    break;
                                case Constant.BLACK_LIST:
                                    userList = data.getJSONArray(RelationAPI.blackList);
                                    break;
                                default:
                                    Alert.info(UserListActivity.this, R.string.unknown_error);
                                    return;
                            }
                            for (int i = 0; i < userList.length(); i++) {
                                UserListItem user = JSONUtil.createUserListItemFromJSON(userList.getJSONObject(i));
                                if (user == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                mUserList.add(user);
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
            };
            switch (mListType) {
                case Constant.FOLLOW_LIST:
                    RelationAPI.getFollowList(data, callback);
                    break;
                case Constant.FAN_LIST:
                    RelationAPI.getFanList(data, callback);
                    break;
                case Constant.BLACK_LIST:
                    RelationAPI.getBlackList(data, callback);
                    break;
            }
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }

    @Override
    public void registerGoUserSpaceListener(OnUserSpaceReturnListener onUserSpaceReturnListener) {
        this.onUserSpaceReturnListener = onUserSpaceReturnListener;
    }

    @Override
    public void goUserSpace(Intent intent) {
        mUserSpaceLauncher.launch(intent);
    }
}