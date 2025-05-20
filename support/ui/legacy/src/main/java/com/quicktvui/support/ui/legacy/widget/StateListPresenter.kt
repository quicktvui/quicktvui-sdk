package com.quicktvui.support.ui.legacy.widget

import com.quicktvui.support.ui.leanback.Presenter

interface StateListPresenter {

    companion object {
        /**
         * 普通状态
         */
        const val STATE_NORMAL = 0
        /**
         * 点击状态
         */
        const val STATE_PRESSED = android.R.attr.state_pressed
        /**
         * 选中状态
         */
        const val STATE_SELECTED = android.R.attr.state_selected
        /**
         * 焦点状态
         */
        const val STATE_FOCUSED = android.R.attr.state_focused

        const val STATE_ACTIVATED = android.R.attr.state_activated
    }


    fun onStateChanged(currentState : Int, viewHolder : Presenter.ViewHolder, item : Any?)


    fun getWrappedPresenter() : Presenter


}