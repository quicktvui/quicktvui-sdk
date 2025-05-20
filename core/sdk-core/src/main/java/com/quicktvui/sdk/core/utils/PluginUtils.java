package com.quicktvui.sdk.core.utils;

import android.app.Activity;
import android.content.Context;


/**
 * <br>
 *
 * <br>
 */
public class PluginUtils {

    public static boolean IS_PLUGIN_MODE = false;

    public static void assertIsInstanceOfActivity(Context context) {
        assert isInstanceOfActivity(context);
    }

    public static void assertIsInstanceOfFragmentActivity(Context context) {
        assert isInstanceOfFragmentActivity(context);
    }

    public static boolean isInstanceOfActivity(Context context) {
        if (!IS_PLUGIN_MODE) return context instanceof Activity;
        return isInstanceOfClass(context, "android.app.Activity");
    }

    public static boolean isInstanceOfFragmentActivity(Context context) {
        return isInstanceOfClass(context, "android.support.v4.app.FragmentActivity", "androidx.fragment.app.FragmentActivity");
    }

    public static boolean isLifecycleOwnerSupport(Context context) {
        return isInstanceOfClass(context, "android.arch.lifecycle.LifecycleOwner");
    }

    public static boolean isLifecycleOwnerAndroidX(Context context) {
        return isInstanceOfClass(context, "androidx.lifecycle.LifecycleOwner");
    }

    public static Class<?> getLifecycleObserverClass(Context context) {
        try {
            if (isLifecycleOwnerAndroidX(context)) {
                return context.getClassLoader().loadClass("androidx.lifecycle.LifecycleObserver");
            } else if (isLifecycleOwnerSupport(context)) {
                return context.getClassLoader().loadClass("android.arch.lifecycle.LifecycleObserver");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isInstanceOfClass(Object object, String... classNames) {
        Class<?> targetClass = object.getClass();
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAssignableFrom(targetClass)) return true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isClientHandlePluginUpgrade() {
        return false;
//        return IS_PLUGIN_MODE && ESAbilityProvider.get().getAbility(IEsCheckPluginUpgrade.NAME) != null;
    }
}
