package com.quicktvui.support.ui.rvsliding.utils;

public enum SlidingEnum {
    TOP_VIEW_EVENT("onTopViewEvent"),//前端定义方法名
    TOP_VIEW_TOP_START("onMoveToTopStart"),//TopView上划开始
    TOP_VIEW_TOP_END("onMoveToTopEnd"),//TopView上划结束
    TOP_VIEW_BOTTOM_START("onMoveToBottomStart"),//TopView下划开始
    TOP_VIEW_BOTTOM_END("onMoveToBottomEnd"),//TopView下划结束
    ON_SCROLL_STATE_CHANGED("onScrollStateChanged"),//列表滑动变化

    SLIDING_CONTENT_LIST("sliding_content_list"),//sliding组件下content tv_list的name
    SERIES_WATERFALL("series_waterfall");//sliding组件下content tv_list的name

    private final String name;

    SlidingEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
