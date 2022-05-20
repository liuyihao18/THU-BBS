package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityDraftBoxBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Draft;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.UserListItem;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DraftBoxActivity extends AppCompatActivity implements GoEditInterface {

    private ActivityDraftBoxBinding binding;
    private ActivityResultLauncher<Intent> mEditLauncher;
    private OnEditReturnListener onEditReturnListener;
    private DraftListAdapter mAdapter;
    private int mBlock = -1;
    private boolean isLoading = false;

    private final LinkedList<Draft> mDraftList = new LinkedList<>();

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                // 加载
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, mDraftList.size() - msg.arg1);
                }
                // 刷新
                else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, mDraftList.size());
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
        binding = ActivityDraftBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 隐藏APP顶部栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initLauncher();
        initAdapter();
        initView();
        initListener();
        getDraftList(true);
    }

    private void initLauncher() {
        mEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            getDraftList(true);
            if (onEditReturnListener != null) {
                onEditReturnListener.onEditReturn(result);
            }
        });
    }

    private void initAdapter() {
        mAdapter = new DraftListAdapter(this, mDraftList, this);
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.draft_box);
    }

    private void initListener() {
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
        // 下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener(() -> getDraftList(true));
        // 上划加载
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        getDraftList(true);
                    } else if (!recyclerView.canScrollVertically(1)) { // 已致底部
                        getDraftList(false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getDraftList(boolean isRefresh) {
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
                                msg.arg2 = mDraftList.size();
                                mDraftList.clear();
                            } else {
                                msg.arg1 = mDraftList.size();
                                msg.arg2 = -1;
                            }
                            JSONArray tweetList = data.getJSONArray(TweetAPI.tweetList);
                            for (int i = 0; i < tweetList.length(); i++) {
                                Draft draft = JSONUtil.createDraftFromJSON(tweetList.getJSONObject(i));
                                if (draft == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                mDraftList.add(draft);
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
            TweetAPI.getDraftList(data, callback);
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }

    @Override
    public void registerOnEditReturnListener(OnEditReturnListener onEditReturnListener) {
        this.onEditReturnListener = onEditReturnListener;
    }

    @Override
    public void goEdit(Intent intent) {
        mEditLauncher.launch(intent);
    }

}