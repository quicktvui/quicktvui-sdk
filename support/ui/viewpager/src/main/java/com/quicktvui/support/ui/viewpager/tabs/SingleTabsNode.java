package com.quicktvui.support.ui.viewpager.tabs;

import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import android.support.v7.widget.RecyclerView;

import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.OnFastItemClickListener;
import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.quicktvui.hippyext.views.fastlist.Utils;
import com.quicktvui.base.ui.FocusDispatchView;
import com.quicktvui.support.ui.viewpager.utils.RenderNodeUtils;
import com.quicktvui.support.ui.viewpager.utils.TabEnum;
import com.quicktvui.support.ui.viewpager.utils.TabUtils;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.CustomControllerHelper;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.hippy.uimanager.InternalExtendViewUtil;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

/**
 * 单页面模式 前端用tv_list代替viewpager
 */
public class SingleTabsNode extends RenderNode {
    private TabsView tabsView;//自身tabsView
    private FastListView tabListView; //导航列表
    private FastListView contentListView; //内容列表
    public static final String TAG = "SingleTabsViewLog";
    private TabsState mTabState;
    private boolean isClickBack = false;//当前是否点击返回顶部
    private View mPageRootView;
    private FastListPageChangeListener fastListPageChangeListener;//内容fastlist的滑动监听
    private FastListScrollToTopListener fastListScrollToTopListener;//内容fastlist的调用scrollToTop监听
    private boolean isSuspension;//是否开启吸顶
    private boolean useSuspensionBg;
    SparseArray<PageItem> mPageList;
    private final static int RESUME_TASK_DELAY = 500;
    private final static int FIRST_RESUME_TASK_DELAY = 1000;//第一次进入页面显示的delay时间
    private Runnable resumeTaskRunnable;
    private int mTargetCurrent;
    private int loadingItemType;
    private boolean alwaysShowLoading = false;//是否每次刷新contentList
    private TabsParam tabsParams;

    public SingleTabsNode(int mId, HippyMap mPropsToUpdate, String className, HippyRootView rootView, ControllerManager componentManager, boolean isLazyLoad) {
        super(mId, mPropsToUpdate, className, rootView, componentManager, isLazyLoad);
        mTabState = new TabsState();
        loadingItemType = mPropsToUpdate.getInt("loadingItemType");
        if (mPropsToUpdate.containsKey("alwaysShowLoading")) {
            alwaysShowLoading = mPropsToUpdate.getBoolean("alwaysShowLoading");
        }
    }

    @Override
    public View createView() {
        return super.createView();
    }

    @Override
    public View createViewRecursive() {
        return super.createViewRecursive();
    }

