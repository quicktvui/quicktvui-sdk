package com.quicktvui.sdk.core.entity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.sunrain.toolkit.utils.NetworkUtils;

import java.util.HashMap;
import java.util.Map;

import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.base.Constants;
import com.quicktvui.sdk.base.core.EsProxy;

/**
 * Create by weipeng on 2022/03/01 18:24
 */
public class DeviceInfo {

    private final Map<String, String> info;

    public DeviceInfo(Context context) {
        info = new HashMap<>();
        info.put("os", "android");
        info.put("os_ver", Build.VERSION.RELEASE);
        info.put("brand", Build.BRAND);
        info.put("model", Build.MODEL);
        info.put("id", Build.ID);
        info.put("mac_eth", NetworkUtils.getEthMac());
        info.put("mac_wifi", NetworkUtils.getWifiMac());
        info.put("app_bundle", context.getPackageName());
        info.put("appid", EsContext.get().getAppId());
        info.put("sdk_channel", EsContext.get().getAppChannel());
        info.put("sdk_ver", EsProxy.get().getSdkVersionName());
        info.put("sdk_ver_code", "" + EsProxy.get().getSdkVersionCode());
        info.put(Constants.ESKIT_V_CODE, "" + EsProxy.get().getEsKitVersionCode());
        info.put(Constants.ESKIT_V_NAME, "" + EsProxy.get().getEsKitVersionName());

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            this.info.put("app_ver_code", String.valueOf(pi.versionCode));
            this.info.put("app_ver_name", pi.versionName);
        } catch (Exception ignore) {
        }

        String androidId;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        } catch (Throwable e) {
            androidId = "";
        }
        info.put("android_id", androidId);
    }

    public Map<String, String> getInfo() {
        return info;
    }
}
