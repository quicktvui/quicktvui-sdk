package com.quicktvui.base.ui;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

/**
 * @author zhaopeng
 * @version 1.0
 * @title
 * @description
 * @company
 * @created 16/5/4
 * @changeRecord [修改记录] <br/>
 * 16/5/4 ：created
 */

public interface IFloatFocus {

    int DURATION = 300;

    String TAG = "FloatFocus";
    /**为fView获得需要移动至的位置和大小的信息
     * @return
     */
    void transformTo(ITVView ITVView, Point offset, float alpha, int duration);

    void getFrameRect(Rect rect);

    int getStrokeWidth();

    int getWidth();

    int getHeight();

    void offset(int dx, int dy);

//    void remove(TVRootView rootView);

    void show(int duration);

    void dismiss(int duration);

    void setVisible(boolean visible);

    void bringToFront();

    void frozen();

    View getView();

}
