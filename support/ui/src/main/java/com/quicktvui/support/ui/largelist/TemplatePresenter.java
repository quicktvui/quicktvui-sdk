package com.quicktvui.support.ui.largelist;

import com.quicktvui.sdk.base.args.EsMap;

import com.quicktvui.support.ui.leanback.Presenter;

public interface TemplatePresenter {

    void applyProps(EsMap props);

    Presenter getPresenter();

}
