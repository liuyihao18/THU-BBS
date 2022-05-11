package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.TitleBarStyle;

import java.util.ArrayList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ComponentImageGroupBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.lib.GlideEngine;

public class ImageGroup extends ConstraintLayout {
    private Activity mActivity = null;
    private ComponentImageGroupBinding binding;
    private ArrayList<String> mImageUrlList = null;
    private ArrayList<MyImageView> mImageViewList = new ArrayList<>();
    private final int totalCount = 9;
    private int index = 0;

    public ImageGroup(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ImageGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = ComponentImageGroupBinding.inflate(LayoutInflater.from(context), this, true);
        initImageViewList();
        initView();
        initListener();
    }

    private void initImageViewList() {
        mImageViewList.add(binding.myImageView1);
        mImageViewList.add(binding.myImageView2);
        mImageViewList.add(binding.myImageView3);
        mImageViewList.add(binding.myImageView4);
        mImageViewList.add(binding.myImageView5);
        mImageViewList.add(binding.myImageView6);
        mImageViewList.add(binding.myImageView7);
        mImageViewList.add(binding.myImageView8);
        mImageViewList.add(binding.myImageView9);
    }

    private void initView() {

    }

    private void initListener() {
        if (index < mImageViewList.size()) {
            mImageViewList.get(index).setOnClickListener(view -> {
                int i = mImageViewList.indexOf((MyImageView) view);
                if (i == index) {
                    selectImage();
                }
            });
        }
    }

    public void bind(Activity activity, ArrayList<String> imageList) {
        mActivity = activity;
        mImageUrlList = imageList;
    }

    public void selectImage() {
        if (mActivity == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constant.STORAGE_PERMISSION);
            return;
        }
        PictureSelectorStyle style = new PictureSelectorStyle();
        TitleBarStyle titleBarStyle = new TitleBarStyle(); // 标题栏样式
        BottomNavBarStyle bottomNavBarStyle = new BottomNavBarStyle(); // 底部导航栏样式
        style.setTitleBarStyle(titleBarStyle);
        style.setBottomBarStyle(bottomNavBarStyle);
        PictureSelector
                .create(getContext())
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectorUIStyle(style)
                .setMaxSelectNum(totalCount - index)
                .setLanguage(86)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        System.out.println(result);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
}
