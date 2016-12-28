package com.chandy.customwidget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Chandy on 2016/12/27.
 */

public class CircleImageView extends View {
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private int mHeight;
    private int mWidth;
    private int paddingL;
    private int paddingT;
    private int paddingB;
    private int paddingR;
    private float radius;
    private boolean isDirty = false;
    private Paint mPaint;
    private Xfermode xfermode;
    private Path path;
    private Region region;
    private onImageClickListener mOnImageClickListener = null;

    public interface onImageClickListener {
        void click(View view);
    }

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mDrawable = a.getDrawable(R.styleable.CircleImageView_backgroud);
        paddingL=getPaddingLeft();
        paddingT=getPaddingTop();
        paddingR=getPaddingRight();
        paddingB=getBottom();
        if (null != mDrawable) {
            setBackgroundSrc(mDrawable);
        }

        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    }


    private void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        drawableToBitamp(drawable);
        invalidate();
    }

    private void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;

        invalidate();
    }

    public void setBackgroundSrc(Drawable drawable) {
        setDrawable(drawable);
    }

    public void setBackgroundSrc(Bitmap bitmap) {
        mDrawable = new BitmapDrawable(bitmap);
        setBitmap(bitmap);
    }

    private void drawableToBitamp(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        setBitmap(bd.getBitmap());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);

        if (hMode == MeasureSpec.EXACTLY || wMode == MeasureSpec.EXACTLY) {
            //padding后的大小
            mHeight = mWidth = h - paddingT - paddingB > w - paddingL - paddingR ? w - paddingL - paddingR : h - paddingT - paddingB;
            radius = mHeight / 2;
        }
        if (null != mBitmap) {
            scaleImage();
        }
        setMeasuredDimension(w, h);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        //1.判断是否设置了图片
        if (mDrawable != null && mBitmap != null) {
            isDirty = true;
        }
        //2.如果设置了图画的话
        if (isDirty) {
            int sc = canvas.saveLayer(0, 0, mWidth + paddingL + paddingR, mHeight + paddingT + paddingB, mPaint, Canvas.ALL_SAVE_FLAG);
            //dst
            canvas.drawBitmap(mBitmap, paddingL, paddingT, mPaint);
            //src
            mPaint.setXfermode(xfermode);
            canvas.drawBitmap(getDrawBitmap(), paddingL, paddingT, mPaint);
            mPaint.setXfermode(null);
            canvas.restoreToCount(sc);
        }
    }

    private void scaleImage() {

        setBitmap(Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, true));
        invalidate();

    }

    //生成遮挡bitmap
    private Bitmap getDrawBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        canvas.drawCircle(radius, radius, radius, paint);
        return bitmap;
    }

    private boolean isClicked(MotionEvent event) {
        path = new Path();
        path.addCircle(radius+paddingL, radius+paddingT, radius, Path.Direction.CW);
        region = new Region();
        region.setPath(path, new Region(paddingL, paddingT, mWidth, mHeight));
        if (region.contains((int) event.getX(), (int) event.getY())) {

            return true;
        }
        return false;

    }

    public void setOnImageClickListener(onImageClickListener Listener) {
        mOnImageClickListener = Listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != getmOnImageClickListener()&&isClicked(event)) {
                    mOnImageClickListener.click(this);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public onImageClickListener getmOnImageClickListener(){
        return mOnImageClickListener;
    }
}

