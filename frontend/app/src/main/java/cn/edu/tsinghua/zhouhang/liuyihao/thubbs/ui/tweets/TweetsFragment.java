package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.EditActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.LoginActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentTweetsBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;

public class TweetsFragment extends Fragment {
    private FragmentTweetsBinding binding;
    private TweetsViewModel mTweetsViewModel;
    private ActivityResultLauncher<Intent> mLoginLauncher;
    private ActivityResultLauncher<Intent> mEditLauncher;
    private ActivityResultLauncher<Intent> mDetailLauncher;
    private TweetListAdapter mAdapter;
    private OnDetailReturnListener onDetailReturnListener;

    private final LinkedList<Tweet> mTweetList = new LinkedList<>();

    interface OnDetailReturnListener {
        void onDetailReturn(ActivityResult result);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTweetsViewModel =
                new ViewModelProvider(this).get(TweetsViewModel.class);

        binding = FragmentTweetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /* 测试数据开始 */
        mTweetList.add(new Tweet(
                1, 1, Tweet.TYPE_TEXT, "Hello, world!", null,
                "2022-05-15", 99, 99, null, null, null,
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, true
        ));
        mTweetList.add(new Tweet(
                2, 1, Tweet.TYPE_TEXT, "这是有位置信息的~", "(116.12°E, 24.5°N)",
                "2022-05-15", 12, 35, null, null, null,
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, false
        ));
        ArrayList<String> imageList = new ArrayList<>();
        imageList.add(Static.Image.getImageUrl("BingWallpaper.jpg"));
        mTweetList.add(new Tweet(
                3, 1, Tweet.TYPE_IMAGE, "这是单张图片的~", "(116.12°E, 24.5°N)",
                "2022-05-15", 16, 33, new ArrayList<>(imageList), null, null,
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, true
        ));
        imageList.add(Static.Image.getImageUrl("R-C.png"));
        mTweetList.add(new Tweet(
                4, 1, Tweet.TYPE_IMAGE, "这是多张图片的~", "(116.12°E, 24.5°N)",
                "2022-05-15", 18, 45, new ArrayList<>(imageList), null, null,
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, false
        ));
        imageList.clear();
        for (int i = 0; i < 9; i++) {
            imageList.add(Static.Image.getImageUrl("BingWallpaper.jpg"));
        }
        mTweetList.add(new Tweet(
                5, 1, Tweet.TYPE_IMAGE, "这是九宫格~", "(116.12°E, 24.5°N)",
                "2022-05-15", 66, 66, new ArrayList<>(imageList), null, null,
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, true
        ));
        mTweetList.add(new Tweet(
                6, 1, Tweet.TYPE_AUDIO, "这是有音频的~", "(116.12°E, 24.5°N)",
                "2022-05-15", 10, 40, null,
                Static.Audio.getAudioUrl("jump.mp3"), null,
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, false
        ));
        mTweetList.add(new Tweet(
                7, 1, Tweet.TYPE_AUDIO, "还是音频的~", "(116.12°E, 24.5°N)",
                "2022-05-15", 6, 23, null,
                Static.Audio.getAudioUrl("success.mp3"), null,
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, false
        ));
        mTweetList.add(new Tweet(
                8, 1, Tweet.TYPE_VIDEO, "开始有视频了~", "(116.12°E, 24.5°N)",
                "2022-05-15", 12, 33, null,
                null, Static.Video.getVideoUrl("test.mp4"),
                "かみ", Static.HeadShot.getHeadShotUrl("default_headshot.jpg"), false, false
        ));
        /* 测试数据结束 */
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
        initMode();
        initView();
        initListener();
    }

    private void initLauncher() {
        mLoginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            init();
        });
        mEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });
        mDetailLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (onDetailReturnListener != null) {
                onDetailReturnListener.onDetailReturn(result);
            }
        });
    }

    private void initMode() {
        Bundle bundle = getArguments();
        int type;
        if (bundle != null) {
            type = bundle.getInt(Constant.TWEETS_TYPE, Constant.TWEETS_EMPTY);
        } else {
            type = Constant.TWEETS_EMPTY;
        }
        switch (type) {
            case Constant.TWEETS_EMPTY:
                mTweetsViewModel.setText("这里是空动态列表");
                break;
            case Constant.TWEETS_ALL:
                mTweetsViewModel.setText("这里是全部动态列表");
                break;
            case Constant.TWEETS_FOLLOW:
                mTweetsViewModel.setText("这里是已关注列表");
                break;
            case Constant.TWEETS_USER:
                mTweetsViewModel.setText("这里是我的动态列表");
                break;
            default:
                mTweetsViewModel.setText("什么玩意？");
                break;
        }
    }

    private void initView() {
        if (State.getState().isLogin) {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.recyclerView.setAdapter(mAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.loginRequiredLayout.setVisibility(View.GONE);
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.loginRequiredLayout.setVisibility(View.VISIBLE);
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
}