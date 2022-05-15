package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTweetsViewModel =
                new ViewModelProvider(this).get(TweetsViewModel.class);

        binding = FragmentTweetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initLauncher();
        init();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
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
            if (result.getResultCode() == Activity.RESULT_OK) {
                init();
            }
        });
        mEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

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
            binding.loginRequiredLayout.setVisibility(View.GONE);
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.loginRequiredLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        if (State.getState().isLogin) {
            binding.fab.setOnClickListener(view -> {
                mEditLauncher.launch(new Intent(getActivity(), EditActivity.class));
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
}