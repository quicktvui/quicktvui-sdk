package com.quicktvui.sdk.core.pm;

import android.view.KeyEvent;
import android.view.View;

public interface IEsPageView {

    View getView();
    void notifyAfterShow();
    void notifyBeforeHide();
    int getPageId();

    void setEventHandler(EventHandler handler);

    /** 页面事件处理 **/
    interface EventHandler {
        boolean dispatchKeyEvent(KeyEvent event);
    }

}
