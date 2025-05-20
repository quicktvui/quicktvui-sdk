package com.quicktvui.support.core.component.image;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.IEsTraceable;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;

public final class TransitionImageView extends View implements IEsComponentView {

    private Bitmap mPrevBitmap;
    private Bitmap mNextBitmap;

    private int mTransitionDuration = 1500;
    private int mRoundedCorner = 0;
    private int mShowAlpha = 255;
    private int mHideAlpha = 255;

    private final Paint mPaint;
    private final Rect mBitmapRect = new Rect();
    private final Rect mLocationRect = new Rect();

    private boolean mAttached = false;
    private boolean mNeedHideAnim = false;

    // 记录操作记录数
    private int mTargetResId = 0;

    private IEsTraceable mTraceable;

    private int mWidth = 0;
    private int mHeigh = 0;

    private final String TAG = TransitionImageView.class.getName();

    public TransitionImageView(IEsTraceable traceable, Context context) {
        super(context);
        mTraceable = traceable;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        mLocationRect.set(l, l, r, b);
        mWidth = r;
        mHeigh = b;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeigh = h;
//        Log.i(TAG,"onSizeChanged w :"+w+",h:"+h);
        postInvalidate();
    }

    public void setTransitionDuration(int duration) {
        mTransitionDuration = duration;
    }

    public void setRoundedCorner(int corner) {
        mRoundedCorner = corner;
    }

    private Bitmap createBitmapWithColor(int color) {
        Bitmap bitmap;
        if (mRoundedCorner > 0) {
            bitmap = Bitmap.createBitmap(mWidth, mHeigh, Bitmap.Config.ARGB_8888);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float width = bitmap.getWidth();
            final float height = bitmap.getHeight();
            final RectF rectF2 = new RectF(mRoundedCorner, mRoundedCorner, width - mRoundedCorner, height - mRoundedCorner);
            Canvas canvas = new Canvas(bitmap);
            paint.setAntiAlias(true);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, mRoundedCorner, mRoundedCorner, paint);
            canvas.drawBitmap(bitmap, rect, rectF2, paint);
        } else {
            bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    bitmap.setPixel(x, y, color);
                }
            }
        }
        return bitmap;
    }

    public void showNextColor(int color) {
        if (!mAttached) return;
        if (L.DEBUG) {
            Log.d(TAG, "showNextColor: --->" + color);
        }
        mNeedHideAnim = color == Color.TRANSPARENT;
        mTargetResId++;
        changeBitmap(createBitmapWithColor(color));
    }

    public void showNextImage(String url) {
        if (L.DEBUG) {
            Log.d(TAG, "showNextImage: --->" + "mAttached--->" + !mAttached);
        }
        if (!mAttached) return;
        mNeedHideAnim = TextUtils.isEmpty(url);
        mTargetResId++;
        fetchImageBitmap(url);
    }

    private void fetchImageBitmap(String url) {
        if (mTraceable == null) return;
        final int target = mTargetResId;
        EsMap data = new EsMap();
        data.pushString("url", url);
        EsProxy.get().loadImageBitmap(mTraceable, data, new EsCallback<Bitmap, Throwable>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if (target == mTargetResId) {
                    changeBitmap(bitmap);
                }
            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    private void changeBitmap(Bitmap bitmap) {
        if (mPrevBitmap != null) {
            mPrevBitmap.recycle();
        }
        mPrevBitmap = mNextBitmap;
        mNextBitmap = bitmap;
        startTransition();
    }

    private ValueAnimator mVShow;
    private ValueAnimator mVHide;

    private void startTransition() {
        if (mVShow != null) mVShow.cancel();
        if (mVShow == null) {
            mVShow = ValueAnimator.ofInt(0, 255).setDuration(mTransitionDuration);
            mVShow.addUpdateListener(a -> {
                mShowAlpha = (int) a.getAnimatedValue();
                invalidate();
                if (mShowAlpha == 255) {
                    if (mPrevBitmap != null) {
                        if (L.DEBUG) {
                            Log.d(TAG, "释放mPrevBitmap");
                        }
                        mPrevBitmap.recycle();
                    }
                }
            });
        }

        if (mVHide != null) mVHide.cancel();

        mHideAlpha = 255;
        if (mNeedHideAnim) {
            if (mVHide == null) {
                mVHide = ValueAnimator.ofInt(255, 0).setDuration(mTransitionDuration);
                mVHide.addUpdateListener(a -> {
                    mHideAlpha = (int) a.getAnimatedValue();
                    if (mRoundedCorner > 0) {
                        invalidate();
                    }
                    //Log.d(TAG, "mHideAlpha" + mHideAlpha);
                });
            }
            mVHide.start();
        }
        mVShow.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mAttached) return;
        mPaint.reset();
        if (mPrevBitmap != null && !mPrevBitmap.isRecycled()) {
            mPaint.setAlpha(mHideAlpha);
            mPaint.setAntiAlias(true);
            mBitmapRect.set(0, 0, mPrevBitmap.getWidth(), mPrevBitmap.getHeight());
            if (mRoundedCorner > 0) {
                RectF rectF2 = new RectF(mLocationRect);
                canvas.drawRoundRect(rectF2, mRoundedCorner, mRoundedCorner, mPaint);
                canvas.drawBitmap(mPrevBitmap, mBitmapRect, mLocationRect, mPaint);
            } else {
                canvas.drawBitmap(mPrevBitmap, mBitmapRect, mLocationRect, mPaint);
            }
        }

        if (mNextBitmap != null && !mNextBitmap.isRecycled()) {
            mPaint.setAlpha(mShowAlpha);
            mPaint.setAntiAlias(true);
            mBitmapRect.set(0, 0, mNextBitmap.getWidth(), mNextBitmap.getHeight());
            if (mRoundedCorner > 0) {
                RectF rectF2 = new RectF(mLocationRect);
                canvas.drawRoundRect(rectF2, mRoundedCorner, mRoundedCorner, mPaint);
                canvas.drawBitmap(mNextBitmap, mBitmapRect, mLocationRect, mPaint);
            } else {
                canvas.drawBitmap(mNextBitmap, mBitmapRect, mLocationRect, mPaint);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTargetResId = 0;
        //mTraceable = null;
        mAttached = false;
        if (mVShow != null) {
            mVShow.cancel();
            mVShow = null;
        }

//            if (mVHide != null) {
//                mVHide.cancel();
//                mVHide = null;
//            }

        if (mPrevBitmap != null) {
            mPrevBitmap.recycle();
            mPrevBitmap = null;
        }

        if (mNextBitmap != null) {
            mNextBitmap.recycle();
            mNextBitmap = null;
        }
    }
}