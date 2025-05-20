package com.quicktvui.support.ui.viewpager.tabs;

import com.tencent.mtt.hippy.common.HippyArray;

import java.util.Arrays;

class TabsParam {
    public long pageSwitchDelay = 100;
    public int resumeTaskDelay = 500;
    public int resumePlayerTaskDelay = 500;
    public int firstResumeTaskDelay = 1000;
    public HippyArray dataList;
    public float speedPerPixel = -1;
    public int offscreenPageLimit = 1;
    public boolean disableScrollAnimation = false;
    public boolean preferSaveMemory = true;
    public boolean alphaTransform = false;
    public String syncNavListSID = null;
    public boolean changePageOnFocusFail = false;
    public boolean enableDrawerAnimation = false;
    public boolean autoChangePageByNative = true;
    public String localCacheKey = null;

    //hardcode for miobox
    public int mineTabFirstFocusIndex = -1;
    /**
     * 只加载一次数据，后续一直用缓存
     */
    public static int STRATEGY_ONE_SHOT = 1;
    /**
     * 每次都加载
     */
    public static int STRATEGY_ALWAYS = 2;
    /**
     * 只加载一次，但数据有过期时间
     */
    public static int STRATEGY_OVER_TIME = 3;

    /**
     * 左右俩边
     */
    public static int PRELOAD_STRATEGY_BOTH_SIDE = 1;
    /**
     * 单一tab
     */
    public static int PRELOAD_STRATEGY_SINGLE = 2;
    /**
     * 在顶部
     */
    public static int TAB_POSITION_TOP = 1;
    /**
     * 在左边
     */
    public static int TAB_POSITION_LEFT = 2;
    /**
     * 在右边
     */
    public static int TAB_POSITION_RIGHT = 3;
    /**
     * 在底部
     */
    public static int TAB_POSITION_BOTTOM = 4;

    public boolean isSuspension;
    public boolean useSuspensionBg; //是否使用默认吸顶背景色
    public int loadDataStrategy = STRATEGY_ONE_SHOT;
    public int preloadStrategy = PRELOAD_STRATEGY_BOTH_SIDE;
    public long outOfDateTime = 5 * 60 * 1000;//1分钟
    public long outOfDateTimeLocalCache = 1000 * 60 * 60 * 24 * 1;//1天
    public boolean hideOnSingleTab = false;
    public int tabPosition = TAB_POSITION_TOP;
    public float checkOffset = 0.1f;
    public boolean autoHandleBackKey = true;
    public boolean autoReFocusOnSingleTab = true;
    public boolean autoBackToDefault = true;
    public boolean useDiff = false;
    public boolean useClickMode = false;
    public boolean autoScrollToTop = true;
    public boolean isHideList = false;//单tab模式下，切换页面是否改变页面Alpha
    public int[] blockFocusDirections;
    public int defaultIndex = 0;//TabsItem里tab的默认位置
    public int preloadItemNumber = 1;//触发加载更多偏移量
    public boolean listenScrollEvent = true; //监听tv-list onScroll事件
    public boolean requestAutofocusOnPageChange = false;
    public long switchDuration = 200;

    public void setLoadDataStrategy(String loadDataStrategy) {
        switch (loadDataStrategy) {
            case "oneShot":
                this.loadDataStrategy = STRATEGY_ONE_SHOT;
                break;
            case "always":
                this.loadDataStrategy = STRATEGY_ALWAYS;
                break;
            case "overTime":
                this.loadDataStrategy = STRATEGY_OVER_TIME;
                break;
        }
    }

    public void setPreLoadDataStrategy(String strategy) {
        switch (strategy) {
            case "single":
                this.preloadStrategy = PRELOAD_STRATEGY_SINGLE;
                break;
            default:
                this.preloadStrategy = PRELOAD_STRATEGY_BOTH_SIDE;
                break;
        }
    }

    public void setTabPosition(String type) {
        switch (type) {
            case "top":
                this.tabPosition = TAB_POSITION_TOP;
                break;
            case "bottom":
                this.tabPosition = TAB_POSITION_BOTTOM;
                break;
            case "left":
                this.tabPosition = TAB_POSITION_LEFT;
                break;
            case "right":
                this.tabPosition = TAB_POSITION_RIGHT;
                break;
        }
    }

    @Override
    public String toString() {
        return "TabsParam{" +
                "pageSwitchDelay=" + pageSwitchDelay +
                ", resumeTaskDelay=" + resumeTaskDelay +
                ", resumePlayerTaskDelay=" + resumePlayerTaskDelay +
                ", firstResumeTaskDelay=" + firstResumeTaskDelay +
                ", dataList=" + dataList +
                ", speedPerPixel=" + speedPerPixel +
                ", offscreenPageLimit=" + offscreenPageLimit +
                ", disableScrollAnimation=" + disableScrollAnimation +
                ", preferSaveMemory=" + preferSaveMemory +
                ", alphaTransform=" + alphaTransform +
                ", syncNavListSID='" + syncNavListSID + '\'' +
                ", changePageOnFocusFail=" + changePageOnFocusFail +
                ", enableDrawerAnimation=" + enableDrawerAnimation +
                ", autoChangePageByNative=" + autoChangePageByNative +
                ", localCacheKey='" + localCacheKey + '\'' +
                ", mineTabFirstFocusIndex=" + mineTabFirstFocusIndex +
                ", isSuspension=" + isSuspension +
                ", useSuspensionBg=" + useSuspensionBg +
                ", loadDataStrategy=" + loadDataStrategy +
                ", preloadStrategy=" + preloadStrategy +
                ", outOfDateTime=" + outOfDateTime +
                ", outOfDateTimeLocalCache=" + outOfDateTimeLocalCache +
                ", hideOnSingleTab=" + hideOnSingleTab +
                ", tabPosition=" + tabPosition +
                ", checkOffset=" + checkOffset +
                ", autoHandleBackKey=" + autoHandleBackKey +
                ", autoReFocusOnSingleTab=" + autoReFocusOnSingleTab +
                ", autoBackToDefault=" + autoBackToDefault +
                ", useDiff=" + useDiff +
                ", useClickMode=" + useClickMode +
                ", autoScrollToTop=" + autoScrollToTop +
                ", isHideList=" + isHideList +
                ", blockFocusDirections=" + Arrays.toString(blockFocusDirections) +
                ", defaultIndex=" + defaultIndex +
                ", preloadItemNumber=" + preloadItemNumber +
                ", listenScrollEvent=" + listenScrollEvent +
                ", requestAutofocusOnPageChange=" + requestAutofocusOnPageChange +
                ", switchDuration=" + switchDuration +
                '}';
    }
}
