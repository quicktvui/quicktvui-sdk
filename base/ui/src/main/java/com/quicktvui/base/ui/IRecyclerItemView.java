package com.quicktvui.base.ui;

import android.view.View;

public interface IRecyclerItemView {
  void onCreate(View rootList);
  void onBind(View rootList, int position, Object item);
  void onAttachToWindow(View rootList, int position, Object item);
  void onDetachFromWindow(View rootList, int position, Object item);
  void onUnBind(View rootList, int position, Object item);
  void setJSEventViewID(int JSEventViewID);
}
