package com.quicktvui.support.ui.legacy.widget;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.quicktvui.support.ui.legacy.view.ITVView;
import com.quicktvui.support.ui.legacy.view.ITVViewGroup;


public interface ITVLayoutManager extends TVRecyclerView.ShakeEndCallback{

    void requestMoveFloatFocus(ITVViewGroup viewGroup, ITVView child, ITVView focused);


    Point handleRequestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate,boolean focusedChildVisible);


    int getItemTotalCount();

}
