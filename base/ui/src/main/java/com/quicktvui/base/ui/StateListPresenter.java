package com.quicktvui.base.ui;

/**
 * Create by WeiPeng on 2020/12/15 12:30
 */
public interface StateListPresenter {

    /**
     * 普通状态
     */
    public static final int STATE_NORMAL = 0;
    /**
     * 点击状态
     */
    public static final int STATE_PRESSED = android.R.attr.state_pressed;
    /**
     * 选中状态
     */
    public static final int STATE_SELECTED = android.R.attr.state_selected;
    /**
     * 焦点状态
     */
    public static final int STATE_FOCUSED = android.R.attr.state_focused;

    public static final int STATE_ACTIVATED = android.R.attr.state_activated;

}
