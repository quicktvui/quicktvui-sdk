package com.quicktvui.sdk.core.ui.wm;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.sunrain.toolkit.utils.log.L;

/**
 * @author WeiPeng
 * @version 1.0
 * @title AbstractWindowManager.java
 * @created 2019/08/20 11:33
 */
public abstract class AbstractWindowManager {

    protected Context context;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mMainView;
    protected boolean mShown = false;

    public static void requestPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) return;
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public AbstractWindowManager(Context ctx) {
        context = ctx.getApplicationContext();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        }

        mParams.format = PixelFormat.RGBA_8888;
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        configWindowManagerLayoutParams(mParams);
    }

    protected void hasFocus(WindowManager.LayoutParams params) {
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        flags = flags | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        params.flags = params.flags | flags;
    }

    protected void noFocus(WindowManager.LayoutParams params) {
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        flags = flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        flags = flags | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        params.flags = params.flags | flags;
    }

    public WindowManager.LayoutParams getWindowParams() {
        return mParams;
    }

    protected boolean addView(View view) {
        try {
            mWindowManager.addView(view, mParams);
            mMainView = view;
            mShown = true;
            return true;
        } catch (Exception e) {
            L.logW("wm add view", e);
        }
        return false;
    }

    protected boolean removeView() {
        return removeView(mMainView);
    }

    protected boolean removeView(View view) {
        try {
            if (view != null) {
                mWindowManager.removeViewImmediate(view);
                mMainView = null;
                mShown = false;
                return true;
            }
        } catch (Exception e) {
            L.logW("wm rm view", e);
        }
        return false;
    }

    protected <T extends View> T getView(int resId) {
        return (T) mMainView.findViewById(resId);
    }

    protected DisplayMetrics getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    protected abstract void configWindowManagerLayoutParams(WindowManager.LayoutParams params);

    public boolean isShown() {
        return mShown;
    }
}
