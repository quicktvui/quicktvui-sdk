package com.quicktvui.support.device.info.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

public class ApplicationUtils {

    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception e) {
            packageInfo = null;
        }
        return packageInfo != null;
    }

}
