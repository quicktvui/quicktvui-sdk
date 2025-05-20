package com.quicktvui.support.core.module.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

/**
 *
 */
public class AndroidSharedPreferencesManager {

    private static final String TAG = "AndroidSPManager";

    private SharedPreferences sharedPreferences;
    private Context context;

    public void init(Context context) {
        this.context = context;
    }

    public void initSharedPreferences(String name) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(name, Context.MODE_PRIVATE);
    }


    public void putLong(String key, long value) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return;
        }
        long tmp = sharedPreferences.getLong(key, -1);
        if (tmp == value) {
            return;
        }

        Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public long getLong(String key, long defValue) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return defValue;
        }
        return sharedPreferences.getLong(key, defValue);
    }

    public void putString(String key, String value) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return;
        }
        String tmp = sharedPreferences.getString(key, "");
        if (!TextUtils.isEmpty(tmp) && tmp.equals(value)) {
            return;
        }

        Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getString(String key, String defValue) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return defValue;
        }
        return sharedPreferences.getString(key, defValue);
    }

    public void putInt(String key, int value) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return;
        }
        int tmp = sharedPreferences.getInt(key, -1);
        if (tmp == value) {
            return;
        }

        Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public int getInt(String key, int defValue) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return defValue;
        }
        return sharedPreferences.getInt(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return;
        }
        Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public Boolean getBoolean(String key, boolean defValue) {
        if (sharedPreferences == null) {
            Log.e(TAG, "error: SharedPreferences instance is null....");
            return defValue;
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    public void release() {
        context = null;
    }
}
