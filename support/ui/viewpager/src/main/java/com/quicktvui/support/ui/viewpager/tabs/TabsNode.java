package com.quicktvui.support.ui.viewpager.tabs;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import com.extscreen.support.viewpager2.widget.ViewPager2;
import com.quicktvui.base.ui.waterfall.Chunk;
import com.quicktvui.support.ui.viewpager.utils.RenderNodeUtils;
import com.quicktvui.support.ui.viewpager.utils.TabEnum;
import com.quicktvui.support.ui.viewpager.utils.TabHelper;
import com.quicktvui.support.ui.viewpager.utils.TabUtils;
import com.sunrain.toolkit.utils.ThreadUtils;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.OnFastItemClickListener;
import com.quicktvui.hippyext.views.fastlist.ReplaceChildView;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.Callback;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.util.List;

/**
 * tabs组件RenderNode，处理属性、function，业务逻辑在此处理
 */
public class TabsNode extends RenderNode {
    private TabsView tabsView;//自身tabsView
    private FastListView tabListView; //导航列表
    private RecyclerViewPager viewPager;//内容viewPager
    private SparseArray<PageState> mPages;//页面数据及状态，每个tab一个实例
    public static final String TAG = "DebugTabs";
    private TabsState mTabState;
    private boolean isClickBack = false;//当前是否点击返回顶部
    private TabsLocalCache mLocalCache;
    private int cacheLoaded = -1;
    private int markToReloadPage = -1;

    public TabsNode(int mId, HippyMap mPropsToUpdate, String className, HippyRootView rootView, ControllerManager componentManager, boolean isLazyLoad) {
        super(mId, mPropsToUpdate, className, rootView, componentManager, isLazyLoad);
        mPages = new SparseArray<>();
        mTabState = new TabsState();

    }

    public RecyclerViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public View createView() {
        return super.createView();
    }

