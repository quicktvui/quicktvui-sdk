package com.quicktvui.base.ui;

import android.graphics.Rect;
import android.view.ViewParent;

/**
 * @author zhaopeng
 * @version 1.0
 * @title ITVView
 * @description 所有View原则上必须实现的接口，baseui组件里的最底层接口。
 * @company
 * @created 16/3/15
 * @changeRecord [修改记录] <br></br>
 * 20160317:添加onHandleFocusScale方法，用来处理焦点获取和丢失时的放大和缩小动画
 */

public interface ITVView extends TVBaseView{
    int FOCUS_INVALID = -1;
    IFloatFocusManager getFloatFocusManager();
    AttachInfo getAttachInfo();
    Rect getFloatFocusMarginRect();
    ViewParent getParent();

    int getWidth();
    int getHeight();
    /***
     * 获得获得焦点时设置的横向放大倍数
     * @return
     */
    /**
     * 设置View获得焦点的横向放大倍数
     * @return
     */
    void setFocusScaleX(float scale);

    /***
     * 获得获得焦点时设置的竖向放大倍数
     * @return
     */
    /**
     * 设置View获得焦点的竖向放大倍数
     * @return
     */
    void setFocusScaleY(float scale);

    float getFocusScaleX();

    float getFocusScaleY();

    void setFillParent(boolean b);
    boolean isFillParent();
    void notifyInReFocus(boolean isIn);
    boolean isInReFocus();

    /**
     * 设置View获得焦点的放大动画的时间，单位ms
     * @return
     */
    void setFocusScaleDuration(int duration);

    /**处理焦点得到与失去时，放大的缩小view的处理,默认处理方式为：<br></br>
     * 只在isFocusable为true时，才执行放大逻辑，否则不处理。
     *
     * @param gainFocus
     * @param direction
     * @param previouslyFocusedRect
     */
    void onHandleFocusScale(boolean gainFocus, int direction, Rect previouslyFocusedRect);

    /**
     * 移动方向（前一个、下一个，上一行，下一行）
     */
    enum TVMovement {
        PREV_ITEM,
        NEXT_ITEM,
        PREV_ROW,
        NEXT_ROW,
        INVALID
    }

    /**
     * 方向，横向，竖向
     */
    enum TVOrientation {
        HORIZONTAL,
        VERTICAL
    }

    void setFloatFocusFocusedAlpha(float alpha);
    void notifyBringToFront(boolean b);
}
