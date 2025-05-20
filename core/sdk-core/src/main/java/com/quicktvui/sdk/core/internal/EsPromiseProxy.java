package com.quicktvui.sdk.core.internal;

import com.tencent.mtt.hippy.HippyEngine;
import com.tencent.mtt.hippy.modules.Promise;

import com.quicktvui.sdk.core.utils.MapperUtils;
import com.quicktvui.sdk.base.EsPromise;

/**
 * Create by weipeng on 2022/03/03 16:39
 */
public class EsPromiseProxy implements EsPromise {

    private final Promise proxy;

    public EsPromiseProxy(Promise promise) {
        proxy = promise;
    }

    @Override
    public void resolve(Object value) {
        proxy.resolve(MapperUtils.tryMapperEsData2HpData(value));
    }

    @Override
    public void reject(Object value) {
        proxy.reject(MapperUtils.tryMapperEsData2HpData(value));
    }

    @Override
    public boolean isCallback() {
        return proxy.isCallback();
    }

    @Override
    public String getCallId() {
        return proxy.getCallId();
    }

    @Override
    public void setTransferType(int type) {
        proxy.setTransferType(HippyEngine.BridgeTransferType.values()[type]);
    }

    @Override
    public Object getProxy() {
        return proxy;
    }
}
