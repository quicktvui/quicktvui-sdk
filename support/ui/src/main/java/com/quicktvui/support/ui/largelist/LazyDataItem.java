package com.quicktvui.support.ui.largelist;

import com.quicktvui.sdk.base.args.EsMap;

public interface LazyDataItem {

    void updateContent(EsMap object);

    EsMap getContentData();
}
