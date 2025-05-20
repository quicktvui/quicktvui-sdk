package com.quicktvui.support.core.module.es;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.Constants;
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
public class ESModule implements IEsModule, IEsInfo {


    public static final String ES_SDK_SCHEMES = "esapp://action/start";

    @Override
    public void init(Context context) {

    }

    public void getESSDKInfo(EsPromise promise) {
        try {
            EsMap esMap = new EsMap();
            try {
                int versionCode = EsProxy.get().getSdkVersionCode();
                esMap.pushInt("versionCode", versionCode);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                String versionName = EsProxy.get().getSdkVersionName();
                esMap.pushString("versionName", versionName);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                String packageName = EsProxy.get().getEsPackageName(this);
                esMap.pushString("packageName", packageName);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                esMap.pushDouble(Constants.ESKIT_V_CODE, EsProxy.get().getEsKitVersionCode());
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                esMap.pushString(Constants.ESKIT_V_NAME, EsProxy.get().getEsKitVersionName());
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                EsMap infos = EsProxy.get().getEsKitInfo();
                esMap.pushAll(infos);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String dirPath = EsProxy.get().getEsAppPath(this);
                esMap.pushString("miniProgramPath", dirPath);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                String appRuntimePath = EsProxy.get().getEsAppRuntimePath(this);
                esMap.pushString("runtimePath", appRuntimePath);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //
            EsArray array = new EsArray();
            array.pushString(ES_SDK_SCHEMES);
            esMap.pushArray("schemes", array);

            if (L.DEBUG) {
                L.logD("---------getESSDKInfo--------->>>>" + esMap);
            }

            promise.resolve(esMap);
        } catch (Throwable e) {
            e.printStackTrace();
            EsMap esMap = new EsMap();
            promise.reject(esMap);
        }
    }

    public void getESSDKSupportSchemes(EsPromise promise) {
        EsArray array = new EsArray();
        array.pushString(ES_SDK_SCHEMES);
        promise.resolve(array);
    }

    public void getESVersionCode(EsPromise promise) {
        try {
            int versionCode = EsProxy.get().getSdkVersionCode();
            promise.resolve(versionCode);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(-1);
        }
    }

    public void getESVersionName(EsPromise promise) {
        try {
            String versionName = EsProxy.get().getSdkVersionName();
            promise.resolve(versionName);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject("");
        }
    }

    public void getESSDKVersionCode(EsPromise promise) {
        try {
            double versionCode = EsProxy.get().getEsKitVersionCode();
            promise.resolve(versionCode);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(-1);
        }
    }

    public void getESSDKVersionName(EsPromise promise) {
        try {
            String versionName = EsProxy.get().getEsKitVersionName();
            promise.resolve(versionName);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject("");
        }
    }

    public void getESPackageName(EsPromise promise) {
        try {
            String packageName = EsProxy.get().getEsPackageName(this);
            promise.resolve(packageName);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject("");
        }
    }

    //-------------------------------------------------------------------
    public void getESMiniProgramPath(EsPromise promise) {
        try {
            String dirPath = EsProxy.get().getEsAppPath(this);
            promise.resolve(dirPath);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject("");
        }
    }

    public void getESRuntimePath(EsPromise promise) {
        try {
            String dirPath = EsProxy.get().getEsAppRuntimePath(this);
            promise.resolve(dirPath);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject("");
        }
    }

    public void sendESNativeMapEventTop(String eventName, EsMap params, EsPromise promise) {
        try {
            EsProxy.get().sendNativeEventTraceable(this, eventName, params);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void sendESNativeArrayEventTop(String eventName, EsArray params, EsPromise promise) {
        try {
            EsProxy.get().sendNativeEventTraceable(this, eventName, params);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void sendESNativeMapEventAll(String eventName, EsMap params, EsPromise promise) {
        try {
            EsProxy.get().sendNativeEventAll(eventName, params);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void sendESNativeArrayEventAll(String eventName, EsArray params, EsPromise promise) {
        try {
            EsProxy.get().sendNativeEventAll(eventName, params);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void sendESNativeMapEvent2App(String packageName, String eventName, EsMap params, EsPromise promise){
        try {
            EsProxy.get().sendNativeEvent2App(packageName, eventName, params);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    public void sendESNativeArrayEvent2App(String packageName, String eventName, EsArray params, EsPromise promise){
        try {
            EsProxy.get().sendNativeEvent2App(packageName, eventName, params);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    /**
     * 判断module是否注册
     *
     * @param className
     * @param promise
     */
    public void isModuleRegistered(String className, EsPromise promise) {
        try {
            boolean isRegistered = EsProxy.get().isRegisterModule(className);
            promise.resolve(isRegistered);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
        }
    }

    /**
     * 判断component是否注册
     *
     * @param className
     * @param promise
     */
    public void isComponentRegistered(String className, EsPromise promise) {
        try {
            boolean isRegistered = EsProxy.get().isRegisterComponent(className);
            promise.resolve(isRegistered);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(false);
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
