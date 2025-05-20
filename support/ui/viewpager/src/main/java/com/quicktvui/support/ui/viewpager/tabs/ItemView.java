package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.view.View;

import com.quicktvui.hippyext.views.fastlist.FastItemView;

public class ItemView extends FastItemView {
    private TabsItemNode tabsItemNode;

    public ItemView(Context context) {
        super(context);
    }

    @Override
    public void notifyResumeTask() {
        super.notifyResumeTask();
        if (tabsItemNode != null) {
            tabsItemNode.notifyResumeTask();
        }
    }

    @Override
    public void onAttachToWindow(View parent, int position, Object item) {
        super.onAttachToWindow(parent, position, item);
        if (tabsItemNode != null) {
            tabsItemNode.attachToWindow(parent, position, item);
        }
    }

    @Override
    public void onDetachFromWindow(View parent, int position, Object item) {
        super.onDetachFromWindow(parent, position, item);
        if (tabsItemNode != null) {
            tabsItemNode.detachFromWindow(parent, position, item);
        }
    }

    @Override
    public void onBind(View parent, int position, Object item) {
        super.onBind(parent, position, item);
        if (tabsItemNode != null) {
            tabsItemNode.onBind(parent, position, item);
        }
    }

    @Override
    public void notifyPauseTask() {
        super.notifyPauseTask();
        if (tabsItemNode != null) {
            tabsItemNode.notifyPauseTask();
        }
    }

    public void setTabsItemNode(TabsItemNode tabsItemNode) {
        this.tabsItemNode = tabsItemNode;
    }
}
