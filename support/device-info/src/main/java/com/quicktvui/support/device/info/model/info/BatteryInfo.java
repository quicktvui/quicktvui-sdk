package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.util.Pair;


import com.quicktvui.support.device.info.utils.BatteryUtils;

import java.util.ArrayList;
import java.util.List;


public class BatteryInfo {

    /**
     * 获取电池信息
     *
     * @return 电池JSON
     */
    public static List<Pair<String, String>> getBatteryInfo(Context context) {
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        BatteryUtils.getBatteryInfo(context, list);
        return list;
    }

}
