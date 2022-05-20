package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import android.content.Intent;

import androidx.activity.result.ActivityResult;

public interface GoEditInterface {
    interface OnEditReturnListener {
        void onEditReturn(ActivityResult result);
    }

    /**
     * 登记回调
     *
     * @param onEditReturnListener 回调接口
     */
    void registerOnEditReturnListener(OnEditReturnListener onEditReturnListener);

    /**
     * 前往用户空间的接口
     *
     * @param intent 携带的数据
     */
    void goEdit(Intent intent);
}
