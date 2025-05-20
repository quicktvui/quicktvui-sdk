package com.quicktvui.support.core.module.mmap;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 *
 */
@ESKitAutoRegister
public class AndroidMMapModule implements IEsModule, IEsInfo {

    private MMapManager mMapManager;
    private String selfPackageName;

    @Override
    public void init(Context context) {
        mMapManager = MMapManager.getInstance();
        //限定包名，防止vue进行修改
        selfPackageName = EsProxy.get().getEsPackageName(this);
        if (L.DEBUG) {
            L.logD("#-----------AndroidMMapModule-------------->>>selfPackageName: " + selfPackageName);
        }
    }

    private String getSelfPackageName() {
        return selfPackageName;
    }

    public void initESPackageName(String packageName, EsPromise promise) {
        promise.resolve(packageName);
    }

    public void getBoolean(String packageName, String key, boolean defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getBoolean-------------->>>" + " packageName: " + packageName + " key: " + key + " defValue: " + defValue);
            }
            boolean value = mMapManager.getBoolean(getSelfPackageName(), packageName, key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void putBoolean(String packageName, String key, boolean value, int mode, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putBoolean-------------->>>" + " packageName: " + packageName + " key: " + key + " value: " + value + " mode: " + mode);
            }
            boolean result = mMapManager.putBoolean(getSelfPackageName(), packageName, key, value, mode);
            promise.resolve(result);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //---------------------------INT-----------------------------------
    public void getInt(String packageName, String key, int defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getInt-------------->>>" + " packageName: " + packageName + " key: " + key + " defValue: " + defValue);
            }
            int value = mMapManager.getInt(getSelfPackageName(), packageName, key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void putInt(String packageName, String key, int value, int mode, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putInt-------------->>>" + " packageName: " + packageName + " key: " + key + " value: " + value + " mode: " + mode);
            }
            boolean result = mMapManager.putInt(getSelfPackageName(), packageName, key, value, mode);
            promise.resolve(result);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //-------------------------LONG-------------------------------------
    public void getLong(String packageName, String key, long defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getLong-------------->>>" + " packageName: " + packageName + " key: " + key + " defValue: " + defValue);
            }
            long value = mMapManager.getLong(getSelfPackageName(), packageName, key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void putLong(String packageName, String key, long value, int mode, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putLong-------------->>>" + " packageName: " + packageName + " key: " + key + " value: " + value + " mode: " + mode);
            }
            boolean result = mMapManager.putLong(getSelfPackageName(), packageName, key, value, mode);
            promise.resolve(result);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //---------------------------STRING-----------------------------------
    public void getString(String packageName, String key, String defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getString-------------->>>" + " packageName: " + packageName + " key: " + key + " defValue: " + defValue);
            }
            String value = mMapManager.getString(getSelfPackageName(), packageName, key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void putString(String packageName, String key, String value, int mode, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putString-------------->>>" + " packageName: " + packageName + " key: " + key + " value: " + value + " mode: " + mode);
            }
            boolean result = mMapManager.putString(getSelfPackageName(), packageName, key, value, mode);
            promise.resolve(result);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //---------------------------ARRAY-----------------------------------
    public void getArray(String packageName, String key, EsArray defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getArray-------------->>>" + " packageName: " + packageName + " key: " + key + " defValue: " + defValue);
            }
            EsArray value = mMapManager.getArray(getSelfPackageName(), packageName, key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void putArray(String packageName, String key, EsArray value, int mode, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putArray-------------->>>" + " packageName: " + packageName + " key: " + key + " value: " + value + " mode: " + mode);
            }
            boolean result = mMapManager.putArray(getSelfPackageName(), packageName, key, value, mode);
            promise.resolve(result);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //---------------------------MAP-----------------------------------
    public void getMap(String packageName, String key, EsMap defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getMap-------------->>>" + " packageName: " + packageName + " key: " + key + " defValue: " + defValue);
            }
            EsMap value = mMapManager.getMap(getSelfPackageName(), packageName, key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void putMap(String packageName, String key, EsMap value, int mode, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putMap-------------->>>" + " packageName: " + packageName + " key: " + key + " value: " + value + " mode: " + mode);
            }
            boolean result = mMapManager.putMap(getSelfPackageName(), packageName, key, value, mode);
            promise.resolve(result);
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
