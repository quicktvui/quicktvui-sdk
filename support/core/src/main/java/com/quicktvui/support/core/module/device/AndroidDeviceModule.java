package com.quicktvui.support.core.module.device;

import android.content.Context;
import android.util.DisplayMetrics;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 * 设备信息
 */
@ESKitAutoRegister
public class AndroidDeviceModule implements IEsModule, IEsInfo {

    private Context context;

    private AndroidDeviceManager deviceManager;
    private DisplayMetrics displayMetrics;

    @Override
    public void init(Context context) {
        this.context = context;
        this.deviceManager = AndroidDeviceManager.getInstance();
        this.displayMetrics = context.getResources().getDisplayMetrics();

        if (L.DEBUG) {
            L.logD("#---------init---------->>>" + deviceManager.getAndroidDevice()
            );
        }
    }

    public void getAndroidDevice(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (L.DEBUG) {
            L.logD("#---------getDevice---------->>>" + device);
        }

        EsMap esMap = new EsMap();

        esMap.pushString("ethMac", device.getEthMac());
        esMap.pushString("wifiMac", device.getWifiMac());

        esMap.pushString("deviceId", device.getDeviceId());
        esMap.pushString("deviceType", device.getDeviceType());
        //
        esMap.pushLong("totalMemory", device.getTotalMemory());
        esMap.pushLong("availableMemory", device.getAvailableMemory());
        //
        esMap.pushLong("screenWidth", device.getScreenWidth());
        esMap.pushLong("screenHeight", device.getScreenHeight());

        esMap.pushString("resolution", device.getResolution());
        esMap.pushString("density", device.getDensity() + "");
        esMap.pushString("densityDpi", device.getDensityDpi() + "");
        esMap.pushString("scaledDensity", device.getScaledDensity() + "");

        esMap.pushString("ipAddress", device.getIpAddress());

        //
        esMap.pushString("buildModel", device.getBuildModel());
        esMap.pushString("buildBrand", device.getBuildBrand());
        esMap.pushString("buildBoard", device.getBuildBoard());
        esMap.pushString("buildDevice", device.getBuildDevice());
        esMap.pushString("buildProduct", device.getBuildProduct());
        esMap.pushString("buildHardware", device.getBuildHardware());
        esMap.pushString("buildManufacturer", device.getBuildManufacturer());
        esMap.pushString("buildSerial", device.getBuildSerial());
        esMap.pushString("buildTags", device.getBuildTags());
        esMap.pushString("buildId", device.getBuildId());
        esMap.pushLong("buildTime", device.getBuildTime());
        esMap.pushString("buildType", device.getBuildType());
        esMap.pushString("buildUser", device.getBuildUser());
        esMap.pushString("buildBootloader", device.getBuildBootloader());
        esMap.pushString("buildDisplay", device.getBuildDisplay());
        esMap.pushString("buildFingerPrint", device.getBuildFingerPrint());

        //
        esMap.pushString("buildVersionIncremental", device.getBuildVersionIncremental());
        esMap.pushString("buildVersionBaseOS", device.getBuildVersionBaseOS());
        esMap.pushString("buildVersionCodeName", device.getBuildVersionCodeName());
        esMap.pushString("buildVersionSecurityPatch", device.getBuildVersionSecurityPatch());
        esMap.pushInt("buildVersionPreviewSDKInt", device.getBuildVersionPreviewSDKInt());
        esMap.pushInt("buildVersionSDKInt", device.getBuildVersionSDKInt());
        esMap.pushString("buildVersionRelease", device.getBuildVersionRelease());

        if (L.DEBUG) {
            L.logD("#---------getDevice-----esMap----->>>" + esMap);
        }
        promise.resolve(esMap);
    }

    public void getEthMac(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getEthMac());
        }
    }

    public void getWifiMac(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getWifiMac());
        }
    }

    public void getDeviceId(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getDeviceId());
        }
    }

    public void getDeviceType(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getDeviceType());
        }
    }

    public void getTotalMemory(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getTotalMemory());
        }
    }

    public void getAvailableMemory(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getAvailableMemory());
        }
    }

    public void getScreenWidth(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getScreenWidth());
        }
    }

    public void getScreenHeight(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getScreenHeight());
        }
    }

    public void getWindowWidth(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getWindowWidth());
        }
    }

    public void getWindowHeight(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getWindowHeight());
        }
    }

    public void getResolution(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getResolution());
        }
    }

    public void getBuildVersionRelease(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildVersionRelease());
        }
    }

    public void getBuildModel(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildModel());
        }
    }

    public void getBuildBrand(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildBrand());
        }
    }

    public void getBuildDevice(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildDevice());
        }
    }

    public void getBuildBoard(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildBoard());
        }
    }

    public void getBuildProduct(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildProduct());
        }
    }

    public void getBuildHardware(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildHardware());
        }
    }

    public void getBuildManufacturer(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getBuildManufacturer());
        }
    }

    //--------------------------------------------------------------
    public void getDensity(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            if (L.DEBUG) L.logD("initDeviceDisplay getDensity:" + device.getDensity());
            promise.resolve(device.getDensity());
        }
    }

    public void getDensityDpi(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getDensityDpi());
        }
    }

    public void getScaledDensity(EsPromise promise) {
        AndroidDevice device = deviceManager.getAndroidDevice();
        if (promise != null && device != null) {
            promise.resolve(device.getScaledDensity());
        }
    }

    //--------------------------------------------------------------
    public void getESDensity(EsPromise promise) {
        if (promise != null && displayMetrics != null) {
            promise.resolve(displayMetrics.density);
        }
    }

    public void getESDensityDpi(EsPromise promise) {
        if (promise != null && displayMetrics != null) {
            promise.resolve(displayMetrics.densityDpi);
        }
    }

    public void getESScaledDensity(EsPromise promise) {
        if (promise != null && displayMetrics != null) {
            promise.resolve(displayMetrics.scaledDensity);
        }
    }

    //----------------------------------------------------------------------

    public void getAndroidDensity(EsPromise promise) {
        if (promise != null) {
            float value = context != null ? context.getResources().getDisplayMetrics().density : DisplayMetrics.DENSITY_DEFAULT;
            promise.resolve(value);
        }
    }

    public void getAndroidDensityDpi(EsPromise promise) {
        if (promise != null) {
            float value = context != null ? context.getResources().getDisplayMetrics().densityDpi : DisplayMetrics.DENSITY_MEDIUM;
            promise.resolve(value);
        }
    }

    public void getAndroidScaledDensity(EsPromise promise) {
        if (promise != null) {
            float value = context != null ? context.getResources().getDisplayMetrics().scaledDensity : 1;
            promise.resolve(value);
        }
    }
    //----------------------------------------------------------------------

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
