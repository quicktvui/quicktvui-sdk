package com.quicktvui.support.core.module.sp;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 *
 */
@ESKitAutoRegister
public class AndroidSharedPreferencesModule implements IEsModule, IEsInfo {

    private AndroidSharedPreferencesManager preferencesManager;

    @Override
    public void init(Context context) {
        this.preferencesManager = new AndroidSharedPreferencesManager();
        this.preferencesManager.init(context);
    }

    /**
     *
     */
    public void initSharedPreferences(String name, EsPromise promise) {
        try {
            //小程序的包名
            if (TextUtils.isEmpty(name)) {
                promise.resolve(false);
                return;
            }
            preferencesManager.initSharedPreferences(name);
            if (L.DEBUG) {
                L.logD(preferencesManager + "#-------------initSharedPreferences-------------->>>" + name);
            }
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    public void initESSharedPreferences(String name, EsPromise promise) {
        try {
            //小程序的包名
            String packageName = EsProxy.get().getEsPackageName(this);
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(name)) {
                promise.resolve(false);
                return;
            }
            preferencesManager.initSharedPreferences(packageName + "_" + name);
            if (L.DEBUG) {
                L.logD(preferencesManager + "#-------------initSharedPreferences-------------->>>" + name);
            }
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    public void getBoolean(String key, boolean defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getBoolean-------------->>>" + key + "---->>>" + defValue);
            }
            boolean value = preferencesManager.getBoolean(key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void putBoolean(String key, boolean value, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putBoolean-------------->>>" + key + "---->>>" + value);
            }
            preferencesManager.putBoolean(key, value);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //--------------------------------------------------------------
    public void getInt(String key, int defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getInt-------------->>>" + key + "---->>>" + defValue);
            }
            int value = preferencesManager.getInt(key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void putInt(String key, int value, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putInt-------------->>>" + key + "---->>>" + value);
            }
            preferencesManager.putInt(key, value);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //--------------------------------------------------------------
    public void getLong(String key, long defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------getLong-------------->>>" + key + "---->>>" + defValue);
            }
            long value = preferencesManager.getLong(key, defValue);
            promise.resolve(value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void putLong(String key, long value, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#-------------putLong-------------->>>" + key + "---->>>" + value);
            }
            preferencesManager.putLong(key, value);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(false);
        }
    }

    //--------------------------------------------------------------
    public void getString(String key, String defValue, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#------1-------getString-------------->>>" + key + "---->>>" + defValue);
            }
            String value = preferencesManager.getString(key, defValue);
            promise.resolve(value);
            if (L.DEBUG) {
                L.logD("#-----2--------getString-------------->>>" + key + "---->>>" + value);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void putString(String key, String value, EsPromise promise) {
        try {
            preferencesManager.putString(key, value);
            if (L.DEBUG) {
                L.logD("#-------------putString-------------->>>" + key + "---->>>" + value);
            }
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
        try {
            if (preferencesManager != null) {
                preferencesManager.release();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
