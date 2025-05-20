package com.quicktvui.sdk.core.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sunrain.toolkit.utils.ReflectUtils;
import com.tencent.mtt.hippy.HippyInstanceContext;

import java.util.HashMap;
import java.util.Map;

import com.quicktvui.sdk.core.R;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;
import com.quicktvui.sdk.core.jsview.JsSlotView;

/**
 * <br>
 *
 * <br>
 */
public class SlotUtils {

    public static void waitPhoneWindowAdded(Context context) {
        try {
            View windowView = getDecorView(context);
            while (windowView.findViewById(Window.ID_ANDROID_CONTENT) == null) {
                Thread.sleep(16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static View getDecorView(Context context) {
        if (context instanceof HippyInstanceContext) {
            context = ((HippyInstanceContext) context).getBaseContext();
        }
        PluginUtils.assertIsInstanceOfActivity(context);
        if(!PluginUtils.IS_PLUGIN_MODE) {
            return ((Activity) context).getWindow().getDecorView();
        }
        return ReflectUtils.reflect(context)
                .method("getWindow").method("getDecorView").get();
    }

    @Nullable
    public static IEsAppLoadHandler getAppLoaderHandler(Context context) {
        View decorView = getDecorView(context);
        if (decorView != null) {
            return (IEsAppLoadHandler) decorView.getTag(R.id.es_js_view_tag1);
        }
        return null;
    }

    public static void setJSViewContainer(JsSlotView jsView) {
        View decorView = getDecorView(jsView.getContext());
        if (decorView != null) {
            Map<String, JsSlotView> map = (Map<String, JsSlotView>) decorView.getTag(R.id.es_js_view_tag2);
            if (map == null) {
                map = new HashMap<>(5);
                decorView.setTag(R.id.es_js_view_tag2, map);
            }
            map.put(jsView.getSid(), jsView);
        }
    }

    @Nullable
    public static JsSlotView getSViewContainer(Context context, String sid) {
        View decorView = getDecorView(context);
        if (decorView != null) {
            Map<String, JsSlotView> map = (Map<String, JsSlotView>) decorView.getTag(R.id.es_js_view_tag2);
            if (map != null) {
                return map.remove(sid);
            }
        }
        return null;
    }

    public static void clearTags(Context context) {
        View decorView = getDecorView(context);
        if (decorView != null) {
            decorView.setTag(R.id.es_js_view_tag1, null);
            decorView.setTag(R.id.es_js_view_tag2, null);
            decorView.setTag(R.id.es_js_view_tag3, null);
        }
    }

    public static void addContentView(Context context, View view, ViewGroup.LayoutParams params) {
        if (PluginUtils.isInstanceOfActivity(context)) {
            ReflectUtils.reflect(context).method("addContentView", view, params);
        }
    }
}
