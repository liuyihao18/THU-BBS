package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ComponentImageGroupBinding;

public class ImageGroup extends ConstraintLayout {
    private ComponentImageGroupBinding binding;
    private ArrayList<String> mImageUrlList = null;
    private final ArrayList<MyImageView> mImageViewList = new ArrayList<>();
    private final ArrayList<ImageView> mCloseButtonList = new ArrayList<>();
    private boolean mEditable = false;
    private final int mTotalCount = Constant.MAX_IMAGE_COUNT;

    public interface ImageGroupListener {
        void onClickImage(MyImageView myImageView, int index);

        void onClickAddImage(MyImageView myImageView, int index);

        void onClickCloseButton(View view, int index);
    }

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

    public ImageGroup bindImageUrlList(ArrayList<String> imageUrlList) {
        mImageUrlList = imageUrlList;
        return this;
    }

    public ImageGroup setEditable(boolean editable) {
        mEditable = editable;
        return this;
    }

    public ImageGroup registerImageGroupListener(ImageGroupListener imageGroupListener) {
        for (int i = 0; i < mTotalCount; i++) {
            mImageViewList.get(i).setOnClickListener(view -> {
                int index = mImageViewList.indexOf((MyImageView) view);
                imageGroupListener.onClickImage(mImageViewList.get(index), index);
                imageGroupListener.onClickAddImage(mImageViewList.get(index), index);
            });
        }
        for (int i = 0; i < mTotalCount; i++) {
            mCloseButtonList.get(i).setOnClickListener(view -> {
                int index = mCloseButtonList.indexOf((ImageView) view);
                imageGroupListener.onClickCloseButton(mCloseButtonList.get(index), index);
            });
        }
        return this;
    }

    public void refresh() {
        int size = mImageUrlList.size();
        if (size < 3) {
            binding.imageGroupRow1.setVisibility(VISIBLE);
            binding.imageGroupRow2.setVisibility(GONE);
            binding.imageGroupRow3.setVisibility(GONE);
        } else if (size < 6) {
            binding.imageGroupRow1.setVisibility(VISIBLE);
            binding.imageGroupRow2.setVisibility(VISIBLE);
            binding.imageGroupRow3.setVisibility(GONE);
        } else {
            binding.imageGroupRow1.setVisibility(VISIBLE);
            binding.imageGroupRow2.setVisibility(VISIBLE);
            binding.imageGroupRow3.setVisibility(VISIBLE);
        }
        for (int i = 0; i < size; i++) {
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
        for (int i = size + 1; i < mTotalCount; i++) {
            mImageViewList.get(i).setVisibility(INVISIBLE);
            mCloseButtonList.get(i).setVisibility(INVISIBLE);
        }
        if (size < mTotalCount) {
            if (mEditable) {
                mImageViewList.get(size).setImageResource(R.drawable.ic_add_image_gray_24dp);
                mImageViewList.get(size).setVisibility(VISIBLE);
            } else {
                mImageViewList.get(size).setVisibility(INVISIBLE);
            }
            mCloseButtonList.get(size).setVisibility(INVISIBLE);
        }
    }

}
