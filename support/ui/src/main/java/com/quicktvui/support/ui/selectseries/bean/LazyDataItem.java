package com.quicktvui.support.ui.selectseries.bean;

import com.tencent.mtt.hippy.common.HippyMap;

public interface LazyDataItem {

    void updateContent(HippyMap object);

    HippyMap getContentData();
}
