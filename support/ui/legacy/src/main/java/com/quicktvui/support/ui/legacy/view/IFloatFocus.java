package com.quicktvui.support.ui.legacy.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;


public interface IFloatFocus{

    int DURATION = 300;

    String TAG = "FloatFocus";

//    void moveTo(int x,int y,int duration);
//
//    void sizeTo(float width, float height,int duration);
//
//    void alphaTo(float alpha,int duration);

//    void move(TVMoveReuqest reuqest,int duration);

//    void transform(int x,int y,int width,int height,float alpha,int duration);

    /**为fView获得需要移动至的位置和大小的信息
     * @return
     */
    void transformTo(ITVView ITVView, Point offset, float alpha, int duration);

    void getFrameRect(Rect rect);

    int getStrokeWidth();

    int getWidth();

    int getHeight();

    void offset(int dx ,int dy);

    void remove(TVRootView rootView);

    void show(int duration);

    void dismiss(int duration);

    void setVisible(boolean visible);

    void bringToFront();

    void frozen();

    View getView();

    void addToContainer(TVRootView rootView);
}
