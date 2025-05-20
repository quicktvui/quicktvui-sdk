package com.quicktvui.support.data.shared;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;

/**
 *
 */
@ESKitAutoRegister
public class ESSharedDataModule implements IEsModule, IEsInfo {

    private ESSharedDataManager sharedDataManager;
    private String selfPackageName;

    @Override
    public void init(Context context) {
        try {
            sharedDataManager = ESSharedDataManager.getInstance();
            sharedDataManager.init(context);
            selfPackageName = EsProxy.get().getEsPackageName(this);
            if (L.DEBUG) {
                L.logD("#-----------ESSharedDataModule-------------->>>selfPackageName: " + selfPackageName);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String getSelfPackageName() {
        return selfPackageName;
    }

    public void getBoolean(String packageName, String key, boolean defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getBoolean-------------->>>" + " packageName: " + packageName + " key: " + key + " defValue: " + defValue);
            }
            boolean value = sharedDataManager.getBoolean(getSelfPackageName(), packageName, key, defValue);
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
            boolean result = sharedDataManager.putBoolean(getSelfPackageName(), packageName, key, value, mode);
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
            int value = sharedDataManager.getInt(getSelfPackageName(), packageName, key, defValue);
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
            boolean result = sharedDataManager.putInt(getSelfPackageName(), packageName, key, value, mode);
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
            long value = sharedDataManager.getLong(getSelfPackageName(), packageName, key, defValue);
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
            boolean result = sharedDataManager.putLong(getSelfPackageName(), packageName, key, value, mode);
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
            String value = sharedDataManager.getString(getSelfPackageName(), packageName, key, defValue);
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
            boolean result = sharedDataManager.putString(getSelfPackageName(), packageName, key, value, mode);
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
            EsArray value = sharedDataManager.getArray(getSelfPackageName(), packageName, key, defValue);
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
            boolean result = sharedDataManager.putArray(getSelfPackageName(), packageName, key, value, mode);
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
            EsMap value = sharedDataManager.getMap(getSelfPackageName(), packageName, key, defValue);
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
            boolean result = sharedDataManager.putMap(getSelfPackageName(), packageName, key, value, mode);
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