    @Override
    public View createViewRecursive() {
        final View v = super.createViewRecursive();
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode createViewRecursive done view:" + v);
        }
        //this.tabsView = v;
        return v;
    }

    private boolean isHideOnSingleTab() {
        return getDom() != null && getDom().param.hideOnSingleTab;
    }

    @Override
    public void update() {
        //craeteView
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode update start");
        }
        super.update();
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode update done");
        }
    }

    final PageState getPageState(int pos) {
        if (mPages == null) {
            mPages = new SparseArray<>();
        }
        if (mPages.get(pos) != null) {
            return mPages.get(pos);
        } else {
            final PageState state = new PageState(pos);
            mPages.put(pos, state);
            return state;
        }
    }


    TabsStyleNode getDom() {
        return (TabsStyleNode) getHippyContext().getDomManager().getNode(getId());
    }

    public TabsParam getParams() {
        return getDom() != null ? getDom().param : new TabsParam();
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


    void onTabsDataChanged(HippyArray data) {
        final int defaultFocusPageIndex = mTabState.defaultFocusPosition;
        final int defaultPageIndex = mTabState.defaultPosition;
        if (isHideTabState()) {
            //单tab模式
            if (tabListView != null) {
                tabListView.setVisibility(View.INVISIBLE);
                tabListView.setAlpha(0);
            }
        } else {
            if (tabListView != null) {
                tabListView.setVisibility(View.VISIBLE);
//                 viewPager.resetSuspensionView();
                tabListView.setAlpha(1);
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

    void updateTabsData(HippyArray data) {
        if (tabsView != null) {
            tabsView.clearMemoryFocused();
        }
        final TabsStyleNode tabDom = getDom();
        final RenderNode tabListNode = getTabListNode();
        final int tabsCount = data == null ? 0 : data.size();
        //判断单tab
        final boolean isDataSingleTab = tabsCount < 2;
        mTabState.isDataSingleTab = isDataSingleTab;
        if(getParams() != null) {
            Log.i(TabsLocalCache.TAG, "TabsLocalCache localCacheKey:" + getParams().localCacheKey + ",overTime:" + getParams().outOfDateTimeLocalCache);
        }
        if(getParams() != null && !TextUtils.isEmpty(getParams().localCacheKey)){
            mLocalCache = new TabsLocalCache(viewPager.getContext(),getParams().localCacheKey);
            mLocalCache.setOverTime(getParams().outOfDateTimeLocalCache);
        }
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
        if (tabDom != null && viewPager != null) {
            //设置内容数据
            //viewPager.setOutOfDateTime(tabDom.outOfDateTime);
            viewPager.setInitInfo(tabsCount, mTabState.defaultPosition, data, isHideTabState());
            if (isHideTabState()) {
                viewPager.setInitFocusPosition(mTabState.defaultFocusPosition);
            }

            if(mLocalCache != null){
                Log.i(TAG,"getCacheSavedData defaultPosition :"+mTabState.defaultPosition);
                mLocalCache.getCacheSavedData("tabData_" + mTabState.defaultPosition, new Callback<TabsLocalCache.TabDataCache>() {
                    @Override
                    public void callback(TabsLocalCache.TabDataCache param, Throwable e) {
                        if (param != null && param.dataValid) {
                            Log.i(TabsLocalCache.TAG, "updateTabsData tabCached dataValid:"+ true +",data Array size:"+(param.data == null ? 0 : param.data.size()));
                            if(param.dataValid){
                                //viewPager.setInitData(tabCached.data);
                                HippyArray cacheArray = param.data;
                                HippyMap map = param.pageParams;
                                ThreadUtils.runOnUiThread(() -> {
                                    if (viewPager != null) {
                                        PageItem pi = viewPager.getPageItem(mTabState.defaultPosition);
                                        if (pi == null || !pi.isDataValid()) {
                                            Log.i(TabsLocalCache.TAG, ">>exe setPageData on tabCached dataValid:"+ true +",data Array size:"+(cacheArray == null ? 0 : cacheArray.size()));
                                            viewPager.setPageData(mTabState.defaultPosition, map, cacheArray);
                                            cacheLoaded = mTabState.defaultPosition;
                                        }

                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
        onTabsDataChanged(data);
    }

    void setTabsData(HippyArray var, Promise promise) {
        if(LogUtils.isDebug()) {
            Log.i(TAG, "TabsNode: setTabsData data" +var);
        }
        final HippyMap initParam = var.getMap(0);
        mTabState.initParams(initParam);
        final HippyArray data = var.getArray(1);
        final TabsStyleNode tabDom = getDom();
        if (tabDom != null) {
            tabDom.setDataList(data);
        }
        updateTabsData(data);
    }

    @Override
    public void setDelete(boolean b) {
        Log.e(TAG, "Tabs delete id :" + getId());
        super.setDelete(b);
        this.destroy();
    }


    @Override
    public void dispatchUIFunction(String functionName, HippyArray var, Promise promise) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode dispatchUIFunction functionName:" + functionName + ",var:" + var);
        }
        super.dispatchUIFunction(functionName, var, promise);
        switch (functionName) {
            case "setTabsData":
                TabUtils.logPerformance("setTabsData");
                setTabsData(var, promise);
                break;
            case "requestTabFocus":
                final int tab = var.getInt(0);
                if (tabListView != null && !isHideTabState()) {
                    tabListView.requestChildFocus(tab, View.FOCUS_DOWN);
                }
                break;
            case "setCurrentPage":
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "requestSwitchToPage page:" + var.getInt(0));
                }
                final int page = var.getInt(0);
                if (tabListView != null && !isHideTabState()) {
                    tabListView.setSelectChildPosition(page, true);
                }
                if (viewPager != null) {
                    viewPager.requestSwitchToPage(page, true);
                }
                break;
            case "addPageData":
                if (viewPager != null) {
                    viewPager.addPageData(var.getInt(0), var.getMap(1), var.getArray(2));
                }
                break;
            case "removePageData":
                if (viewPager != null) {
                    viewPager.removePageData(var.getInt(0), var.getInt(1), var.getInt(2));
                }
                break;
            case "insertPageData":
                if (viewPager != null) {
                    viewPager.insertPageData(var.getInt(0), var.getInt(1), var.getArray(2));
                }
                break;
//            updateChildNode(page,position,childIndex,item){
            //2.3
            case "updateChildNode":
                if (viewPager != null) {
                    if (var.size() == 5) {
                        viewPager.updateChildNode(var.getInt(0), var.getInt(1), var.getInt(2), var.get(3), var.getBoolean(4),true);
                    } else if(var.size() == 6){
                        viewPager.updateChildNode(var.getInt(0), var.getInt(1), var.getInt(2), var.get(3), var.getBoolean(4),var.getBoolean(5));
                    }else{
                        viewPager.updateChildNode(var.getInt(0), var.getInt(1), var.getInt(2), var.get(3), true,true);
                    }
                }
                break;
            //2.4
            case "getChildNodeState":
                if (viewPager != null) {
                    HippyMap stateMap;
                    stateMap = viewPager.getChildNodeState(var.getInt(0), var.getInt(1), var.getInt(2), var.getString(3));
                    if (stateMap != null) {
                        stateMap.pushBoolean("valid", true);
                    } else {
                        stateMap = new HippyMap();
                        stateMap.pushBoolean("valid", false);
                    }
                    promise.resolve(stateMap);
                }
                break;
            //2.4
            case "getRootNodeState":
                if (viewPager != null) {
                    HippyMap stateMap;
                    stateMap = viewPager.getRootNodeState(var.getInt(0), var.getInt(1));
                    if (stateMap != null) {
                        stateMap.pushBoolean("valid", true);
                    } else {
                        stateMap = new HippyMap();
                        stateMap.pushBoolean("valid", false);
                    }
                    promise.resolve(stateMap);
                }
                break;
            //2.4
            case "updateRootNode":
                if (viewPager != null) {
                    if(var.size() == 3) {
                        viewPager.updateRootNode(var.getInt(0), var.getInt(1), var.get(2),true);
                    }else if(var.size() == 4){
                        viewPager.updateRootNode(var.getInt(0), var.getInt(1), var.get(2),var.getBoolean(3));
                    }
                }
                break;
            //2.4
            case "dispatchUIFunctionOnChildNode":
                if (viewPager != null) {
                    viewPager.dispatchUIFunctionOnChildNode(var, promise);
                }
                break;
            //2.3
            case "updateItemMatched":
                if (viewPager != null) {
                    if (var.size() == 2) {
                        viewPager.updateItemMatched("id", var.get(0), var.getObject(1),true);
                    } else if (var.size() == 3) {
                        //支持自定义id的key
                        viewPager.updateItemMatched(var.getString(0), var.get(1), var.getObject(2),true);
                    }else if(var.size() == 4){
                        viewPager.updateItemMatched(var.getString(0), var.get(1), var.getObject(2),var.getBoolean(3));
                    }
                }
                break;
            case "setPageData":
                TabUtils.logPerformance("setPageData "+var.getInt(0));
                //Log.i(TabsLocalCache.TAG, "setPageData by user pageIndex:"+var.getInt(0));
                final int pageIndex = var.getInt(0);
                final HippyMap params = var.getMap(1);
                final HippyArray data = var.getArray(2);
                //Log.i(ReplaceChildView.TAG, "setPageData by user pageIndex:"+var.getInt(0)+",params:"+params);
                if(mTabState.defaultPosition == pageIndex && mLocalCache != null){
                    mLocalCache.cachePageToLocal("tabData_"+pageIndex,data,params);
                }
                if (viewPager != null) {
                    if(cacheLoaded  > -1 && cacheLoaded  == pageIndex){
                        Log.i(TabsLocalCache.TAG,"cacheLoaded pageIndex:"+cacheLoaded);
                        cacheLoaded = -1;
                        if(var.size() > 3) {
                            viewPager.setPageData(pageIndex, params, data, var.getBoolean(3));
                        }else{
                            viewPager.setPageData(pageIndex, params, data);
                        }
                    }else{
                        viewPager.setPageData(pageIndex, params, data);
                    }
                }
                break;
            case "requestNodeFocus":
                if (viewPager != null) {
                    viewPager.requestNodeFocus(var.getString(0));
                }
                break;
            case "requestPageFocus":
                if (viewPager != null) {
                    viewPager.requestPageFocus(var.getInt(0));
                }
                break;
            //2.3
            case "updatePageData":
                Log.i(ReplaceChildView.TAG, "updatePageData called var:" + var);
                if (viewPager != null) {
                    if (var.size() == 2) {
                        viewPager.updatePageData(var.getInt(0), var.getArray(1), null);
                    } else {
                        viewPager.updatePageData(var.getInt(0), var.getArray(1), var.getMap(2));
                    }
                }
                break;
            //2.3
            case "reloadAll":
                if (viewPager != null) {
                    if (var.size() == 1) {
                        viewPager.reloadAll(var.getBoolean(0));
                    } else {
                        if(var.size() > 1) {
                            viewPager.reloadAll(var.getBoolean(0),var.getBoolean(1));
                        }else{
                            viewPager.reloadAll(true);
                        }
                    }
                }
                break;
            //2.3
            case "reloadPage":
                if (viewPager != null) {
                    if(var.size() > 1){
                        viewPager.reloadPage(var.getInt(0),var.getBoolean(1));
                    }else {
                        viewPager.reloadPage(var.getInt(0));
                    }
                }
                break;
            //2.4
            case "setFirstFocusTarget":
                if (viewPager != null) {
                    viewPager.setFirstFocusTarget(var.getInt(0), var.getString(1));
                }
                break;
            case "contentScrollToFocus":
                if (viewPager != null) {
                    viewPager.scrollToFocus(var.getInt(0));
                }
                break;
                //2.8.8
            case "performBackKeyPressed":
                performBackKeyPressed();
                break;
            case "invokeContentFunction":
                if (viewPager != null) {
                    if (var.size() == 3) {
                        viewPager.invokeContentFunction(var.getInt(0), var.getString(1), var.getArray(2), promise);
                    } else {
                        viewPager.invokeContentFunction(viewPager.getCurrentItem(), var.getString(0), var.getArray(1), promise);
                    }
                }
                break;
            case "destroy":
                destroy();
                break;
            case "getCurrentPage":
                if (viewPager != null) {
                    promise.resolve(viewPager.getCurrentItem());
                } else {
                    promise.resolve(-1);
                }
                break;
            case "focusBackToTop":
                if (viewPager != null) {
                    if (!mTabState.isDataSingleTab) {
                        tabListView.requestChildFocus(viewPager.getCurrentItem(), View.FOCUS_DOWN);
                    }
                    viewPager.handleBackPressed(viewPager.getCurrentItem(), mTabState.isDataSingleTab);
                }
                break;
            case "translationLeft":
                if (viewPager != null) {
                    viewPager.translationLeft();
                }
                break;
            case "translationRight":
                if (viewPager != null) {
                    viewPager.translationRight();
                }
                break;
            case "cancelAll":
                cancelAll();
                break;
            case "searchReplaceItem":
                viewPager.searchReplaceItemByItemID(var.getString(0), var.getObject(1));
                break;
                //2.8.8
            case "markToReloadPage":
                markToReloadPage = var.getInt(0);
                Log.i(TAG,"markToReloadPage :"+markToReloadPage);
                viewPager.markToReload(markToReloadPage);
                break;
//            case "getState":
//                try {
//                    HippyMap hippyMap = new HippyMap();
//
//                    promise.resolve(hippyMap);
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//                break;
        }
    }

    void cancelAll() {
        Log.i(TAG,"tabs cancelAll ");
        if (viewPager != null) {
            viewPager.cancelAll();
        }
        if (tabsView != null) {
            tabsView.cancelAll();
        }
        if (tabListView != null) {
//            tabListView.ca
            tabListView.pausePostTask();
            tabListView.clearAllTask();
        }

    }

    void getState(HippyMap hippyMap) {
        if (viewPager != null) {
            View pv = viewPager.findWaterfallView(viewPager.getCurrentItem());
            hippyMap.pushInt(TabEnum.CURRENTPAGE.getName(), viewPager.getCurrentItem());
            if (pv != null) {
                FastListView fv = (FastListView) pv;
                HippyMap contentMap = ExtendUtil.getViewState(fv);
                hippyMap.pushMap(TabEnum.CONTENT_STATE.getName(), contentMap);
                if (viewPager.getTabHelpers() != null) {
                    List<TabHelper> tabHelpers = viewPager.getTabHelpers();
                    final TabHelper th = tabHelpers != null ? tabHelpers.get(viewPager.getCurrentItem()) : null;
                    if (th != null) {
                        hippyMap.pushBoolean(TabEnum.IS_SUSPENSION.getName(), th.getFastListPageChangeListener().isOnTop());
                    }
                }
            }
        }
        if (tabListView != null) {
//            HippyMap tabMap = Utils.getFastListState(tabListView);
            HippyMap tabMap = ExtendUtil.getViewState(tabListView);
            hippyMap.pushMap(TabEnum.TAB_STATE.getName(), tabMap);
        }
    }

    protected void destroy() {
        if (LogUtils.isDebug()) {
            Log.i(TAG, "tabsNode destroy called");
        }
        if (tabListView != null) {
            tabListView.destroy();
        }
        if (viewPager != null) {
            viewPager.destroy();
        }
        if (tabsView != null) {
            tabsView.destroy();
        }

    }

    protected HippyEngineContext getHippyContext() {
        return RenderNodeUtils.getHippyContext(mRootView);
    }

    void configTabList(FastListView tabListView, TabsStyleNode node) {
        TabsParam param = node.param;
        tabListView.setEnableSelectOnFocus(!param.useClickMode);
        tabListView.setNegativeKeyTime(10);
        tabListView.setUseDiff(param.useDiff);
        if (param.tabPosition == TabsParam.TAB_POSITION_TOP) {

        }
    }

    /*** tab数据样例
     tabsData:[
     {
     title:'推荐',
     decoration:{right:10},
     content:[
     {
     type:1,
     cover:'https://img0.baidu.com/it/u=553569124,3531403696&fm=253&fmt=auto&app=120&f=JPEG?w=960&h=600',
     text:'myText1',
     title:'hello',
     titleSize:'30px'
     },
     {
     type:1,
     cover:'https://img0.baidu.com/it/u=2911487270,282554651&fm=253&fmt=auto&app=138&f=JPEG?w=360&h=360',
     text:'myText3',
     title:'hello3'
     },
     ],
     },
     {
     title:'精选',decoration:{right:10}
     },
     ]
     */

    /**
     * 配置viewPager
     *
     * @param viewPager
     */
    void configViewPager(RecyclerViewPager viewPager, TabsStyleNode tNode, RenderNode viewPagerNode) {

        //取消viewPager内部寻焦逻辑
        viewPager.setup(tNode.param, viewPagerNode);
        viewPager.setFocusSearchEnabled(false);
        //设置数据回调接口，当viewPager中需要接口时，会回调此方法
        viewPager.setPageDataLoader((pos, callback, useDiff) -> {
            final TabsStyleNode tabDom = getDom();
            if (tabDom == null || tabDom.getTabsData() == null) {
                if (LogUtils.isDebug()) {
                    Log.e(TAG, "setPageDataLoader tabDom == null || tabDom.getTabsData() == null");
                }
                return;
            }
            HippyMap item = tabDom.getTabsData().getMap(pos);
            HippyMap map = new HippyMap();
            if (item.containsKey("content")) {
                HippyMap pageData = item.getMap("content");
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "TabsNode configViewPager dataLoaded page: " + pos + ",data:" + pageData);
                }
                //当前页面中有多少条数据
                map.pushInt("itemCount", pageData == null ? 0 : pageData.size());
                callback.callback(pageData, null);
            } else {
                //callback.callback(null,null);
                map.pushInt("itemCount", 0);
            }
            //发送回调，让上层获取数据,上层通过setPageData来更新数据
            HippyViewEvent event = new HippyViewEvent("onLoadPageData");
            //哪一页的数据
            map.pushInt("pageIndex", pos);
            map.pushMap("data", item);
            map.pushBoolean("useDiff", useDiff);
            event.send(tabsView, map);
        });
        viewPager.setOnPageEventListener(new RecyclerViewPager.OnPageEventListener() {
            @Override
            public void onPageChanged(int page) {
                if (tabsView != null) {
                    if (getParams().useClickMode){
                        tabListView.setSelectChildPosition(page, false);
                        doContentChange(page);
                    }
                    final TabsStyleNode tabDom = getDom();
                    if (tabDom == null || tabDom.getTabsData() == null) {
                        if (LogUtils.isDebug()) {
                            Log.e(TAG, "setOnPageEventListener tabDom == null || tabDom.getTabsData() == null");
                        }
                        return;
                    }
                    HippyMap item = tabDom.getTabsData().getMap(page);
                    HippyViewEvent event = new HippyViewEvent("onPageChanged");
                    HippyMap map = new HippyMap();
                    map.pushInt("pageIndex", page);
                    map.pushMap("data", item);
                    event.send(tabsView, map);
                }
            }
        });
    }

    boolean isHideTabState() {
        return this.tabListView == null || (getDom() != null && (getDom().param.hideOnSingleTab && mTabState.isDataSingleTab));
    }


    void configTabs() {


    }

    public boolean isLayoutVertical() {
        //是否是上下模式
        return tabListView != null && tabListView.getOrientation() == RecyclerView.VERTICAL;
    }

//    void blockRootView() {
//        FocusDispatchView.blockFocus(mPageRootView);
//    }
//
//    void unBlockRootView() {
//        FocusDispatchView.unBlockFocus(mPageRootView);
//    }

    private FastListView findTabListView() {
        FastListView listView = null;
        //通过node获取tabListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof FastListView) {
                listView = (FastListView) nodeView;
                mTabState.listNodeIndex = i;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "TabsNode findTabListView index:" + i + ",view :" + listView);
                }
            }
        }
        return listView;
    }

    private RecyclerViewPager findContentView() {
        RecyclerViewPager v = null;
        //通过node获取tabListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof RecyclerViewPager) {
                mTabState.contentNodeIndex = i;
                v = (RecyclerViewPager) nodeView;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "TabsNode findContentView index:" + i + ",view :" + v);
                }
            }
        }
        return v;
    }

    void doContentChange(int position) {
        if (viewPager != null) {
            if (LogUtils.isDebug()) {
                Log.i("ListViewPagerLog", "PageAdapterEvent:requestSwitchToPage onFocusListener page:" + position + ",prevPage:" + viewPager.mPrevPage);
            }
            //如果是点击返回键到顶部，则不刷新
            if (isClickBack) {
                isClickBack = false;
                return;
            }
            if (position == viewPager.mPrevPage) {
                Log.e("ListViewPagerLog","doContentChange return on current is page shown :"+position);
                return;
            }
            if (mTabState.tabPendingFocus > -1) {
                //Log.d("ListViewPagerLogTest","doContentChange if 当前position值---->"+position);
                viewPager.requestSwitchToPage(position, false);
                mTabState.tabPendingFocus = -1;
            } else {
                final int prevPageDiff = Math.abs(position - viewPager.mPrevPage);
                if (LogUtils.isDebug()) {
                    Log.i("ListViewPagerLog", "PageAdapterEvent:requestSwitchToPage currentDiff page:" + position + ",prevPageDiff" + prevPageDiff);
                }
                //Log.d("ListViewPagerLogTest","doContentChange else 当前position值---->"+position);
                viewPager.requestSwitchToPage(position, true);
            }
            final TabsStyleNode tabDom = getDom();
            if (tabDom != null && tabDom.getTabsData() != null) {
                HippyMap item = tabDom.getTabsData().getMap(position);
                if (item != null) {
                    HippyMap map = new HippyMap();
                    map.pushInt(TabEnum.ITEM_POSITION.getName(), position);
                    map.pushMap(TabEnum.ITEM_DATA.getName(), item);
                    TabUtils.sendTabsEvent(tabsView, TabEnum.TAB_CHANGED.getName(), map);
                }
            }

        }
    }

    @Override
    public void manageChildrenComplete() {
        super.manageChildrenComplete();
        if (LogUtils.isDebug()) {
            Log.i(TAG, "TabsNode manageChildrenComplete childCount :" + getChildCount());
        }
        TabUtils.logPerformance("manageChildrenComplete");
        //通过node获取tabsView
        tabsView = (TabsView) RenderNodeUtils.findViewById(getHippyContext(), getId());

        assert tabsView != null : "tabsView不可为空";
        final TabsStyleNode tabDom = getDom();
        tabsView.setBoundNode(this);
//        mPageRootView = HippyViewGroup.findPageRootView(tabsView);

        Log.i(TAG,"Tabs create params:"+getParams());
        this.tabListView = findTabListView();
        if (tabListView != null) {
            if (getParams().useClickMode) {
                tabListView.getFastAdapter().setOnFastItemClickListener(new OnFastItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        if (LogUtils.isDebug()) {
                            Log.d("--onItemClick--", position + "点击");
                        }
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
//                    Log.i(TAG,"Tab Item hasFocus:"+hasFocus+" position:"+position);
                    HippyMap map = new HippyMap();
                    map.pushInt("position",position);
                    map.pushBoolean("hasFocus",hasFocus);
                    TabUtils.sendTabsEvent(tabsView,"onTabFocusChange",map);
                    if (hasFocus) {
                        if (LogUtils.isDebug()) {
                            Log.d(TAG, position + "获得了焦点");
                        }
                        doContentChange(position);
                        if (viewPager != null) {
                            Log.i(TAG,"Tab Item hasFocus position:"+position+",markToReloadPage:"+markToReloadPage+",current:"+viewPager.getCurrentItem());
                            if(viewPager.getCurrentItem()  == position && markToReloadPage == position){
                                viewPager.reloadPage(position);
                                markToReloadPage = -1;
                            }
                        }
                    }
                });
            }
            configTabList(tabListView, tabDom);
        }
        if (this.viewPager == null) {
            this.viewPager = findContentView();
        }
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode manageChildrenComplete tabList,vp:" + this.viewPager);
        }
