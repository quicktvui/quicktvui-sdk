package com.quicktvui.support.ui.legacy.graphics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.quicktvui.support.ui.legacy.FConfig;


public class LoadingDrawable extends Drawable {

    int size = -1;

    Paint mPaint;

    RectF mRect;

    float mStroke = 10;

    float mCustomStroke = 0;

    float mSweepAngle = MIN_SWEEP;

    float mStartAngle = INIT_ANGLE;


    final static float MIN_SWEEP = 30;

    final static float INIT_ANGLE = 270 - MIN_SWEEP;

    final static int ROTATION_SPEED = 5;

    final int mDuration = 1600;

    final int MIN_STROKE = 1;
    final int MAX_STROKE = 12;

    Animator mRotatetAnim;

    View mAttachedView;

    int mRotation = 0;

    final static boolean DEBUG = FConfig.DEBUG;

    public LoadingDrawable(View attached){
        mPaint = new Paint();

        mPaint.setColor(Color.WHITE);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStroke);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        this.mAttachedView = attached;


    }

    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        return super.onLayoutDirectionChanged(layoutDirection);
    }



    @Override
    protected void onBoundsChange(Rect bounds) {
        if(DEBUG){
            Log.v("FLoadingDrawableTest","onBoundsChange bounds is "+bounds+" this is "+this);
        }
        setSize(Math.min(bounds.width(),bounds.height()));
        super.onBoundsChange(bounds);
    }

    public void setStroke(int stroke){
        if(DEBUG){
            Log.v("FLoadingDrawableTest","setmCustomStroke is "+stroke);
        }
        this.mCustomStroke = stroke;
    }

    void setSize(int size){


        this.size = size;

        updateStorkeWhenSizeChanged();

        final float start = mStroke;
        final float end = size - mStroke;

        mRect = new RectF();
        mRect.set(start,start,end,end);

        if(DEBUG){
            Log.v("FLoadingDrawableTest","setSize size is "+size+" mStroke is "+mStroke+" outRect is "+mRect);
        }


    }

    public void relaylout(){
        if(size > 0){
            setSize(size);
        }
        if(DEBUG){
            Log.v("FLoadingDrawableTest","relaylout size is "+size+" mStroke is "+mStroke+" outRect is "+mRect);
        }
    }

    void updateStorkeWhenSizeChanged(){

        if(mCustomStroke > 0){
            mStroke = mCustomStroke;
        }else{
            mStroke = size / 8f;
            mStroke = Math.min(MAX_STROKE,mStroke);
            mStroke = Math.max(MIN_STROKE,mStroke);
        }

        mPaint.setStrokeWidth(mStroke);

        if(DEBUG){
            Log.v("FLoadingDrawableTest","updateStorkeWhenSizeChanged mCustomStroke is "+mCustomStroke+" mStroke is "+mStroke+" this is "+this);
        }

        invalidateSelf();

    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        if(!visible){
            if(mRotatetAnim != null){
                mRotatetAnim.cancel();
                mRotatetAnim = null;
            }
        }else{
            startRotateAnimation();
        }
        return super.setVisible(visible, restart);
    }

    public void setColor(int color){
        mPaint.setColor(color);
    }


    public void startRotateAnimation(){
        mSweepAngle = MIN_SWEEP;
        mStartAngle = INIT_ANGLE;
        mRotation = 0;
        if(mRotatetAnim != null){
            mRotatetAnim.cancel();
            mRotatetAnim = null;
        }
        invalidateSelf();
    }


    protected Animator genratePastAnimator(){

        AnimatorSet set = new AnimatorSet();

        final int mDuration = this.mDuration / 2;


        ValueAnimator firstPart = ValueAnimator.ofFloat(MIN_SWEEP,240);
        firstPart.setDuration(mDuration);

        firstPart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mSweepAngle = (float) animation.getAnimatedValue();
                invalidateDelay();
            }
        });

        firstPart.setInterpolator(new AccelerateInterpolator(1));

        AnimatorSet secondPart = new AnimatorSet();

        ValueAnimator secondPartSweep = ValueAnimator.ofFloat(240,MIN_SWEEP);
        secondPartSweep.setDuration(mDuration);

        secondPartSweep.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = (float) animation.getAnimatedValue();
                mSweepAngle = value;
            }
        });


        ValueAnimator secondPartStartAngle = ValueAnimator.ofFloat(INIT_ANGLE,INIT_ANGLE + 360);

        secondPartStartAngle.setDuration(mDuration);

        secondPart.setInterpolator(new DecelerateInterpolator(1));

        secondPartStartAngle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartAngle = (float) animation.getAnimatedValue();
                invalidateDelay();
            }
        });


        secondPart.playTogether(secondPartSweep,secondPartStartAngle);


        set.playSequentially(firstPart,secondPart);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRotatetAnim = null;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });



        return set;
    }


    private void invalidateDelay(){
        if(mAttachedView != null){
            mAttachedView.postInvalidateDelayed(16);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        if(mRect != null && isVisible()) {

            int saveCount = canvas.save();

            if (mRotatetAnim == null) {
                mRotatetAnim = genratePastAnimator();
                mRotatetAnim.start();
            }

            canvas.rotate(mRotation,size * 0.5f,size * 0.5f);

            mRotation += ROTATION_SPEED;

            mRotation %= 360;

            canvas.drawArc(mRect, mStartAngle,  Math.max(MIN_SWEEP,mSweepAngle), false, mPaint);

            canvas.restoreToCount(saveCount);
        }

    }

    /**
     * Specify an alpha value for the drawable. 0 means fully transparent, and
     * 255 means fully opaque.
     *
     * @param alpha
     */
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        //do nothing
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }

}
