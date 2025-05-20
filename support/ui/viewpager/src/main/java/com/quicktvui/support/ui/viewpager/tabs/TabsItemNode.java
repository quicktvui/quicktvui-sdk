package com.quicktvui.support.ui.viewpager.tabs;


import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.OnFastItemClickListener;
import com.quicktvui.hippyext.views.fastlist.OnFastItemFocusChangeListener;
import com.quicktvui.support.ui.viewpager.utils.RenderNodeUtils;
import com.quicktvui.support.ui.viewpager.utils.TabUtils;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

public class TabsItemNode extends RenderNode {
    private ItemView itemView;
    private FastListView tabListView;
    private FastListView contentListView;
    private View mPageRootView;
    private TabsState mTabState;
    SparseArray<PageItem> mPageList;
    private int mTargetCurrent;
    private Runnable resumeTaskRunnable;
    private int loadingItemType;
    private boolean autoRefreshContent = true;//是否每次都显示loading并且刷新
    private FastListPageChangeListener fastListPageChangeListener;//内容fastlist的滑动监听
    private TabsParam tabsParams;
    private final static int RESUME_TASK_DELAY = 600;

    public TabsItemNode(int mId, HippyMap mPropsToUpdate, String className, HippyRootView rootView, ControllerManager componentManager, boolean isLazyLoad) {
        super(mId, mPropsToUpdate, className, rootView, componentManager, isLazyLoad);
        mTabState = new TabsState();
        loadingItemType = mPropsToUpdate.getInt("loadingItemType");
        if (mPropsToUpdate.containsKey("autoRefreshContent")) {
            autoRefreshContent = mPropsToUpdate.getBoolean("autoRefreshContent");
        }
    }

