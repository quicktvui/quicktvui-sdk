package com.quicktvui.support.core.device;

import static com.quicktvui.support.core.device.ESRuntimeSPConstants.ES_RUNTIME_SP_KEY_DEVICE_ID;
import static com.quicktvui.support.core.device.ESRuntimeSPConstants.ES_RUNTIME_SP_NAME;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.support.core.module.sp.AndroidSharedPreferencesManager;
import com.sunrain.toolkit.utils.log.L;


/**
 *
 */
public class DeviceIdManager {

    private static DeviceIdManager instance;
    private String deviceId;

    private DeviceIdManager() {
    }

    public static DeviceIdManager getInstance() {
        synchronized (DeviceIdManager.class) {
            if (instance == null) {
                instance = new DeviceIdManager();
            }
        }
        return instance;
    }

    public void init(Context context, String channel) {
        DeviceIdGenerator deviceIdGenerator = new DeviceIdGenerator();
        AndroidSharedPreferencesManager sharedPreferencesManager = new AndroidSharedPreferencesManager();
        sharedPreferencesManager.init(context);
        sharedPreferencesManager.initSharedPreferences(ES_RUNTIME_SP_NAME);
        String deviceId = sharedPreferencesManager.getString(ES_RUNTIME_SP_KEY_DEVICE_ID, null);
        if (TextUtils.isEmpty(deviceId)) {
            try {
                deviceId = deviceIdGenerator.generate(context);
                sharedPreferencesManager.putString(ES_RUNTIME_SP_KEY_DEVICE_ID, deviceId);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        this.deviceId = deviceId;

        if (L.DEBUG) {
            L.logD("-----------deviceId-------->>>>>" + deviceId);
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
