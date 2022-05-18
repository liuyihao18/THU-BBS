package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;

public class MyImageView extends androidx.appcompat.widget.AppCompatImageView {

    public MyImageView(Context context) {
        super(context);
        init();
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    public void setImageUrl(String url) {
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.ic_loading_spinner_black_24dp)
                .into(this);
    }
}
