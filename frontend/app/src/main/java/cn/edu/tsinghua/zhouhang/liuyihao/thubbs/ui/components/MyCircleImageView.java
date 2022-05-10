package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class MyCircleImageView extends MyImageView{

    private Paint mPaint;
    private Matrix mMatrix;
    public MyCircleImageView(Context context) {
        super(context);
        init();
    }

    public MyCircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//给画笔设置抗锯齿
        mMatrix = new Matrix();
        //该方法千万别放到onDraw()方法里面调用，否则会不停的重绘的，因为该方法调用了invalidate() 方法
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁用硬加速
    }

    //重写onDraw()方法获取BitmapDrawable进行处理
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            //通过BitmapShader的方式实现
            drawRoundByShaderMode(canvas, bitmap);
            //super.onDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    private void drawRoundByShaderMode(Canvas canvas, Bitmap bitmap) {
        //获取到Bitmap的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        //获取到ImageView的宽高
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        //根据Bitmap生成一个BitmapShader，这里有个TileMode设置有三个参数可以选择：
        //CLAMP, MIRROR, REPEAT 在后面会细说。
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.reset();

        float minScale = Math.min(viewWidth / (float) bitmapWidth, viewHeight / (float) bitmapHeight);
        mMatrix.setScale(minScale, minScale);//将Bitmap保持比列根据ImageView的大小进行缩放
        bitmapShader.setLocalMatrix(mMatrix); //将矩阵变化设置到BitmapShader,其实就是作用到Bitmap
        mPaint.setShader(bitmapShader);

        bitmapWidth *= minScale;
        bitmapHeight *= minScale;
        //绘制圆形
        canvas.drawCircle(bitmapWidth/2, bitmapHeight / 2, Math.min(bitmapWidth / 2, bitmapHeight / 2), mPaint);
    }
}
