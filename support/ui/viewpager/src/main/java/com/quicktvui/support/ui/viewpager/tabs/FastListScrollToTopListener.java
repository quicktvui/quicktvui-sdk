package com.quicktvui.support.ui.viewpager.tabs;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.RecyclerView;

import com.quicktvui.hippyext.views.fastlist.FastListView;


import com.tencent.mtt.hippy.common.HippyMap;

import com.quicktvui.support.ui.viewpager.utils.TabEnum;

public class FastListScrollToTopListener extends RecyclerView.OnScrollListener {
    private TabsView tabsView;
    private int mCurrPosition;

    public FastListScrollToTopListener(TabsView tabsView, int mCurrPosition) {
        this.tabsView = tabsView;
        this.mCurrPosition = mCurrPosition;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //触发了滑动到顶部
        if (tabsView != null && recyclerView.getFocusedChild() != null) {
            if (recyclerView.getChildAdapterPosition(recyclerView.getFocusedChild()) != 0) {
                HippyMap map = new HippyMap();
                //当前是第几页
                map.pushInt("pageIndex", mCurrPosition);
                ((FastListView) recyclerView).sendScrollEvent(tabsView, TabEnum.ON_SCROLLTO_START.getName(), map);
            }
        }
    }
}
