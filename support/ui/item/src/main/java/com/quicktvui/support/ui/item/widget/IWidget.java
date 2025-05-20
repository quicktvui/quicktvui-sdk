package com.quicktvui.support.ui.item.widget;


import android.view.View;

import com.quicktvui.support.ui.render.RenderNode;


public interface IWidget {

    void setWidgetScale(float scale);

    void onFocusChange(boolean gainFocus);

    int width();
    int height();

    int getY();
    int getX();

    RenderNode getRenderNode();

    void onViewDetachedFromWindow(View view);
    void onViewAttachedToWindow(View view);

}
