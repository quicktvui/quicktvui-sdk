package com.quicktvui.support.ui.legacy.view

import android.view.View


interface ITVFocusGroup {

    /**
     * 获得下一个特指的焦点view
     * @param focused 当前焦点
     * @param direction 方向
     * @return
     */
    fun getNextSpecifiedFocus(focused: View?, direction: Int): View?


    /**
     * 设置下一个特指的焦点view
     * 此方法会将所有的方向的焦点都指定为批index
     * @param index getChildAt(index)中的index值
     */
    fun setNextSpecifiedFocusIndex(index: Int){

    }

    /**
     * 为指定方向设置下一个特指的焦点view
     * @param direction 方向
     * @param index getChildAt(index)中的index值
     */
    fun setNextSpecifiedFocusIndex(direction: Int, index: Int){

    }

    var nextSpecifiedFocusUpId : Int

    var nextSpecifiedFocusDownId : Int

    var nextSpecifiedFocusLeftId : Int

    var nextSpecifiedFocusRightId : Int

}