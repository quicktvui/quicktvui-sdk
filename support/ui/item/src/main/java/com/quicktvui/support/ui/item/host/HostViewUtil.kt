package com.quicktvui.support.ui.item.host

import android.view.View
import com.quicktvui.support.ui.item.widget.IWidget
import com.quicktvui.support.ui.render.RenderNode

/**
 *@author zhaopeng
 *@date 2020/10/12 6:47 PM
 *@description
 */
object HostViewUtil {



    fun notifyWidgetViewAttached(node : RenderNode?, v : View) {
        node?.children()?.map {
            if(it is IWidget){
                it.onViewAttachedToWindow(v)
            }
        }
    }

    fun notifyWidgetViewDetached(node : RenderNode?, v : View) {
        node?.children()?.map {
            if(it is IWidget){
                it.onViewDetachedFromWindow(v)
            }
        }
    }


}