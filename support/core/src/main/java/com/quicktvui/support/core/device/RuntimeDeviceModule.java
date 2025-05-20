package com.quicktvui.support.core.device;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.core.BuildConfig;

@ESKitAutoRegister
public class RuntimeDeviceModule implements IEsModule, IEsInfo {

    @Override
    public void init(Context context) {

    }

    public void getRuntimeDeviceInfo(EsPromise promise) {
        EsMap esMap = new EsMap();
        try {
            String deviceId = DeviceIdManager.getInstance().getDeviceId();
            String deviceType = DeviceTypeManager.getInstance().getDeviceType();
            esMap.pushString("deviceId", deviceId);
            esMap.pushString("deviceType", deviceType);
            promise.resolve(esMap);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(esMap);
        }
    }

    public void getRuntimeDeviceId(EsPromise promise) {
        try {
            String deviceId = DeviceIdManager.getInstance().getDeviceId();
            promise.resolve(deviceId);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject("");
        }
    }

    public void getRuntimeDeviceType(EsPromise promise) {
        try {
            String deviceType = DeviceTypeManager.getInstance().getDeviceType();
            promise.resolve(deviceType);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject("");
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushString(IEsInfo.ES_PROP_INFO_PACKAGE_NAME, BuildConfig.LIBRARY_PACKAGE_NAME);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }


    @Override
    public void destroy() {

    }
}
