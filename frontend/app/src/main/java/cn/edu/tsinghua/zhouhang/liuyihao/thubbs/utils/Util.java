package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import static java.lang.Math.abs;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

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

    public static String FormatLocation(@NonNull Location location) {
        DecimalFormat df = new DecimalFormat("#.00");
        double longitude = location.getLongitude();
        String longitude_str = df.format(abs(longitude)) + "°" +
                (longitude < 0 ? "W" : "E");
        double latitude = location.getLatitude();
        String latitude_str = df.format(abs(latitude)) + "°" +
                (latitude < 0 ? "N" : "S");
        return "(" + longitude_str + ", " + latitude_str + ")";
    }
}
