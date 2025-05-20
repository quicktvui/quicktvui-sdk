package com.quicktvui.support.ui.legacy.view

import android.graphics.Rect
import android.view.View
import android.view.ViewParent

import com.quicktvui.support.ui.legacy.FConfig

@Deprecated("ITVView接口已过时，使用com.quicktvui.base.ui.ITVView代替", ReplaceWith("com.quicktvui.base.ui.ITVView"))
interface ITVView {

    /***
     * 获得获得焦点时设置的横向放大倍数
     * @return
     */
    /**
     * 设置View获得焦点的横向放大倍数
     * @return
     */
    var focusScaleX: Float

    /***
     * 获得获得焦点时设置的竖向放大倍数
     * @return
     */
    /**
     * 设置View获得焦点的竖向放大倍数
     * @return
     */
    var focusScaleY: Float

    fun getParent(): ViewParent

    /**
     * 获得真正的View
     * @return
     */
    val view: View

    fun getWidth() : Int

    fun getHeight() : Int

    val floatFocusMarginRect: Rect


    val fRootView: TVRootView

    val attachInfo: AttachInfo

    val floatFocusManager: IFloatFocusManager



    /**
     * 设置View获得焦点的整体放大倍数
     * @return
     */
    fun setFocusScale(scale: Float)

    /**
     * 设置View获得焦点的放大动画的时间，单位ms
     * @return
     */
    fun setFocusScaleDuration(duration: Int)

    /**处理焦点得到与失去时，放大的缩小view的处理,默认处理方式为：<br></br>
     * 只在isFocusable为true时，才执行放大逻辑，否则不处理。
     *
     * @param gainFocus
     * @param direction
     * @param previouslyFocusedRect
     */
    fun onHandleFocusScale(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect)

    /**
     * 移动方向（前一个、下一个，上一行，下一行）
     */
    enum class TVMovement {

        PREV_ITEM,
        NEXT_ITEM,
        PREV_ROW,
        NEXT_ROW,
        INVALID
    }

    /**
     * 方向，横向，竖向
     */
    enum class TVOrientation {
        HORIZONTAL,
        VERTICAL
    }

    fun setFloatFocusFocusedAlpha(alpha: Float)

    companion object {

        val DEBUG = FConfig.DEBUG

        val FOCUS_INVALID = -1

        val TAG = "ITVView"



    }





//    fun findUserSpecifiedNextFocusView(direction: Int) : ITVView?
}
