package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentTweetsBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;

public class TweetsFragment extends Fragment {
    public static final String TWEETS_TYPE = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.fragments.TweetsFragment.TWEETS_TYPE";
    public static final int TWEETS_EMPTY = 0; // 空
    public static final int TWEETS_ALL = 1; // 全部推送
    public static final int TWEETS_MY = 2; // 关注的人的推送
    public static final int TWEETS_ME = 3; // 我的推送

    private FragmentTweetsBinding binding;
    private TweetsViewModel mTweetsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTweetsViewModel =
                new ViewModelProvider(this).get(TweetsViewModel.class);

        binding = FragmentTweetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void init() {
        initModel();
        initView();
        initListener();
    }

    private void initModel() {
        Bundle bundle = getArguments();
        int type;
        if (bundle != null) {
            type = bundle.getInt(TWEETS_TYPE, TWEETS_EMPTY);
        } else {
            type = TWEETS_EMPTY;
        }
        switch (type) {
            case TWEETS_EMPTY:
                mTweetsViewModel.setText("这里是空动态列表");
                break;
            case TWEETS_ALL:
                mTweetsViewModel.setText("这里是全部动态列表");
                break;
            case TWEETS_MY:
                mTweetsViewModel.setText("这里是已关注列表");
                break;
            case TWEETS_ME:
                mTweetsViewModel.setText("这里是我的动态列表");
                break;
            default:
                mTweetsViewModel.setText("什么玩意？");
                break;
        }
    }

    private void initView() {
        final TextView textView = binding.textTweets;
        mTweetsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
    }

    private void initListener() {
        binding.search.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                // TODO: 进行搜索
                Alert.info(getContext(), R.string.is_searching);
            }
            Util.HideKeyBoard(getActivity(), textView);
            return true;
        });
        binding.spinner.setSelection(0, true);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Alert.info(getContext(), getResources().getStringArray(R.array.typeArray)[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}