package com.quicktvui.sdk.core.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Pair;

import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.sdk.base.EsCallback;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 * Created by WeiPeng on 2023-12-19 10:07
 */
public class PermissionRequestHelper {

    public static final int PERMISSION_REQUEST_CODE = 12321;

    private List<String> granted;
    private List<String> never;
    private List<String> denied;

    private Activity activity;
    private EsCallback<List<String>, Pair<List<String>, List<String>>> callback;

    public void requestPermission(Activity activity, String[] permissions,
                                  EsCallback<List<String>, Pair<List<String>, List<String>>> callback) {
        this.activity = activity;
        this.callback = callback;

        if (granted == null) {
            granted = new ArrayList<>(permissions.length);
            never = new ArrayList<>(permissions.length);
            denied = new ArrayList<>(permissions.length);
        }

        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE) return;
        int len = permissions.length;
        for (int i = 0; i < len; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                L.logIF(permissions[i] + " GRANTED");
                granted.add(permissions[i]);
            } else if (grantResults[i] == PackageManager.PERMISSION_DENIED
                    && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                L.logIF(permissions[i] + " DENIED NEVER");
                never.add(permissions[i]);
            } else {
                L.logIF(permissions[i] + " DENIED");
                denied.add(permissions[i]);
            }
        }

        try {
            if (this.denied.size() != 0 || this.never.size() != 0) {
                callback.onFailed(new Pair<>(never, denied));
            } else {
                callback.onSuccess(granted);
            }
        } catch (Exception e) {
            L.logW("permission", e);
        } finally {
            activity = null;
            callback = null;
            granted.clear();
            denied.clear();
            never.clear();
            granted = denied = never = null;
        }
    }
}
