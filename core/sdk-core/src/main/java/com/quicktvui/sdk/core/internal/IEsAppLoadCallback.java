package com.quicktvui.sdk.core.internal;

import com.tencent.mtt.hippy.HippyRootView;

import com.quicktvui.sdk.core.internal.loader.RecordInfo;
import com.quicktvui.sdk.core.pm.EsPageView;
import com.quicktvui.sdk.base.EsEventPacket;
import com.quicktvui.sdk.base.EsException;

/**
 *
 */
public interface IEsAppLoadCallback {

    /**
     * 开始加载快应用
     * @param handler VUE交互对象
     */
    void onStartLoad(IEsAppLoadHandler handler);

    /** 加载成功 **/
    void onViewLoaded(HippyRootView view);

    default void addSubPage(EsPageView view){}

    default void removeSubPage(EsPageView view){}

    /** 加载失败 **/
    void onLoadError(EsException e);

    void onEsAppEvent(EsEventPacket packet);

    /** vue代码请求结束界面 **/
    void requestFinish();

    default void onAppOpened(RecordInfo recordInfo){}

}
