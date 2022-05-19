package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class Alert {

    public static void error(Context context, @StringRes int resId) {
        if (context != null) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }

    public static void error(Context context, String str) {
        if (context != null) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }

    public static void info(Context context, @StringRes int resId) {
        if (context != null) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }

    public static void info(Context context, String str) {
        if (context != null) {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }
}
