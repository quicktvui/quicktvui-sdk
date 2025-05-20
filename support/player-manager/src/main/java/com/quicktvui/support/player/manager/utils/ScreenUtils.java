package com.quicktvui.support.player.manager.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

    private static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

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
     * 返回dp值
     */
    public static int dp2px(Context context, int dp) {
        try {
            if (context != null) {
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
            } else {
                return 0;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 返回dp值
     */
    public static int dp2px(Context context, float dp) {
        try {
            if (context != null) {
                int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
                return value;
            } else {
                return 0;
            }
        } catch (Throwable e) {
            return 0;
        }
    }

    public static float dp2pxToFloat(Context context, float dp) {
        try {
            if (context != null) {
                int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
                return value;
            } else {
                return 0;
            }
        } catch (Throwable e) {
            return 0;
        }
    }

    /**
     * convert px to its equivalent sp
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * convert sp to its equivalent px
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
