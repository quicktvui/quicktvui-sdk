package com.quicktvui.support.device.info.utils;

import static android.content.Context.DISPLAY_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.quicktvui.sdk.base.core.EsProxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;


public class DensityUtils {

    /**
     * dp转px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取 dpi
     *
     * @param context
     */
    public static int getDensityDpi(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.densityDpi;
    }

    /**
     * 获取屏幕标准密度倍数
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    /**
     * 获取像素密度等级
     *
     * @param context
     * @return
     */
    public static String getDensityId(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        if (density < 1.0) {
            return "ldpi";
        } else if (density <= 1.0) {
            return "mdpi";
        } else if (density <= 1.5) {
            return "hdpi";
        } else if (density <= 2.0) {
            return "xhdpi";
        } else if (density <= 3.0) {
            return "xxhdpi";
        } else {
            return "xxxhdpi";
        }
    }

    /**
     * 获取屏幕宽度，单位 px
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        int widthPx = 0;
        int heightPx;
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        WindowManager mWindowManager = Objects.requireNonNull(EsProxy.get().getTopActivity()).getWindowManager();
        Resources mResources = Objects.requireNonNull(EsProxy.get().getTopActivity()).getResources();
        Display mDisplay = null;
        DisplayMetrics mDisplayMetrics = null;
        if (mWindowManager != null) {
            mDisplay = mWindowManager.getDefaultDisplay();
        }
        if (mResources != null) {
            mDisplayMetrics = mResources.getDisplayMetrics();
        }
        if (!(mDisplay == null || mDisplayMetrics == null)) {
            float dpiX = mDisplayMetrics.xdpi;
            float dpiY = mDisplayMetrics.ydpi;
            if (Build.VERSION.SDK_INT >= 17) {
                Point realSize = new Point();
                mDisplay.getRealSize(realSize);
                widthPx = realSize.x;
                heightPx = realSize.y;
            } else if (Build.VERSION.SDK_INT >= 14) {
                try {
                    widthPx = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(mDisplay, new Object[0])).intValue();
                    heightPx = ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(mDisplay, new Object[0])).intValue();
                } catch (Exception e) {
                    widthPx = mDisplayMetrics.widthPixels;
                    heightPx = mDisplayMetrics.heightPixels;
                }
            } else {
                widthPx = mDisplayMetrics.widthPixels;
                heightPx = mDisplayMetrics.heightPixels;
            }
        }
        return widthPx;
    }

    /**
     * 获取屏幕高度，单位 px
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        int widthPx = 0;
        int heightPx = 0;
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        WindowManager mWindowManager = Objects.requireNonNull(EsProxy.get().getTopActivity()).getWindowManager();
        Resources mResources = Objects.requireNonNull(EsProxy.get().getTopActivity()).getResources();
        Display mDisplay = null;
        DisplayMetrics mDisplayMetrics = null;
        if (mWindowManager != null) {
            mDisplay = mWindowManager.getDefaultDisplay();
        }
        if (mResources != null) {
            mDisplayMetrics = mResources.getDisplayMetrics();
        }
        if (!(mDisplay == null || mDisplayMetrics == null)) {
            float dpiX = mDisplayMetrics.xdpi;
            float dpiY = mDisplayMetrics.ydpi;
            if (Build.VERSION.SDK_INT >= 17) {
                Point realSize = new Point();
                mDisplay.getRealSize(realSize);
                widthPx = realSize.x;
                heightPx = realSize.y;
            } else if (Build.VERSION.SDK_INT >= 14) {
                try {
                    widthPx = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(mDisplay, new Object[0])).intValue();
                    heightPx = ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(mDisplay, new Object[0])).intValue();
                } catch (Exception e) {
                    widthPx = mDisplayMetrics.widthPixels;
                    heightPx = mDisplayMetrics.heightPixels;
                }
            } else {
                widthPx = mDisplayMetrics.widthPixels;
                heightPx = mDisplayMetrics.heightPixels;
            }
        }
        return heightPx;
    }

    /**
     * 获取屏幕宽度，单位 dp
     *
     * @param context
     * @return
     */
    public static int getScreenWidthWithDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) (displayMetrics.widthPixels / density + 0.5f);
    }

    /**
     * 获取屏幕高度，单位 dp
     *
     * @param context
     * @return
     */
    public static int getScreenHeightWithDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) (displayMetrics.heightPixels / density + 0.5f);
    }

    /**
     * 获取屏幕刷新率
     *
     * @param activity
     * @return
     */
    public static int getRefreshRate(Activity activity) {
        return (int) activity.getWindowManager().getDefaultDisplay().getRefreshRate();
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private static int getStatusBarHeightWithReflect(Context context) {
        int statusBarHeight = -1;
        try {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(Objects.requireNonNull(clazz.getField("status_bar_height").get(object)).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static boolean hideStatusBar(Context context) {
        return checkFullScreenByTheme(context)
                || checkFullScreenByCode(context)
                || checkFullScreenByCode2(context);
    }

    private static boolean checkFullScreenByTheme(Context context) {
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            TypedValue typedValue = new TypedValue();
            boolean result = theme.resolveAttribute(android.R.attr.windowFullscreen, typedValue, false);
            if (result) {
                typedValue.coerceToString();
                if (typedValue.type == TypedValue.TYPE_INT_BOOLEAN) {
                    return typedValue.data != 0;
                }
            }
        }
        return false;
    }

    private static boolean checkFullScreenByCode(Context context) {
        if (context instanceof Activity) {
            Window window = ((Activity) context).getWindow();
            if (window != null) {
                View decorView = window.getDecorView();
                return (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
        }
        return false;
    }

    private static boolean checkFullScreenByCode2(Context context) {
        if (context instanceof Activity) {
            return (((Activity) context).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    == WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        return false;
    }

    public static boolean hasNavigationBar(Context context) {
        if (context instanceof Activity) {
            WindowManager windowManager = ((Activity) context).getWindowManager();
            Display d = windowManager.getDefaultDisplay();
            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                d.getRealMetrics(realDisplayMetrics);
            }
            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);
            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;
            return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        }
        return false;
    }

    public static boolean isHDRSupported() {
        MediaCodecList codecList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
            MediaCodecInfo[] codecs = codecList.getCodecInfos();
            for (MediaCodecInfo codec : codecs) {
                if (!codec.isEncoder()) {  // 只检测解码器
                    String[] supportedTypes = codec.getSupportedTypes();
                    for (String type : supportedTypes) {
                        if (type.equalsIgnoreCase("video/hevc") || type.equalsIgnoreCase("video/avc")) {
                            MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(type);
                            if (capabilities.colorFormats != null) {
                                for (int colorFormat : capabilities.colorFormats) {
                                    if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible) {
                                        return true; // 设备支持 HDR
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isHDMIOutputSupported() {
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.hdmi.enable");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String hdmiEnable = reader.readLine();
            reader.close();
            return "1".equals(hdmiEnable);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getHDMIVersion() {
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.hdmi.version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String hdmiVersion = reader.readLine();
            reader.close();

            if (hdmiVersion == null || hdmiVersion.isEmpty()) {
//                return "无法获取 HDMI 版本";
                return "";
            } else {
                Log.d("test", "getHDMIVersion: ---------->" + hdmiVersion);
                return hdmiVersion;
            }
        } catch (Exception e) {
            return "获取 HDMI 版本失败：" + e.getMessage();
        }
    }

    public static String getHDMIInfoFromSys() {
        String path = "/sys/class/hdmi/hdmi_version";  // 这是一个假设路径，根据设备可能不同
        File file = new File(path);
        if (!file.exists()) {
            Log.d("HDMI Info", "未找到 HDMI 信息路径");
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String hdmiVersion = reader.readLine();
            Log.d("HDMI Info", "HDMI 版本: " + (hdmiVersion != null ? hdmiVersion : "未找到 HDMI 版本"));
            if (!TextUtils.isEmpty(hdmiVersion)) {
                return hdmiVersion;
            }
        } catch (IOException e) {
            Log.e("HDMI Info", "读取 HDMI 信息失败: " + e.getMessage());
        }
        return "";
    }

    public static String getDisplayPanelInfo() {
        try {
            // 获取屏幕厂商或其他信息
            Process process = Runtime.getRuntime().exec("getprop ro.product.model");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String displayInfo = reader.readLine();
            reader.close();
            Log.d("Display Info", "屏幕信息: " + (displayInfo != null ? displayInfo : "未找到屏幕信息"));
            if (TextUtils.isEmpty(displayInfo)) {
                return "";
            }
            return displayInfo;
        } catch (Exception e) {
            Log.e("Display Info", "获取屏幕信息失败: " + e.getMessage());
            return "";
        }
    }

    public static String getPanelInfo() {
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.lcd_vendor");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String panelVendor = reader.readLine();
            reader.close();

            process = Runtime.getRuntime().exec("getprop ro.display.technology");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String displayTech = reader.readLine();
            reader.close();
            return (panelVendor.isEmpty() ? "" : "屏幕供应商: " + panelVendor) + (displayTech.isEmpty() ? "" : "显示技术: " + displayTech);
        } catch (Exception e) {
            return "无法获取面板信息: " + e.getMessage();
        }
    }

    public static String getPanelInfoFromSys(Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();

        for (Display display : displays) {
            String displayName = display.getName();
            if (!TextUtils.isEmpty(displayName)) {
                Log.d("Display Info", "显示器: " + displayName);
                return displayName;
            }
        }
        return "";
    }
}
