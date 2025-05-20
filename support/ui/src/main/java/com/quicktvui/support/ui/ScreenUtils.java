package com.quicktvui.support.ui;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import android.support.annotation.RequiresApi;

/**
 * 获取屏幕分辨率等信息
 *
 * @author yu
 * @date 2017/10/26
 */

public class ScreenUtils {
    private static final String TAG = ScreenUtils.class.getSimpleName();

    private ScreenUtils() {
        throw new UnsupportedOperationException("cannot be instantiated !");
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static String getResolution(Context context) {
        int screenWidth = getDisplayMetrics(context).widthPixels;
        int screenHeight = getDisplayMetrics(context).heightPixels;
        String resolution = screenWidth + "*" + screenHeight;
        return resolution;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Point getPoint(Context context) {
        Display display = ((WindowManager) (context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        return point;
    }

    /**
     * 获取屏幕Width
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        int screenWidth = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            screenWidth = getPoint(context).x;
        } else {
            screenWidth = getDisplayMetrics(context).widthPixels;
        }
        return screenWidth;
    }

    /**
     * 获取屏幕Height
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        int screenHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            screenHeight = getPoint(context).y;
        } else {
            screenHeight = getDisplayMetrics(context).heightPixels;
        }
        return screenHeight;
    }

    /**
     * 获取屏幕密度
     *
     * @param context
     * @return 密度（0.75 / 1.0 / 1.5）
     */
    public static float getDensity(Context context) {
        float density = getDisplayMetrics(context).density;
        return density;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp==dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}