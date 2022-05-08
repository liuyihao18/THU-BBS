package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.BaseRequest;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.APIConstant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.util.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyImageView extends androidx.appcompat.widget.AppCompatImageView {

    private final Handler handler = new Handler(Looper.myLooper(), msg -> {
        switch (msg.what) {
            case APIConstant.REQUEST_OK:
                Bitmap bitmap = (Bitmap) msg.obj;
                setImageBitmap(bitmap);
                break;
            case APIConstant.NETWORK_ERROR:
                Alert.error(getContext(), R.string.network_error);
                break;
            case APIConstant.SERVER_ERROR:
                Alert.error(getContext(), R.string.server_error);
                break;
        }
        return true;
    });

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String url) {
        BaseRequest.get(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message msg = new Message();
                msg.what = APIConstant.NETWORK_ERROR;
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                Message msg = new Message();
                if (body == null) {
                    msg.what = APIConstant.SERVER_ERROR;
                    return;
                }
                InputStream is = body.byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                msg.what = APIConstant.REQUEST_OK;
                msg.obj = bitmap;
                handler.sendMessage(msg);
                is.close();
            }
        });
    }
}
