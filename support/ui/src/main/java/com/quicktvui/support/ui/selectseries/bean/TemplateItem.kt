package com.quicktvui.support.ui.selectseries.bean

import com.tencent.mtt.hippy.common.HippyMap
import com.quicktvui.support.ui.largelist.TemplateBean
import com.quicktvui.support.ui.item.presenter.SimpleItemPresenter

open class TemplateItem : SimpleItemPresenter(),
    LazyDataItem,
    TemplateBean {
    var type: String = "normal"

    var content: HippyMap? = null

    override fun updateContent(o: HippyMap?) {
        content = o
    }

    override fun getContentData(): HippyMap? {
        return content
    }

    override fun getTemplateType(): String {
        return type
    }

    fun isNotEmpty(): Boolean {
        return content != null && content!!.size() > 0
    }


}