//        assert viewPager != null : "viewPager";
        if (viewPager == null) {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "Tabs:tabs内必须指定viewPager");
            }
            return;
        }
        viewPager.setTabsView(tabsView);
        int indexOfViewPager = mTabState.contentNodeIndex;
        configViewPager(viewPager, tabDom, getChildAt(indexOfViewPager));
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode manageChildrenComplete tabListView " + ",viewPager:" + viewPager);
        }
        //fastList组件需要listNode,实际上是第一个子节点
        final RenderNode contentFastListNode = RenderNodeUtils.getRenderNode(viewPager);
        //设置吸顶
        boolean isSuspension = tabDom.isSuspension();
        boolean useSuspensionBg = tabDom.useSuspensionBg();
        boolean isHideOnSingleTab = isHideOnSingleTab();
        if (tabListView != null) {
            viewPager.setSuspensionView(tabListView);
            viewPager.setSuspension(isSuspension);
            viewPager.setUseSuspensionBg(useSuspensionBg);
            viewPager.setHideOnSingleTab(isHideOnSingleTab);
        }
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode manageChildrenComplete tabDom ");
        }
        configTabs();
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
        }

        if (tabDom.isDataListValid()) {
            if (LogUtils.isDebug()) {
                Log.i(TAG, "TabsNode updateTabsData on manageChildrenComplete");
            }
            updateTabsData(tabDom.getTabsData());
        }
    }


    @Override
    public void updateNode(HippyMap map) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsNode updateNode");
        }
        super.updateNode(map);
    }

    public void onChunkAttachedToWindow(Chunk chunk) {
        if (viewPager != null) {
            viewPager.onChunkAttachedToWindow(chunk);
        }
    }

    final static class PageState {
        final int pos;

        PageState(int pos) {
            this.pos = pos;
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

    boolean performBackKeyPressed(){
        if (isHideTabState()) {
            final View v = viewPager.findWaterfallView(0);
            if (v != null) {
                FastListView fv = (FastListView) v;
                if (fv.isShakeEnd()) {
                    return viewPager.hasFocus() && viewPager.handleBackPressed(0, true);
                }
                return viewPager.hasFocus();
            }
            return viewPager.hasFocus() && viewPager.handleBackPressed(0, true);
        } else if (tabListView != null) {
            final int selection = tabListView.getSelectChildPosition();
            final int defaultPosition = mTabState.defaultPosition;
//            if (LogUtils.isDebug()) {
                Log.v("TabsNodeDispatchEvent", "selection:" + selection + " === defaultPosition:" +
                        "" + defaultPosition + " === tabListViewFocus:" + tabListView.hasFocus() +
                        " === viewPageFocus:" + viewPager.hasFocus());
//            }
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
            } else if (viewPager.hasFocus()) {
                final View v = viewPager.findWaterfallView(selection);
                if (v != null) {
                    FastListView fv = (FastListView) v;
                    //如果这里竖向排布，按返回时，默认先回到顶部
                    if(viewPager.getOrientation() == ViewPager2.ORIENTATION_VERTICAL){
                        //纵向单独处理
                        boolean handle = viewPager.handleBackPressed(selection, false);
                        if (!handle) {
                            tabListView.requestChildFocus(selection, View.FOCUS_DOWN);
                        }
                        Log.i(TAG,"handleBackPressed on vertical handle:"+handle);
                        return true;
                    }else if (fv.isShakeEnd()) {
                        isClickBack = true;
                        //焦点在content里，将焦点还给tab
                        tabListView.requestChildFocus(selection, View.FOCUS_DOWN);
                        viewPager.handleBackPressed(selection, false);
                    }
                    return true;
                }
            }
        }
        return false;
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
            if (performBackKeyPressed()) {
                return true;
            }
        }
        return false;
    }
}
