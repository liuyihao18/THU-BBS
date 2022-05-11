package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities.CommentNotificationActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities.FollowNotificationActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.notifications.launchedActivities.LikeActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private LinkedList<String> titleList, contentList, imageList;

    private ActivityResultLauncher<Intent> mLikeLauncher;
    private ActivityResultLauncher<Intent> mFollowLauncher;
    private ActivityResultLauncher<Intent> mCommentLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        titleList = new LinkedList<String>();
        contentList = new LinkedList<String>();
        imageList = new LinkedList<String>();
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        for(int i = 0; i < 5; i++) {
            titleList.add("测试用标题");
            contentList.add(getString(R.string.test_notification_content));
            imageList.add(getString(R.string.default_headshot_url));
        }
        initLauncher();
        initRecycleView();
        initListener();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initLauncher() {
        mLikeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });

        mFollowLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });

        mCommentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });
    }

    private void initListener() {
        LinearLayout likeLayout = binding.notificationLikeLayout;
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLikeLauncher.launch(new Intent(getActivity(), LikeActivity.class));
            }
        });

        LinearLayout followLayout = binding.notificationFollowLayout;
        followLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFollowLauncher.launch(new Intent(getActivity(), FollowNotificationActivity.class));
            }
        });

        LinearLayout commentLayout = binding.notificationCommentLayout;
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommentLauncher.launch(new Intent(getActivity(), CommentNotificationActivity.class));
            }
        });
    }

    private void initRecycleView() {
        RecyclerView recyclerView = binding.notifications;
        recyclerView.setAdapter(new NotificationListAdapter(getActivity(), titleList, contentList, imageList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}