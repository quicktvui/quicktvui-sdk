package com.quicktvui.support.ui.selectseries.utils

import com.tencent.mtt.hippy.common.HippyMap
import com.quicktvui.support.ui.largelist.GroupItem
import com.quicktvui.support.ui.largelist.TemplateItemPresenterSelector
import com.quicktvui.support.ui.selectseries.presenters.GroupItemPresenter
import com.quicktvui.support.ui.selectseries.bean.Param
import com.quicktvui.support.ui.selectseries.bean.TemplateItem
import com.quicktvui.support.ui.leanback.ArrayObjectAdapter

class TemplateHelper {

    companion object {

        @JvmStatic
        fun buildTemplateItemListObjectAdapter(
            total: Int
        ): ArrayList<TemplateItem> {
            val list = ArrayList<TemplateItem>()
            for (i in 0 until total) {
                val item = TemplateItem()
                item.apply {
                    type = MyTemplateHelper.PURE_CUSTOM_TYPE
                }
                list.add(item)
            }
            return list
        }

        @JvmStatic
        fun buildGroupObjectAdapter(
            p: Param,
            group: HippyMap
        ): ArrayObjectAdapter {
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