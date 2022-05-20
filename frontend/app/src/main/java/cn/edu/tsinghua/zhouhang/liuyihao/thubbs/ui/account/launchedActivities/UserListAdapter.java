package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
<<<<<<< HEAD
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NoErrorAPI;
=======
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NoMoreWantToDoAPI;
>>>>>>> ecf6672fb80f7bcea741ddfccdb70db76f4cc4dd
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.UserListItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.UserListItem;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListItemViewHolder> {
    private final LinkedList<UserListItem> mUserList;
    private final int mType;
    private final Context mContext;
    private final GoUserSpaceInterface mParent;

    class UserListItemViewHolder extends RecyclerView.ViewHolder {
        UserListItemBinding binding;
        UserListItem mUserListItem;

        public UserListItemViewHolder(UserListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            initListener();
        }

        private void initListener() {
            binding.getRoot().setOnClickListener(view -> {
                Intent intent = new Intent(mContext, UserSpaceActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, mUserListItem.getUserId());
                mParent.goUserSpace(intent);
            });
        }

        public UserListItemViewHolder setUserListItem(UserListItem userListItem) {
            mUserListItem = userListItem;
            return this;
        }

        public void refresh() {
            binding.headshot.setImageUrl(mUserListItem.getHeadshot());
            binding.nickname.setText(mUserListItem.getNickname());
            binding.description.setText(R.string.description_empty);
            switch (mType) {
                case Constant.FOLLOW_LIST:
                    if (mUserListItem.isFollow) {
                        binding.button.setText(R.string.button_unfollow);
                        binding.button.setBackgroundColor(mContext.getColor(R.color.button_disabled));
                    } else {
                        binding.button.setText(R.string.follow);
                        binding.button.setBackgroundColor(mContext.getColor(R.color.pink));
                    }
                    binding.button.setOnClickListener(view -> {
                        if (mUserListItem.isFollow) {
<<<<<<< HEAD
                            NoErrorAPI.unfollow(mContext, mUserListItem.getUserId(), () -> {
=======
                            NoMoreWantToDoAPI.unfollow(mContext, mUserListItem.getUserId(), () -> {
>>>>>>> ecf6672fb80f7bcea741ddfccdb70db76f4cc4dd
                                mUserListItem.isFollow = false;
                                binding.button.setText(R.string.follow);
                                binding.button.setBackgroundColor(mContext.getColor(R.color.pink));
                            });
                        } else {
<<<<<<< HEAD
                            NoErrorAPI.follow(mContext, mUserListItem.getUserId(), () -> {
=======
                            NoMoreWantToDoAPI.follow(mContext, mUserListItem.getUserId(), () -> {
>>>>>>> ecf6672fb80f7bcea741ddfccdb70db76f4cc4dd
                                mUserListItem.isFollow = true;
                                binding.button.setText(R.string.button_unfollow);
                                binding.button.setBackgroundColor(mContext.getColor(R.color.button_disabled));
                            });
                        }
                    });
                    break;
                case Constant.FAN_LIST:
                    binding.button.setVisibility(View.GONE);
                    break;
                case Constant.BLACK_LIST:
                    binding.button.setText(R.string.button_white);
                    binding.button.setBackgroundColor(mContext.getColor(R.color.button_disabled));
                    binding.button.setOnClickListener(view -> {
                        int index = mUserList.indexOf(mUserListItem);
                        if (index < 0) {
                            Alert.error(mContext, R.string.unknown_error);
                        } else {
<<<<<<< HEAD
                            NoErrorAPI.white(mContext, mUserListItem.getUserId(), () -> {
                                Alert.info(mContext, R.string.white_success);
                                mUserList.remove(index);
                                notifyItemRemoved(index);
                            });
=======
                            mUserList.remove(index);
                            notifyItemRemoved(index);
>>>>>>> ecf6672fb80f7bcea741ddfccdb70db76f4cc4dd
                        }
                    });
                    break;
            }
        }
    }

    public UserListAdapter(Context context, LinkedList<UserListItem> userList, int type, GoUserSpaceInterface parent) {
        mContext = context;
        mUserList = userList;
        mType = type;
        mParent = parent;
    }

    @NonNull
    @Override
    public UserListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserListItemBinding binding = UserListItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new UserListItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListItemViewHolder holder, int position) {
        holder.setUserListItem(mUserList.get(position)).refresh();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
