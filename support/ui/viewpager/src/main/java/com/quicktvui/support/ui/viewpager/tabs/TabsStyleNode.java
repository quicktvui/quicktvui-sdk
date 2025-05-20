package com.quicktvui.support.ui.viewpager.tabs;

import android.util.Log;
import android.view.View;

import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.utils.LogUtils;

public class TabsStyleNode extends StyleNode {

    public static final String TAG = "TabsViewLog";
    final TabsParam param;


    public TabsStyleNode() {
        this.param = new TabsParam();
    }

    @SuppressWarnings("unused")
    @HippyControllerProps(name = "tabs", defaultType = HippyControllerProps.ARRAY)
    public void setDataList(HippyArray array) {
        this.param.dataList = array;
        if (LogUtils.isDebug()) {
            Log.d(TAG, "TabsStyleNode setDataList array：" + array);
        }
    }

    @HippyControllerProps(name = "suspension", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = false)
    public void setSuspension(boolean suspension) {
        this.param.isSuspension = suspension;
    }

    @HippyControllerProps(name = "useSuspensionBg", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = false)
    public void setUseSuspensionBg(boolean useSuspensionBg) {
        this.param.useSuspensionBg = useSuspensionBg;
    }

    @HippyControllerProps(name = "hideOnSingleTab", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = false)
    public void setEnableHideTabOnSingle(boolean hideOnSingleTab) {
        this.param.hideOnSingleTab = hideOnSingleTab;
    }

    @HippyControllerProps(name = "speedPerPixel", defaultType = HippyControllerProps.NUMBER, defaultNumber = 0)
    public void setSpeedPerPixel(float speedPerPixel) {
        this.param.speedPerPixel = speedPerPixel;
    }

    @HippyControllerProps(name = "switchDuration", defaultType = HippyControllerProps.NUMBER, defaultNumber = 0)
    public void setSwitchDuration(int switchDuration) {
        this.param.switchDuration = switchDuration;
    }

    @HippyControllerProps(name = "preloadItemNumber", defaultType = HippyControllerProps.NUMBER, defaultNumber = 1)
    public void setPreloadItemNumber(int preloadItemNumber) {
        this.param.preloadItemNumber = preloadItemNumber;
    }

    @HippyControllerProps(name = "autoHandleBackKey", defaultType = HippyControllerProps.BOOLEAN)
    public void setAutoHandleBackKey(boolean autoHandleBackKey) {
        this.param.autoHandleBackKey = autoHandleBackKey;
    }

    @HippyControllerProps(name = "autoReFocusOnSingleTab", defaultType = HippyControllerProps.BOOLEAN)
    public void autoReFocusOnSingleTab(boolean autoReFocusOnSingleTab) {
        this.param.autoReFocusOnSingleTab = autoReFocusOnSingleTab;
    }

