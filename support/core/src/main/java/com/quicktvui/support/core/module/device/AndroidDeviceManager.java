package com.quicktvui.support.core.module.device;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

import com.sunrain.toolkit.utils.NetworkUtils;
import com.sunrain.toolkit.utils.log.L;


/**
 * 设备信息管理
 */
public class AndroidDeviceManager {

    private static AndroidDeviceManager instance;

    private Context context;
    private AndroidDevice device;


    private AndroidDeviceManager() {
    }

    public static AndroidDeviceManager getInstance() {
        synchronized (AndroidDeviceManager.class) {
            if (instance == null) {
                instance = new AndroidDeviceManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        //
        this.device = new AndroidDevice();

        this.initMacAddress();
        this.initBuild();
        this.initDeviceDisplay();
        this.initIpAddress();
        this.initDeviceMemory();
        if (L.DEBUG) {
            L.logD("#-------init-------->>>>>" + device);
        }
    }

    @SuppressLint("MissingPermission")
    private void initIpAddress() {
        NetworkUtils.NetworkType type = NetworkUtils.getNetworkType();
        if (type == NetworkUtils.NetworkType.NETWORK_WIFI) {
            device.setIpAddress(NetworkUtils.getIpAddressByWifi());
        } else {
            device.setIpAddress(NetworkUtils.getIPAddress(true));
        }
    }

    private void initMacAddress() {

        try {
            String ethMac = NetworkUtils.getEthMac();
            device.setEthMac(ethMac);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            String wifiMac = NetworkUtils.getWifiMac();
            device.setWifiMac(wifiMac);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private void initBuild() {
        //
        device.setBuildModel(Build.MODEL);
        device.setBuildBrand(Build.BRAND);
        device.setBuildBoard(Build.BOARD);
        device.setBuildDevice(Build.DEVICE);
        device.setBuildProduct(Build.PRODUCT);
        device.setBuildHardware(Build.HARDWARE);
        device.setBuildManufacturer(Build.MANUFACTURER);
        device.setBuildSerial(Build.SERIAL);
        device.setBuildTags(Build.TAGS);
        device.setBuildId(Build.ID);
        device.setBuildTime(Build.TIME);
        device.setBuildType(Build.TYPE);
        device.setBuildUser(Build.USER);
        device.setBuildBootloader(Build.BOOTLOADER);
        device.setBuildDisplay(Build.DISPLAY);
        device.setBuildFingerPrint(Build.FINGERPRINT);

        //
        device.setBuildVersionIncremental(Build.VERSION.INCREMENTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.setBuildVersionBaseOS(Build.VERSION.BASE_OS);
        }
        device.setBuildVersionCodeName(Build.VERSION.CODENAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.setBuildVersionSecurityPatch(Build.VERSION.SECURITY_PATCH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.setBuildVersionPreviewSDKInt(Build.VERSION.PREVIEW_SDK_INT);
        }
        device.setBuildVersionSDKInt(Build.VERSION.SDK_INT);
        device.setBuildVersionRelease(Build.VERSION.RELEASE);
    }

    private void initDeviceDisplay() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        device.setScreenWidth(screenWidth);
        device.setScreenHeight(dm.heightPixels);
        device.setDensity(dm.density);
        device.setDensityDpi(dm.densityDpi);
        device.setScaledDensity(dm.scaledDensity);
        device.setResolution(screenWidth + "*" + screenHeight);
        if (L.DEBUG) L.logD("initDeviceDisplay density:" + dm.density);
        if (L.DEBUG) L.logD("initDeviceDisplay densityDpi:" + dm.densityDpi);
        if (L.DEBUG) L.logD("initDeviceDisplay scaledDensity:" + dm.scaledDensity);
    }

    private void initDeviceMemory() {
        try {
            long totalMemory = getTotalMemory();
            device.setTotalMemory(totalMemory);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            long availMemoryMemory = getAvailMemory();
            device.setAvailableMemory(availMemoryMemory);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private long getTotalMemory() {
        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return memInfo.totalMem / 1024 / 1024;
        }
        return -1;
    }

    private long getAvailMemory() {
        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);
        return memInfo.availMem / 1024 / 1024;
    }

    public AndroidDevice getAndroidDevice() {
        return device;
    }
}
