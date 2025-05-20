package com.quicktvui.support.rippleview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.quicktvui.sdk.base.component.IEsComponentView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XingRuGeng on 2021/12/15
 * Desc:WaterRippleView
 */
public class RippleView extends View implements IEsComponentView {

    private static final String TAG = RippleView.class.getSimpleName();
    // 画笔颜色
    private int mColor;
    //个数
    private int rippleCount = 3;
    private List<Circle> mRipples;

    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(String color, String imgPath) {
        this.mColor = Color.parseColor(color);
        setFocusable(false);
//        setCircle();
    }

    protected void setCircle() {
        if (mRipples != null) {
            mRipples.clear();
        }
        mRipples = new ArrayList<>(rippleCount);
        int[] delays = new int[]{0, 500, 1000};
        int[] startPs = new int[]{0, 1, 2};
        int[] duration = new int[]{2200, 2200, 2200};

        for (int i = 0; i < rippleCount; i++) {
            Circle circle = new Circle(this, delays[i], startPs[i], duration[i]);
            mRipples.add(circle);
            circle.startAnim();
        }
    }

    void requestUpdate() {
        startAnim();
    }

    void startAnim() {
        if (getVisibility() == View.VISIBLE && getAlpha() != 0 && getWidth() > 0 && getHeight() > 0) {
            setCircle();
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        requestUpdate();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (View.VISIBLE == visibility) {
            requestUpdate();
        } else {
            stopAnim();
        }
    }

    void stopAnim() {
        if (mRipples != null) {
            for (int i = 0; i < mRipples.size(); i++) {
                mRipples.get(i).stopAnim();
            }
        }
        mRipples = null;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mRipples != null) {
            for (int i = 0; i < mRipples.size(); i++) {
                mRipples.get(i).draw(canvas);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }


    class Circle implements ValueAnimator.AnimatorUpdateListener {
        RectF rect;
        Animator anim;
        View view;
        Paint paint;
        int delay;
        int startP;
        int duration;
        float alpha = 1;

        Circle(View view, int delay, int startP, int duration) {
            this.view = view;
            this.delay = delay;
            this.startP = startP;
            this.duration = duration;
            paint = new Paint();
            paint.setColor(mColor);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            rect = new RectF();
        }

        void draw(Canvas canvas) {
            if (rect != null) {
                canvas.save();
                canvas.translate(getWidth() * 0.5f - rect.width() * 0.5f, getHeight() * 0.5f - rect.height() * 0.5f);
                canvas.drawArc(rect, 0, 360, true, paint);
                canvas.restore();
            }
        }

        void startAnim() {
            if (anim != null) {
                anim.cancel();
                anim = null;
            }

            //透明度
            @SuppressLint("ObjectAnimatorBinding")
            PropertyValuesHolder valueHolder = PropertyValuesHolder.ofFloat("", 0, getWidth());
            @SuppressLint("ObjectAnimatorBinding")
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat("alpha", 1, 0);
            //旋转1/3圈的动画
            @SuppressLint("ObjectAnimatorBinding")
//            PropertyValuesHolder rotationHolder = PropertyValuesHolder.ofFloat("rotation", 0,  -120);

            ValueAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(valueHolder, alphaHolder);
            objectAnimator.setDuration(duration);
            objectAnimator.setStartDelay(delay);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            objectAnimator.addUpdateListener(this);
            objectAnimator.start();

            this.anim = objectAnimator;
        }

        public void stopAnim() {
            if (anim != null) {
                anim.cancel();
                anim = null;
            }
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float value = (float) animation.getAnimatedValue();
            float alpha = (float) animation.getAnimatedValue("alpha");
            this.alpha = alpha;
            paint.setAlpha((int) (255 * alpha));
            rect.left = 0;
            rect.top = 0;
            rect.right = value;
            rect.bottom = value;
            view.postInvalidateDelayed(16);
        }

    }
}
