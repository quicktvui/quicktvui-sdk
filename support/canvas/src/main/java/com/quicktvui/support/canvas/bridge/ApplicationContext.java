/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.bridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.ArrayMap;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;


public class ApplicationContext {

    private static final String TAG = "ApplicationContext";

    private static ArrayMap<String, ArrayMap<File, SharedPreferences>> sSharedPrefsCache;
    private ArrayList<PageLifecycleCallbacks> mPageLifecycleCallbacks = new ArrayList<>();
    private Context mContext;
    private String mPackage;
    private ArrayMap<String, File> mSharedPrefsPaths;

    /**
     * Create a new ApplicationContext instance from a context and package name.
     *
     * @param context android context
     * @param pkg     package name of current App
     * @throws IllegalArgumentException If pkg is null.
     */
    public ApplicationContext(Context context, String pkg) {
        mContext = context.getApplicationContext();
        if (pkg == null) {
            // getXXXDir may throws NullPointerException when pkg is null
            throw new IllegalArgumentException("Package Name is not valid");
        }
        mPackage = pkg;
    }

    public Context getContext() {
        return mContext;
    }

    public String getPackage() {
        return mPackage;
    }

    private void checkMode(int mode) {
        if (mContext.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.N) {
            if ((mode & Context.MODE_WORLD_READABLE) != 0) {
                throw new SecurityException("MODE_WORLD_READABLE no longer supported");
            }
            if ((mode & Context.MODE_WORLD_WRITEABLE) != 0) {
                throw new SecurityException("MODE_WORLD_WRITEABLE no longer supported");
            }
        }
    }

    private String getSharedPreferenceName() {
        return "default";
    }

    @Override
    public int hashCode() {
        return mPackage.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ApplicationContext)) {
            return false;
        }
        return mPackage.equals(((ApplicationContext) obj).mPackage);
    }

    public void registerPageLifecycleCallbacks(PageLifecycleCallbacks callback) {
        mPageLifecycleCallbacks.add(callback);
    }

    public void unregisterPageLifecycleCallbacks(PageLifecycleCallbacks callback) {
        mPageLifecycleCallbacks.remove(callback);
    }

    public void dispatchPageStart(IPage page) {
        for (PageLifecycleCallbacks callback : mPageLifecycleCallbacks) {
            callback.onPageStart(page);
        }
    }

    public void dispatchPageStop(IPage page) {
        for (PageLifecycleCallbacks callback : mPageLifecycleCallbacks) {
            callback.onPageStop(page);
        }
    }

    public void dispatchPageDestroy(IPage page) {
        for (PageLifecycleCallbacks callback : mPageLifecycleCallbacks) {
            callback.onPageDestroy(page);
        }
    }

    public interface PageLifecycleCallbacks {
        void onPageStart(@NonNull IPage page);

        void onPageStop(@NonNull IPage page);

        void onPageDestroy(@NonNull IPage page);
    }

}