    @Override
    public void manageChildrenComplete() {
        super.manageChildrenComplete();
        itemView = (ItemView) RenderNodeUtils.findViewById(getHippyContext(), getId());
        assert itemView != null : "itemView不可为空";
        final TabsItemStyleNode tabDom = getDom();
        itemView.setTabsItemNode(this);
        mPageRootView = HippyViewGroup.findPageRootView(itemView);
        this.tabListView = findTabListView();
        if (tabListView != null) {
            tabListView.setEnableSelectOnFocus(true);
            tabListView.setNegativeKeyTime(10);
            tabListView.setUseDiff(tabDom.param.useDiff);
        }
        if (this.contentListView == null) {
            this.contentListView = findContentView();
        }
        tabsParams = tabDom.param;
        final RenderNode contentFastListNode = RenderNodeUtils.getRenderNode(contentListView);
        if (tabListView != null) {
            //从dom中获得数据
            HippyArray array = null;
            if (tabDom.getTabsData() != null) {
                array = tabDom.getTabsData();
            }
            if (array != null && array.size() > 0) {
                //更新列表
                tabListView.setPendingData(array, contentFastListNode, false);
            }
            tabListView.getFastAdapter().setOnFastItemClickListener(new OnFastItemClickListener() {
                @Override
                public void onItemClickListener(View view, int position) {

                }

                @Override
                public boolean onItemLongClickListener(View view, int i) {
                    return false;
                }
            });
            tabListView.getFastAdapter().setOnFastItemFocusChangeListener(new OnFastItemFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus, int position) {
                    if (hasFocus) {
                        if (LogUtils.isDebug()) {
                            Log.d("--onItemFocus--", position + "获得了焦点");
                        }
                        if (contentListView != null) {
                            final int prev = mTargetCurrent;
                            if (prev == position) {
                                return;
                            }
                            mTargetCurrent = position;
                            if (mTargetCurrent > -1) {
                                contentListView.pausePostTask();
                            }
                            //暂停所有非展示出来的页面
                            contentListView.removeCallbacks(resumeTaskRunnable);
                            contentListView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tryLoadPageData(tabDom, mTargetCurrent);
//                                    HippyMap map = new HippyMap();
//                                    map.pushInt(TabEnum.ITEM_POSITION.getName(), position);
//                                    TabUtils.sendTabsEvent(tabsView, TabEnum.TAB_CHANGED.getName(), map);
                                }
                            }, 100);
                        }
                    }
                }
            });
        }
        if (contentListView != null) {
            contentListView.setUseDiff(tabDom.param.useDiff);
        }
        if (tabDom.isDataListValid()) {
            updateTabsData(tabDom.getTabsData());
        }
    }

    public void updateTabsData(HippyArray data) {
        final TabsItemStyleNode tabDom = getDom();
        final RenderNode tabListNode = getTabListNode();
        final RenderNode contentNode = getContentNode();
        final int tabsCount = data == null ? 0 : data.size();
        //判断单tab
        final boolean isDataSingleTab = tabsCount < 2;
        mTabState.isDataSingleTab = isDataSingleTab;
        //设置顶部tabList
        if (tabDom != null && tabListView != null && tabListNode != null) {
            if (!isHideTabState()) {
                tabListView.setPendingData(data, tabListNode);
            }
        }
        if (tabListView != null) {
            fastListPageChangeListener = new FastListPageChangeListener(itemView, tabListView, false, false,
                    isHideTabState(), 0, 0, tabsParams.listenScrollEvent);
        } else {
            fastListPageChangeListener = new FastListPageChangeListener(itemView, false, false, isHideTabState(),
                    0, 0, tabsParams.listenScrollEvent);
        }
        if (contentNode != null && contentListView != null) {
            /** */
            this.mTargetCurrent = mTabState.defaultPosition;
            setup(tabsCount, data);
            getPageData(tabDom, mTabState.defaultPosition);
            /**  */
        }
        onTabsDataChanged();
    }

    public void setup(int count, HippyArray data) {
        if (mPageList != null) {
            mPageList.clear();
        }
        mPageList = new SparseArray<>();
        for (int i = 0; i < count; i++) {
            PageItem pi = new PageItem(i);
            pi.tabsParam = tabsParams;
            mPageList.put(i, pi);
        }
    }

    public void onTabsDataChanged() {
        final int defaultFocusPageIndex = mTabState.defaultFocusPosition;
        final int defaultPageIndex = getDom().param.defaultIndex;
        if (isHideTabState()) {
            //单tab模式
            if (tabListView != null) {
                tabListView.setVisibility(View.INVISIBLE);
            }
        } else {
            if (tabListView != null) {
                tabListView.setVisibility(View.VISIBLE);
                tabListView.setSelectChildPosition(defaultPageIndex, true);
                if (defaultPageIndex > -1) {
                    HippyMap im = new HippyMap();
                    im.pushInt("focusPosition", defaultPageIndex);
                    im.pushBoolean("force", true);
                    tabListView.setInitPositionInfo(im);
                }
            }
        }
    }

    /**
     * 前端对应tab tv_list的name：tab_list
     *
     * @return
     */
    private FastListView findTabListView() {
        FastListView listView = null;
        //通过node获取tabListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof FastListView && node.getProps().getString("name").equals("tab_list")) {
                listView = (FastListView) nodeView;
                mTabState.listNodeIndex = i;
            }
        }
        return listView;
    }

    /**
     * 前端对应content tv_list的name：content_list
     *
     * @return
     */
    private FastListView findContentView() {
        FastListView v = null;
        //通过node获取tabListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof FastListView && node.getProps().getString("name").equals("content_list")) {
                mTabState.contentNodeIndex = i;
                v = (FastListView) nodeView;
                break;
            }
        }
        return v;
    }

    private void tryLoadPageData(TabsItemStyleNode tabDom, int position) {
        final PageItem pageItem = getPageItem(position);
        if (autoRefreshContent || pageItem.isNeedLoad()) {
            pageItem.notifyLoading();
            if (contentListView != null) {
                if (loadingItemType != 0) {
                    contentListView.setList(getPageData(loadingItemType));
                }
                getPageData(tabDom, position);
            }
        } else {
            if (pageItem.dataState == 1) {
                pageItem.markDataDirty();
                tryUpdatePageView(position);
                requestResumeCurrentPage(position);
            }
        }
    }

    public void tryUpdatePageView(int item) {
        final PageItem data = getPageItem(item);
        if (data != null) {
            contentListView.removeCallbacks(data.postDataTask);
            requestUIPageViewBindData(data, item);
        }
    }

    public void requestResumeCurrentPage(int page) {
        requestResumePostTask(page, RESUME_TASK_DELAY);
    }

    public void requestUIPageViewBindData(PageItem pi, int pos) {
        pi.postDataTask = () -> {
            exeUIPageViewBindData(contentListView, pi, pos);
        };
        contentListView.postDelayed(pi.postDataTask, 100);
    }

    public void requestResumePostTask(int page, int delay) {
        resumeTaskRunnable = new Runnable() {
            @Override
            public void run() {
                resumePostTaskOnHide();
                onCurrentPageToShow(page);
            }
        };
        contentListView.postDelayed(resumeTaskRunnable, delay);
    }

    public void resumePostTaskOnHide() {
        if (contentListView != null) {
            contentListView.resumePostTask();
            TabUtils.unBlockFocus(contentListView);
        }
    }

    public boolean exeUIPageViewBindData(View pv, PageItem pi, int pos) {
        final RenderNode node = getContentNode();
        if (pv != null) {
            if (pi.updateDirty) {
                final Object data = pi.pageData;
                if (pi.pageData == null) {

                } else {
                    contentListView.pausePostTask();
                    bindPageData(pi.position, pv, pi, node, fastListPageChangeListener);
                    pi.updateDirty = false;
                }
                requestLayoutManual();
                return true;
            }
        }
        return false;
    }

    public void bindPageData(int position, View pv, PageItem pageItem, RenderNode templateNode
            , FastListPageChangeListener fastListPageChangeListener) {
        FastListView fv = (FastListView) pv;
        fv.getEventDeliverer().setOnEventListener((hippyViewEvent, i, hippyEngineContext, params) -> {
            if (params != null) {
                params.pushInt("pageIndex", position);
            }
        });
        HippyArray array = (HippyArray) pageItem.pageData.rawData;
        HippyMap params = pageItem.pageData.params;
        if (params != null) {
            boolean disableScrollOnFirstScreen = params.getBoolean("disableScrollOnFirstScreen");
            fv.getLayoutManagerCompat().setNoScrollOnFirstScreen(disableScrollOnFirstScreen);
            pageItem.disableScrollOnFirstScreen = disableScrollOnFirstScreen;
        }
        if (array != null) {
            fv.setPendingData(array, templateNode);
        } else {
            fv.setPendingData(new HippyArray(), templateNode);
        }
        fv.setOnScrollListener(fastListPageChangeListener);
    }

    public void requestLayoutManual() {
        if (getId() != -1) {
            RenderUtil.requestNodeLayout(contentListView);
        } else {
            RenderUtil.reLayoutView(contentListView, (int) getX(), (int) getY(), getWidth(), getHeight());
        }
    }

    RenderNode getTabListNode() {
        if (mTabState.listNodeIndex > -1 && getChildCount() > mTabState.listNodeIndex) {
            return getChildAt(mTabState.listNodeIndex);
        }
        return null;
    }

    RenderNode getContentNode() {
        if (mTabState.contentNodeIndex > -1 && getChildCount() > mTabState.contentNodeIndex) {
            return getChildAt(mTabState.contentNodeIndex);
        }
        return null;
    }

    public void onCurrentPageToShow(int page) {
        final PageItem pi = getPageItem(page);
        if (contentListView != null) {
            if (pi.pendingFocusPosition > -1) {
                if (pi.isDataValid()) {
                    contentListView.requestChildFocus(pi.pendingFocusPosition, View.FOCUS_DOWN);
                    pi.pendingFocusPosition = -1;
                }
            }
            contentListView.setAlpha(1);
        }
    }

    public PageItem getPageItem(int index) {
        if (mPageList == null) {
            return null;
        }
        if (index > -1 && index < mPageList.size()) {
            return mPageList.get(index);
        }
        return null;
    }

    /**
     * 通知前端获取数据
     */
    public void getPageData(TabsItemStyleNode tabDom, int position) {
        //发送回调，让上层获取数据,上层通过setPageData来更新数据
        HippyViewEvent event = new HippyViewEvent("onLoadPageData");
        HippyMap map = new HippyMap();
        HippyMap item = tabDom.getTabsData().getMap(position);
        if (item.containsKey("content")) {
            HippyMap pageData = item.getMap("content");
            //当前页面中有多少条数据
            map.pushInt("itemCount", pageData == null ? 0 : pageData.size());
        } else {
            //callback.callback(null,null);
            map.pushInt("itemCount", position);
        }
        //哪一页的数据
        map.pushInt("pageIndex", position);
        event.send(itemView, map);
    }

    @Override
    public void dispatchUIFunction(String functionName, HippyArray parameter, Promise promise) {
        super.dispatchUIFunction(functionName, parameter, promise);
        switch (functionName) {
            case "setTabsData":
                setTabsData(parameter, promise);
                break;
            case "setPageData":
                if (contentListView != null) {
                    setPageData(parameter.getInt(0), parameter.getMap(1), parameter.getArray(2));
                }
                break;
        }
    }

    public void setTabsData(HippyArray var, Promise promise) {
        final HippyMap initParam = var.getMap(0);
        mTabState.initParams(initParam);
        final HippyArray data = var.getArray(1);
        final TabsItemStyleNode tabDom = getDom();
        if (tabDom != null) {
            tabDom.setDataList(data);
        }
        updateTabsData(data);
    }

    public void setPageData(int page, HippyMap params, HippyArray array) {
        //先更新数据
        updateDataOnly(page, params, array);
        tryUpdatePageView(page);
        requestResumeCurrentPage(page);
    }

    public void updateDataOnly(int page, HippyMap params, Object data) {
        final PageItem pi = getPageItem(page);
        pi.pageData = new RecyclerViewPager.PageData(data);
        pi.pageData.params = params;
        assert pi != null : "updateDataOnly error,这里数据不可为空";
        pi.markDataDirty();
        if (data != null) {
            pi.notifyLoaded();
        } else {
            pi.notifyReset();
        }
    }

    TabsItemStyleNode getDom() {
        return (TabsItemStyleNode) getHippyContext().getDomManager().getNode(getId());
    }

    protected HippyEngineContext getHippyContext() {
        return RenderNodeUtils.getHippyContext(mRootView);
    }

    public boolean isHideTabState() {
        return this.tabListView == null || (getDom() != null && (getDom().param.hideOnSingleTab && mTabState.isDataSingleTab));
    }

    private HippyArray getPageData(int type) {
        HippyArray array = new HippyArray();
        HippyMap map = new HippyMap();
        map.pushInt("type", type);
        array.pushMap(map);
        return array;
    }

    public void attachToWindow(View parent, int position, Object item) {

    }

    public void detachFromWindow(View parent, int position, Object item) {
        if (tabListView != null) {
            tabListView.recycle();
        }
        if (contentListView != null) {
            contentListView.recycle();
        }
    }

    public void onBind(View parent, int position, Object item) {
        if (tabListView != null) {
            RenderNode tabListNode = RenderNodeUtils.getRenderNode(tabListView);
            HippyArray array = null;
            if (getDom().getTabsData() != null) {
                array = getDom().getTabsData();
            }
            if (array != null && array.size() > 0) {
                //用现有数据更新tab
                tabListView.setPendingData(array, tabListNode, false);
                fastListPageChangeListener = new FastListPageChangeListener(itemView, false, false,
                        isHideTabState(), 0, 0, tabsParams.listenScrollEvent);
                int selection = tabListView.getSelectChildPosition();
                tabListView.setVisibility(View.VISIBLE);
                tabListView.setSelectChildPosition(selection, true);
            }
        }
        if (contentListView != null) {
            //TODO:加载列表数据更新数据
//            updateDataOnly(position, params, array);
            tryUpdatePageView(position);
            requestResumeCurrentPage(position);
        }
    }

    public void notifyResumeTask() {
        if (tabListView != null) {
            tabListView.notifyResumeTask();
        }
        if (contentListView != null) {
            contentListView.notifyResumeTask();
        }
    }

    public void notifyPauseTask() {
        if (tabListView != null) {
            tabListView.notifyPauseTask();
        }
        if (contentListView != null) {
            contentListView.notifyPauseTask();
        }
    }

    final static class TabsState {
        int defaultPosition = 0; //默认选中tab
        int defaultFocusPosition = -1; //默认选中tab
        int tabPendingFocus = -1;
        int listNodeIndex = -1;
        int contentNodeIndex = -1;
        boolean isDataSingleTab = false;
        boolean isHideTabState = false;

        void initParams(HippyMap params) {
            this.defaultPosition = params.getInt("defaultIndex");
            this.defaultFocusPosition = params.getInt("focusIndex");
            this.tabPendingFocus = this.defaultPosition;
        }
    }

}
