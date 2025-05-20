package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.hippyext.AutoFocusManager;
import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.hippyext.TriggerTaskManagerModule;
import com.quicktvui.hippyext.views.fastlist.FastAdapter;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.FastPendingView;
import com.quicktvui.hippyext.views.fastlist.OnFastScrollStateChangedListener;
import com.quicktvui.hippyext.views.fastlist.ReplaceChildView;
import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.quicktvui.hippyext.views.fastlist.Utils;
import com.quicktvui.support.ui.viewpager.utils.RenderNodeUtils;
import com.quicktvui.support.ui.viewpager.utils.TabEnum;
import com.quicktvui.support.ui.viewpager.utils.TabUtils;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.CustomControllerHelper;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.hippy.uimanager.InternalExtendViewUtil;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.utils.PixelUtil;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

import java.util.ArrayList;

public class SingleTabContentAdapter extends PageContentAdapter {

    private static final String DEFAULT_POOL_NAME = "DebugSingleTab";
    static final String TAG = DEFAULT_POOL_NAME;
    final RecyclerViewPager mViewPager;
    boolean requestAutofocusOnPageChange = false;

    private WaterfallListView singleWaterfallView;

    @Override
    public void setRequestAutofocusOnPageChange(boolean requestAutofocusOnPageChange) {
        this.requestAutofocusOnPageChange = requestAutofocusOnPageChange;
    }

    int loadingType = 0;
    int emptyType = 0;
    int errorType = 0;

    protected SingleTabContentAdapter(Context context, RecyclerViewPager mViewPager, HippyMap initProp) {
        super(context);
        this.mViewPager = mViewPager;
//        if(initProp.containsKey("stateType")){
//        HippyMap map = initProp.getMap("stateType");
        loadingType = initProp.getInt("loadingItemType");
//            emptyType = map.getInt("empty");
//            errorType = map.getInt("error");
//        }
    }

    boolean isPreferSaveMemory(){
        return mViewPager == null || mViewPager.isPreferSaveMemory();
    }

    @Override
    public void onViewAttachedToWindow(View itemView, int bindingAdapterPosition) {
        super.onViewAttachedToWindow(itemView, bindingAdapterPosition);
//        TabUtils.unBlockFocus(itemView);
    }


    @Override
    public void onViewDetachedFromWindow(View itemView, int bindingAdapterPosition) {
        super.onViewDetachedFromWindow(itemView, bindingAdapterPosition);
//        if(isBlockPausePageFocus()) {
//            TabUtils.blockFocus(itemView);
//        }
    }

    @Override
    public void onBindViewHolder(View itemView, int position) {
        super.onBindViewHolder(itemView, position);
//        if(isBlockPausePageFocus()) {
//            TabUtils.blockFocus(itemView);
//        }


    }

    @Nullable WaterfallListView getSingleWaterfallView(View v){
        return singleWaterfallView;
    }

    @Override
    public void requestFirstFocus(View v, int position) {
        super.requestFirstFocus(v, position);
        FastListView fv = getSingleWaterfallView(v);
        //fv.requestChildFocus(position, View.FOCUS_DOWN);
        if(fv.isFocused() && fv.getChildCount() > 0){
            fv.requestChildFocus(0,View.FOCUS_DOWN);
        }else{
            fv.requestFocus(View.FOCUS_DOWN);
        }
        Log.e(TAG, "requestFirstFocus fv getChildCount:" + fv.getChildCount());
    }

