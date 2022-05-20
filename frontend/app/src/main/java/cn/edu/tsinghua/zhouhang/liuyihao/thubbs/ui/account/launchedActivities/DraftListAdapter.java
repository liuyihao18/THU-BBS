package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.DraftItemBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Draft;

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

        }

        public DraftViewHolder setDraft(Draft draft) {
            mDraft = draft;
            return this;
        }

        public void refresh() {

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
