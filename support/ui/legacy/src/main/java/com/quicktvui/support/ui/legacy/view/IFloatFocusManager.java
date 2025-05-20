package com.quicktvui.support.ui.legacy.view;

import android.view.View;


public interface IFloatFocusManager {

    /**
     *浮动焦点类型
     */
    enum FloatFocusType{

        Default,None, Custom
    }

    boolean mEnableFloatFocus = true;

    /**获得焦点view
     * */
    View getFloatFocusView();

    /***设置自定义的floatFocus*/
    void setFloatFocus(IFloatFocus floatFocus);

    /**锁定/解决锁定 浮动焦点
     * */
    void setFrozen(boolean b);

    /***
     *根据目前选中的焦点，重新定位其位置
     */
    void reLocateFocused(int duration);

    /***
     *将焦点框定位到指定fview
     * @param view 指定fview
     */
    void locateView(ITVView view, int duration);

    /***
     * 设置可见状态
     */
    void setVisible(boolean visible);

    /***
     * 动画显示蓝框
     */
    void show(int duration);

    /***
     * 动画隐藏蓝框
     */
    void dismiss(int duration);

    /***
     * 偏移蓝框位置
     */
    void offsetFLoatFocus(int dx,int dy);

    /***
     *一次性的将浮动焦点冻结（根据focused判断一次）
     */
    void pauseMoveOneShot(ITVView focused);

    void dismissAndReappear(int interval);

    void cancelDismissAndReappear();

//    /***
//     *一次性的将浮动焦点冻结（根据focused判断一次）
//     */
//    void resumeMoveOneShot(ITVView focused);


}
