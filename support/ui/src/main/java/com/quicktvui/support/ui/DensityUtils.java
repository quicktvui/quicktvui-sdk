package com.quicktvui.support.ui;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtils {

    // 根据手机的分辨率将dp的单位转成px(像素)
	public static int dip2px(Context context, float dpValue) {
		final float scale = ScreenUtils.getDensity(context);
		return (int) (dpValue * scale + 0.5f);
	}

    // 根据手机的分辨率将px(像素)的单位转成dp
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

    // 将px值转换为sp值
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

    // 将sp值转换为px值
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

    // 屏幕宽度（像素）
    public static  int getWindowWidth(Activity context){
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    // 屏幕高度（像素）
    public static int getWindowHeight(Activity activity){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static void showDensity(Activity context){
	    DisplayMetrics dm = new DisplayMetrics();
	    context.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public static int  getDensityDpi(Activity context){
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.densityDpi;
	}
}
