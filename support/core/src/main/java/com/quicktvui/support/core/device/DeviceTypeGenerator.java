package com.quicktvui.support.core.device;

import android.content.Context;

import com.sunrain.toolkit.utils.log.L;

/**
 *
 */
public class DeviceTypeGenerator implements IDeviceGenerator {

    @Override
    public String generate(Context context) {
        if (L.DEBUG) {
            L.logD("---DeviceTypeGenerator---generate-------->>>>" + android.os.Build.MODEL);
        }
        return android.os.Build.MODEL;
    }
}
