package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class Alert {
    public static void error(Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
