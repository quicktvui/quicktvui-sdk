package com.quicktvui.sdk.core.utils;

import com.sunrain.toolkit.utils.ReflectUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class ReflectExecutor {

    public static final String ENTRY_POINT = "entry";
    public static final String FROM = "from";
    public static final String METHOD = "method";
    public static final String METHOD_CONSTRUCTOR = "constructor";
    public static final String PARAMS = "params";

    private JSONObject mJsonObject;

    public ReflectExecutor(JSONObject jsonObject) {
        mJsonObject = jsonObject;
    }

    public Object execute() {
        if (mJsonObject == null) return null;
        if (!mJsonObject.has(ENTRY_POINT)) return null;
        ReflectUtils doEntry = doEntry(mJsonObject.optJSONObject(ENTRY_POINT));
        mJsonObject = null;
        return doEntry.get();
    }

    private ReflectUtils doEntry(JSONObject jo) {
        String from = jo.optString(FROM, null);
        if (from == null || from.isEmpty()) return null;
        String method = jo.optString(METHOD, null);
        if (method == null || method.isEmpty()) return null;
        ReflectUtils ref;
        if (mJsonObject.has(from)) {
            Object obj = doEntry(mJsonObject.optJSONObject(from)).get();
            ref = ReflectUtils.reflect(obj);
        } else {
            ref = ReflectUtils.reflect(from);
        }

        JSONArray params = jo.optJSONArray(PARAMS);

        if (params == null || params.length() <= 0) {
            if(Objects.equals(METHOD_CONSTRUCTOR, method)){
                ref = ref.newInstance();
            }else{
                ref = ref.method(method);
            }
        } else {
            Object[] objects = new Object[params.length()];
            for (int i = 0; i < params.length(); i++) {
                Object param = params.opt(i);
                if (param instanceof String) {
                    if (mJsonObject.has((String) param)) {
                        objects[i] = doEntry(mJsonObject.optJSONObject((String) param)).get();
                        continue;
                    }
                }
                if("NULL".equals(param)) {
                    objects[i] = null;
                    continue;
                }
                objects[i] = param;
            }
            if(Objects.equals(METHOD_CONSTRUCTOR, method)){
                ref = ref.newInstance(objects);
            }else{
                ref = ref.method(method, objects);
            }
        }
        return ref;
    }
}
