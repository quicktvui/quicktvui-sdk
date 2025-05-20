package com.quicktvui.support.ui.largelist

import com.quicktvui.sdk.base.args.EsMap
import com.quicktvui.support.ui.item.bean.StandItem
import com.quicktvui.support.ui.item.presenter.SimpleItemPresenter

open class TemplateItem : StandItem, SimpleItemPresenter(), LazyDataItem,
    TemplateBean {
    var text: String? = null
    var itemWidth: Float = 0.0f
    var itemHeight: Float = 0.0f
    var flagText: String? = ""
    var floatText: String? = ""
    private var cover: String? = null

    var type: String = "normal"

    var content: EsMap? = null

    override fun obtainFlagText(): String? {
        return flagText
    }

    override fun obtainFlagVisible(): Boolean {
        return true
    }

    override fun obtainFloatText(): String? {
        return floatText
    }

    override fun obtainFloatTextVisible(): Boolean {
        return true
    }


    override fun obtainNormalTitle(): String? {
        return text
    }

    override fun getCover(): Any? {
        return cover
    }

    override fun getItemNumViewShow(): Int {
        return 0
    }

    override fun getNumIndex(): Int {
        return 0
    }

    override fun getNumberScaleOffset(): Float {
        return 0.0f
    }


    override fun updateContent(o: EsMap?) {
        content = o
        o?.let {
            this.cover = it.getString("cover")
            if ("number" != type) {
                this.text = it.getString("title")
                this.floatText = it.getString("floatText")
            }
            this.flagText = it.getString("flagText")
        }
    }

    override fun getContentData(): EsMap? {
        return content
    }

    override fun getTemplateType(): String {
        return type
    }

    fun isNotEmpty(): Boolean {
        return content != null && content!!.size() > 0
    }


}