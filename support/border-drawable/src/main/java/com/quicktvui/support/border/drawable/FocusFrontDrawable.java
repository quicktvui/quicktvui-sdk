package com.quicktvui.support.border.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



public class FocusFrontDrawable extends Drawable {

    Paint mPaint = new Paint();

    Paint mPaintBg = new Paint();

    int strokeWidth = 3;

    int blackStroke = 2;

    float roundCorner ;

    float blackRoundCorner ;

    RectF drawRect = new RectF();
    RectF blackRect = new RectF();
    boolean isBlackRectEnable = true;
    public static int FOCUS_INSET = 0;

    float frameInterval = 0.5f;
    private boolean borderVisible=true;

    public FocusFrontDrawable() {

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaintBg.setAntiAlias(true);
        mPaintBg.setColor(Color.BLACK);
        mPaintBg.setStyle(Paint.Style.STROKE);
        mPaintBg.setStrokeWidth(strokeWidth);

        roundCorner  = 8;
        blackRoundCorner = Math.max(0,roundCorner - 2);
    }

  public void setBlackRectEnable(boolean blackRectEnable) {
    isBlackRectEnable = blackRectEnable;
    invalidateSelf();
  }

  @Override
    public void draw(@NonNull Canvas canvas) {
      if(isVisible()&&borderVisible) {
        if(isBlackRectEnable) {
          canvas.drawRoundRect(blackRect, blackRoundCorner, roundCorner, mPaintBg);
        }
        canvas.drawRoundRect(drawRect, roundCorner, roundCorner, mPaint);
      }
    }




  @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left , top  ,  right , bottom );
    }



    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        blackRect.set(bounds.left,bounds.top,bounds.right,bounds.bottom);
        if(isBlackRectEnable) {
          blackRect.inset(3 + FOCUS_INSET, 3 + FOCUS_INSET);
          drawRect.set(blackRect);
          drawRect.inset(1 - strokeWidth,1 - strokeWidth);
        }else{
          drawRect.set(blackRect);
          drawRect.inset(4 - strokeWidth,4 - strokeWidth);
        }
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

    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
        invalidateSelf();
    }

  public void setRoundCorner(float roundCorner) {
    this.roundCorner = roundCorner;
    invalidateSelf();
  }

  public void setFocusBorderWidth(int width) {
    this.strokeWidth = width;
    mPaint.setStrokeWidth(width);
    invalidateSelf();
  }


}
