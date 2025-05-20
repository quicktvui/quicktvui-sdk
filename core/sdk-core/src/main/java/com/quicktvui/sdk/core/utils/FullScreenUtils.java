package com.quicktvui.sdk.core.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sunrain.toolkit.utils.log.L;

/**
 * <br>
 *
 * <br>
 */
public class FullScreenUtils {

    public static void apply(Activity activity) {
        setActivityFullScreen(activity);
        dismissActionBar(activity);
        setWindowShortEdges(activity);
    }

    public static void onWindowFocusChanged(Activity activity, boolean hasFocus) {
        if (hasFocus){
            try {
                View decorView = activity.getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } catch (Exception e) {
                L.logWF("window focus change", e);
            }
        }
    }

    private static void setActivityFullScreen(Activity activity) {
        try {
//            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        } catch (Exception e) {
            L.logW("full screen", e);
        }
    }

    private static void dismissActionBar(Activity activity) {
        try {
//            ActionBar actionBar = getSupportActionBar();
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        } catch (Exception e) {
            L.logWF("hide action", e);
        }
    }

    private static void setWindowShortEdges(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams wlp = activity.getWindow().getAttributes();
            wlp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(wlp);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
