package com.quicktvui.support.ui.item.host;

import android.graphics.Rect;
import android.view.View;

import com.quicktvui.support.ui.render.RenderNode;


public interface ItemHostView {


    ItemHostView addWidget(RenderNode widget);


    ItemHostView addWidgetToBack(RenderNode widget);

    View getHostView();

    void changeSize(int width, int height);

    int getWidth();
    int getHeight();

    <T> T as();

    <T> T findIWidget(String Name);


    void callFocusChange(boolean gainFocus);


    interface FocusChangeListener{
        void onFocusChanged(View v,boolean gainFocus, int direction, Rect previouslyFocusedRect);
    }

    interface OnHostViewSizeChangeListener{
        void onSizeChanged(ItemHostView hostView,int width,int height);
    }

    void setOnHostViewSizeChangeListener(OnHostViewSizeChangeListener listener);


    void setFocusChangeListener(FocusChangeListener focusChangeListener);
}
