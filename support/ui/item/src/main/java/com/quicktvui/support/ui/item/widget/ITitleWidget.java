package com.quicktvui.support.ui.item.widget;

import android.graphics.Rect;

import android.support.annotation.Nullable;




public interface ITitleWidget extends IWidget{

    //必须唯一名称
    String NAME = "Bar";

    void setVisible(boolean visible);

    void cancelLoadText();

    int height();

    void setTitle(@Nullable String title);

//    void setTitleColor(int color);

//    void setTitleTextSize(int unit, float size);

    void changeFocus(boolean gainFocus, int direction, Rect previouslyFocusedRect);

    @Nullable String getTitleText();


    void setSubTitle(@Nullable String bar_text);

//    void setSubTitleColor(int color);

//    void setSubTitleTextSize(int unit, float size);

    @Nullable String getSubTitleText();
}
