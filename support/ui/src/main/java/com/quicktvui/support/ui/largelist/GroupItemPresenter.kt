package com.quicktvui.support.ui.largelist

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import com.quicktvui.sdk.base.args.EsMap
import com.quicktvui.support.ui.ScreenAdapt
import com.quicktvui.support.ui.leanback.Presenter

class GroupItemPresenter : Presenter() {
    var itemWidth = ScreenAdapt.getInstance().transform(85)
    var itemHeight =  ScreenAdapt.getInstance().transform(30)
    var textSize = ScreenAdapt.getInstance().transform(15)

    var colorStateList: ColorStateList? = null

    var groupMap: EsMap? = null
    var padding: Rect? = null

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val screenAdapt = ScreenAdapt.getInstance()

        val iv = ItemView(parent!!.context, itemWidth, itemHeight, textSize, colorStateList)
        val h = Holder(iv)
        val stateListDrawable =
            TemplateUtil.createGradientDrawableDrawable(groupMap, "focusBackground")

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
        return h
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        if (viewHolder is Holder) {
            val g: GroupItem = item as GroupItem
            viewHolder.setText(g.text)
        }
    }


    fun apply(g: EsMap) {
        val screenAdapt = ScreenAdapt.getInstance()

        this.textSize = screenAdapt.transform(g.getInt("textSize"))
        colorStateList = TemplateUtil.createColorStateList(g, "textColor")
        if (g.containsKey("focusBackground")) {
            this.groupMap = g
            val m = g.getMap("focusBackground")
            if (m != null && m.containsKey("padding")) {
                val h = screenAdapt.transform(m.getArray("padding").getInt(0))
                val v = screenAdapt.transform(m.getArray("padding").getInt(1))
                this.padding = Rect(-h, -v, h, v)
            }
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