    @HippyControllerProps(name = "useDiff", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setUseDiff(boolean useDiff) {
        this.param.useDiff = useDiff;
    }

    @HippyControllerProps(name = "autoScrollToTop", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setAutoBackToTop(boolean autoScrollToTop) {
        this.param.autoScrollToTop = autoScrollToTop;
    }

    @HippyControllerProps(name = "dataStrategy", defaultType = HippyControllerProps.STRING)
    public void setLoadDataStrategy(String loadDataStrategy) {
        this.param.setLoadDataStrategy(loadDataStrategy);
    }


    @HippyControllerProps(name = "preloadStrategy", defaultType = HippyControllerProps.STRING)
    public void setPreloadStrategy(String loadDataStrategy) {
        this.param.setPreLoadDataStrategy(loadDataStrategy);
    }

//    @HippyControllerProps(name = "playerBindingRelation", defaultType = HippyControllerProps.MAP)
//    public void setPlayerBindingRelation(HippyMap relation) {
//        this.param.playerBindingRelation = relation;
//        Log.i(ReplaceChildView.TAG,"setPlayerBindingRelation relation:"+relation);
//    }

    @HippyControllerProps(name = "isHideList", defaultType = HippyControllerProps.BOOLEAN)
    public void setIsHideList(boolean isHideList) {
        this.param.isHideList = isHideList;
    }

    @HippyControllerProps(name = "tabPosition", defaultType = HippyControllerProps.STRING)
    public void setTabPosition(String type) {
        this.param.setTabPosition(type);
    }

    @HippyControllerProps(name = "outOfDateTime", defaultType = HippyControllerProps.NUMBER)
    public void setOutOfDateTime(long outOfDateTime) {
        this.param.outOfDateTime = outOfDateTime;
    }

    @HippyControllerProps(name = "suspensionHideOffset", defaultType = HippyControllerProps.NUMBER)
    public void setCheckScrollOffset(float checkOffset) {
        this.param.checkOffset = checkOffset;
    }

    @HippyControllerProps(name = "blockFocusDirections", defaultType = HippyControllerProps.ARRAY)
    public void setBlockFocusDirections(HippyArray array) {
        if (array != null) {
            int[] directions = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                switch (array.getString(i)) {
                    case "up":
                        directions[i] = View.FOCUS_UP;
                        break;
                    case "down":
                        directions[i] = View.FOCUS_DOWN;
                        break;
                    case "left":
                        directions[i] = View.FOCUS_LEFT;
                        break;
                    case "right":
                        directions[i] = View.FOCUS_RIGHT;
                        break;
                }
            }
            this.param.blockFocusDirections = directions;
        }
    }

    @HippyControllerProps(name = "useClickMode", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setEnableSelectOnFocus(boolean useClickMode) {
        this.param.useClickMode = useClickMode;
    }

    @HippyControllerProps(name = "disableScrollAnimation", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setDisableScrollAnimation(boolean disable) {
        this.param.disableScrollAnimation = disable;
    }

    @HippyControllerProps(name = "autoChangePageByNative", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setAutoChangePageByNative(boolean autoChangePageByNative) {
        this.param.autoChangePageByNative = autoChangePageByNative;
    }
    @HippyControllerProps(name = "pageSwitchDelay", defaultType = HippyControllerProps.NUMBER)
    public void setPageSwitchDelay(long delay) {
        this.param.pageSwitchDelay = delay;
    }

    @HippyControllerProps(name = "firstResumeTaskDelay", defaultType = HippyControllerProps.NUMBER)
    public void setFirstResumeTaskDelay(int firstResumeTaskDelay) {
        this.param.firstResumeTaskDelay = firstResumeTaskDelay;
    }

    @HippyControllerProps(name = "checkAutofocusOnPageChange", defaultType = HippyControllerProps.BOOLEAN)
    public void setRequestAutofocusOnPageChange(boolean requestAutofocusOnPageChange) {
        this.param.requestAutofocusOnPageChange = requestAutofocusOnPageChange;
    }

    @HippyControllerProps(name = "changePageOnFocusFail", defaultType = HippyControllerProps.BOOLEAN)
    public void setChangePageOnFocusFail(boolean changePageOnFocusFail) {
        this.param.changePageOnFocusFail = changePageOnFocusFail;
    }

    @HippyControllerProps(name = "enableDrawerAnimation", defaultType = HippyControllerProps.BOOLEAN)
    public void setEnableDrawerAnimation(boolean enableDrawerAnimation) {
        this.param.enableDrawerAnimation = enableDrawerAnimation;
    }

    @HippyControllerProps(name = "preferSaveMemory", defaultType = HippyControllerProps.BOOLEAN)
    public void setPreferSaveMemory(boolean preferSaveMemory) {
        this.param.preferSaveMemory = preferSaveMemory;
    }

    @HippyControllerProps(name = "alphaTransform", defaultType = HippyControllerProps.BOOLEAN)
    public void setAlphaTransform(boolean alphaTransform) {
        this.param.alphaTransform = alphaTransform;
    }

    @HippyControllerProps(name = "offscreenPageLimit", defaultType = HippyControllerProps.NUMBER)
    public void setOffscreenPageLimit(int offscreenPageLimit) {
        this.param.offscreenPageLimit = offscreenPageLimit;
    }

    @HippyControllerProps(name = "customNavListSID", defaultType = HippyControllerProps.STRING)
    public void setNavListSID(String sid) {
        this.param.syncNavListSID = sid;
    }


    @HippyControllerProps(name = "resumeTaskDelay", defaultType = HippyControllerProps.NUMBER)
    public void setResumeTaskDelay(int resumeTaskDelay) {
        this.param.resumeTaskDelay = resumeTaskDelay;
    }

    @HippyControllerProps(name = "resumePlayerTaskDelay", defaultType = HippyControllerProps.NUMBER)
    public void setResumePlayerTaskDelay(int resumeTaskDelay) {
        this.param.resumePlayerTaskDelay = resumeTaskDelay;
    }

    @HippyControllerProps(name = "autoBackToDefault", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setAutoBackToDefault(boolean b) {
        this.param.autoBackToDefault = b;
    }

    @HippyControllerProps(name = "listenScrollEvent", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = false)
    public void setListenScrollEvent(boolean b) {
        this.param.listenScrollEvent = b;
    }

    @HippyControllerProps(name = "outOfDateTimeLocalCache", defaultType = HippyControllerProps.NUMBER)
    public void setOutOfDateTimeLocalCache(long time) {
        this.param.outOfDateTimeLocalCache = time;
    }

    @HippyControllerProps(name = "localCacheKey", defaultType = HippyControllerProps.STRING)
    public void setLocalCacheKey(String localCacheKey) {
        this.param.localCacheKey = localCacheKey;
    }

    public HippyArray getTabsData() {
        return this.param.dataList;
    }

    public boolean isSuspension() {
        return this.param.isSuspension;
    }

    public boolean useSuspensionBg() {
        return this.param.useSuspensionBg;
    }

    public boolean isDataListValid() {
        return getTabsData() != null;
    }

    public boolean isAutoBackToDefault() {
        return this.param.autoBackToDefault;
    }

}
