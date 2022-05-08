package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentTweetsBinding;

public class TweetsFragment extends Fragment {
    public static final String TWEETS_TYPE = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.fragments.TweetsFragment.TWEETS_TYPE";
    public static final int TWEETS_EMPTY = 0; // 空
    public static final int TWEETS_ALL = 1; // 全部推送
    public static final int TWEETS_MY = 2; // 关注的人的推送
    public static final int TWEETS_ME = 3; // 我的推送


    private FragmentTweetsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TweetsViewModel tweetsViewModel =
                new ViewModelProvider(this).get(TweetsViewModel.class);

        binding = FragmentTweetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTweets;
        tweetsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Bundle bundle = getArguments();
        int type;
        if (bundle != null) {
            type = bundle.getInt(TWEETS_TYPE, TWEETS_EMPTY);
        } else {
            type = TWEETS_EMPTY;
        }
        switch (type) {
            case TWEETS_EMPTY:
                tweetsViewModel.setText("这里是空动态列表");
                break;
            case TWEETS_ALL:
                tweetsViewModel.setText("这里是全部动态列表");
                break;
            case TWEETS_MY:
                tweetsViewModel.setText("这里是已关注列表");
                break;
            case TWEETS_ME:
                tweetsViewModel.setText("这里是我的动态列表");
                break;
            default:
                tweetsViewModel.setText("什么玩意？");
                break;
        }

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}