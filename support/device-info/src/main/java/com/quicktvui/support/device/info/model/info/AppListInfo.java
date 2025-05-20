package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.quicktvui.support.device.info.model.beans.ApplicationBean;

import java.util.ArrayList;
import java.util.List;

public class AppListInfo {

    /**
     * 获取应用列表
     *
     * @param context
     * @return
     */
    public static List<ApplicationBean> getAppListInfo(Context context) {
        List<ApplicationBean> list = new ArrayList<>();
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo info : installedPackages) {
            ApplicationBean bean = new ApplicationBean();
            bean.setName(info.applicationInfo.loadLabel(packageManager).toString());
            bean.setPackageName(info.packageName);
            bean.setVersion(info.versionName);
            bean.setIcon(info.applicationInfo.loadIcon(packageManager));
            bean.setBuildVersion(info.applicationInfo.targetSdkVersion);
            if ((ApplicationInfo.FLAG_SYSTEM & info.applicationInfo.flags) == 0) {
                bean.setSystemApp(false);
            } else {
                bean.setSystemApp(true);
            }
            list.add(bean);
        }
        return list;
    }

}
