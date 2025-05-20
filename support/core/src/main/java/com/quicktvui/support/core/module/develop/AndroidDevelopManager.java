package com.quicktvui.support.core.module.develop;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.quicktvui.sdk.base.core.EsProxy;

/**
 *
 */
public class AndroidDevelopManager {

    private static AndroidDevelopManager instance;

    private Develop develop;
    private Context context;

    private AndroidDevelopManager() {
    }

    public static AndroidDevelopManager getInstance() {
        synchronized (AndroidDevelopManager.class) {
            if (instance == null) {
                instance = new AndroidDevelopManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        if (this.context == null) {
            return;
        }
        this.develop = new Develop();
        this.develop.setChannel(EsProxy.get().getChannel());
        //
        String packageName = context.getPackageName();
        this.develop.setPackageName(packageName);

        initVersion();
    }

    private void initVersion() {
        //
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            this.develop.setVersionCode(versionCode);
            this.develop.setVersionName(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Develop getDevelop() {
        return develop;
    }
}