    void doContentChange(int position) {
        if (contentListView != null) {
            //如果是点击返回键到顶部，则不刷新
            if (isClickBack) {
                isClickBack = false;
                return;
            }
            final int prev = mTargetCurrent;
            if (prev == position) {
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "PageAdapterEvent:requestSwitchToPage return on same position:" + position);
                }
                return;
            }
            TabUtils.blockFocus(contentListView);
            HippyMap loadingMap = new HippyMap();
            loadingMap.pushInt("pageIndex", position);
            loadingMap.pushBoolean(TabEnum.IS_SHOWLOADING.getName(), true);
            contentListView.sendScrollEvent(tabsView, TabEnum.SHOW_LOADING.getName(), loadingMap);
            PageItem pageItem = getPageItem(position);
            if (pageItem != null && pageItem.disableScrollOnFirstScreen && tabsParams.autoScrollToTop) {
                //当tab切换后，需要滚动动顶部
                contentListView.scrollToTop();
            }
            if (tabsParams.isHideList) {
                setDisplay(contentListView, false);
            }
            mTargetCurrent = position;
            if (mTargetCurrent > -1) {
                contentListView.pausePostTask();
            }
            //暂停所有非展示出来的页面
            contentListView.removeCallbacks(resumeTaskRunnable);
            contentListView.postDelayed(() -> {
                if (getDom() != null && getDom().getTabsData() != null) {
                    tryLoadPageData(getDom(), mTargetCurrent);
                    HippyMap item = getDom().getTabsData().getMap(position);
                    HippyMap map = new HippyMap();
                    map.pushInt(TabEnum.ITEM_POSITION.getName(), position);
                    map.pushMap(TabEnum.ITEM_DATA.getName(), item);
                    TabUtils.sendTabsEvent(tabsView, TabEnum.TAB_CHANGED.getName(), map);
                }
            }, 100);
        }
    }

    @Override
    public void manageChildrenComplete() {
        super.manageChildrenComplete();
        //通过node获取tabsView
        tabsView = (TabsView) RenderNodeUtils.findViewById(getHippyContext(), getId());

        assert tabsView != null : "tabsView不可为空";
        final TabsStyleNode tabDom = getDom();
        tabsView.setSingleBoundNode(this);
        mPageRootView = HippyViewGroup.findPageRootView(tabsView);

        this.tabListView = findTabListView();
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode manageChildrenComplete tabList" + this.tabListView + ",mRootView:" + mPageRootView);
        }
        if (tabListView != null) {
            configTabList(tabListView, tabDom);
        }
        if (this.contentListView == null) {
            this.contentListView = findContentView();
        }
        tabsParams = tabDom.param;
        //fastList组件需要listNode,实际上是第一个子节点
        final RenderNode contentFastListNode = RenderNodeUtils.getRenderNode(contentListView);
        //设置吸顶
        boolean isSuspension = tabDom.isSuspension();
        boolean useSuspensionBg = tabDom.useSuspensionBg();
        if (tabListView != null) {
            this.isSuspension = isSuspension;
            this.useSuspensionBg = useSuspensionBg;
        }
        if (tabListView != null) {
            //从dom中获得数据
            HippyArray array = null;
            if (tabDom.getTabsData() != null) {
                array = tabDom.getTabsData();
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "TabsNode manageChildrenComplete tabs array ");
                }
            }
            if (array != null && array.size() > 0) {
                //更新列表
                tabListView.setPendingData(array, contentFastListNode, false);
            } else {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "TabsNode manageChildrenComplete array is null ");
                }
            }
            if (tabsParams.useClickMode) {
                tabListView.getFastAdapter().setOnFastItemClickListener(new OnFastItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        tabListView.setSelectChildPosition(position, false);
                        doContentChange(position);
                    }

                    @Override
                    public boolean onItemLongClickListener(View view, int i) {
                        return false;
                    }
                });
            } else {
                tabListView.getFastAdapter().setOnFastItemFocusChangeListener((view, hasFocus, position) -> {
                    if (hasFocus) {
                        doContentChange(position);
                    }
                });
            }
        }
        if (contentListView != null) {
            contentListView.setTemplateNode(contentFastListNode);
            contentListView.setAgentView(tabsView);
            if (tabsParams != null) {
                contentListView.setUseDiff(tabsParams.useDiff);
            }
            contentListView.getLayoutManagerCompat().setFocusEventListener(new TVListView.FocusEventListener() {
                @Override
                public View onInterceptFocusSearchFailed(View focused, int focusDirection, int vector) {
                    if (vector == 0) {
                        if (InternalExtendViewUtil.isContainBlockDirection(focusDirection, contentListView.getBlockFocusOnFail())) {
                            return focused;
                        }
                    }
                    return super.onInterceptFocusSearchFailed(focused, focusDirection, vector);
                }
            });
        }
        if (tabDom.isDataListValid()) {
            if (LogUtils.isDebug()) {
                Log.i(TAG, "TabsNode updateTabsData on manageChildrenComplete");
            }
            updateTabsData(tabDom.getTabsData());
        }
    }

    private boolean isHideOnSingleTab() {
        return getDom() != null && getDom().param.hideOnSingleTab;
    }

    public boolean isHideTabState() {
        return this.tabListView == null || (getDom() != null && (getDom().param.hideOnSingleTab && mTabState.isDataSingleTab));
    }

    void configTabList(FastListView tabListView, TabsStyleNode node) {
        tabListView.setEnableSelectOnFocus(true);
        tabListView.setNegativeKeyTime(10);
        TabsParam param = node.param;
        tabListView.setUseDiff(param.useDiff);
        if (param.tabPosition == TabsParam.TAB_POSITION_TOP) {

        }
    }

    @Override
    public void dispatchUIFunction(String functionName, HippyArray var, Promise promise) {
        super.dispatchUIFunction(functionName, var, promise);
        switch (functionName) {
            case "setTabsData":
                setTabsData(var, promise);
                break;
            case "requestTabFocus":
                final int tab = var.getInt(0);
                if (tabListView != null && !isHideTabState()) {
                    tabListView.requestChildFocus(tab, View.FOCUS_DOWN);
                }
                break;
            case "addPageData":
                if (contentListView != null) {
                    addPageData(var.getMap(1), var.getArray(2));
                }
                break;
            case "setPageData":
                if (contentListView != null) {
                    setPageData(var.getInt(0), var.getMap(1), var.getArray(2));
                }
                break;
            case "invokeContentFunction":
                if (contentListView != null) {
                    if (var.size() == 3) {
                        invokeContentFunction(contentListView, var.getInt(0), var.getString(1), var.getArray(2), promise);
                    } else {
                        invokeContentFunction(contentListView, mTargetCurrent, var.getString(0), var.getArray(1), promise);
                    }
                }
                break;
            case "contentScrollToFocus":
                if (contentListView != null) {
                    contentListView.scrollToFocus(var.getInt(0));
                }
                break;
            case "destroy":
                destroy();
                break;
            case "getCurrentPage":
                promise.resolve(mTargetCurrent);
                break;
            case "getState":
            case "getViewState":
                try {
                    HippyMap hippyMap = new HippyMap();
                    if (contentListView != null) {
                        HippyMap contentMap = ExtendUtil.getViewState(contentListView);
                        hippyMap.pushMap(TabEnum.CONTENT_STATE.getName(), contentMap);
                        if (fastListPageChangeListener != null) {
                            hippyMap.pushBoolean(TabEnum.IS_SUSPENSION.getName(), fastListPageChangeListener.isOnTop());
                        }
                    }
                    if (tabListView != null) {
                        HippyMap tabMap = ExtendUtil.getViewState(tabListView);
                        hippyMap.pushMap(TabEnum.TAB_STATE.getName(), tabMap);
                    }
                    hippyMap.pushInt(TabEnum.CURRENTPAGE.getName(), mTargetCurrent);
                    promise.resolve(hippyMap);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    void setTabsData(HippyArray var, Promise promise) {
        final HippyMap initParam = var.getMap(0);
        mTabState.initParams(initParam);
        final HippyArray data = var.getArray(1);
        final TabsStyleNode tabDom = getDom();
        if (tabDom != null) {
            tabDom.setDataList(data);
        }
        updateTabsData(data);
    }

    void updateTabsData(HippyArray data) {
        final TabsStyleNode tabDom = getDom();
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
            } else {
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "setTabsData in singleTab mode!");
                }
            }
        }
        if (tabListView != null) {
            fastListPageChangeListener = new FastListPageChangeListener(tabsView, tabListView, isSuspension, useSuspensionBg,
                    isHideTabState(), 0, 0, tabsParams.listenScrollEvent);
        } else {
            fastListPageChangeListener = new FastListPageChangeListener(tabsView, isSuspension, useSuspensionBg, isHideTabState(),
                    0, 0, tabsParams.listenScrollEvent);
        }
        fastListScrollToTopListener = new FastListScrollToTopListener(tabsView, 0);
        if (contentListView != null) {
            contentListView.setOnScrollListener(fastListPageChangeListener);
            contentListView.setScrollToTopListener(fastListScrollToTopListener);
        }
        if (contentNode != null && contentListView != null) {
            /** */
            this.mTargetCurrent = mTabState.defaultPosition;
            setup(tabsCount, data);
            if (isHideTabState()) {
                setInitFocusPosition(mTabState.defaultFocusPosition);
            }
            getPageData(tabDom, mTabState.defaultPosition);
            /**  */
        }
        onTabsDataChanged(data);
    }

    void onTabsDataChanged(HippyArray data) {
        final int defaultFocusPageIndex = mTabState.defaultFocusPosition;
        final int defaultPageIndex = mTabState.defaultPosition;
        if (isHideTabState()) {
            //单tab模式
            if (tabListView != null) {
                tabListView.setVisibility(View.INVISIBLE);
            }
        } else {
            if (tabListView != null) {
                tabListView.setVisibility(View.VISIBLE);
                tabListView.setSelectChildPosition(defaultPageIndex, true);
                if (defaultFocusPageIndex > -1) {
                    HippyMap im = new HippyMap();
                    im.pushInt("focusPosition", defaultFocusPageIndex);
                    im.pushBoolean("force", true);
                    tabListView.setInitPositionInfo(im);
                }
            }
        }
    }

    private void tryLoadPageData(TabsStyleNode tabDom, int position) {
        final PageItem pageItem = getPageItem(position);
        if (pageItem.isNeedLoad()) {
            pageItem.notifyLoading();
            if (contentListView != null) {
                if (loadingItemType != 0) {
                    contentListView.setList(getPageData(loadingItemType));
                }
                getPageData(tabDom, position);
            }
        } else {
            if (alwaysShowLoading) {
                if (contentListView != null) {
                    if (loadingItemType != 0) {
                        contentListView.setList(getPageData(loadingItemType));
                    }
                }
            }
            if (pageItem.dataState == 1) {
                pageItem.markDataDirty();
                tryUpdatePageView(position);
                requestResumeCurrentPage(position);
            } else {
                if (LogUtils.isDebug()) {
                    Log.e(TAG, "tryLoadPageData 没有设置数据，pageItem:" + pageItem);
                }
            }
        }
    }

    /**
     * 通知前端获取数据
     */
    public void getPageData(TabsStyleNode tabDom, int position) {
        //发送回调，让上层获取数据,上层通过setPageData来更新数据
        HippyViewEvent event = new HippyViewEvent("onLoadPageData");
        HippyMap map = new HippyMap();
        if (tabDom != null) {
            HippyMap item = tabDom.getTabsData().getMap(position);
            if (item.containsKey("content")) {
                HippyMap pageData = item.getMap("content");
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "TabsNode configViewPager dataLoaded page: " + position + ",data:" + pageData);
                }
                //当前页面中有多少条数据
                map.pushInt("itemCount", pageData == null ? 0 : pageData.size());
            } else {
                //callback.callback(null,null);
                map.pushInt("itemCount", position);
            }
            //哪一页的数据
            map.pushInt("pageIndex", position);
            map.pushMap("data", item);
            event.send(tabsView, map);
        }
    }

    //为某一页设置数据
    public void setPageData(int page, HippyMap params, HippyArray array) {
        //HippyArray array = pageData.getArray("data");
        //getCurrentList().setPendingData();
        //先更新数据
        updateDataOnly(page, params, array);
        if (mTargetCurrent == page) {
            //如果current == page，证明需要更新当前页面
            tryUpdatePageView(page);
            requestResumeCurrentPage(page);
        }
    }

    public void addPageData(HippyMap params, HippyArray array) {
//        addPageDataOnly(page, array);
//        if (page == vp2.getCurrentItem()) {
//            tryUpdatePageView(page);
//            requestResumeCurrentPage(page);
//        }
        int deleteCount = 0;
        if (params != null) {
            deleteCount = params.getInt("deleteCount");
        }
        if (contentListView != null) {
            contentListView.addData(array, deleteCount);
        }
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

    public void tryUpdatePageView(int item) {
        final PageItem data = getPageItem(item);
        if (data != null) {
            contentListView.removeCallbacks(data.postDataTask);
            requestUIPageViewBindData(data, item);
        } else {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "tryUpdatePageView no need pageItem :" + null + ",index:" + item);
            }
        }
    }

    public void requestUIPageViewBindData(PageItem pi, int pos) {
        pi.postDataTask = () -> {
            exeUIPageViewBindData(contentListView, pi, pos);
        };
        contentListView.postDelayed(pi.postDataTask, 100);
    }

    /**
     * 真正给列表设置数据
     *
     * @param pv
     * @param pi
     */
    public boolean exeUIPageViewBindData(View pv, PageItem pi, int pos) {
        final RenderNode node = getContentNode();
        if (pv != null) {
            if (pi.updateDirty) {
                final Object data = pi.pageData;
                TabUtils.blockFocus(pv);
                if (pi.pageData == null) {
                    if (LogUtils.isDebug()) {
                        Log.e(TAG, "exeUIPageViewBindData pageData is null return");
                    }
                } else {
                    contentListView.pausePostTask();
                    bindPageData(pi.position, pv, pi, node);
                    pi.updateDirty = false;
                }
                requestLayoutManual();
                return true;
            } else {
                if (LogUtils.isDebug()) {
                    Log.e(TAG, "exeUIPageViewBindData return on updateDirty Pos: " + pos);
                }
            }
        } else {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "exeUIPageViewBindData error return on pv is null  pi:" + pi);
            }
        }
        return false;
    }

    public void bindPageData(int position, View pv, PageItem pageItem, RenderNode templateNode) {
        FastListView fv = (FastListView) pv;
        fv.getEventDeliverer().setOnEventListener((hippyViewEvent, i, hippyEngineContext, params) -> {
            if (params != null) {
                params.pushInt("pageIndex", position);
            }
        });
        fv.setOnLoadMoreListener(new TVListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int pos, int itemCount) {
                if (fv.getAgentView() != null) {
                    if (itemCount < 2) {
                        //只有一个item，判定为无需laodMore
                        return;
                    }
                    HippyMap map = new HippyMap();
                    map.pushInt("pageIndex", position);
                    map.pushInt("itemPosition", pos);
                    map.pushInt("itemCount", itemCount);
                    fv.sendScrollEvent(fv.getAgentView(), TabEnum.ON_LOADMORE.getName(), map);
                }
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
    }

    public void requestLayoutManual() {
        if (getId() != -1) {
            RenderUtil.requestNodeLayout(contentListView);
        } else {
            RenderUtil.reLayoutView(contentListView, (int) getX(), (int) getY(), getWidth(), getHeight());
        }
    }

    private HippyArray getPageData(int type) {
        HippyArray array = new HippyArray();
        HippyMap map = new HippyMap();
        map.pushInt("type", type);
        array.pushMap(map);
        return array;
    }

    public void requestResumeCurrentPage(int page) {
        requestResumePostTask(page, tabsParams != null ? tabsParams.resumeTaskDelay : RESUME_TASK_DELAY);
    }

    public void requestResumePostTask(int page, int delay) {
        resumeTaskRunnable = new Runnable() {
            @Override
            public void run() {
                if (contentListView != null) {
                    HippyMap loadingMap = new HippyMap();
                    loadingMap.pushInt("pageIndex", page);
                    loadingMap.pushBoolean(TabEnum.IS_SHOWLOADING.getName(), false);
                    contentListView.sendScrollEvent(tabsView, TabEnum.SHOW_LOADING.getName(), loadingMap);
                }
                resumePostTaskOnHide();
                onCurrentPageToShow(page);
            }
        };
        final PageItem pi = getPageItem(page);
        if (contentListView != null) {
            if (pi.pendingFocusPosition > -1) {
                if (pi.isDataValid()) {
                    delay = FIRST_RESUME_TASK_DELAY;
                }
            }
        }
        contentListView.postDelayed(resumeTaskRunnable, delay);
    }

    public void resumePostTaskOnHide() {
        if (contentListView != null) {
            contentListView.resumePostTask();
            TabUtils.unBlockFocus(contentListView);
        }
    }

    public void onCurrentPageToShow(int page) {
        final PageItem pi = getPageItem(page);
        if (LogUtils.isDebug()) {
            Log.d(TAG, "onCurrentPageToShow page:" + page + ",dataValid:" + pi.isDataValid() + ",pi.pendingFocusPosition：" + pi.pendingFocusPosition);
        }
        if (contentListView != null) {
            if (pi.pendingFocusPosition > -1) {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "onCurrentPageToShow page:" + page + ",dataValid:" + pi.isDataValid());
                }
                if (pi.isDataValid() && !isLoadingShown(contentListView)) {
                    if (!isLoadingShown(contentListView)) {
                        contentListView.requestFocus(View.FOCUS_DOWN);
                        pi.pendingFocusPosition = -1;
                    }
                }
            }
            if (tabsParams.isHideList) {
                setDisplay(contentListView, true);
            }
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

    RenderNode findContentTemplateNode() {
        if (getChildAt(0) != null && getChildAt(0).getChildCount() > 0) {
            return getChildAt(0);
        } else {
            return null;
        }
    }

    private FastListView findTabListView() {
        FastListView listView = null;
        //通过node获取tabListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof FastListView && node.getProps().getString("name").equals(TabEnum.TABS_TAB_LIST.getName())) {
                listView = (FastListView) nodeView;
                mTabState.listNodeIndex = i;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "TabsNode findTabListView index:" + i + ",view :" + listView);
                }
            }
        }
        return listView;
    }

    /**
     * 前端对应content tv_list的name：tabs_content_list
     *
     * @return
     */
    private FastListView findContentView() {
        FastListView v = null;
        //通过node获取tabListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof FastListView && node.getProps().getString("name").equals(TabEnum.TABS_CONTENT_LIST.getName())) {
                mTabState.contentNodeIndex = i;
                v = (FastListView) nodeView;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "TabsNode findContentView index:" + 0 + ",view :" + v);
                }
                break;
            }
        }
        return v;
    }

    public void setup(int count, HippyArray data) {
        if (mPageList != null) {
            mPageList.clear();
        }
        mPageList = new SparseArray<>();
        //wanglei 添加自定义tv-list属性
        for (int i = 0; i < count; i++) {
            PageItem pi = new PageItem(i);
            pi.tabsParam = tabsParams;
            mPageList.put(i, pi);
        }
    }

    /**
     * 处理焦点回退
     *
     * @param event
     * @return
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (getParams() != null && getParams().autoHandleBackKey &&
                event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isHideTabState()) {
                if (contentListView != null) {
                    if (contentListView.isShakeEnd()) {
                        return contentListView.hasFocus() && handleBackPressed(0, true);
                    }
                    return contentListView.hasFocus();
                }
                return contentListView.hasFocus() && handleBackPressed(0, true);
            } else if (tabListView != null) {
                final int selection = tabListView.getSelectChildPosition();
                final int defaultPosition = mTabState.defaultPosition;
                if (tabListView.hasFocus()) {
                    if (selection == defaultPosition) {
                        //已经在默认tab上，不处理
                        return false;
                    }
                    if (getDom().isAutoBackToDefault()) {
                        //不在default上，default请求焦点
                        final View view = tabListView.findViewByPosition(defaultPosition);
                        if (view != null) {
                            tabListView.requestChildFocus(defaultPosition, View.FOCUS_DOWN);
                        } else {
                            HippyMap im = new HippyMap();
                            im.pushInt("focusPosition", defaultPosition);
                            im.pushInt("scrollToPosition", defaultPosition);
                            im.pushBoolean("hide", false);
                            im.pushBoolean("force", true);
                            tabListView.setInitPositionInfo(im);
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else if (contentListView.hasFocus()) {
                    if (contentListView.isShakeEnd()) {
                        isClickBack = true;
                        //焦点在content里，将焦点还给tab
                        tabListView.requestChildFocus(selection, View.FOCUS_DOWN);
                        handleBackPressed(selection, false);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleBackPressed(int pos, boolean isSingleTabState) {
        if (contentListView != null) {
            if (fastListPageChangeListener != null) {
                if (fastListPageChangeListener.isOnTop()) {
                    fastListPageChangeListener.setOnTop(false);
                    if (isHideTabState()) {
                        TabUtils.sendTabsEvent(tabsView, TabEnum.SUSPENSION_BOTTOM_START.getName(), new HippyMap());
                    } else {
                        TabUtils.moveToBottom(tabListView, tabsView, fastListPageChangeListener.isUseSuspensionBg(), fastListPageChangeListener.getSuspensionTop());
                    }
                }
                fastListPageChangeListener.setTotalDy(0);
            }
            if (isSingleTabState) {
                if (!isOnScrollTop()) {
                    requestDefaultFocus(200);
                    return true;
                } else {
                    return false;
                }
            }
            if (contentListView != null) {
                contentListView.scrollToTop();
                return true;
            }
        }
        return false;
    }

    void requestDefaultFocus(int delay) {
        blockRootView();
        if (contentListView != null) {
            contentListView.clearFocus();
            contentListView.scrollToTop();
            contentListView.postDelayed(() -> {
                unBlockRootView();
                contentListView.requestFocus(View.FOCUS_DOWN);
            }, delay);
        }
    }

    private void blockRootView() {
        FocusDispatchView.blockFocus(HippyViewGroup.findPageRootView(contentListView));
    }

    private void unBlockRootView() {
        FocusDispatchView.unBlockFocus(HippyViewGroup.findPageRootView(contentListView));
    }

    public boolean isOnScrollTop() {
        if (contentListView.getOrientation() == RecyclerView.VERTICAL) {
            return contentListView.getOffsetY() < 20;
        } else {
            return contentListView.getOffsetX() < 20;
        }
    }

    TabsStyleNode getDom() {
        return (TabsStyleNode) getHippyContext().getDomManager().getNode(getId());
    }

    public TabsParam getParams() {
        return getDom() != null ? getDom().param : null;
    }

    protected HippyEngineContext getHippyContext() {
        return RenderNodeUtils.getHippyContext(mRootView);
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

    public void invokeContentFunction(View view, int pageIndex, String functionName, HippyArray var, Promise promise) {
        final HippyEngineContext context = Utils.getHippyContext(view);
        final FastListView fv = (FastListView) view;

        if (context != null) {
            final HippyViewController vc = CustomControllerHelper.getViewController(context.getRenderManager().getControllerManager(), fv.getTemplateNode());
            if (vc != null) {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "dispatchUIFunction pageIndex:" + pageIndex + ",functionName:" + functionName + ",view:" + view + ",var :" + var);
                }
                if (promise != null && promise.isCallback()) {
                    vc.dispatchFunction(view, functionName, var, promise);
                } else {
                    vc.dispatchFunction(view, functionName, var);
                }
            }
        }
    }

    public void setInitFocusPosition(int i) {
        final PageItem pi = getPageItem(i);
        if (pi != null) {
            pi.pendingFocusPosition = 0;
        }
    }

    public void setDisplay(View view, boolean b) {
        view.setAlpha(b ? 1 : 0);
    }

    public boolean isLoadingShown(View view) {
        if (loadingItemType != 0) {
            FastListView fv = (FastListView) view;
            if (fv.getFastAdapter() != null) {
                return fv.getFastAdapter().getItemCount() == 1 && fv.getFastAdapter().getItemViewType(0) == loadingItemType;
            }
        }
        return false;
    }

    protected void destroy() {
        if (tabListView != null) {
            tabListView.destroy();
        }
        if (contentListView != null) {
            contentListView.destroy();
        }
    }
}
