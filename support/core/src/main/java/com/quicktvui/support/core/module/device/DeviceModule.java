package com.quicktvui.support.core.module.device;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Create by weipeng on 2022/04/18 10:13
 */
@ESKitAutoRegister
public class DeviceModule implements IEsModule, IEsInfo {

    @Override
    public void init(Context context) {

    }

    public void getDeviceInfo(EsPromise promise) {
        EsMap data = new EsMap();
        Field[] fields = Build.class.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                if (!Modifier.isPublic(field.getModifiers())) continue;
                try {
                    data.pushObject(field.getName(), field.get(null));
                } catch (Exception e) {
                }
            }
        }

        // Mem info
        Context context = EsProxy.get().getContext();
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long total = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                total = mi.totalMem;
            }
            data.pushLong("mem_free", mi.availMem);
            data.pushLong("mem_total", total);
        }

        PromiseHolder.create(promise).put(data).sendSuccess();
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
