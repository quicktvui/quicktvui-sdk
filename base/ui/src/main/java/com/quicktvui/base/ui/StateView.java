package com.quicktvui.base.ui;

public interface StateView {


  void setShowOnState(int[] showOnState);
  void setShowOnCustomState(String[] showOnState);

  void setCustomState(String state,boolean on);

  int STATE_FOCUS_AND_SELECT = 100;
  int STATE_FOCUS_NO_SELECT = STATE_FOCUS_AND_SELECT + 1;
  int STATE_SELECT_NO_FOCUS = STATE_FOCUS_AND_SELECT + 2;

}
