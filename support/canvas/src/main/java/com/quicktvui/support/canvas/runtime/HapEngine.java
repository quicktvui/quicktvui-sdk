/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.runtime;

import android.content.Context;

import com.quicktvui.support.canvas.bridge.ApplicationContext;
import com.sunrain.toolkit.utils.Utils;

import java.util.concurrent.ConcurrentHashMap;

public class HapEngine {
    private static final String TAG = "HapEngine";
    private static final ConcurrentHashMap<String, HapEngine> sEngines = new ConcurrentHashMap();
    private Context mContext;
    private String mPackage;
    private Mode mMode;
    private ApplicationContext mApplicationContext;

    public HapEngine(Context context, String pkg) {
        mContext = context;
        mPackage = pkg;
        mMode = Mode.APP;
    }

    public static HapEngine getInstance(String pkg) {
        HapEngine engine = sEngines.get(pkg);
        if (engine == null) {
            engine = new HapEngine(Utils.getApp(), pkg);
            HapEngine oldEngine = sEngines.putIfAbsent(pkg, engine);
            if (oldEngine != null) {
                engine = oldEngine;
            }
        }
        return engine;
    }

    public Context getContext() {
        return mContext;
    }

    public String getPackage() {
        return mPackage;
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }

    public boolean isCardMode() {
        return mMode == Mode.CARD;
    }

    public boolean isInsetMode() {
        return mMode == Mode.INSET;
    }

    public int getDesignWidth() {
        /*AppInfo appInfo = getApplicationContext().getAppInfo();
        ConfigInfo info = appInfo == null ? null : appInfo.getConfigInfo();
        return info == null ? ConfigInfo.DEFAULT_DESIGN_WIDTH : info.getDesignWidth();*/
        return 750;
    }

    public ApplicationContext getApplicationContext() {
        if (mApplicationContext == null) {
            mApplicationContext = new ApplicationContext(mContext, mPackage);
        }
        return mApplicationContext;
    }

    public static class Mode {

        public static final Mode APP = new Mode(0, "APP");
        public static final Mode CARD = new Mode(1, "CARD");
        public static final Mode INSET = new Mode(2, "INSET");

        int value;
        String name;

        public Mode(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return value;
        }

        public String name() {
            return name;
        }
    }
}
