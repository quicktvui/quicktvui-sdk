package com.quicktvui.sdk.base.args;

/**
 * Create by weipeng on 2022/03/03 15:32
 */

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EsMap implements Serializable {

    private final HashMap<String, Object> mDatas;

    @Override
    public String toString() {
        return mDatas.toString();
    }

    public EsMap() {
        mDatas = new HashMap<>(5);
    }

    public boolean containsKey(String key) {
        return mDatas.containsKey(key);
    }

    public int size() {
        return mDatas.size();
    }

    public Set<String> keySet() {
        return mDatas.keySet();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return mDatas.entrySet();
    }

    public Object get(String key) {
        return mDatas.get(key);
    }

    public String getString(String key) {
        Object value = mDatas.get(key);
        return value == null ? null : String.valueOf(value);
    }

    public void remove(String key) {
        mDatas.remove(key);
    }

    public double getDouble(String key) {
        Object value = mDatas.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : 0;
    }

    public float getFloat(String key) {
        Object value = mDatas.get(key);
        return value instanceof Number ? ((Number) value).floatValue() : 0;
    }

    public int getInt(String key) {
        Object value = mDatas.get(key);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    public boolean getBoolean(String key) {
        Object value = mDatas.get(key);
        return value != null && (boolean) value;
    }

    public long getLong(String key) {
        Object value = mDatas.get(key);
        return value instanceof Number ? ((Number) value).longValue() : 0;
    }

    public EsMap getMap(String key) {
        Object value = mDatas.get(key);
        if (value instanceof EsMap) {
            return (EsMap) value;
        }
        return null;
    }

    public EsArray getArray(String key) {
        Object value = mDatas.get(key);
        if (value instanceof EsArray) {
            return (EsArray) value;
        }
        return null;
    }

    public boolean isNull(String key) {
        return mDatas.get(key) == null;
    }

    public void pushNull(String key) {
        mDatas.put(key, null);
    }

    public void pushInt(String key, int value) {
        mDatas.put(key, value);
    }

    public void pushString(String key, String value) {
        mDatas.put(key, value);
    }

    public void pushBoolean(String key, boolean value) {
        mDatas.put(key, value);
    }

    public void pushDouble(String key, double value) {
        mDatas.put(key, value);
    }

    public void pushLong(String key, long value) {
        mDatas.put(key, value);
    }

    public void pushArray(String key, EsArray array) {
        mDatas.put(key, array);
    }

    public void pushMap(String key, EsMap map) {
        if (map == null) map = new EsMap();
        mDatas.put(key, map);
    }

    public void pushAll(EsMap map) {
        if (null != map) {
            mDatas.putAll(map.mDatas);
        }
    }

    public void pushObject(String key, Object obj) {
        if (obj == null) {
            pushNull(key);
        } else if (obj instanceof String) {
            pushString(key, (String) obj);
        } else if (obj instanceof EsMap) {
            pushMap(key, (EsMap) obj);
        } else if (obj instanceof EsArray) {
            pushArray(key, (EsArray) obj);
        } else if (obj instanceof Integer) {
            pushInt(key, (Integer) obj);
        } else if (obj instanceof Boolean) {
            pushBoolean(key, (Boolean) obj);
        } else if (obj instanceof Double) {
            pushDouble(key, (Double) obj);
        } else if (obj instanceof Float) {
            pushDouble(key, ((Number) obj).doubleValue());
        } else if (obj instanceof Long) {
            pushLong(key, (Long) obj);
        } else if (obj instanceof Byte) {
            int iObj = ((Byte) obj).intValue();
            pushInt(key, iObj);
        } else {
            Class<?> clazz = obj.getClass();
            if (clazz.isAssignableFrom(int.class)) {
                //noinspection ConstantConditions
                pushInt(key, (Integer) obj);
            } else if (clazz.isAssignableFrom(boolean.class)) {
                //noinspection ConstantConditions
                pushBoolean(key, (Boolean) obj);
            } else if (clazz.isAssignableFrom(double.class)) {
                //noinspection ConstantConditions
                pushDouble(key, (Double) obj);
            } else if (clazz.isAssignableFrom(float.class)) {
                pushDouble(key, ((Number) obj).doubleValue());
            } else if (clazz.isAssignableFrom(long.class)) {
                //noinspection ConstantConditions
                pushLong(key, (Long) obj);
            } else {
                throw new RuntimeException("push unsupported object into EsMap [" + key + "," + obj + "] " + obj.getClass());
            }
        }
    }

    public void clear() {
        mDatas.clear();
    }

    public EsMap copy() {
        EsMap newMap = new EsMap();
        Iterator<Map.Entry<String, Object>> it = mDatas.entrySet().iterator();
        Map.Entry<String, Object> entry;
        Object value;
        Object newValue;
        while (it.hasNext()) {
            entry = it.next();
            value = entry.getValue();

            if (value instanceof EsMap) {
                newValue = ((EsMap) value).copy();
            } else if (value instanceof EsArray) {
                newValue = ((EsArray) value).copy();
            } else {
                newValue = value;
            }
            newMap.pushObject(entry.getKey(), newValue);
        }

        return newMap;
    }

    public void pushJSONObject(JSONObject jObject) {
        if (jObject == null) {
            return;
        }

        try {
            Iterator<?> it = jObject.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object obj = jObject.opt(key);
                if (jObject.isNull(key)) {
                    pushNull(key);
                } else if (obj instanceof JSONObject) {
                    EsMap hippyMap = new EsMap();
                    hippyMap.pushJSONObject((JSONObject) obj);
                    pushMap(key, hippyMap);
                } else if (obj instanceof JSONArray) {
                    EsArray EsArray = new EsArray();
                    EsArray.pushJSONArray((JSONArray) obj);
                    pushArray(key, EsArray);
                } else {
                    pushObject(key, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushBundle(Bundle bundle) {
        if (bundle == null) return;
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            Object obj = bundle.get(key);
            if (obj instanceof Bundle) {
                EsMap map = new EsMap();
                map.pushBundle((Bundle) obj);
                pushMap(key, map);
            } else {
                pushObject(key, obj);
            }
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jObject = new JSONObject();
        if (size() <= 0) {
            return jObject;
        }

        Iterator<?> var2 = entrySet().iterator();
        try {
            while (var2.hasNext()) {
                @SuppressWarnings({"unchecked",
                        "rawtypes"}) Map.Entry<String, Object> entry = (Map.Entry) var2.next();
                String key = entry.getKey();
                if (entry.getValue() instanceof EsMap) {
                    JSONObject jObjectMap = ((EsMap) entry.getValue()).toJSONObject();
                    jObject.put(key, jObjectMap);
                } else if (entry.getValue() instanceof EsArray) {
                    JSONArray jObjectArray = ((EsArray) entry.getValue()).toJSONArray();
                    jObject.put(key, jObjectArray);
                } else {
                    jObject.put(key, entry.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObject;
    }
}