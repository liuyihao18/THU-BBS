package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.TitleBarStyle;

import java.util.ArrayList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ComponentImageGroupBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.lib.GlideEngine;

public class ImageGroup extends ConstraintLayout {
    private Activity mActivity = null;
    private ComponentImageGroupBinding binding;
    private ArrayList<String> mImageUrlList = null;
    private final ArrayList<MyImageView> mImageViewList = new ArrayList<>();
    private final ArrayList<ImageView> mCloseButtonList = new ArrayList<>();
    ArrayList<LocalMedia> mSelectedData = new ArrayList<>();
    private boolean mEditable = false;
    private final int mTotalCount = 9;
    private int mIndex = 0;

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
        initList();
        initView();
        initListener();
    }

    private void initList() {
        mImageViewList.add(binding.myImageView1);
        mImageViewList.add(binding.myImageView2);
        mImageViewList.add(binding.myImageView3);
        mImageViewList.add(binding.myImageView4);
        mImageViewList.add(binding.myImageView5);
        mImageViewList.add(binding.myImageView6);
        mImageViewList.add(binding.myImageView7);
        mImageViewList.add(binding.myImageView8);
        mImageViewList.add(binding.myImageView9);
        mCloseButtonList.add(binding.closeButton1);
        mCloseButtonList.add(binding.closeButton2);
        mCloseButtonList.add(binding.closeButton3);
        mCloseButtonList.add(binding.closeButton4);
        mCloseButtonList.add(binding.closeButton5);
        mCloseButtonList.add(binding.closeButton6);
        mCloseButtonList.add(binding.closeButton7);
        mCloseButtonList.add(binding.closeButton8);
        mCloseButtonList.add(binding.closeButton9);
    }

    private void initView() {
        binding.imageGroupRow1.setVisibility(GONE);
        binding.imageGroupRow2.setVisibility(GONE);
        binding.imageGroupRow3.setVisibility(GONE);
        for (int i = 0; i < mTotalCount; i++) {
            mImageViewList.get(i).setVisibility(INVISIBLE);
            mCloseButtonList.get(i).setVisibility(INVISIBLE);
        }
    }

    private void initListener() {
        for (int i = 0; i < mTotalCount; i++) {
            mImageViewList.get(i).setOnClickListener(view -> {
                int index = mImageViewList.indexOf((MyImageView) view);
                if (index == mIndex) {
                    selectImage();
                }
            });
        }
        for (int i = 0; i < mTotalCount; i++) {
            mCloseButtonList.get(i).setOnClickListener(view -> {
                int index = mCloseButtonList.indexOf((ImageView) view);
                if (index < mIndex) {
                    mSelectedData.remove(index);
                }
                mIndex--;
                refresh();
            });
        }
    }

    public void setEditable(boolean editable) {
        mEditable = editable;
    }

    public void bind(Activity activity, ArrayList<String> imageList) {
        mActivity = activity;
        mImageUrlList = imageList;
        refresh();
    }

    private void refresh() {
        mImageUrlList.clear();
        mIndex = 0;
        for (LocalMedia media : mSelectedData) {
            mImageUrlList.add(media.getPath());
            mIndex++;
        }
        if (mIndex < 3) {
            binding.imageGroupRow1.setVisibility(VISIBLE);
            binding.imageGroupRow2.setVisibility(GONE);
            binding.imageGroupRow3.setVisibility(GONE);
        } else if (mIndex < 6) {
            binding.imageGroupRow1.setVisibility(VISIBLE);
            binding.imageGroupRow2.setVisibility(VISIBLE);
            binding.imageGroupRow3.setVisibility(GONE);
        } else {
            binding.imageGroupRow1.setVisibility(VISIBLE);
            binding.imageGroupRow2.setVisibility(VISIBLE);
            binding.imageGroupRow3.setVisibility(VISIBLE);
        }
        for (int i = 0; i < mIndex; i++) {
            Glide.with(getContext())
                    .load(mImageUrlList.get(i))
                    .centerCrop()
                    .placeholder(R.drawable.ic_loading_spinner_black_24dp)
                    .into(mImageViewList.get(i));
            mImageViewList.get(i).setVisibility(VISIBLE);
            if (mEditable) {
                mCloseButtonList.get(i).setVisibility(VISIBLE);
            }
        }
        for (int i = mIndex + 1; i < mTotalCount; i++) {
            mImageViewList.get(i).setVisibility(INVISIBLE);
            mCloseButtonList.get(i).setVisibility(INVISIBLE);
        }
        if (mIndex < mTotalCount && mEditable) {
            System.out.println(mIndex);
            mImageViewList.get(mIndex).setImageResource(R.drawable.ic_add_image_gray_24dp);
            mImageViewList.get(mIndex).setVisibility(VISIBLE);
            mCloseButtonList.get(mIndex).setVisibility(INVISIBLE);
        }
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
                .setMaxSelectNum(mTotalCount)
                .setSelectedData(mSelectedData)
                .setLanguage(86)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        mSelectedData = result;
                        refresh();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
}
