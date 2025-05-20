package com.quicktvui.support.core.module.storage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class AndroidStorageManager {

    private static AndroidStorageManager instance;

    private Context context;

    private AndroidStorageManager() {
    }

    public static AndroidStorageManager getInstance() {
        synchronized (AndroidStorageManager.class) {
            if (instance == null) {
                instance = new AndroidStorageManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public File getCacheDir() {
        return this.context.getCacheDir();
    }

    public File getFilesDir() {
        return this.context.getFilesDir();
    }

    public File getExternalCacheDir() {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return appCacheDir;
    }

    public String getExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    public boolean hasExternalStoragePermission() {
        return PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
    }

}
