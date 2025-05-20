package com.quicktvui.support.ui.largelist

import android.text.TextUtils
import android.util.Log
import com.quicktvui.sdk.base.args.EsArray
import com.quicktvui.sdk.base.args.EsMap
import com.quicktvui.support.ui.ScreenAdapt
import com.quicktvui.support.ui.leanback.ArrayObjectAdapter

class TemplateHelper {


    companion object {

        @JvmStatic
        fun buildTemplateItemListObjectAdapter(total: Int, template: EsMap): ArrayList<TemplateItem> {
            val screenAdapt = ScreenAdapt.getInstance()

            val list = ArrayList<TemplateItem>()
            val width = screenAdapt.transform(template.getInt("width"))
            val height = screenAdapt.transform(template.getInt("height"))
            val tType = template.getString("type")
            if(width < 1 || height < 1 || TextUtils.isEmpty(tType)){
                Log.e(LargeListViewGroup.TAG, "TemplateHelper 创建数据失败，参数不合法 width:$width,height:${height} type:${tType}")
            }
            for (i in 0 until total) {
                val item = TemplateItem()
                item.apply {
                    type = tType
                    if (type == "number") {
                        text = if (i < 9) {
                            "0" + (i + 1)
                        } else {
                            "" + (i + 1)
                        }
                    }
                    itemWidth = width.toFloat()
                    itemHeight = height.toFloat()
                }
                list.add(item)
            }
            return list
        }

        @JvmStatic
        fun buildTemplateItemListObjectAdapter(data: EsArray, template: EsMap): ArrayList<Any> {
            val list = ArrayList<Any>()
            val width = template.getInt("width")
            val height = template.getInt("height")
            val tType = template.getString("type")
            val total = data.size()
            for (i in 0 until total) {
                val item = TemplateItem()
                item.apply {
                    type = tType
                    if (type == "number") {
                        text = if (i < 9) {
                            "0" + (i + 1)
                        } else {
                            "" + (i + 1)
                        }
                    }
                    updateContent(data.getMap(i))
                    itemWidth = width.toFloat()
                    itemHeight = height.toFloat()
                }
                list.add(item)
            }
            return list
        }


        @JvmStatic
        fun buildGroupObjectAdapter(p: LargeListViewGroup.Param, group: EsMap): ArrayObjectAdapter {
            val total = p.totalCount
            val groupSize = p.groupSize
            val count = if (total % groupSize == 0) total / groupSize else total / groupSize + 1

            val g = GroupItemPresenter()
            g.apply(group)
            g.itemWidth = p.groupItemWidth
            g.itemHeight = p.groupItemHeight

            val a = ArrayObjectAdapter(g)
            for (i in 0 until count) {
                val g = GroupItem()
                a.add(g)
                g.let {
                    it.start = (i * groupSize)       //1,6,11
                    it.end = it.start + groupSize - 1    //5,10,15
                    if (it.end > total - 1) {
                        it.end = total - 1
                    }
                    if (it.end == it.start) {
                        if (it.start + 1 < 10) {
                            it.text = "0${it.start + 1}"
                        } else {
                            it.text = "${it.start + 1}"
                        }
                    } else {
                        if (it.start + 1 < 10) {

                            if (it.end + 1 < 10) {
                                it.text = "0${it.start + 1}-0${it.end + 1}"
                            } else {
                                it.text = "0${it.start + 1}-${it.end + 1}"
                            }
                        } else {
                            it.text = "${it.start + 1}-${it.end + 1}"
                        }
                    }
                }
            }
            return a
        }


        @JvmStatic
        fun setupPresenters(): TemplateItemPresenterSelector {
            return TemplateItemPresenterSelector()
        }

        @JvmStatic
        fun computeDisplayPageCount(total: Int, groupSize: Int): Int {
            return if (total % groupSize == 0) total / groupSize else total / groupSize + 1
        }
    }
}