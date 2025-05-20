package com.quicktvui.base.ui;

import android.view.View;

public interface TVBaseView {

  boolean isAutoFocus();
  void setAutoFocus(boolean b);
  void setAutoFocus(boolean b,boolean requestFocusOnExit);
  View getView();
  /**
   * 设置View获得焦点的整体放大倍数
   * @return
   */
  void setFocusScale(float scale);
  float getFocusScale();
  void setSkipRequestFocus(boolean b);
  void onSetSid(String sid);
}
