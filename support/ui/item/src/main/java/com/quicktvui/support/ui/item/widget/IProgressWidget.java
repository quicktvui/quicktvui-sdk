package com.quicktvui.support.ui.item.widget;


public interface IProgressWidget extends IWidget{

    //必须唯一名称
    String NAME = "Progress";

    void setProgress(int progress);
    void setMarginBottom(int margin);
    void setProgressBarHeight(int height);

    void setProgressColor(int color);
    void setProgressShadowColor(int shadowColor);
    void setProgressCornerRadius(float radius);
    void setProgressVisible(boolean visible);
}
