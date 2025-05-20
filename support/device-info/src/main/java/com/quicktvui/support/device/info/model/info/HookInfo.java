package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.util.Pair;

import com.quicktvui.support.device.info.utils.XposedHookUtils;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.device.info.utils.HookUtils;

public class HookInfo {

    public static List<Pair<String, String>> getHookInfo(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("Process", HookUtils.checkRunningProcesses(context) + ""));
        list.add(new Pair<>("XposedHookMethod", HookUtils.chargeXposedHookMethod()));
        list.add(new Pair<>("XposedJars", HookUtils.chargeXposedJars()));
        list.add(new Pair<>("XposedPackage", HookUtils.chargeXposedPackage(context)));
        list.add(new Pair<>("XposedInject", XposedHookUtils.checkXposedInjet(context).toString()));
        list.add(new Pair<>("XposedClass", HookUtils.classCheck() + ""));
        return list;
    }

}
