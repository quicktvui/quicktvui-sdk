package com.quicktvui.support.ui.viewpager.tabs;

import android.util.Log;

import com.quicktvui.base.ui.ITVView;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.utils.LogUtils;

public class PageItem {
    static String TAG = "ListViewPagerLog";
    int position;
    public int pendingFocusPosition = -1;
    RecyclerViewPager.PageData pageData;
    Runnable postDataTask;
    boolean updateDirty = true;
    boolean disableScrollOnFirstScreen = false;
    int dataState = -1;
    long loadTime = -1;
    boolean drawerAnimationDirty = true;
    boolean drawerAnimationStartDirty = true;
    boolean updateDirtyOnNullPageView = false;
    String bindingPlayer = "";
    TabsParam tabsParam;
    ITVView bindPlayerView;
    int currentPlayerViewIndex = -1;
    boolean resumedOnLayout = false;
    //-1 no data
    //0 loading
    //1 dataValid

    PageItem(int position) {
        this.position = position;
    }

    public boolean isNeedLoad() {
        //只有没有加载过数据，或者数据过期了才重新加载数据，数据加载中也不加载
        boolean b = false;
        final int strategy = tabsParam.loadDataStrategy;
        if (strategy == TabsParam.STRATEGY_ALWAYS) {
            b = dataState != 0;//loading时不需加载
        } else if (strategy == TabsParam.STRATEGY_ONE_SHOT) {
            b = dataState == -1;
        } else if (strategy == TabsParam.STRATEGY_OVER_TIME) {
            if (dataState == -1) {
                b = true;
            } else {
                assert (tabsParam.outOfDateTime > 0) : "outOfDateTime必须大于0";
                long now = System.currentTimeMillis();
                boolean outOfDate = loadTime == -1 || ((now - loadTime) > tabsParam.outOfDateTime);
                if (outOfDate) {
                    if (LogUtils.isDebug()) {
                        Log.e(TAG, "PageItem isNeedLoad outOfDate true loadTime:" + loadTime + ",pi:" + this);
                    }
                }
                b = outOfDate;
            }
        }
        if (LogUtils.isDebug()) {
            Log.d(TAG, "PageItem isNeedLoad :" + b + ",loadTime :" + loadTime + ",dataState:" + dataState + ",this:" + this);
        }
        return b;
    }

    public void insertSections(int position, HippyArray data) {
        if (LogUtils.isDebug()) {
            Log.v(TAG, "PageItem insertPageData pi:" + this + ",position:" + position + ",data:" + data);
        }
        if (pageData == null) {
            pageData = new RecyclerViewPager.PageData(data);
        }else {
            if (pageData.rawData instanceof HippyArray) {
                ((HippyArray) pageData.rawData).addObject(position, data);
                Log.i(TAG,"PageItem insertPageData success position:"+position);
            }else{
                Log.e(TAG,"PageItem insertPageData pageData.rawData is not HippyArray");
            }
        }
    }

    public void removeSections(int position,int count){
        if(pageData != null && pageData.rawData instanceof HippyArray){
            for(int i = 0; i < count; i++) {
                ((HippyArray) pageData.rawData).removeAt(position);
            }
        }else{
            Log.e(TAG,"PageItem removeSections pageData is null or pageData.rawData is not HippyArray");
        }
    }

    public void notifyLoaded() {
        this.dataState = 1;
        if (LogUtils.isDebug()) {
            Log.v(TAG, "PageItem notifyLoaded pi:" + this);
        }
    }

    public void notifyReset() {
        this.dataState = -1;
        this.bindPlayerView = null;
        if (LogUtils.isDebug()) {
            Log.v(TAG, "PageItem notifyReset pi:" + this);
        }
    }

    public void notifyLoading() {
        this.dataState = 0;
        this.loadTime = System.currentTimeMillis();
        if (LogUtils.isDebug()) {
            Log.v(TAG, "PageItem notifyLoading pi:" + this + ",loadTime:" + this.loadTime);
        }
    }

    public boolean isDataValid() {
        return this.dataState == 1;
    }

    @Override
    public String toString() {
        return "PageItem{" +
                "position=" + position +
                ", hasCode=" + hashCode() +
                ", updateDirty=" + updateDirty +
                ", dataState=" + dataState +
                ", loadTime=" + loadTime +
                ", bindingPlayer=" + bindingPlayer +
                ", this=" + hashCode() +
                '}';
    }

    public void markDataDirty() {
        this.updateDirty = true;
    }

    public void markToReload(){
        this.notifyReset();
        this.markDataDirty();
        bindPlayerView = null;
    }
}
