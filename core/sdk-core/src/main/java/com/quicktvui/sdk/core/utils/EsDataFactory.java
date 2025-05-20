package com.quicktvui.sdk.core.utils;

import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.EsStartParam;
import com.quicktvui.sdk.core.protocol.EsStartTarget;

/**
 * EsData工厂方法
 * <p>
 * Create by weipeng on 2022/06/23 16:17
 */
public class EsDataFactory {

    // ------------------------ EsData自动参数映射 ------------------------ //
    private static SoftReference<Map<String, EsStartTarget>> CACHE_PARAMS_MAPPER_FIELDS;

    private static EsStartTarget getTarget(String key) {
        Map<String, EsStartTarget> cache;
        if (CACHE_PARAMS_MAPPER_FIELDS == null || (cache = CACHE_PARAMS_MAPPER_FIELDS.get()) == null) {
            cache = prepareMethodData();
            CACHE_PARAMS_MAPPER_FIELDS = new SoftReference<>(cache);
        }
        return cache.get(key);
    }

    private static Map<String, EsStartTarget> prepareMethodData() {
        Method[] methods = EsData.class.getMethods();
        Map<String, EsStartTarget> cache = new HashMap<>();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            if (method.getReturnType() != EsData.class) continue;
            Annotation[] annotations = method.getAnnotations();
            if (annotations == null || annotations.length == 0) continue;
            for (Annotation annotation : annotations) {
                if (annotation instanceof EsStartParam) {
                    cache.put(((EsStartParam) annotation).value(), new EsStartTarget(
                            method, method.getParameterTypes()[0]));
                }
            }
        }
        return cache;
    }

    public static EsData create(JSONObject jo) {
        EsData data = null;
        try {
            if (jo != null) {
                data = new EsData();
                Iterator<String> keys = jo.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    EsStartTarget target = getTarget(key);
                    if (target != null) {
                        target.method.invoke(data, new Object[]{target.parseData(jo.opt(key))});
                    }
                }
            }
        } catch (Exception e) {
            L.logW("crate esdata", e);
            return null;
        }
        return data;
    }
}
