package com.quicktvui.support.ui.selectseries.utils

import com.quicktvui.support.ui.largelist.TemplateItemPresenterSelector
import com.quicktvui.support.ui.selectseries.presenters.CustomItemViewPresenter

object MyTemplateHelper {
    const val PURE_CUSTOM_TYPE = "pureCustom"

    @JvmStatic
    fun initTemplate(
        presenterSelector: TemplateItemPresenterSelector
    ) {
        val p =
            CustomItemViewPresenter()
        presenterSelector.addPresenter(PURE_CUSTOM_TYPE, p.presenter)

    }
}