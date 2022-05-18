package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import static java.lang.Math.abs;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

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

    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，通过document id处理，内部会调用Uri.decode(docId)进行解码
            String docId = DocumentsContract.getDocumentId(uri);
            String[] splits = docId.split(":");
            String type = null, id = null;
            if (splits.length == 2) {
                type = splits[0];
                id = splits[1];
            }
            switch (uri.getAuthority()) {
                case "com.android.externalstorage.documents":
                    if ("primary".equals(type)) {
                        path = context.getExternalFilesDir(null) + File.separator + id;
                    }
                    break;
                case "com.android.providers.downloads.documents":
                    if ("raw".equals(type)) {
                        path = id;
                    } else {
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                        path = getMediaPathFromUri(context, contentUri, null, null);
                    }
                    break;
                case "com.android.providers.media.documents":
                    Uri externalUri = null;
                    switch (Objects.requireNonNull(type)) {
                        case "image":
                            externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "video":
                            externalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "audio":
                            externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "document":
                            externalUri = MediaStore.Files.getContentUri("external");
                            break;
                    }
                    if (externalUri != null) {
                        String selection = "_id=?";
                        String[] selectionArgs = new String[]{id};
                        path = getMediaPathFromUri(context, externalUri, selection, selectionArgs);
                    }
                    break;
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            path = getMediaPathFromUri(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri(uri.fromFile)，直接获取图片路径即可
            path = uri.getPath();
        }
        // 确保如果返回路径，则路径合法
        return path == null ? null : (new File(path).exists() ? path : null);
    }

    private static String getMediaPathFromUri(Context context, Uri uri, String
            selection, String[] selectionArgs) {
        String path;
        uri.getAuthority();
        path = uri.getPath();
        String sdPath = context.getExternalFilesDir(null).getAbsolutePath();
        if (!path.startsWith(sdPath)) {
            int sepIndex = path.indexOf(File.separator, 1);
            if (sepIndex == -1) path = null;
            else {
                String sdPah = context.getExternalFilesDir(null).getAbsolutePath();
                path = sdPah + path.substring(sepIndex);
            }
        }

        if (path == null || !new File(path).exists()) {
            ContentResolver resolver = context.getContentResolver();
            String[] projection = new String[]{"_data"}; // MediaStore.MediaColumns.DATA
            Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    try {
                        int index = cursor.getColumnIndexOrThrow(projection[0]);
                        if (index != -1) path = cursor.getString(index);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        path = null;
                    } finally {
                        cursor.close();
                    }
                }
            }
        }
        return path;
    }
    /*
    ————————————————
    版权声明：本文为CSDN博主「福州-司马懿」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
    原文链接：https://blog.csdn.net/chy555chy/article/details/104198956
    */
}
