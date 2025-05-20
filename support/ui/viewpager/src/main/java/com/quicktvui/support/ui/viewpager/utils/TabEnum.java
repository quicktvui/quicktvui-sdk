package com.quicktvui.support.ui.viewpager.utils;

public enum TabEnum {
    TAB_EVENT("onTabsEvent"),//前端定义方法名
    SUSPENSION_TOP_START("onMoveToTopStart"),//吸顶开始
    SUSPENSION_TOP_END("onMoveToTopEnd"),//吸顶结束
    SUSPENSION_BOTTOM_START("onMoveToBottomStart"),//恢复吸顶开始
    ON_REPLACE_PLAYER("onReplacePlayer"),//恢复吸顶开始
    SUSPENSION_BOTTOM_END("onMoveToBottomEnd"),//恢复吸顶结束
    ON_SCROLLSTATE_CHANGED("onScrollStateChanged"),//列表滑动变化
    TAB_CHANGED("onTabChanged"),//tab切换
    ON_SCROLLTO_START("onScrollToStart"),//列表滑动到开始
    ON_SCROLLTO_END("onScrollToEnd"),//列表滑动到最后
    ON_LOADMORE("onLoadMoreData"),//加载更多
    SHOW_LOADING("onShowLoading"),//展示loading
    ON_SCROLL("onScroll"),//
    /**
     * =========== 分隔线 ===========
     */
    ITEM_POSITION("itemPosition"),//当前选中tab
    ITEM_DATA("data"),//当前选中tab的data
    TABS_TAB_LIST("tabList"),//tabs组件单page模式下tab tv_list的name
    TABS_CONTENT_LIST("contentList"),//tabs组件单page模式下content tv_list的name
    IS_SHOWLOADING("isShowLoading"),//通知前端是否显示loading
    /**
     * =========== 分隔线 ===========
     */
    TAB_STATE("tabState"),//tab状态
    CONTENT_STATE("contentState"),//列表页状态
    IS_SUSPENSION("isSuspension"),//吸顶状态
    CURRENTPAGE("currentPage");//当前页

    private final String name;

    TabEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
