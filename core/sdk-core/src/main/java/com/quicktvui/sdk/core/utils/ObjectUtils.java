package com.quicktvui.sdk.core.utils;

import com.sunrain.toolkit.utils.log.L;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <br>
 *
 * <br>
 */
public class ObjectUtils {

    private static final String TAG = "[-ObjectUtils-]";

    public static void print(Object obj) {
        if (obj == null) {
            L.d(TAG, "null");
            return;
        }
        L.d(TAG, "---------------------------------");
        Class<?> clazz = obj.getClass();
        L.d(TAG, clazz.getName());
        Field[] fields = clazz.getFields();
        L.d(TAG, "属性:");
        for (Field field : fields) {
            if (field.isAccessible()) {
                try {
                    L.d(TAG, "  " + field.getName() + "  " + field.get(obj));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Method[] methods = clazz.getMethods();
        L.d(TAG, "方法:");
        for (Method method : methods) {
            if (method.isAccessible() && method.getParameterTypes().length == 0) {
                try {
                    L.d(TAG, "  " + method.getName() + "  " + method.invoke(obj));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
