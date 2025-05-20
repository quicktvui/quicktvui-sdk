package com.quicktvui.support.core.module.power;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 *
 */
@ESKitAutoRegister
public class AndroidPowerModule implements IEsModule, IEsInfo {

    private AndroidPowerManager devicePowerManager;

    @Override
    public void init(Context context) {
        this.devicePowerManager = AndroidPowerManager.getInstance();
    }

    public void wakeLockAcquire(EsPromise promise) {
        try {
            this.devicePowerManager.wakeLockAcquire();
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    public void wakeLockRelease(EsPromise promise) {
        try {
            this.devicePowerManager.wakeLockRelease();
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
            map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }

    @Override
    public void destroy() {

    }
}
