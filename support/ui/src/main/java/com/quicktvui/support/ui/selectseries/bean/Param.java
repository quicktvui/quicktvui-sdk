package com.quicktvui.support.ui.selectseries.bean;

import android.support.annotation.NonNull;

import com.quicktvui.support.ui.ScreenAdapt;
import com.quicktvui.support.ui.largelist.AdvanceCenterScroller;
import com.tencent.mtt.hippy.common.HippyMap;

public class Param {
    // 核心必传参数
    public int totalCount; // 数据总数量
    public int pageSize; // 每次加载数据数量
    // 大列表参数
    public int contentHeight = -1;
    public int contentWidth = -1;
    public int itemGap = ScreenAdapt.getInstance().transform(20); // 大列表item间隔

    public int scrollType;
    // scrollType为1翻页时，参数
    public int paddingForPageLeft = ScreenAdapt.getInstance().transform(60);
    public int paddingForPageRight = ScreenAdapt.getInstance().transform(60);
    public int arrowWidth = ScreenAdapt.getInstance().transform(24);
    public int arrowHeight = ScreenAdapt.getInstance().transform(42);
    public int arrowMarginRight = -1;
    public int arrowMarginLeft = -1;
    public int pageDisplayCount; // 翻页个数，有groupSize优先取groupSize
    // group相关
    public int groupSize;

    public boolean groupUp = false;

    //    public int groupHeight = 46;
    public int groupHeight = -1;
    public int groupTopMargin = ScreenAdapt.getInstance().transform(24);
    public boolean enableGroup;

    public int groupGap = ScreenAdapt.getInstance().transform(50);
    public int groupItemWidth = 0;
    public int groupItemHeight = 0;
    public HippyMap group = new HippyMap();

    public int scrollTargetOffset = -1;

    public int initPosition = -1;
    public int initFocusPosition = -1;
    public int preLoadNumber = 3;

    public int disableScrollOnMinCount = 3;
    public int updateAdditionRange = 3;
    public boolean blockFocus = true;
    public boolean isAutoChangeOnFocus = true;
    public int marginLeft;
    public int groupMarginLeft;

    private final ScreenAdapt screenAdapt;

    public Param() {
        screenAdapt = ScreenAdapt.getInstance();
    }

    public void setCoreData(int totalCount, int pageSize) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
    }

    public void setGroupData(HippyMap map) {
        if (map == null)
            return;
        group = map;
        if (group.containsKey("enableGroup")) {
            enableGroup = group.getBoolean("enableGroup");
        } else {
            enableGroup = true;
        }
        if (!enableGroup) {
            return;
        }

        if (!group.containsKey("groupSize")) {
            throw new IllegalArgumentException("使用快速选集功能，groupSize必传");
        }
        groupSize = group.getInt("groupSize");

        if (group.containsKey("groupUp")) {
            groupUp = group.getBoolean("groupUp");
        }

        if (scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
            pageDisplayCount = groupSize;
        }
        if (group.containsKey("groupHeight")) {
            groupHeight = screenAdapt.transform(group.getInt("groupHeight"));
        }
        if (group.containsKey("itemGap"))
            groupGap = screenAdapt.transform(group.getInt("itemGap"));
        groupItemWidth = screenAdapt.transform(group.getInt("itemWidth"));
        groupItemHeight = screenAdapt.transform(group.getInt("itemHeight"));

        if (group.containsKey("groupTopMargin"))
            groupTopMargin = screenAdapt.transform(group.getInt("groupTopMargin"));

        scrollTargetOffset = group.containsKey("scrollTargetOffset") ? group.getInt("scrollTargetOffset") : -1;
        if (scrollTargetOffset < 0) {
            scrollTargetOffset = groupSize % 2 == 0 ? groupSize / 2 - 1 : groupSize / 2;
        }

        groupMarginLeft = screenAdapt.transform(group.getInt("groupMarginLeft"));
    }

    public void setScrollParam(HippyMap map) {
        if (map == null)
            return;
        scrollType = map.getInt("scrollType");
        if (scrollType != AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
            return;
        }
        pageDisplayCount = groupSize > 0 ? groupSize
                : (map.containsKey("pageDisplayCount") ? map.getInt("pageDisplayCount") : 0);
        if (map.containsKey("paddingForPageLeft")) {
            paddingForPageLeft = screenAdapt.transform(map.getInt("paddingForPageLeft"));
        }
        if (map.containsKey("paddingForPageRight")) {
            paddingForPageRight = screenAdapt.transform(map.getInt("paddingForPageRight"));
        }
        if (map.containsKey("arrowMarginLeft")) {
            arrowMarginLeft = screenAdapt.transform(map.getInt("arrowMarginLeft"));
        }
        if (map.containsKey("arrowMarginRight")) {
            arrowMarginRight = screenAdapt.transform(map.getInt("arrowMarginRight"));
        }
        if (map.containsKey("arrowWidth")) {
            arrowWidth = screenAdapt.transform(map.getInt("arrowWidth"));
        }
        if (map.containsKey("arrowHeight")) {
            arrowHeight = screenAdapt.transform(map.getInt("arrowHeight"));
        }
    }

    public void setCommonParam(HippyMap map) {
        if (map == null) {
            return;
        }
        if (map.containsKey("initPosition")) {
            initPosition = map.getInt("initPosition");
        }
        if (map.containsKey("initFocusPosition")) {
            initFocusPosition = map.getInt("initFocusPosition");
        }
        marginLeft = map.containsKey("marginLeft") ? screenAdapt.transform(map.getInt("marginLeft")) : 0;
        disableScrollOnMinCount = map.containsKey("disableScrollOnMinCount") ? map.getInt("disableScrollOnMinCount") : 3;
        updateAdditionRange = map.containsKey("updateAdditionRange") ? map.getInt("updateAdditionRange") : 3;
        blockFocus = !map.containsKey("blockFocus") || map.getBoolean("blockFocus");
        isAutoChangeOnFocus = !map.containsKey("isAutoChangeOnFocus") || map.getBoolean("isAutoChangeOnFocus");

        if (map.containsKey("itemGap")) {
            itemGap = screenAdapt.transform(map.getInt("itemGap"));
        }
        if (map.containsKey("contentHeight")) {
            contentHeight = screenAdapt.transform(map.getInt("contentHeight"));
        }
        if (map.containsKey("contentWidth")) {
            contentWidth = screenAdapt.transform(map.getInt("contentWidth"));
        }

        preLoadNumber = map.getInt("preLoadNumber");
    }

    @NonNull
    @Override
    public String toString() {
        return "Param{" +
                "itemGap=" + itemGap +
                ", groupGap=" + groupGap +
                ", groupItemWidth=" + groupItemWidth +
                ", groupItemHeight=" + groupItemHeight +
                ", enableGroup=" + enableGroup +
                ", totalCount=" + totalCount +
                ", pageSize=" + pageSize +
                ", groupSize=" + groupSize +
                ", pageDisplayCount=" + pageDisplayCount +
                ", initPosition=" + initPosition +
                ", initFocusPosition=" + initFocusPosition +
                ", scrollType=" + scrollType +
                ", contentHeight=" + contentHeight +
                ", contentWidth=" + contentWidth +
                ", groupHeight=" + groupHeight +
                ", groupTopMargin=" + groupTopMargin +
                ", preLoadNumber=" + preLoadNumber +
                ", group=" + group +
                '}';
    }
}
