package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NoErrorAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.UserListItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.UserListItem;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.activity.UserSpaceActivity;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Util;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListItemViewHolder> {
    private final LinkedList<UserListItem> mUserList;
    private final int mListType;
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
            if (mUserListItem.getDescription().isEmpty()) {
                binding.description.setText(R.string.description_empty);
            } else {
                String description = mUserListItem.getDescription();
                if (description.length() > Constant.MAX_DIGEST_LENGTH) {
                    description = description.substring(0, Constant.MAX_DIGEST_LENGTH);
                    description += "...";
                }
                binding.description.setText(description);
            }
            switch (mListType) {
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
                            NoErrorAPI.unfollow(mContext, mUserListItem.getUserId(), () -> {
                                mUserListItem.isFollow = false;
                                binding.button.setText(R.string.follow);
                                binding.button.setBackgroundColor(mContext.getColor(R.color.pink));
                            });
                        } else {
                            NoErrorAPI.follow(mContext, mUserListItem.getUserId(), () -> {
                                mUserListItem.isFollow = true;
                                binding.button.setText(R.string.button_unfollow);
                                binding.button.setBackgroundColor(mContext.getColor(R.color.button_disabled));
                            });
                        }
                    });
                    break;
                case Constant.FAN_LIST:
                    binding.button.setText(R.string.follower);
                    break;
                case Constant.BLACK_LIST:
                    binding.button.setText(R.string.button_white);
                    binding.button.setBackgroundColor(mContext.getColor(R.color.button_disabled));
                    binding.button.setOnClickListener(view -> new AlertDialog.Builder(mContext)
                            .setTitle(R.string.question_white)
                            .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                            }))
                            .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                                int index = mUserList.indexOf(mUserListItem);
                                if (index < 0) {
                                    // Alert.error(mContext, R.string.unknown_error);
                                    Util.doNothing();
                                } else {
                                    NoErrorAPI.white(mContext, mUserListItem.getUserId(), () -> {
                                        Alert.info(mContext, R.string.white_success);
                                        mUserList.remove(index);
                                        notifyItemRemoved(index);
                                    });
                                }
                            }).
                            create().show());
                    break;
            }
        }
    }

    public UserListAdapter(Context context, LinkedList<UserListItem> userList, int listType, GoUserSpaceInterface parent) {
        mContext = context;
        mUserList = userList;
        mListType = listType;
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
