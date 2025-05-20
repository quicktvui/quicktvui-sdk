package com.quicktvui.support.core.utils;

import android.os.Bundle;
import android.text.TextUtils;

import android.support.annotation.Nullable;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.quicktvui.sdk.base.args.EsMap;

public class BundleMapper {

    /**
     * 将JSONObject或者json字符串转为EsMap
     **/
    public static EsMap json2EsMap(Object data) {
        if (data != null) {
            try {
                JSONObject jsonObject = tryMapperObject2JsonObject(data);
                if (jsonObject != null) {
                    EsMap esMap = new EsMap();
                    esMap.pushJSONObject(jsonObject);
                    return esMap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将json转为bundle
     **/
    public static Bundle tryMapperJson2Bundle(String json) throws Exception {
        if (TextUtils.isEmpty(json)) json = "{}";
        return tryMapperJson2Bundle(new JSONObject(json));
    }

    /**
     * 将json转为bundle
     **/
    public static Bundle tryMapperJson2Bundle(JSONObject json) throws Exception {
        Bundle bundle = new Bundle();
        try {
            Iterator<String> iterator = json.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = json.get(key);
                switch (value.getClass().getSimpleName()) {
                    case "String":
                        bundle.putString(key, (String) value);
                        break;
                    case "Integer":
                        bundle.putInt(key, (Integer) value);
                        break;
                    case "Long":
                        bundle.putLong(key, (Long) value);
                        break;
                    case "Boolean":
                        bundle.putBoolean(key, (Boolean) value);
                        break;
                    case "JSONObject":
//                        bundle.putBundle(key, tryMapperJson2Bundle((JSONObject) value));
                        bundle.putString(key, value.toString());
                        break;
                    case "JSONArray":
                        fillArray2Bundle(bundle, key, (JSONArray) value);
//                        bundle.putString(key, value.toString());
                        break;
                    case "Float":
                        bundle.putFloat(key, (Float) value);
                        break;
                    case "Double":
                        bundle.putDouble(key, (Double) value);
                        break;
                    default:
                        bundle.putString(key, value.getClass().getSimpleName());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private static void fillArray2Bundle(Bundle bundle, String key, JSONArray array) throws Exception {
        int len = array.length();
        if (len <= 0) return;
        Object o = array.get(0);
        switch (o.getClass().getSimpleName()) {
            case "JSONArray":
            case "JSONObject":
            case "String":
                bundle.putStringArrayList(key, mapper2StringList(array));
                break;
            case "Integer":
                bundle.putIntegerArrayList(key, mapper2List(array));
                break;
            case "Boolean":
                bundle.putBooleanArray(key, (boolean[]) mapper2Array(Boolean.class, array));
                break;
            case "Float":
                bundle.putFloatArray(key, (float[]) mapper2Array(Float.class, array));
                break;
            case "Double":
                bundle.putDoubleArray(key, (double[]) mapper2Array(Float.class, array));
                break;
        }
    }

    private static <T> ArrayList<T> mapper2List(JSONArray array) throws Exception {
        ArrayList<T> aa = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            aa.add((T) array.get(i));
        }
        return aa;
    }

    private static ArrayList<String> mapper2StringList(JSONArray array) throws Exception {
        ArrayList<String> aa = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            aa.add(array.get(i).toString());
        }
        return aa;
    }

    private static Object mapper2Array(Class c, JSONArray array) throws Exception {
        Object aa = Array.newInstance(c, array.length());
        for (int i = 0; i < array.length(); i++) {
            Array.set(aa, i, array.get(i));
        }
        return aa;
    }

    /**
     * 将bundle转为json
     **/
    public static JSONObject bundle2JsonObject(Bundle bundle) {
        JSONObject jo = new JSONObject();
        try {
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object obj = bundle.get(key);
                    if (obj instanceof Bundle) {
                        jo.put(key, bundle2JsonObject((Bundle) obj));
                    } else {
                        jo.put(key, tryWrap(obj));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jo;
    }

    private static Object tryWrap(@Nullable Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return new JSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                JSONArray ja = new JSONArray();
                final int length = Array.getLength(o);
                for (int i = 0; i < length; ++i) {
                    ja.put(tryWrap(Array.get(o, i)));
                }
                return ja;
            }
            if (o instanceof Map) {
                return new JSONObject((Map) o);
            }
            if (o instanceof Boolean || o instanceof Byte || o instanceof Character || o instanceof Double || o instanceof Float || o instanceof Integer || o instanceof Long || o instanceof Short || o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static JSONObject tryMapperObject2JsonObject(Object obj) {
        if (obj instanceof String) {
            try {
                return new JSONObject((String) obj);
            } catch (Exception ignore) {
            }
        } else if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }

        return null;
    }
}