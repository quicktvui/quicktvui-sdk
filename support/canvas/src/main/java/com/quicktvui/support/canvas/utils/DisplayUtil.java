/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.sunrain.toolkit.utils.Utils;


public class DisplayUtil {
    private static final String TAG = "DisplayUtil";

    private static int sViewPortWidth;
    private static int sViewPortHeight;

    private DisplayUtil() {
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int dip2px(float dipValue) {
        Context context = Utils.getApp();
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);  //+0.5是为了向上取整
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        Context context = Utils.getApp();
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float getRealPxByWidth(float designPx, int designWidth) {
        /*Context context = Utils.getApp();
        float densityScaledRatio = 1f;
        return designPx * getScreenWidth(context) / designWidth * densityScaledRatio;*/
        return designPx;
    }

    public static float getDesignPxByWidth(float realPx, int designWidth) {
        Context context = Utils.getApp();
        float densityScaledRatio = 1f;
        return realPx / getScreenWidth(context) * designWidth / densityScaledRatio;
    }

    public static boolean isPortraitMode(Context context) {
        if (context != null
                && context.getResources() != null
                && context.getResources().getDisplayMetrics() != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.heightPixels > displayMetrics.widthPixels;
        }
        return false;
    }

    public static boolean isLandscapeMode(Context context) {
        if (context != null
                && context.getResources() != null
                && context.getResources().getDisplayMetrics() != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.widthPixels > displayMetrics.heightPixels;
        }
        return false;
    }

    public static int getScreenWidth(Context context) {
        //return DeviceManager.getInstance().getScreenWidth();
        return getScreenWidthByDP();
    }

    public static int getScreenHeight(Context context) {
        //return DeviceManager.getInstance().getScreenHeight();
        return getScreenHeightByDp();
    }

    public static int dip2Pixel(Context context, int dip) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) (dip * density + 0.5f);
    }

    public static int getDestinyDpi() {
        DisplayMetrics dm = Utils.getApp().getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    public static int getScreenWidthByDP() {
        Context context = Utils.getApp();
        if (context == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    public static int getScreenHeightByDp() {
        Context context = Utils.getApp();
        if (context == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels / displayMetrics.density);
    }

    public static int getViewPortWidthByDp() {
        Context context = Utils.getApp();
        if (context == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (sViewPortWidth / displayMetrics.density);
    }

    public static int getViewPortHeightByDp() {
        Context context = Utils.getApp();
        if (context == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (sViewPortHeight / displayMetrics.density);
    }

    /**
     * 判断是否为电视设备
     *
     * @param context
     * @return
     */
    public static boolean isTelevisionDevice(Context context) {
        UiModeManager uiModeManager =
                (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager == null) {
            return false;
        }
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    public static int getNavigationBarHeight(Context context) {
        int resourceId =
                context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }

        return 0;
    }
}
