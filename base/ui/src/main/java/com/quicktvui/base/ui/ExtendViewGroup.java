package com.quicktvui.base.ui;

import android.view.View;

/**
 * add by zhaopeng 20201227
 */
public interface ExtendViewGroup {
    int AUTOFOCUS_TYPE_VISIBILITY = 0;
    int AUTOFOCUS_TYPE_SIZE_VALID = 1;
    int AUTOFOCUS_TYPE_ATTACH = 2;
    int AUTOFOCUS_TYPE_FORCE = 3;

  void setDispatchChildFocusEvent(boolean enable);
  void changePageHidden(boolean hidden);
  boolean isPageHidden();
  void onRequestAutofocus(View child, View target, int type);
  void setSkipRequestFocus(boolean b);
  String ROOT_TAG = "ESRoot";

  static String getAutofocusTypeString(int type){
    switch (type){
      case AUTOFOCUS_TYPE_VISIBILITY:
        return "AUTOFOCUS_TYPE_VISIBILITY";
      case AUTOFOCUS_TYPE_SIZE_VALID:
        return "AUTOFOCUS_TYPE_SIZE_VALID";
      case AUTOFOCUS_TYPE_ATTACH:
        return "AUTOFOCUS_TYPE_ATTACH";
      case AUTOFOCUS_TYPE_FORCE:
        return "AUTOFOCUS_TYPE_FORCE";
      default:
        return "UNKNOWN";
    }
  }
}

