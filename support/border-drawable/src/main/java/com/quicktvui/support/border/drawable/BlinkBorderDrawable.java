package com.quicktvui.support.border.drawable;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.quicktvui.base.ui.graphic.BaseBorderDrawable;


public class BlinkBorderDrawable extends BaseBorderDrawable {

    Paint mPaint = new Paint();

    Paint mPaintBg = new Paint();

    int strokeWidth = 3;

    int blackStroke = 2;

    float roundCorner;

    float blackRoundCorner;

    RectF drawRect = new RectF();
    RectF blackRect = new RectF();
    RectF bounds = new RectF();
    boolean isBlackRectEnable = false;
    public static int FOCUS_INSET = 0;

    float frameInterval = 0.5f;
    private boolean borderVisible = true;
    private ObjectAnimator alphaAnim = null;
    private float frameIndex = 1f;
    private boolean isCircle = false;

    public BlinkBorderDrawable() {

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaintBg.setAntiAlias(true);
        mPaintBg.setColor(Color.BLACK);
        mPaintBg.setStyle(Paint.Style.STROKE);
        mPaintBg.setStrokeWidth(strokeWidth);

        roundCorner = 8;
        blackRoundCorner = Math.max(0, roundCorner - 2);
    }

    public void setBlackRectEnable(boolean blackRectEnable) {
        isBlackRectEnable = blackRectEnable;
        invalidateSelf();
    }

    @Override
    public void onDraw(Canvas canvas) {
        draw(canvas);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        setBounds(0, 0, w, h);
    }

    @Override
    public void onFocusChanged(View view, boolean visible) {
        setVisible(visible, false);
        if (visible){
            startAlpha();
        }else{
            cancelAlpha();
        }
    }

    @Override
    public void onDrawableStateChanged(View view, boolean focused) {
        setVisible(focused, false);
        if (focused){
            startAlpha();
        }else{
            cancelAlpha();
        }
    }

    @Override
    public void onDetachedFromWindow(View view) {
        cancelAlpha();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int alpha = (int) (255 * frameIndex);
        mPaint.setAlpha(Math.min(255, alpha));
        mPaintBg.setAlpha(Math.min(255, alpha));
        if (isVisible() && borderVisible) {
            if (isCircle) {
                canvas.drawCircle(bounds.width() / 2, bounds.height() / 2, Math.min(bounds.width(), bounds.height()) / 2 + strokeWidth / 2, mPaint);
            } else {
                if (isBlackRectEnable) {
                    canvas.drawRoundRect(blackRect, roundCorner, roundCorner, mPaintBg);
                }
                canvas.drawRoundRect(drawRect, roundCorner, roundCorner, mPaint);
            }
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    public void startAlpha() {
        if (alphaAnim != null && alphaAnim.isRunning()) {
            return;
        }
        if (alphaAnim == null) {
            alphaAnim = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
            alphaAnim.setDuration(700);
            alphaAnim.setRepeatMode(ValueAnimator.REVERSE);
            alphaAnim.setRepeatCount(ValueAnimator.INFINITE);
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    setFrame(animatedValue);
                }
            });
            alphaAnim.start();
        } else {
            alphaAnim.start();
        }
    }

    public void cancelAlpha() {
        if (alphaAnim != null) {
            alphaAnim.cancel();
            setFrame(0);
        }
    }

    private void setFrame(float frame) {
        frameIndex = frame;
        invalidateSelf();
    }


    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        bounds = new RectF(left, top, right, bottom);
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        blackRect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        drawRect.set(bounds.left - strokeWidth / 2, bounds.top - strokeWidth / 2, bounds.right + strokeWidth / 2, bounds.bottom + strokeWidth / 2);
        invalidateSelf();
    }


    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }


    public void setBorderColor(int borderColor) {
        mPaint.setColor(borderColor);
        invalidateSelf();
    }

    @Override
    public void setBorderCorner(float roundCorner) {
        this.roundCorner = roundCorner;
        invalidateSelf();
    }

    @Override
    public void setBorderWidth(int width) {
        this.strokeWidth = width;
        mPaint.setStrokeWidth(width);
        invalidateSelf();
    }

    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
        invalidateSelf();
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
        invalidateSelf();
    }
}
