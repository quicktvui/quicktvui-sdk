package com.quicktvui.support.core.module.log;

import android.content.Context;
import android.util.Log;

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
public class ESLogModule implements IEsModule, IEsInfo {

    @Override
    public void init(Context context) {
    }

    public void v(String tag, String log) {
        Log.v(tag, log);
    }

    public void d(String tag, String log) {
        Log.d(tag, log);
    }

    public void i(String tag, String log) {
        Log.i(tag, log);
    }

    public void w(String tag, String log) {
        Log.w(tag, log);
    }

    public void e(String tag, String log) {
        Log.e(tag, log);
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
