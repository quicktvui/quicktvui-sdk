package com.quicktvui.support.ui.selectseries.presenters

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.tencent.mtt.hippy.common.HippyMap
import com.quicktvui.support.ui.ScreenAdapt
import com.quicktvui.support.ui.largelist.GroupItem
import com.quicktvui.support.ui.largelist.ItemView
import com.quicktvui.support.ui.selectseries.utils.TemplateUtil
import com.quicktvui.support.ui.leanback.Presenter

class GroupItemPresenter : Presenter() { // 可传参数：textSize、textColor、focusBackground、mark
    var itemWidth = 0
    var itemHeight = 0
    var textSize = ScreenAdapt.getInstance().transform(30)

    var colorStateList: ColorStateList? = null

    var groupMap: HippyMap? = null
    var padding: Rect? = null

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val screenAdapt = ScreenAdapt.getInstance()

        val iv = ItemView(parent!!.context, itemWidth, itemHeight, textSize, colorStateList)
        val layoutParams = iv.layoutParams
        if (itemWidth > 0)
            if (padding != null)
                layoutParams.width = itemWidth - padding!!.width()
            else
                layoutParams.width = itemWidth
        if (itemHeight > 0)
            layoutParams.height = itemHeight
        iv.layoutParams = layoutParams

        val h = Holder(iv)
        val stateListDrawable =
            TemplateUtil.createGradientDrawableDrawable(groupMap, "focusBackground")
        var stateListDrawable2: Drawable? = null
        if (groupMap != null && groupMap!!.containsKey("background")) {
            stateListDrawable2 =
                TemplateUtil.createGradientDrawableDrawable(groupMap, "background")
        }

        if (groupMap?.containsKey("mark") == true) {
            val m = groupMap!!.getMap("mark")
            if (m.containsKey("color")) {
                iv.setMarkColor(Color.parseColor(m.getString("color")));
            }
            if (m.containsKey("width")) {
                iv.setMarkWidth(screenAdapt.transform(m.getInt("width")));
            }
            if (m.containsKey("height")) {
                iv.setMarkHeight(screenAdapt.transform(m.getInt("height")));
            }
            if (m.containsKey("corner")) {
                iv.setMarkRounder(screenAdapt.transform(m.getInt("corner")));
            }
            if (m.containsKey("margin")) {
                iv.setMarkMargin(screenAdapt.transform(m.getInt("margin")));
            }
        }

        if (stateListDrawable != null) {
            iv.setFocusDrawable(stateListDrawable, this.padding)
        }

        if (stateListDrawable2 != null) {
            iv.setCommonDrawable(stateListDrawable2, this.padding)
        }

        return h
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        if (viewHolder is Holder) {
            val g: GroupItem = item as GroupItem
            viewHolder.setText(g.text)
        }
    }


    fun apply(g: HippyMap) {
        val screenAdapt = ScreenAdapt.getInstance()

        this.groupMap = g
        if (g.containsKey("textSize"))
            this.textSize = screenAdapt.transform(g.getInt("textSize"))
        colorStateList = TemplateUtil.createColorStateList(g, "textColor")
        if (g.containsKey("focusBackground")) {
            val m = g.getMap("focusBackground")
            if (m != null && m.containsKey("padding")) {
                val h = screenAdapt.transform(m.getArray("padding").getInt(0))
                val v = screenAdapt.transform(m.getArray("padding").getInt(1))
                this.padding = Rect(-h, -v, h, v)
            } else {
                val h = screenAdapt.transform(34)
                val v = screenAdapt.transform(6)
                this.padding = Rect(-h, -v, h, v)
            }
        } else {
            val h = screenAdapt.transform(34)
            val v = screenAdapt.transform(6)
            this.padding = Rect(-h, -v, h, v)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val h = viewHolder as Holder
        h.itemView.setBGVisible(false)
    }

    class Holder(itemView: View) : ViewHolder(itemView) {
        var itemView: ItemView = itemView as ItemView
        fun setText(text: String?) {
            itemView.setText(text)
        }
    }
}