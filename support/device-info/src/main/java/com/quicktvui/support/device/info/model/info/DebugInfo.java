package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.util.Pair;

import com.quicktvui.support.device.info.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;


public class DebugInfo {

    public static List<Pair<String, String>> getDebugInfo(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("DebugOpen", DebugUtils.isOpenDebug(context) + ""));
        list.add(new Pair<>("UsbDebugStatus", DebugUtils.getUsbDebugStatus()));
        list.add(new Pair<>("TracerPid", DebugUtils.getTracerPid() + ""));
        list.add(new Pair<>("DebugVersion", DebugUtils.isDebugVersion(context) + ""));
        list.add(new Pair<>("DebugConnected", DebugUtils.isDebugConnected() + ""));
        list.add(new Pair<>("AllowMockLocation", DebugUtils.isAllowMockLocation(context) + ""));
        return list;
    }

}
