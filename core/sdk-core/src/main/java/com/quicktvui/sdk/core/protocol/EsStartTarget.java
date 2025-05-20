package com.quicktvui.sdk.core.protocol;

import java.lang.reflect.Method;

/**
 * 参数映射数据保存类
 * <p>
 * Create by weipeng on 2022/06/23 16:07
 */
public final class EsStartTarget {

    public Method method;
    public Class<?> c;

    public EsStartTarget(Method method, Class<?> c) {
        this.method = method;
        this.c = c;
//        if (L.DEBUG) L.logD(method.getName());
    }

    public <T> T parseData(Object data) {
        if (data.getClass() == c) return (T) data;
        if (c == String.class) return (T) String.valueOf(data);
        if (c == int.class) return (T) Integer.valueOf(String.valueOf(data));
        if (c == boolean.class)
            return (T) ((Object) Boolean.parseBoolean(String.valueOf(data)));
        throw new RuntimeException("未匹配的类型: " + c.getSimpleName());
    }

    @Override
    public String toString() {
        return "EsStartTarget{" +
                "method=" + method +
                ", c=" + c +
                '}';
    }
}
