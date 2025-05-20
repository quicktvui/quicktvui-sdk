package com.quicktvui.support.ui.selectseries.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import android.support.annotation.NonNull;

import com.quicktvui.support.ui.largelist.EasyListView;
import com.quicktvui.support.ui.selectseries.SelectSeriesViewGroup;

@SuppressLint("ViewConstructor")
public class GroupListView extends EasyListView {
    private int lastCheckPos = -1;

    private final SelectSeriesViewGroup parent;

    public GroupListView(@NonNull Context context, SelectSeriesViewGroup parent) {
        super(context, false);
        this.parent = parent;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        parent.event.notifyGroupPositionEvent(mFocusChildPosition);
        if (lastCheckPos > -1 && lastCheckPos != mFocusChildPosition) {
            parent.onGroupChange(mFocusChildPosition);
        }
        lastCheckPos = mFocusChildPosition;
        parent.lastFocusItem = false;
    }

    @Override
    public View onInterceptFocusSearch(@NonNull View focused, int direction) {
        View v = null;
        if (parent.mParam.blockFocus && direction == View.FOCUS_LEFT && mFocusChildPosition == 0) {
            return focused;
        }
        if (direction == (parent.groupUp ? View.FOCUS_DOWN : View.FOCUS_UP)) {
            v = parent.largeListView.getEasyLayoutManager().findViewByPosition(parent.mData.targetItemPos);
        }
        if (v == null) {
            v = super.onInterceptFocusSearch(focused, direction);
        }

        return v;
    }
}
