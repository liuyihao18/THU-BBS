package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtil {
    public interface OnDelayOverListener {
        void onDelayOver();
    }

    /**
     * @param mills               延迟的毫秒
     * @param onDelayOverListener 延迟结束后回调
     */
    public static void delay(int mills, OnDelayOverListener onDelayOverListener) {
        Handler handler = new Handler(Looper.myLooper());
        new Thread(() -> {
            try {
                Thread.sleep(mills);
                handler.post(() -> {
                    if (onDelayOverListener != null) {
                        onDelayOverListener.onDelayOver();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
