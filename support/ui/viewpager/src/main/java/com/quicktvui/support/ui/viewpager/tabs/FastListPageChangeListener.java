package com.quicktvui.support.ui.viewpager.tabs;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.support.annotation.NonNull;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.tencent.mtt.hippy.common.HippyMap;

import com.quicktvui.support.ui.viewpager.utils.TabEnum;
import com.quicktvui.support.ui.viewpager.utils.TabUtils;

/**
 * fastlist滑动事件
 */
public class FastListPageChangeListener extends RecyclerView.OnScrollListener {
    private int totalDy = 0;
    //接收事件的view
    private View tabsView;
    private View suspensionView;
    private int suspensionTop;
    //是否开启顶部吸顶功能
    private boolean isSuspension;
    //是否使用吸顶默认背景色
    private boolean useSuspensionBg;
    private boolean isOnTop = false;

    private boolean isHideOnSingleTab;
    private float checkOffset;
    private int mCurrPosition;
    private boolean listenScrollEvent;

    public FastListPageChangeListener(View tabsView, boolean isSuspension, boolean useSuspensionBg, boolean isHideOnSingleTab,
                                      float checkOffset, int position, boolean listenScrollEvent) {
        this.tabsView = tabsView;
        this.isSuspension = isSuspension;
        this.useSuspensionBg = useSuspensionBg;
        this.isHideOnSingleTab = isHideOnSingleTab;
        this.suspensionView = null;
        this.checkOffset = checkOffset;
        this.mCurrPosition = position;
        this.listenScrollEvent = listenScrollEvent;
    }

    public FastListPageChangeListener(View tabsView, View suspensionView, boolean isSuspension, boolean useSuspensionBg,
                                      boolean isHideOnSingleTab, float checkOffset, int position, boolean listenScrollEvent) {
        this.tabsView = tabsView;
        this.isSuspension = isSuspension;
        this.useSuspensionBg = useSuspensionBg;
        this.suspensionView = suspensionView;
        this.isHideOnSingleTab = isHideOnSingleTab;
        this.suspensionTop = suspensionView.getTop();
        this.checkOffset = checkOffset;
        this.mCurrPosition = position;
        this.listenScrollEvent = listenScrollEvent;
    }

    public void setCheckOffset(float checkOffset) {
        this.checkOffset = checkOffset;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (listenScrollEvent) {
            HippyMap hippyMap = new HippyMap();
            hippyMap.pushInt("pageIndex", mCurrPosition);
            hippyMap.pushInt("orientation", ((FastListView) recyclerView).getOrientation());
            hippyMap.pushInt("x", dx);
            hippyMap.pushInt("y", dy);
            TabUtils.sendTabsEvent(tabsView, TabEnum.ON_SCROLL.getName(), hippyMap);
        }

        if (isSuspension && suspensionView != null) {
            //totalDy += dy;

            assert recyclerView instanceof TVListView;
            final TVListView tl = (TVListView) recyclerView;
            int scrollPosition = tl.getOffsetY();
            if (scrollPosition < tl.getHeight() * checkOffset) {
//                View root = HippyViewGroup.findPageRootView(tabsView);
                if (isOnTop) {
                    isOnTop = false;
//                    if (isHideOnSingleTab) {
//                        //单tab下延迟500ms发出消息，避免前端view隐藏时找不到焦点
//                        TabUtils.sendTabsEvent(tabsView, TabEnum.SUSPENSION_BOTTOM_START.getName(), new HippyMap());
//                    } else {
//                        TabUtils.moveToBottom(suspensionView, tabsView, useSuspensionBg, suspensionTop);
//                    }
                    TabUtils.moveToBottom(suspensionView, tabsView, useSuspensionBg, suspensionTop);
                }
            } else {
                if (!isOnTop) {
                    isOnTop = true;
//                    if (isHideOnSingleTab) {
//                        TabUtils.sendTabsEvent(tabsView, TabEnum.SUSPENSION_TOP_START.getName(), new HippyMap());
//                    } else {
//                        TabUtils.moveToTop(suspensionView, tabsView, useSuspensionBg, suspensionTop);
//                    }
                    TabUtils.moveToTop(suspensionView, tabsView, useSuspensionBg, suspensionTop);
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int state = recyclerView.getScrollState();
        LinearLayoutManager layoutManager = (LinearLayoutManager) ((FastListView) recyclerView).getLayoutManagerCompat().getRealLayout();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        HippyMap map = new HippyMap();
        //当前是第几页
        map.pushInt("pageIndex", mCurrPosition);
        if (((FastListView) recyclerView).getOrientation() == RecyclerView.VERTICAL) {
            if (((FastListView) recyclerView).getLayoutManagerCompat().getExecutor().isScrollUp()) {
                //上滑
                //滑到底部且滑动停止 滑动到底了!
                if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 &&
                        lastCompletelyVisibleItemPosition == totalItemCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE) {
                    ((FastListView) recyclerView).sendScrollEvent(tabsView, TabEnum.ON_SCROLLTO_END.getName(), map);
                }
            } else {
                //下滑
                //滑到顶部且滑动停止 滑动到顶了!
                if (visibleItemCount > 0 && firstVisibleItemPosition == 0 && firstCompletelyVisibleItemPosition == 0
                        && state == RecyclerView.SCROLL_STATE_IDLE) {
                    ((FastListView) recyclerView).sendScrollEvent(tabsView, TabEnum.ON_SCROLLTO_START.getName(), map);
                }
            }
        } else {
            if (((FastListView) recyclerView).getLayoutManagerCompat().getExecutor().isScrollLeft()) {
                //左滑
                //滑到最右且滑动停止 滑动到右了!
                if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 &&
                        lastCompletelyVisibleItemPosition == totalItemCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE) {
                    ((FastListView) recyclerView).sendScrollEvent(tabsView, TabEnum.ON_SCROLLTO_END.getName(), map);
                }
            } else {
                //右滑
                //滑到最左且滑动停止 滑动到左了!
                if (visibleItemCount > 0 && firstVisibleItemPosition == 0 && firstCompletelyVisibleItemPosition == 0
                        && state == RecyclerView.SCROLL_STATE_IDLE) {
                    ((FastListView) recyclerView).sendScrollEvent(tabsView, TabEnum.ON_SCROLLTO_START.getName(), map);
                }
            }
        }
    }

    public int getTotalDy() {
        return totalDy;
    }

    public void setTotalDy(int totalDy) {
        this.totalDy = totalDy;
    }

    public boolean isOnTop() {
        return isOnTop;
    }

    public void setOnTop(boolean onTop) {
        isOnTop = onTop;
    }

    public int getSuspensionTop() {
        return suspensionTop;
    }

    public boolean isUseSuspensionBg() {
        return useSuspensionBg;
    }
}
