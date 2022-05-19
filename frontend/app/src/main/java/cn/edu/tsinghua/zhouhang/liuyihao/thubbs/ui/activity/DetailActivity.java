package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.TweetAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityDetailBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.CommentItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.TweetItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Comment;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.JSONUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.MediaResource;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.ThreadUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.TweetUtil;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DetailActivity extends AppCompatActivity {
    private final int GET_COMMENT_LIST_OK = 1;
    private final int COMMENT_OK = 2;
    private final int DELETE_COMMENT_OK = 3;

    private ActivityDetailBinding binding;
    private TweetItemBinding tweetItemBinding;
    private final MediaResource mediaResource = new MediaResource();
    private ActivityResultLauncher<Intent> mUserSpaceLauncher;
    private Tweet mTweet;
    private boolean isLoading = false;
    private final LinkedList<Comment> mCommentList = new LinkedList<>();
    private final Queue<OnGetCommentSuccessListener> mOnGetCommentSuccessCallback = new LinkedList<>();

    interface OnGetCommentSuccessListener {
        void onGetCommentSuccess();
    }

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case GET_COMMENT_LIST_OK:
                // 刷新
                bindComment();
                // 延迟回调
                ThreadUtil.delay(Constant.DELAY_TIME, () -> {
                    if (!mOnGetCommentSuccessCallback.isEmpty()) {
                        mOnGetCommentSuccessCallback.remove().onGetCommentSuccess();
                    }
                });
                break;
            case COMMENT_OK:
                Alert.info(this, R.string.comment_success);
                binding.comment.setText(null);
                getCommentList();
                mOnGetCommentSuccessCallback.add(() -> binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                break;
            case DELETE_COMMENT_OK:
                Alert.info(this, R.string.delete_comment_success);
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
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        // 动态项绑定
        tweetItemBinding = TweetItemBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        binding.tweet.addView(tweetItemBinding.getRoot());
        setContentView(binding.getRoot());
        // 隐藏APP顶部栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 获取数据
        Intent intent = getIntent();
        String action = intent.getAction();
        initLauncher();
        initView();
        initListener();
        // 携带数据打开的
        if (action.equals(Constant.DETAIL_HAVE_DATA)) {
            mTweet = (Tweet) intent.getSerializableExtra(Constant.EXTRA_TWEET);
            bindTweet();
            getCommentList();
        }
        // 不携带数据打开的
        else if (action.equals(Constant.DETAIL_NO_DATA)) {
            // TODO: 获取动态数据
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_TWEET, mTweet);
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    private void initLauncher() {
        mUserSpaceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // 级联返回（从个人主页返回详情）
            if (result.getResultCode() == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_title)).setText(R.string.detail);
        if (State.getState().user != null) {
            binding.commentHeadshot.setImageUrl(State.getState().user.headshot);
        } else {
            binding.commentHeadshot.setImageUrl(Constant.DEFAULT_HEADSHOT);
            State.getState().refreshMyProfile(this, null);
        }
        // 默认显示没有更多动态
        binding.noCommentLayout.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        // 返回
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
        // 发送评论按钮
        binding.sendButton.setOnClickListener(view -> {
            commentTweet();
            Util.HideKeyBoard(this, view);
        });
        // 下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener(this::getCommentList);
    }

    private void bindTweet() {
        // 绑定动态
        TweetUtil.bind(this, tweetItemBinding, mTweet, Constant.TWEETS_DETAIL, mediaResource,
                // 屏蔽用户
                view ->
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.question_black)
                                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                                }))
                                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                                    setResult(RESULT_OK);
                                    finish();
                                }).create().show(),
                // 级联返回（从个人主页返回详情）
                view -> {
                    Intent intent = new Intent(this, UserSpaceActivity.class);
                    intent.putExtra(Constant.EXTRA_USER_ID, mTweet.getUserID());
                    mUserSpaceLauncher.launch(intent);
                });
    }

    void bindComment() {
        binding.commentGroup.removeAllViews();
        for (int i = binding.commentGroup.getChildCount(); i < mCommentList.size(); i++) {
            Comment comment = mCommentList.get(i);
            CommentItemBinding commentItemBinding = CommentItemBinding.inflate(getLayoutInflater(), binding.commentGroup, false);
            commentItemBinding.headshot.setImageUrl(comment.getHeadshot());
            commentItemBinding.nickname.setText(comment.getNickname());
            commentItemBinding.commentTime.setText(comment.getCommentTime());
            commentItemBinding.content.setText(comment.getContent());
            if (comment.getUserId() == State.getState().userId) {
                commentItemBinding.closeButton.setVisibility(View.VISIBLE);
                commentItemBinding.closeButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                        .setTitle(R.string.question_delete_comment)
                        .setNegativeButton(R.string.button_cancel, ((dialogInterface, ii) -> {
                        }))
                        .setPositiveButton(R.string.button_ok, (dialogInterface, ii) -> {
                            int index = mCommentList.indexOf(comment);
                            if (index >= 0) {
                                deleteComment(comment.getCommentId());
                                mCommentList.remove(index);
                                binding.commentGroup.removeViewAt(index);
                            }
                        })
                        .create().show());
            } else {
                commentItemBinding.closeButton.setVisibility(View.GONE);
            }
            int finalI = i;
            commentItemBinding.headshot.setOnClickListener(view -> {
                Intent intent = new Intent(this, UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, mCommentList.get(finalI).getUserId());
                this.startActivity(intent);
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            View view = commentItemBinding.getRoot();
            view.setLayoutParams(layoutParams);
            binding.commentGroup.addView(view);
        }
    }

    private void getCommentList() {
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
            data.put(TweetAPI.tweetId, mTweet.getTweetID());
            TweetAPI.getTweetCommentList(data, new Callback() {
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
                            msg.what = GET_COMMENT_LIST_OK;
                            mCommentList.clear();
                            JSONArray tweetList = data.getJSONArray(TweetAPI.commentList);
                            for (int i = 0; i < tweetList.length(); i++) {
                                Comment comment = JSONUtil.createCommentFromJSON(tweetList.getJSONObject(i));
                                if (comment == null) {
                                    msg.what = APIConstant.SERVER_ERROR;
                                    break;
                                }
                                mCommentList.add(comment);
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

    private void commentTweet() {
        try {
            JSONObject data = new JSONObject();
            data.put(TweetAPI.tweetId, mTweet.getTweetID());
            data.put(TweetAPI.comment, binding.comment.getText().toString());
            TweetAPI.commentTweet(data, new Callback() {
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
                            msg.what = COMMENT_OK;
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

    private void deleteComment(int commentId) {
        try {
            JSONObject data = new JSONObject();
            data.put(TweetAPI.commentId, commentId);
            TweetAPI.deleteComment(data, new Callback() {
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
                            msg.what = DELETE_COMMENT_OK;
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