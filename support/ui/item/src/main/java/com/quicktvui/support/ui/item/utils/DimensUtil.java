package com.quicktvui.support.ui.item.utils;

import android.content.Context;


public class DimensUtil {

    static Context mContext;
    // 将px值转换为sp值，保证文字大小不变

    public static void init(Context context){
        mContext = context;

    }

//    private static float density = -1;

    @Deprecated
    public static float getPXWithDP(Context context,float dp){
        return context.getResources().getDisplayMetrics().density * dp;
    }


    public static int dp2Px(Context context,float dp){
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }


    public static int sp2Px(float sp){
        if(mContext == null){
            throw new RuntimeException("请在Application中调用ItemCenter进行初始化");
        }
        return (int) (mContext.getResources().getDisplayMetrics().scaledDensity * sp + 0.5f);
    }


    public static int dp2Px(float dp){
        if(mContext == null){
            throw new RuntimeException("请在Application中调用ItemCenter进行初始化");
        }
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }


    public static int pxFrom(Context context,int resource){
        return (int) (context.getResources().getDimension(resource) + 0.5f);
    }

    // 将px值转换为sp值，保证文字大小不变
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    // 将sp值转换为px值，保证文字大小不变
    public static int sp2px(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }
    //屏幕适配所需的缩放系数，以1080P为基准
    public static float getFitScale(Context context){
        int heiPixels = context.getResources().getDisplayMetrics().heightPixels;
        float scale = heiPixels*1.0f / 1080 * 1.0f;
        return scale;
    }
}
