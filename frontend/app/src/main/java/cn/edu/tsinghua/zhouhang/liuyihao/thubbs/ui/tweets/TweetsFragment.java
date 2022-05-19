package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResult;
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

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.TweetAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentTweetsBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.EditActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TweetsFragment extends Fragment {
    private FragmentTweetsBinding binding;
    private int mType;
    private int mUserId = 0;
    private ActivityResultLauncher<Intent> mEditLauncher;
    private ActivityResultLauncher<Intent> mDetailLauncher;
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;
    private TweetListAdapter mAdapter;
    private OnDetailReturnListener onDetailReturnListener;
    private OnUserSpaceReturnListener onUserSpaceReturnListener;
    private int mBlock = -1;
    private boolean firstTypeSelect = true;
    private boolean firstOrderSelect = true;
    private boolean isLoading = false;

    private final LinkedList<Tweet> mTweetList = new LinkedList<>();

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                // 加载
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, mTweetList.size() - msg.arg1);
                    if (mTweetList.size() - msg.arg1 > 0) {
                        String loadStr = getString(R.string.continue_load_tweet);
                        Alert.info(getContext(), String.format(loadStr, mTweetList.size() - msg.arg1));
                    } else {
                        Alert.info(getContext(), R.string.no_new_load_tweet);
                    }
                }
                // 刷新
                else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, mTweetList.size());
                    String loadStr = getString(R.string.initial_load_tweet);
                    Alert.info(getContext(), String.format(loadStr, mTweetList.size()));
                    binding.recyclerView.smoothScrollBy(0, 0);
                }
                refresh();
                break;
            case APIConstant.REQUEST_ERROR:
                Alert.error(getContext(), (String) msg.obj);
                break;
            case APIConstant.NETWORK_ERROR:
                Alert.error(getContext(), R.string.network_error);
                break;
            case APIConstant.SERVER_ERROR:
                Alert.error(getContext(), R.string.server_error);
                break;
        }
        isLoading = false;
        binding.swipeRefreshLayout.setRefreshing(false);
        return true;
    });

    interface OnDetailReturnListener {
        void onDetailReturn(ActivityResult result);
    }

    interface OnUserSpaceReturnListener {
        void onUserSpaceReturn(ActivityResult result);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTweetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initLauncher();
        initAdapter();
        initType();
        init();
        getTweetList(true);
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void init() {
        initView();
        initListener();
    }

    private void initLauncher() {
        mEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getTweetList(true);
            }
        });
        mDetailLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (onDetailReturnListener != null) {
                onDetailReturnListener.onDetailReturn(result);
            }
        });
        mUserSpaceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (onUserSpaceReturnListener != null) {
                onUserSpaceReturnListener.onUserSpaceReturn(result);
            }
        });
    }

    private void initType() {
        // 首先确认类型
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(Constant.TWEETS_TYPE, Constant.TWEETS_EMPTY);
        } else {
            mType = Constant.TWEETS_EMPTY;
        }
        // 如果是个人主页，则获取用户ID
        if (mType == Constant.TWEETS_USER) {
            mUserId = bundle.getInt(Constant.EXTRA_USER_ID, 0);
        }
    }

    private void initView() {
        // 登录
        if (State.getState().isLogin) {
            binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
            binding.loginRequiredLayout.setVisibility(View.GONE);
            // 暂时显示无更多动态
            binding.noTweetLayout.setVisibility(View.VISIBLE);
            // 用户空间不显示顶部工具栏和浮动按钮
            if (mType == Constant.TWEETS_USER) {
                binding.menu.setVisibility(View.GONE);
                binding.fab.setVisibility(View.GONE);
            }
            // 初始获取数据
            binding.recyclerView.setAdapter(mAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        // 未登录
        else {
            binding.swipeRefreshLayout.setVisibility(View.GONE);
            binding.loginRequiredLayout.setVisibility(View.VISIBLE);
            binding.noTweetLayout.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        // 登录
        if (State.getState().isLogin) {
            // 添加按钮
            binding.fab.setOnClickListener(view -> mEditLauncher.launch(new Intent(getActivity(), EditActivity.class).setAction(Constant.EDIT_FROM_BLANK)));
            // 搜索按钮
            binding.search.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (!binding.search.getText().toString().isEmpty()) {
                        Alert.info(getContext(), R.string.is_searching);
                        getTweetList(true);
                    }
                }
                Util.HideKeyBoard(getActivity(), textView);
                return true;
            });
            // 切换筛选类型
            binding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (firstTypeSelect) {
                        firstTypeSelect = false;
                    } else {
                        getTweetList(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            // 切换排序类型
            binding.orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (firstOrderSelect) {
                        firstOrderSelect = false;
                    } else {
                        getTweetList(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            // 下拉刷新
            binding.swipeRefreshLayout.setOnRefreshListener(() -> getTweetList(true));
            // 上划加载
            binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!recyclerView.canScrollVertically(1)) { // 已致底部
                            getTweetList(false);
                        }
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
        // 未登录
        else {
            // 登录按钮
            binding.loginButton.setOnClickListener(view ->
                    State.getState()
                            .setOnLoginListener(this::init)
                            .login(getContext()));
        }
    }

    private void initAdapter() {
        mAdapter = new TweetListAdapter(getContext(), mTweetList, this);
    }

    public int getType() {
        return mType;
    }

    public TweetsFragment setOnDetailReturnListener(OnDetailReturnListener onDetailReturnListener) {
        this.onDetailReturnListener = onDetailReturnListener;
        return this;
    }

    /**
     * 前往Detail页面
     *
     * @param intent 前往Detail页面的intent
     */
    public void goDetail(Intent intent) {
        mDetailLauncher.launch(intent);
    }

    public TweetsFragment setOnOnUserSpaceReturnListener(OnUserSpaceReturnListener onUserSpaceReturnListener) {
        this.onUserSpaceReturnListener = onUserSpaceReturnListener;
        return this;
    }

    /**
     * 前往UserSpace页面
     *
     * @param intent 前往Detail页面的intent
     */
    public void goUserSpace(Intent intent) {
        mUserSpaceLauncher.launch(intent);
    }

    /**
     * 动态刷新“动态”或“暂时没有更多动态”
     */
    public void refresh() {
        if (mTweetList.size() > 0) {
            binding.noTweetLayout.setVisibility(View.GONE);
        } else {
            binding.noTweetLayout.setVisibility(View.VISIBLE);
        }
    }

    public void notifyRefresh() {
        getTweetList(true);
    }

    private void getTweetList(boolean isRefresh) {
        if (mType == Constant.TWEETS_EMPTY) {
            return;
        }
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
            data.put(TweetAPI.of, mType);
            if (mType == Constant.TWEETS_USER) {
                data.put(TweetAPI.userId, mUserId);
            }
            int orderByIndex = binding.orderSpinner.getSelectedItemPosition();
            if (orderByIndex == 0) {
                data.put(TweetAPI.orderBy, "time");
            } else {
                data.put(TweetAPI.orderBy, "likes");
            }
            int tweetTypeIndex = binding.typeSpinner.getSelectedItemPosition();
            if (tweetTypeIndex > 0) {
                data.put(TweetAPI.tweetType, tweetTypeIndex - 1);
            }
            if (!binding.search.getText().toString().isEmpty()) {
                data.put(TweetAPI.searchStr, binding.search.getText().toString());
            }
            TweetAPI.getTweetList(data, new Callback() {
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
                                msg.arg2 = mTweetList.size();
                                mTweetList.clear();
                            } else {
                                msg.arg1 = mTweetList.size();
                                msg.arg2 = -1;
                            }
                            JSONArray tweetList = data.getJSONArray(TweetAPI.tweetList);
                            for (int i = 0; i < tweetList.length(); i++) {
                                Tweet tweet = JSONUtil.createTweetFromJSON(tweetList.getJSONObject(i));
                                if (tweet == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                mTweetList.add(tweet);
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
        } catch (JSONException je) {
            System.err.println("Bad request format.");
        }
    }
}