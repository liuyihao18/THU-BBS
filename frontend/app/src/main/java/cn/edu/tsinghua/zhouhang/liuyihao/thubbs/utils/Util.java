package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Util {
    public static void HideKeyBoard(Activity activity, View v) {
        InputMethodManager manager = null;
        if (activity != null) {
            manager = ((InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if (manager != null)
            manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        v.clearFocus();
    }
}
