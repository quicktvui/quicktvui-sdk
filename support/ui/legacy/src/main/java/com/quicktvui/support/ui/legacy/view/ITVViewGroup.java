package com.quicktvui.support.ui.legacy.view;


public interface ITVViewGroup extends ITVView {

    /**请求移动浮动焦点到对应view
     * @param child
     * @param focused
     */
    void requestChildMoveFloatFocus(ITVView child, ITVView focused);

    /***
     * 获取浮动焦点view
     * @return
     */
    ITVView getFloatFocusFocusableView();





}
