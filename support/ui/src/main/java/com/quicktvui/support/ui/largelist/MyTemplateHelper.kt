package com.quicktvui.support.ui.largelist

import com.quicktvui.sdk.base.args.EsMap

object MyTemplateHelper {
    @JvmStatic
    fun initTemplate(presenterSelector: TemplateItemPresenterSelector, templateMap: EsMap) {
        val type = templateMap.getString("type")
        var focusScale = 1.1f
        if (templateMap.containsKey("focusScale")) {
            focusScale = templateMap.getDouble("focusScale").toFloat()
        }
        when (type) {
            "topDown" -> {
                val p: TemplatePresenter = StandItemViewPresenter(focusScale)
                p.applyProps(templateMap)
                presenterSelector.addPresenter(type, p.presenter)
            }
            "leftRight" -> {
                val p: TemplatePresenter = StandItemViewPresenter(focusScale)
                p.applyProps(templateMap)
                presenterSelector.addPresenter(type, p.presenter)
            }
            else -> {
                val p: TemplatePresenter = NumberEpisodeItemPresenter(focusScale)
                p.applyProps(templateMap)
                presenterSelector.addPresenter(type, p.presenter)
            }
        }
        val lp: TemplatePresenter = LoadingPresenter()
        lp.applyProps(templateMap)
        presenterSelector.addPresenter("loading", lp.presenter)

    }
}