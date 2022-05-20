package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NoErrorAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.DraftItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Draft;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class DraftListAdapter extends RecyclerView.Adapter<DraftListAdapter.DraftViewHolder> {
    private final LinkedList<Draft> mDraftList;
    private final Context mContext;
    private final GoEditInterface mParent;

    class DraftViewHolder extends RecyclerView.ViewHolder {
        DraftItemBinding binding;
        Draft mDraft;

        public DraftViewHolder(DraftItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            initListener();
        }

        private void initListener() {

            binding.closeButton.setOnClickListener(view -> new AlertDialog.Builder(mContext)
                    .setTitle(R.string.question_delete_draft)
                    .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                    }))
                    .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                        int index = mDraftList.indexOf(mDraft);
                        if (index < 0) {
                            Alert.error(mContext, R.string.unknown_error);
                        } else {
                            NoErrorAPI.deleteTweet(mContext, mDraft.getTweetId(), () -> {
                                Alert.info(mContext, R.string.delete_draft_success);
                                mDraftList.remove(index);
                                notifyItemRemoved(index);
                            });
                        }
                    }).
                    create().show());
        }

        public DraftViewHolder setDraft(Draft draft) {
            mDraft = draft;
            return this;
        }

        public void refresh() {
            binding.authorHeadshot.setImageUrl(State.getState().user.headshot);
            binding.authorName.setText(State.getState().user.nickname);
            binding.lastModified.setText(mDraft.getLastModified());
            if (mDraft.getTitle().isEmpty()) {
                binding.title.setText(R.string.no_title);
            } else {
                binding.title.setText(mDraft.getTitle());
            }
            if (mDraft.getContent().isEmpty()) {
                binding.contentText.setText(R.string.no_content);
            } else {
                binding.contentText.setText(mDraft.getContent());
            }
        }
    }

    public DraftListAdapter(Context context, LinkedList<Draft> draftList, GoEditInterface parent) {
        mContext = context;
        mDraftList = draftList;
        mParent = parent;
    }

    @NonNull
    @Override
    public DraftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DraftItemBinding binding = DraftItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new DraftViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DraftViewHolder holder, int position) {
        holder.setDraft(mDraftList.get(position)).refresh();
    }

    @Override
    public int getItemCount() {
        return mDraftList.size();
    }
}
