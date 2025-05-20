package com.quicktvui.support.core.device;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.quicktvui.support.core.module.sp.AndroidSharedPreferencesManager;
import com.sunrain.toolkit.utils.log.L;


/**
 *
 */
public class DeviceTypeManager {

    private static DeviceTypeManager instance;
    private String deviceType;

    private DeviceTypeManager() {
    }

    public static DeviceTypeManager getInstance() {
        synchronized (DeviceTypeManager.class) {
            if (instance == null) {
                instance = new DeviceTypeManager();
            }
        }
        return instance;
    }

    public void init(Context context, String channel) {
        if (L.DEBUG) {
            L.logD("-----------Build.BRAND-------->>>>>brand:" + Build.BRAND + "---->>channel:" + channel);
        }
        AndroidSharedPreferencesManager sharedPreferencesManager = new AndroidSharedPreferencesManager();
        sharedPreferencesManager.init(context);
        sharedPreferencesManager.initSharedPreferences(ESRuntimeSPConstants.ES_RUNTIME_SP_NAME);
        deviceType = sharedPreferencesManager.getString(ESRuntimeSPConstants.ES_RUNTIME_SP_KEY_DEVICE_TYPE, null);

        if (!TextUtils.isEmpty(deviceType)) {
            return;
        }

        try {
            IDeviceGenerator deviceTypeGenerator = null;
            //
            if (deviceTypeGenerator == null) {
                deviceTypeGenerator = new DeviceTypeGenerator();
            }
            deviceType = deviceTypeGenerator.generate(context);
            sharedPreferencesManager.putString(ESRuntimeSPConstants.ES_RUNTIME_SP_KEY_DEVICE_TYPE, deviceType);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (L.DEBUG) {
            L.logD("-----------deviceType-------->>>>>" + deviceType);
        }
    }

    public String getDeviceType() {
        return deviceType;
    }
}
