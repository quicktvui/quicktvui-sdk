package com.quicktvui.base.ui.graphic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;


/**
 * @author zhaopeng
 * @version 1.0
 * @title
 * @description
 * @company
 * @created 2018/3/20
 * @changeRecord [修改记录] <br/>
 * 2018/3/20 ：created
 */


public class BorderFrontDrawable extends BaseBorderDrawable {
  Paint mPaint = new Paint();
  Paint mPaintBg = new Paint();
  int strokeWidth = 3;
  int focus_inset = 0;
  float roundCorner;
  float blackRoundCorner;
  RectF drawRect = new RectF();
  RectF blackRect = new RectF();
  boolean isBlackRectEnable = true;
  @Deprecated
  public static int FOCUS_INSET = 0;
  float frameInterval = 0.5f;
  private boolean borderVisible = true;

  public BorderFrontDrawable() {
    super();
    mPaint.setAntiAlias(true);
    mPaint.setColor(Color.WHITE);
    mPaint.setStrokeWidth(strokeWidth);
    mPaint.setStyle(Paint.Style.STROKE);

    mPaintBg.setAntiAlias(true);
    mPaintBg.setColor(Color.BLACK);
    mPaintBg.setStyle(Paint.Style.STROKE);
    mPaintBg.setStrokeWidth(3);

    roundCorner = 8;
    blackRoundCorner = Math.max(0, roundCorner - 3);
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
  }

  @Override
  public void onDrawableStateChanged(View view, boolean focused) {
    setVisible(focused, false);
  }

  @Override
  public void onDetachedFromWindow(View view) {
    setVisible(false, false);
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    if (isVisible() && borderVisible) {
      if (isBlackRectEnable) {
        canvas.drawRoundRect(blackRect, blackRoundCorner, blackRoundCorner, mPaintBg);
      }
      canvas.drawRoundRect(drawRect, roundCorner, roundCorner, mPaint);
    }
  }

  @Override
  public void setBounds(int left, int top, int right, int bottom) {
    super.setBounds(left, top, right, bottom);
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

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    blackRect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
    if(focus_inset != 0){
      if (isBlackRectEnable) {
        //3是黑色边框的宽度
        blackRect.inset(3 + focus_inset, 3 + focus_inset);
        drawRect.set(blackRect);
        drawRect.inset( - strokeWidth , - strokeWidth);
      } else {
        drawRect.set(blackRect);
        drawRect.inset(focus_inset, focus_inset);
        drawRect.inset(4 - strokeWidth, 4 - strokeWidth);
      }
    }else {
      //兼容之前版本逻辑
      if (isBlackRectEnable) {
        blackRect.inset(3 + FOCUS_INSET, 3 + FOCUS_INSET);
        drawRect.set(blackRect);
        drawRect.inset(1 - strokeWidth, 1 - strokeWidth);
      } else {
        drawRect.set(blackRect);
        drawRect.inset(FOCUS_INSET, FOCUS_INSET);
        drawRect.inset(4 - strokeWidth, 4 - strokeWidth);
      }
    }
    invalidateSelf();
  }

  public void setBorderColor(int borderColor) {
    mPaint.setColor(borderColor);
    invalidateSelf();
  }

  public void setBorderVisible(boolean visible) {
    this.borderVisible = visible;
    invalidateSelf();
  }

  public void setBorderCorner(float roundCorner) {
    this.roundCorner = roundCorner;
    this.blackRoundCorner = Math.max(0, roundCorner - 3);
    invalidateSelf();
  }

  public void setBorderWidth(int width) {
    this.strokeWidth = width;
    mPaint.setStrokeWidth(width);
    invalidateSelf();
  }

  @Override
  public void setBorderInset(int inset) {
    this.focus_inset = inset;
    invalidateSelf();
  }


}
