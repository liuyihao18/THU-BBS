package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.account.launchedActivities;

import android.content.Intent;

import androidx.activity.result.ActivityResult;

public interface GoUserSpaceInterface {
    interface OnUserSpaceReturnListener {
        void onUserSpaceReturn(ActivityResult result);
    }

    /**
     * 登记回调
     *
     * @param onUserSpaceReturnListener 回调接口
     */
    void registerOnUserSpaceReturnListener(OnUserSpaceReturnListener onUserSpaceReturnListener);

    /**
     * 前往用户空间的接口
     *
     * @param intent 携带的数据
     */
    void goUserSpace(Intent intent);
}
