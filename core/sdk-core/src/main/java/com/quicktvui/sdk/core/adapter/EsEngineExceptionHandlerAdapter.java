package com.quicktvui.sdk.core.adapter;

import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.adapter.exception.HippyExceptionHandlerAdapter;
import com.tencent.mtt.hippy.common.HippyJsException;

/**
 * Create by weipeng on 2022/03/01 15:17
 */
public class EsEngineExceptionHandlerAdapter implements HippyExceptionHandlerAdapter {
    private String esPkg = "";

    public void setCurrentEsAppPackage(String esPkg) {
        this.esPkg = esPkg;
    }

    @Override
    public void handleJsException(HippyJsException exception) {
        L.logE("handleJsException", exception);
    }

    @Override
    public void handleNativeException(Exception exception, boolean haveCaught) {
        L.logE("handleNativeException haveCaught:" + haveCaught, exception);
    }

    @Override
    public void handleBackgroundTracing(String details) {
        L.logE("handleBackgroundTracing:" + details);
    }
}
