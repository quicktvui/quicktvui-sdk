package com.quicktvui.support.ui.legacy.view;

import android.graphics.Point;
import android.view.View;


public interface IScrollView {
     /**完成计算浮动焦点移动偏移量,复写此方法，可以最后一次机会调整浮动窗口移动的位置
      * @param focused
      * @param offsetResult
      */
     void onFLoatFocusMoveOffsetCaculated(final View child,final View focused,final Point offsetResult);

}
