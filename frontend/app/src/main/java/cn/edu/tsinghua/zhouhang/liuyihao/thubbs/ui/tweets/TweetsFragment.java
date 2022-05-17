package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.TweetAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.EditActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.LoginActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentTweetsBinding;
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
    private ActivityResultLauncher<Intent> mLoginLauncher;
    private ActivityResultLauncher<Intent> mEditLauncher;
    private ActivityResultLauncher<Intent> mDetailLauncher;
    private TweetListAdapter mAdapter;
    private OnDetailReturnListener onDetailReturnListener;

    private final LinkedList<Tweet> mTweetList = new LinkedList<>();

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                if (msg.arg2 < 0) {
                    mAdapter.notifyItemRangeInserted(msg.arg1, mTweetList.size() - msg.arg1);
                } else {
                    mAdapter.notifyItemRangeRemoved(msg.arg1, msg.arg2);
                    mAdapter.notifyItemRangeInserted(0, mTweetList.size());
                }
                binding.recyclerView.smoothScrollToPosition(msg.arg1);
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
        return true;
    });

    interface OnDetailReturnListener {
        void onDetailReturn(ActivityResult result);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTweetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initLauncher();
        initAdapter();
        init();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void init() {
        initType();
        initView();
        initListener();
    }

    private void initLauncher() {
        mLoginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> init());
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
    }

    private void initType() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(Constant.TWEETS_TYPE, Constant.TWEETS_EMPTY);
        } else {
            mType = Constant.TWEETS_EMPTY;
        }
        if (mType == Constant.TWEETS_USER) {
            mUserId = bundle.getInt(Constant.EXTRA_USER_ID, 0);
        }
    }

    private void initView() {
        if (State.getState().isLogin) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.recyclerView.setAdapter(mAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.loginRequiredLayout.setVisibility(View.GONE);
            getTweetList(true);
            binding.noTweetLayout.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.loginRequiredLayout.setVisibility(View.VISIBLE);
            binding.noTweetLayout.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        if (State.getState().isLogin) {
            binding.fab.setOnClickListener(view -> {
                mEditLauncher.launch(new Intent(getActivity(), EditActivity.class).setAction(Constant.EDIT_FROM_BLANK));
            });
            binding.search.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    // TODO: 进行搜索
                    Alert.info(getContext(), R.string.is_searching);
                }
                Util.HideKeyBoard(getActivity(), textView);
                return true;
            });
            binding.typeSpinner.setSelection(0, true);
            binding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Alert.info(getContext(), getResources().getStringArray(R.array.type_array)[i]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            binding.loginButton.setOnClickListener(view -> {
                mLoginLauncher.launch(new Intent(getActivity(), LoginActivity.class));
            });
        }
    }

    private void initAdapter() {
        mAdapter = new TweetListAdapter(getContext(), mTweetList, this);
    }

    public TweetsFragment setOnDetailReturnListener(OnDetailReturnListener onDetailReturnListener) {
        this.onDetailReturnListener = onDetailReturnListener;
        return this;
    }

    public void goDetail(Intent intent) {
        mDetailLauncher.launch(intent);
    }

    public void refresh() {
        if (mTweetList.size() > 0) {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.noTweetLayout.setVisibility(View.GONE);
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.noTweetLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getTweetList(boolean isRefresh) {
        if (mType == Constant.TWEETS_EMPTY) {
            return;
        }
        try {
            JSONObject data = new JSONObject();
            if (isRefresh) {
                data.put(TweetAPI.block, 0);
            } else {
                data.put(TweetAPI.block, mTweetList.size());
            }
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
                                    msg.what = APIConstant.REQUEST_ERROR;
                                    msg.obj = getResources().getString(R.string.server_error);
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