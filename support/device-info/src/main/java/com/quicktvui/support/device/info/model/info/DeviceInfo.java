package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.device.info.utils.DeviceUtils;


public class DeviceInfo {

    //    @AddTrace(name = "DeviceInfo.getDeviceInfo")
    public static List<Pair<String, String>> getDeviceInfo(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("AndroidId", DeviceUtils.getAndroidId(context)));
        list.add(new Pair<>("IMEI", DeviceUtils.getIMEI(context)));
        DeviceUtils.getDeviceInfo(context, list);
        list.add(new Pair<>("ICCID", DeviceUtils.getIccId(context)));
        DeviceUtils.getSimInfo(context, list);
        DeviceUtils.getOtherInfo(context, list);
        return list;
    }

}
