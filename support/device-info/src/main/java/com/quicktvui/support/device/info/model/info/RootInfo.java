package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.util.Pair;

import com.quicktvui.support.device.info.utils.CommandUtils;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.device.info.utils.RootUtils;

public class RootInfo {

    public static List<Pair<String, String>> getRootInfo(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("Su_v", CommandUtils.execute("su -v")));
        list.add(new Pair<>("RwPaths", RootUtils.existingRWPaths().toString()));
        list.add(new Pair<>("DangerousProperties", RootUtils.existingDangerousProperties().toString()));
        list.add(new Pair<>("RootFiles", RootUtils.existingRootFiles().toString()));
        list.add(new Pair<>("RootPackages", RootUtils.existingRootPackages(context).toString()));
        return list;
    }

}
