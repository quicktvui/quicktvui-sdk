package com.quicktvui.support.ui.viewpager.utils;

import com.quicktvui.support.ui.viewpager.tabs.FastListPageChangeListener;
import com.quicktvui.support.ui.viewpager.tabs.FastListScrollToTopListener;
import com.quicktvui.support.ui.viewpager.tabs.TabsView;

public class TabHelper {
    //tabsView接收处理event事件
    private TabsView tabsView;
    private FastListPageChangeListener fastListPageChangeListener;
    private FastListScrollToTopListener fastListScrollToTopListener;

    public TabHelper() {
    }

    public TabsView getTabsView() {
        return tabsView;
    }

    public void setTabsView(TabsView tabsView) {
        this.tabsView = tabsView;
    }

    public FastListPageChangeListener getFastListPageChangeListener() {
        return fastListPageChangeListener;
    }

    public void setFastListPageChangeListener(FastListPageChangeListener fastListPageChangeListener) {
        this.fastListPageChangeListener = fastListPageChangeListener;
    }

    public FastListScrollToTopListener getFastListScrollToTopListener() {
        return fastListScrollToTopListener;
    }

    public void setFastListScrollToTopListener(FastListScrollToTopListener fastListScrollToTopListener) {
        this.fastListScrollToTopListener = fastListScrollToTopListener;
    }
}
