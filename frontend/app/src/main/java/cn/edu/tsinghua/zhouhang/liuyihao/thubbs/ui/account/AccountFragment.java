package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentAccountBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components.MyCircleImageView;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initView();
        initListener();
        return root;
    }

    private void initView() {
        if (State.getState().isLogin) {
            binding.accountHeadshot.setImageUrl(State.getState().user.headshot);
            binding.tweetCount.setText(String.valueOf(State.getState().user.tweetCount));
            binding.followCount.setText(String.valueOf(State.getState().user.followCount));
            binding.followerCount.setText(String.valueOf(State.getState().user.followerCount));
        }
    }

    private void initListener() {
        binding.accountHeadshot.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), UserSpaceActivity.class);
            intent.putExtra(Constant.EXTRA_USER_ID, State.getState().userId);
            startActivity(intent);
        });
        binding.logoutButton.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.question_logout)
                    .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                    }))
                    .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                        State.getState().jwt = null;
                        State.getState().userId = 0;
                        State.getState().isLogin = false;
                        Activity activity = getActivity();
                        if (activity == null) {
                            Alert.error(getContext(), R.string.unknown_error);
                            return;
                        }
                        SharedPreferences preferences = activity.getSharedPreferences(Constant.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
                        preferences.edit().remove(Constant.JWT).apply();
                        preferences.edit().remove(Constant.USER_ID).apply();
                        Alert.info(getContext(), R.string.logout_success);
                    }).
                    create().show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}