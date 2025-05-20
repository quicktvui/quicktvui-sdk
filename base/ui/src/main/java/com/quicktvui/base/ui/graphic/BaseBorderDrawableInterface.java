package com.quicktvui.base.ui.graphic;

import android.graphics.Canvas;
import android.view.View;

public interface BaseBorderDrawableInterface {

  //设置边框开关
  void setBorderVisible(boolean visible);

  //设置边框颜色
  void setBorderColor(int borderColor);

  //设置边框弧度
  void setBorderCorner(float roundCorner);

  //设置边框宽度
  void setBorderWidth(int width);

  void setBorderInset(int width);

  //设置黑色内边框开关
  void setBlackRectEnable(boolean blackRectEnable);

  //HippyImageView的draw回调
  void onDraw(Canvas canvas);

  //HippyImageView的onSizeChanged回调
  void onSizeChanged(int w, int h, int oldw, int oldh);

  //HippyImageView的onFocusChanged回调
  void onFocusChanged(View view, boolean visible);

  //HippyImageView的drawableStateChanged的回调
  void onDrawableStateChanged(View view, boolean focused);

  //HippyImageView的onDetachedFromWindow事件回调
  void onDetachedFromWindow(View view);

}
