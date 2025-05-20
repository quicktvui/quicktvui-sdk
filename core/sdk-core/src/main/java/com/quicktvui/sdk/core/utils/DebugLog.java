package com.quicktvui.sdk.core.utils;

import com.sunrain.toolkit.utils.log.L;

/**
 *
 */
public class DebugLog {

    private static final String TAG = "DEBUG_FILTER ";

    public static void d(Object obj, String message) {
        L.logDF(TAG + getObjectInfo(obj) + message);
    }

    public static void w(Object obj, String message) {
        L.logWF(TAG + getObjectInfo(obj) + message);
    }

    public static void e(Object obj, String message) {
        L.logEF(TAG + getObjectInfo(obj) + message);
    }

    private static String getObjectInfo(Object obj){
        if(obj == null) return "null 0";
        return obj.getClass().getSimpleName() + " " + obj.hashCode() + " ";
    }
}
