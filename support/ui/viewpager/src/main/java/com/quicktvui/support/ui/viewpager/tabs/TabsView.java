package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.quicktvui.hippyext.AutoFocusManager;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.FastListViewController;
import com.quicktvui.base.ui.waterfall.Chunk;
import com.quicktvui.base.ui.waterfall.Tabs;
import com.quicktvui.support.ui.viewpager.utils.TabUtils;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.uimanager.ViewStateProvider;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

import com.quicktvui.support.ui.viewpager.utils.RenderNodeUtils;

public class TabsView extends HippyViewGroup implements ViewStateProvider, Tabs {

    private TabsNode boundNode;
    private SingleTabsNode singleBoundNode;
    //监听content里tv-list的onScroll事件
    private boolean mListenScrollEvent = false;

    String autoFocusID = null;

    AutoFocusManager mAutoFocusManager;

    public static final String TAG = "DebugTabs";
    public static final boolean DEBUG = LogUtils.isDebug();

    public TabsView(Context context) {
        super(context);
        this.mAutoFocusManager = new AutoFocusManager(this);
    }

    public void setBoundNode(TabsNode boundNode) {
        this.boundNode = boundNode;
    }

    public TabsNode getBoundNode() {
        return boundNode;
    }

    public void setSingleBoundNode(SingleTabsNode singleBoundNode) {
        this.singleBoundNode = singleBoundNode;
    }

    public void setAutoFocusID(String id){
        boolean run = id != null && !id.equals(autoFocusID);
        this.autoFocusID = id;
        if(run){
            AutoFocusUtils.setAppearFocusTag(this,id,0);
        }
    }

    @Nullable
    ESRecyclerViewPagerController getContentController() {
        final RenderNode node = RenderNodeUtils.getRenderNode(this);
        if (node.getChildCount() > 1) {
            return RenderNodeUtils.findViewController(getHippyContext(), node.getChildAt(1));
        }
        return null;
    }

    @Nullable
    FastListViewController getTabListController() {
        final RenderNode node = RenderNodeUtils.getRenderNode(this);
        if (node.getChildCount() > 0) {
            return RenderNodeUtils.findViewController(getHippyContext(), node.getChildAt(0));
        }
        return null;
    }

    public void destroy(){
        boundNode = null;
        singleBoundNode = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (LogUtils.isDebug()){
            Log.e(TAG,"onDetachedFromWindow view count:"+getChildCount());
        }
    }

    public void setTabsData(HippyArray data) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "setTabsData data:" + data);
        }
    }

    private FastListView findTabView() {
        return getChildCount() > 0 ? (FastListView) getChildAt(0) : null;
    }

    private RecyclerViewPager findContentView() {
        return getChildCount() > 1 ? (RecyclerViewPager) getChildAt(1) : null;
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
    }

    @Override
    protected HippyEngineContext getHippyContext() {
        return super.getHippyContext();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (boundNode != null && boundNode.dispatchKeyEvent(event)) {
            //被拦截了，直接返回true
            return true;
        }
        if (singleBoundNode != null && singleBoundNode.dispatchKeyEvent(event)) {
            return true;
        }
        //执行系统默认
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void getState(@NonNull HippyMap hippyMap) {
        super.getState(hippyMap);
        if(boundNode != null){
            boundNode.getState(hippyMap);
        }
    }

    public boolean isListenScrollEvent() {
        return mListenScrollEvent;
    }

    public void setListenScrollEvent(boolean mListenScrollEvent) {
        this.mListenScrollEvent = mListenScrollEvent;
    }

    public void cancelAll() {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        TabUtils.logPerformance("Tabs onAttachedToWindow");
    }

    @Override
    public void onChunkAttachedToWindow(Chunk chunk) {
        if(boundNode != null){
            boundNode.onChunkAttachedToWindow(chunk);
        }
    }
}