    @Override
    public void dispatchUIFunction(View view, int pageIndex, String functionName, HippyArray var, Promise promise) {
        final HippyEngineContext context = Utils.getHippyContext(view);
        final FastListView fv = getSingleWaterfallView(view);

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

    @Override
    public void scrollToFocus(View view, int pos) {
        super.scrollToFocus(view, pos);
        FastListView fv = getSingleWaterfallView(view);
        fv.scrollToFocus(pos);
    }

    private boolean isBlockPausePageFocus(){
//        if (mViewPager != null) {
//            if(mViewPager.isSlidingEnable()){
//                return false;
//            }
//        }
        return   isPreferSaveMemory() || (mViewPager.getTabsParam() != null && mViewPager.getTabsParam().resumeTaskDelay > 1);
    }

    @Override
    View createContentView(RenderNode templateNode, View agentView) {
        TabWrapperView wrapperView = new TabWrapperView(context);
        if (singleWaterfallView == null) {
            final WaterfallListView listView = new WaterfallListView(context, templateNode.getProps());
            listView.setAgentView(agentView);
//            listView.setBackgroundColor(Color.GRAY);
            listView.getFastAdapter().setEnablePlaceholder(false);
            listView.setFocusable(false);
            if(isBlockPausePageFocus()) {
                TabUtils.blockFocus(listView);
                listView.setSkipFocusOnPause(true);
            }
            listView.getLayoutManagerCompat().setSearchFocusInItem(true);
            listView.getLayoutManagerCompat().setFocusEventListener(new TVListView.FocusEventListener() {
                @Override
                public View onInterceptFocusSearch(@NonNull View focused, int direction) {
                    if (mViewPager != null) {
                        mViewPager.onInterceptContentViewFocusSearch(listView,focused,direction);
                    }
                    return  super.onInterceptFocusSearch(focused, direction);
                }

                @Override
                public View onInterceptFocusSearchFailed(View focused, int focusDirection, int vector) {
                    if (vector == 0) {
                        final int current = mViewPager.getCurrentItem();
                        final int total = mViewPager.getTotalPage();
                        if (mViewPager.getTabsParam() != null && InternalExtendViewUtil.isContainBlockDirection(focusDirection, mViewPager.getTabsParam().blockFocusDirections)) {
                            if (LogUtils.isDebug()) {
                                Log.e(TAG, "return focused on isContainBlockDirection");
                            }
                            return focused;
                        }
                        //这里来确定焦点拦截
                        //int vectorForVp2 = TabUtils.getVectorByDirection(focusDirection, mViewPager.getOrientation());

                        boolean containBLock = InternalExtendViewUtil.isContainBlockDirection(focusDirection, mViewPager.mBlockFocusOnFail);
                        if (containBLock) {
                            if (LogUtils.isDebug()) {
                                Log.e(TAG, "onInterceptFocusSearchFailed focused:" + focused + ",current:" + current + ",total:" + total + ",containBLock:" + containBLock);
                            }
                        } else {
                            if (LogUtils.isDebug()) {
                                Log.d(TAG, "onInterceptFocusSearchFailed focused:" + focused + ",current:" + current + ",total:" + total + ",containBLock:" + containBLock);
                            }
                        }
                        if (containBLock) {
                            HippyViewEvent event = new HippyViewEvent("onContentFocusSearchFailed");
                            HippyMap map = new HippyMap();
                            map.pushInt("direction", focusDirection);
                            map.pushInt("currentPage", mViewPager.getCurrentItem());
                            event.send(mViewPager, map);
                        }
                        return containBLock ? focused : null;
                    } else {
                        //这里会造成单页面焦点下不去
//                    if (mViewPager.getTabsParam() != null && InternalExtendViewUtil.isContainBlockDirection(focusDirection, mViewPager.getTabsParam().blockFocusDirections)) {
//                        if (LogUtils.isDebug()) {
//                            Log.e(TAG, "return focused on isContainBlockDirection");
//                        }
//                        return focused;
//                    }
                        Log.e(TAG,"onInterceptFocusSearchFailed on focusDirection: "+focusDirection+",vector:"+vector);
                    }
                    if (mViewPager.useAdvancedFocusSearch) {
                        View v = HippyViewGroup.findPageRootView(mViewPager);
                        if (v instanceof HippyViewGroup) {
                            HippyViewGroup root = (HippyViewGroup) v;
                            View view = root.findNextSpecialFocusView(focused, focusDirection);
                            Log.e(TAG, "onInterceptFocusSearchFailed special : " + view);
                            return view;
                        } else {
                            Log.e(TAG, "onInterceptFocusSearchFailed special root is null");
                        }
                    }
                    return null;
                }
            });
            if (LogUtils.isDebug()) {
                Log.d(TAG, "createContentView listView:" + listView);
            }
            final String cachePoolName = DEFAULT_POOL_NAME + hashCode();
            listView.setCachePoolName(cachePoolName);
            listView.setTemplateNode(templateNode);
            RenderNodeUtils.doDiffProps(RenderNodeUtils.findViewController(listView, templateNode), templateNode.getProps(), listView);
            listView.setOnScrollStateChangedListener(new OnFastScrollStateChangedListener() {
                @Override
                public void onScrollStateChanged(int lastState, int state, int dx, int dy) {
                    HippyMap stateMap = new HippyMap();
                    stateMap.pushInt("oldState", lastState);
                    stateMap.pushInt("newState", state);
                    HippyMap contentOffset = new HippyMap();
                    contentOffset.pushDouble("x", PixelUtil.px2dp(listView.getOffsetX()));
                    contentOffset.pushDouble("y", PixelUtil.px2dp(listView.getOffsetY()));
                    HippyMap map = new HippyMap();
                    map.pushMap("state", stateMap);
                    map.pushMap("contentOffset", contentOffset);
                    TabUtils.sendTabsEvent(listView.getAgentView(), TabEnum.ON_SCROLLSTATE_CHANGED.getName(), map);

                    if (mViewPager != null) {
                        map.pushInt("pageIndex", mViewPager.getCurrentItem());
                        if (LogUtils.isDebug()) {
                            Log.i(TAG, "onScrollStateChanged lastState:" + lastState + ",state:" + state + ",dx:" + dx + ",dy:" + dy);
                        }
                        if (state == RecyclerView.SCROLL_STATE_IDLE) {
                            if (lastState != state) {
                                TriggerTaskManagerModule.dispatchTriggerTask(mViewPager, "onContentScrollStateIdle");
                            }
                        } else if (lastState == RecyclerView.SCROLL_STATE_IDLE) {
                            TriggerTaskManagerModule.dispatchTriggerTask(mViewPager, "onContentScrollStateScrolling");
                        }
                    }
                }

                @Override
                public void onTriggerScrollYGreater() {
                    TriggerTaskManagerModule.dispatchTriggerTask(mViewPager, "onContentScrollYGreater");
                }

                @Override
                public void onTriggerScrollYLesser() {
                    TriggerTaskManagerModule.dispatchTriggerTask(mViewPager, "onContentScrollYLesser");
                }
            });
            singleWaterfallView = listView;
        }
        return wrapperView;
    }

    @Override
    public void clearPageData(int position, View pv) {
        super.clearPageData(position, pv);
//        FastListView fv = getSingleWaterfallView(pv);
//        if (fv != null && fv.getFastAdapter() != null && fv.getFastAdapter().getItemCount() > 0) {
//            fv.pausePostTask();
//            if (LogUtils.isDebug()) {
//                Log.e(FastAdapter.TAG_POST, "SCROLL_POSTER clearAllTask on ViewPager2 clearPageData position " + position);
//            }
//            fv.clearAllTask();
//            fv.clearData();
//            //fv.getFastAdapter().clearData();
//            // fv.getAdapter().notifyDataSetChanged();
//        }
    }

    @Override
    public boolean searchReplaceItemTraverse(int pageIndex, View pv, PageItem pageItem,String sid, Object itemData) {
        return ExtendUtil.searchReplaceItemByItemID(getSingleWaterfallView(pv), sid, itemData);
    }

    // FIXME sdk<19时，isInLayout方法会报VerifyError，需要删除
    @Override
    public void updateItemByID(int pageIndex, View pv, PageItem pageItem, int itemPosition, Object itemData,boolean traverse) {
        FastListView fv = getSingleWaterfallView(pv);
        HippyArray array = (HippyArray) pageItem.pageData.rawData;
        if (LogUtils.isDebug()) {
            Log.i(TAG, " updateItemByID pageIndex:" + pageIndex + ",itemPosition:" + itemPosition + ",itemData:" + itemData);
        }
        if (array != null) {
            if (fv.isComputingLayout() || fv.isInLayout()) {
                Log.e(TAG, "updateItemByID return on InLayout");
                return;
            }
            fv.updateItem(itemPosition, itemData,traverse);
        }
    }

    FastPendingView findListViewFromChild(View view) {
        if (view instanceof FastPendingView) {
            return (FastPendingView) view;
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                FastPendingView result = findListViewFromChild(((ViewGroup) view).getChildAt(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static @Nullable HippyArray getChildrenFromItem(HippyMap item){
        if (item == null) {
            return null;
        }
        HippyArray result = item.getArray("children");
        if (result != null) {
            return result;
        }else{
            return item.getArray("itemList");
        }

    }

    @Override
    public void updateChildItemByID(View pv, PageItem pageItem, int itemPosition, int childIndex, Object itemData,boolean traverse) {
        FastListView fv = getSingleWaterfallView(pv);
        View sectionView = fv.findViewByPosition(itemPosition);
        if (LogUtils.isDebug()) {
            Log.i(TAG, " updateChildItemByID findViewByPosition itemPosition:" + itemPosition + ",sectionView:" + sectionView + ",childIndex:" + childIndex);
        }
        //HippyArray pageArray = (HippyArray) pageItem.pageData.rawData;
        if (fv.getFastAdapter() != null) {
            Object rootItem = fv.getFastAdapter().getRawObject(itemPosition);
            if (rootItem instanceof HippyMap) {
                HippyArray array = getChildrenFromItem((HippyMap) rootItem);
                if (array != null) {
                    array.setObject(childIndex, itemData);
                }
            }
            if (LogUtils.isDebug()) {
                Log.i(TAG, "updateChildItemByID rootItem : " + rootItem);
            }
            //HippyArray array = new HippyArray();
        }
        FastPendingView itemView = findListViewFromChild(sectionView);
        if (itemView != null) {
            itemView.updateItem(childIndex, itemData,traverse);
        } else {
            Log.w(TAG, "updateChildItemByID error ,cant find target view , itemPosition:" + itemPosition + ",childIndex:" + childIndex);
        }

    }

    @Override
    public View findRootItemView(View v, int itemPosition) {
        final WaterfallListView pv = getSingleWaterfallView(v);
        if (pv == null) {
            Log.e(TAG, "findRootItemView error on pv is :" + pv);
            return null;
        }
        return pv.findViewByPosition(itemPosition);
    }

    @Override
    public void bindPageData(int position, View pv, PageItem pageItem, RenderNode templateNode
            , FastListPageChangeListener fastListPageChangeListener,
                             FastListScrollToTopListener fastListScrollToTopListener, boolean useDiff) {
        WaterfallListView fv = getSingleWaterfallView(pv);
        changeContentParent(pv,position);
        fv.setBindPosition(position);
        Log.i(TAG,">>>bindPageData position:"+position);
        fv.viewPager = mViewPager;
        if (pageItem.tabsParam != null) {
            fv.setUseDiff(pageItem.tabsParam.useDiff);
        }
        fv.getEventDeliverer().setOnEventListener((hippyViewEvent, i, hippyEngineContext, params) -> {
            if (params != null) {
                params.pushInt("pageIndex", position);
            }
        });
        fv.setOnLayoutListener(new TVListView.OnLayoutListener() {

            @Override
            public void onBeforeLayout(TVListView tvListView, RecyclerView.State state) {

            }

            @Override
            public void onLayoutComplete(TVListView tvListView, RecyclerView.State state) {
                //Log.v("ListViewPagerLog","onLayoutComplete tvListView :"+tvListView);
                if (tvListView instanceof WaterfallListView) {

                    if (mViewPager != null) {
                        final int pos = ((WaterfallListView) tvListView).getBindPosition();
                        final PageItem pi = mViewPager.getPageItem(pos);
                        if(!pi.resumedOnLayout) {
                            if (LogUtils.isDebug()) {
                                Log.e("ListViewPagerLog", "requestResumeCurrentPage on onLayoutComplete :" + ((WaterfallListView) tvListView).getBindPosition());
                            }
                            pi.resumedOnLayout = true;
                            mViewPager.requestResumeCurrentPage(((WaterfallListView) tvListView).getBindPosition());
                        }
                    }
                }

            }

        });
        fv.setOnLoadMoreListener((pos, itemCount) -> {
            if (fv.getAgentView() != null) {
                if (itemCount < 2) {
                    //只有一个item，判定为无需laodMore
                    if (LogUtils.isDebug()) {
                        Log.e(TAG, " onLoadMore checking return on only one item");
                    }
                    return;
                }
                HippyMap map = new HippyMap();
                map.pushInt("pageIndex", position);
                map.pushInt("itemPosition", pos);
                map.pushInt("itemCount", itemCount);
                fv.sendScrollEvent(fv.getAgentView(), TabEnum.ON_LOADMORE.getName(), map);
            }
        });
        HippyArray array = (HippyArray) pageItem.pageData.rawData;
        HippyMap params = pageItem.pageData.params;
        if (params != null) {
            boolean disableScrollOnFirstScreen = params.getBoolean("disableScrollOnFirstScreen");
            fv.getLayoutManagerCompat().setNoScrollOnFirstScreen(disableScrollOnFirstScreen);
            pageItem.disableScrollOnFirstScreen = disableScrollOnFirstScreen;
            fv.setFirstFocusTarget(params.getString("firstFocusTargetID"));

        }
        //fv.scrollToPosition(0);
        if (LogUtils.isDebug()) {
            Log.i(FastAdapter.TAG_POST, "SCROLL_POSTER setPendingData on ViewPager2  position:" + position);
        }
        if (array != null) {
            fv.setPendingData(array, templateNode, false, useDiff);
            //this.setPendingData(data, templateNode, false, this.useDiff);
        } else {
            fv.setPendingData(new HippyArray(), templateNode, false, useDiff);
        }
        //fv.clearOnScrollListeners();
        //fv.addOnScrollListener(fastListPageChangeListener);
        fv.setOnScrollListener(fastListPageChangeListener);
        fv.setScrollToTopListener(fastListScrollToTopListener);
    }

    @Override
    public void addPageData(int position, View pv, HippyArray data, int deleteCount) {
        FastListView fv = getSingleWaterfallView(pv);
//        TabUtils.blockRootFocus(fv);
        fv.addData(data, deleteCount);
        if (LogUtils.isDebug()) {
            Log.e(TAG, " addPageData page :"+position+",deleteCount:"+deleteCount+",data size:"+(data == null ? 0 : data.size()));
        }
//        fv.postDelayed(() -> TabUtils.unBlockRootFocus(fv),200);
    }

    @Override
    public void removePageData(int position, View pv,  int deleteCount) {
        FastListView fv = getSingleWaterfallView(pv);
        fv.deleteItemRange(position,deleteCount);
    }

    @Override
    public void insertPageData(int position, View pv, HippyArray data) {
        FastListView fv = getSingleWaterfallView(pv);
        fv.insertItemRange(position,data);
    }


    @Override
    public void pausePostTask(int position, View pv) {
        super.pausePostTask(position, pv);
        Log.i(TAG,"pausePostTask position:"+position);
        FastListView fv = getSingleWaterfallView(pv);
        fv.pausePostTask();
        if(isBlockPausePageFocus()) {
            TabUtils.blockFocus(fv);
        }
//        fv.notifyBringToFront(false);
        notifyAllBringToFront(fv,false);
    }

    @Override
    public boolean isSingleContent() {
        return true;
    }

    @Override
    public void onBeforeChangeCurrentPage(View parentView, int nextPage, int prevPage) {
        super.onBeforeChangeCurrentPage(parentView,nextPage, prevPage);
        if (singleWaterfallView != null) {
            singleWaterfallView.clearAllTask();
            singleWaterfallView.recycle();
        }
//        changeContentParent(parentView,nextPage);
    }

    void changeContentParent(View parentView,int page){
        Log.i(TAG,"exe changeContentParent page:"+page);
        if (parentView instanceof TabWrapperView) {
            if (singleWaterfallView != null) {
                ((TabWrapperView) parentView).pushContentView(singleWaterfallView);
            }
        }
    }

    static void removeFromParentIfNeed(View view){
        if (view != null && view.getParent() instanceof ViewGroup) {
            //删除view
            if(LogUtils.isDebug()) {
                Log.i(ReplaceChildView.TAG, "removeFromParentIfNeed view:" + ExtendUtil.debugView(view) + ",parent:" + ExtendUtil.debugView((View) view.getParent()));
            }
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void resumePostTask(int position, View pv) {
        super.resumePostTask(position, pv);
        FastListView fv = getSingleWaterfallView(pv);
        Log.e(TAG,"resumePostTask position:"+position+",isPause:"+fv.isPostTaskPaused());
        if(fv.isPostTaskPaused()){
            fv.resumePostTask();
//            fv.notifyBringToFront(true);
            TabUtils.unBlockFocus(fv);

//            RenderUtil.reLayoutView(fv);
//            RenderUtil.reLayoutView(mViewPager);
            if(requestAutofocusOnPageChange) {
                AutoFocusManager.requestAutofocusTraverse(fv);
            }
        }
//        Log.e("IndieViewLog","++++++++++WaterfallPageView resumed position:"+position);
        notifyAllBringToFront(fv,true);
    }

    private void notifyAllBringToFront(View v,boolean front){
        if(v instanceof FastAdapter.ScrollTaskHandler){
            ((FastAdapter.ScrollTaskHandler) v).notifyBringToFront(front);
        }
        if(v instanceof ViewGroup){
            for(int i = 0 ; i < ((ViewGroup) v).getChildCount(); i ++){
                notifyAllBringToFront(((ViewGroup) v).getChildAt(i),front);
            }
        }
    }

    @Override
    public void recycle(int position, View pv) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "recycle on " + position);
        }
//        FastListView fv = getSingleWaterfallView(pv);
//        fv.getEventDeliverer().setOnEventListener(null);
//        fv.clearOnScrollListeners();
//        if (fv instanceof WaterfallListView) {
//            ((WaterfallListView) fv).viewPager = null;
//        }
//        fv.pausePostTask();
//        if (LogUtils.isDebug()) {
//            Log.e(FastAdapter.TAG_POST, "SCROLL_POSTER clearAllTask on ViewPager2 recycle position:" + position);
//        }
//        fv.clearAllTask();
//        fv.setOnLayoutListener(null);
//        fv.setOnScrollListener(null);
//        fv.setOnLoadMoreListener(null);
//        fv.setScrollToTopListener(null);
//        if (fv.getFastAdapter() != null && fv.getFastAdapter().getItemCount() > 0) {
//            fv.getFastAdapter().clearData();
//        }
    }

    @Override
    public void changeLoading(View view, int pos, boolean show) {
        super.changeLoading(view, pos, show);
//        if (loadingType != 0) {
//            if (show) {
//                FastListView fv = getSingleWaterfallView(view);
//                fv.setList(getPageData(loadingType));
////                RenderUtil.reLayoutView(fv);
//            }
//        }
    }

    @Override
    public boolean isLoadingShown(View view, int pos) {
        // return super.isLoadingShown(view, pos);
//        if (loadingType != 0) {
//            FastListView fv = getSingleWaterfallView(view);
//            if (fv.getFastAdapter() != null) {
//                return fv.getFastAdapter().getItemCount() == 1 && fv.getFastAdapter().getItemViewType(0) == loadingType;
//            }
//        }
        return false;
    }

    @Override
    public void reset(int position, View pv) {
        super.reset(position, pv);
        FastListView fv = getSingleWaterfallView(pv);
        fv.recycle();
        fv.clearData();
        fv.pausePostTask();
        if (LogUtils.isDebug()) {
            Log.e(FastAdapter.TAG_POST, "SCROLL_POSTER clearAllTask on ViewPager2 recycle position:" + position);
        }
        fv.clearAllTask();
    }

    private HippyArray getPageData(int type) {
        HippyArray array = new HippyArray();
        HippyMap map = new HippyMap();
        map.pushInt("type", type);
        array.pushMap(map);
        return array;
    }

    @Override
    public void reuseAfterRecycle(View pv) {
        super.reuseAfterRecycle(pv);
        // FastListView fv = (FastListView) pv;
    }

    @Override
    public void contentToTop(View v, int pos) {
        FastListView fv = getSingleWaterfallView(v);
        fv.scrollToTop();
    }

    @Override
    public boolean isOnScrollTop(View v, int pos, float checkValue) {
        FastListView fv = getSingleWaterfallView(v);
        if (fv.getOrientation() == RecyclerView.VERTICAL) {
            return fv.getOffsetY() < checkValue;
        } else {
            return fv.getOffsetX() < checkValue;
        }
    }

    @Override
    public void setDisplay(View view, int position, boolean b) {
//        view.setAlpha(b ? 1 : 0);
//        FastListView fv = getSingleWaterfallView(v)iew;
//        fv.setDisplay(b,false);
        if (mViewPager.getTabsParam() != null && mViewPager.getTabsParam().disableScrollAnimation) {
            FastListView fv = getSingleWaterfallView(view);
            fv.setDisplay(b,false);
        }
    }

    @Override
    public void destroy(View v) {
        super.destroy(v);
        FastListView pv = getSingleWaterfallView(v);
        if (pv != null) {
            pv.destroy();
            pv.setOnLayoutListener(null);
        }
        singleWaterfallView = null;
        //FastAdapter.clearGlobalCache();
    }

    static class TabWrapperView extends ViewGroup {

        private WaterfallListView contentView;
        public TabWrapperView(@NonNull Context context) {
            super(context);
            setFocusable(false);
//            setBackgroundColor(Color.);
        }

        public WaterfallListView getContentView() {
            return contentView;
        }

        @Override
        public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
            Log.i(TAG,"addFocusables contentView:"+contentView);
            if (contentView != null) {
                contentView.addFocusables(views,direction,focusableMode);
            }else {
                super.addFocusables(views, direction, focusableMode);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            if (contentView != null) {
//                contentView.measure(widthMeasureSpec,heightMeasureSpec);
//            }
        }

        public void pushContentView(WaterfallListView contentView){
            if (contentView.getParent() == this) {
                Log.i(TAG,"pushContentView return on same parent ");
                return;
            }else{
                removeFromParentIfNeed(contentView);
            }
            if (getChildCount() > 0) {
                removeAllViews();
            }
            Log.i(TAG,">>>changeContent to  this:"+this);
            addView(contentView);
            if (getWidth() > 0 && getHeight() > 0) {
                RenderUtil.reLayoutView(contentView,0,0,getWidth(),getHeight());
            }
            this.contentView  = contentView;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (contentView != null) {
                RenderUtil.reLayoutView(contentView,0,0,w,h);
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (contentView != null) {
                RenderUtil.reLayoutView(contentView,0,0,r-l,b - t );
            }
        }
    }
}
