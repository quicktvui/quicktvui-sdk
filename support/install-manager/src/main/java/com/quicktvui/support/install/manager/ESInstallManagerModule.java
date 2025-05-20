package com.quicktvui.support.install.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import android.support.v4.content.FileProvider;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


@ESKitAutoRegister
public class ESInstallManagerModule implements IEsModule, IEsInfo {
    private Context mContext;

    private static final String TAG = "ESInstallManagerModule";

    @Override
    public void getEsInfo(EsPromise promise) {

    }

    @Override
    public void init(Context context) {
        this.mContext = context;
    }

    @Override
    public void destroy() {

    }

    public void installPackage(String filePath, String apkName, EsPromise promise) {
        File apkFile = new File(filePath, apkName);//data/data/包名/
        if (!apkFile.exists()) {
            EsMap map = new EsMap();
            map.pushString("message", "文件不存在");
            promise.resolve(map);
            Toast.makeText(EsProxy.get().getContext(), "文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = null;
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mContext != null) {
                uri = FileProvider.getUriForFile(Objects.requireNonNull(EsProxy.get().getContext()), mContext.getApplicationContext().getPackageName() + ".fileProvider", apkFile);
            } else {
                uri = FileProvider.getUriForFile(Objects.requireNonNull(EsProxy.get().getContext()), EsProxy.get().getContext().getApplicationContext().getPackageName() + ".fileProvider", apkFile);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        try {
            Objects.requireNonNull(EsProxy.get().getContext()).startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(EsProxy.get().getContext(), "安装包异常", Toast.LENGTH_SHORT).show();
        }
    }

    public void installESPackage(String filePath, String apkName, EsPromise promise) {
        File apkFile = new File(filePath, apkName);//data/data/包名/
        if (!apkFile.exists()) {
            EsMap map = new EsMap();
            map.pushString("message", "文件不存在");
            promise.resolve(map);
            Toast.makeText(EsProxy.get().getContext(), "文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(EsProxy.get().getContext()), "eskit.sdk.support.install.manager.fileProvider", apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent = new Intent();
            Uri uri = Uri.fromFile(apkFile);
            intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            Objects.requireNonNull(EsProxy.get().getContext()).startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(EsProxy.get().getContext(), "安装包异常", Toast.LENGTH_SHORT).show();
        }
    }

    private static Uri getApkUri(File apkFile) {
        Log.d(TAG, apkFile.toString());
        //如果没有设置 SDCard 写权限，或者没有 SDCard,apk 文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        Uri uri = Uri.fromFile(apkFile);
        Log.d(TAG, uri.toString());
        return uri;
    }

    /**
     * @param permission
     * @param path
     */
    public void chmod(String permission, String path, Intent intent) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec(command);
            int state = p.waitFor();
            if (state == 0) {
                Toast.makeText(EsProxy.get().getContext(), "权限修改成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EsProxy.get().getContext(), "权限修改失败", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
