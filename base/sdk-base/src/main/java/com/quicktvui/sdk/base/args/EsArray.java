package com.quicktvui.sdk.base.args;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Create by weipeng on 2022/03/03 15:34
 */
public class EsArray implements Serializable {
    
    private final ArrayList<Object> mDatas;

    public EsArray() {
        mDatas = new ArrayList<>();
    }

    public int size() {
        return mDatas.size();
    }

    public Object get(int index) {
        return mDatas.get(index);
    }

    public void pushObject(Object obj) {
        mDatas.add(obj);
    }

    public void setObject(int index, Object obj) {
        mDatas.set(index, obj);
    }

    public int getInt(int index) {
        if (mDatas.size() > index) {
            Object value = mDatas.get(index);
            return value instanceof Number ? ((Number) value).intValue() : 0;
        }
        return 0;
    }

    public long getLong(int index) {
        if (mDatas.size() > index) {
            Object value = mDatas.get(index);
            return value instanceof Number ? ((Number) value).longValue() : 0;
        }
        return 0;
    }

    public double getDouble(int index) {
        if (mDatas.size() > index) {
            Object value = mDatas.get(index);
            return value instanceof Number ? ((Number) value).doubleValue() : 0;
        }
        return 0;
    }

    public String getString(int index) {
        if (mDatas.size() > index) {
            return String.valueOf(mDatas.get(index));
        }
        return null;
    }

    public boolean getBoolean(int index) {
        if (mDatas.size() > index) {
            Object value = mDatas.get(index);
            return (value instanceof Boolean) && (boolean) value;
        }
        return false;
    }

    public EsArray getArray(int index) {
        if (mDatas.size() > index) {
            Object value = mDatas.get(index);
            return value instanceof EsArray ? (EsArray) value : null;
        }
        return null;
    }

    public EsMap getMap(int index) {
        if (mDatas.size() > index) {
            Object value = mDatas.get(index);
            return value instanceof EsMap ? (EsMap) value : null;
        }
        return null;
    }

    public Object getObject(int index) {
        if (mDatas.size() > index) {
            return mDatas.get(index);
        }
        return null;
    }

    public void pushInt(int value) {
        mDatas.add(value);
    }

    public void pushLong(long value) {
        mDatas.add(value);
    }

    public void pushDouble(double value) {
        mDatas.add(value);
    }

    public void pushBoolean(boolean value) {
        mDatas.add(value);
    }

    public void pushString(String value) {
        mDatas.add(value);
    }

    public void pushArray(EsArray array) {
        mDatas.add(array);
    }

    public void pushMap(EsMap map) {
        mDatas.add(map);
    }

    public void pushNull() {
        mDatas.add(null);
    }

    @SuppressWarnings("unused")
    public void clear() {
        mDatas.clear();
    }

    public EsArray copy() {
        EsArray newArray = new EsArray();
        Iterator<Object> it = mDatas.iterator();
        Object value;
        Object newValue;
        while (it.hasNext()) {
            value = it.next();
            if (value instanceof EsMap) {
                newValue = ((EsMap) value).copy();
            } else if (value instanceof EsArray) {
                newValue = ((EsArray) value).copy();
            } else {
                newValue = value;
            }
            newArray.pushObject(newValue);
        }

        return newArray;
    }

    @Override
    public String toString() {
        return mDatas.toString();
    }

    public void pushJSONArray(JSONArray jArray) {
        if (jArray == null || jArray.length() <= 0) {
            return;
        }

        try {
            for (int i = 0; i < jArray.length(); i++) {
                Object obj = jArray.opt(i);
                if (obj == null) {
                    pushNull();
                } else if (obj instanceof JSONObject) {
                    EsMap EsMap = new EsMap();
                    EsMap.pushJSONObject((JSONObject) obj);
                    pushMap(EsMap);
                } else if (obj instanceof JSONArray) {
                    EsArray EsArray = new EsArray();
                    EsArray.pushJSONArray((JSONArray) obj);
                    pushArray(EsArray);
                } else {
                    pushObject(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONArray toJSONArray() {
        JSONArray jArray = new JSONArray();
        if (size() <= 0) {
            return jArray;
        }

        try {
            Iterator<Object> it = mDatas.iterator();
            Object value;
            while (it.hasNext()) {
                value = it.next();
                if (value instanceof EsMap) {
                    JSONObject jObjectMap = ((EsMap) value).toJSONObject();
                    jArray.put(jObjectMap);
                } else if (value instanceof EsArray) {
                    JSONArray jObjectArray = ((EsArray) value).toJSONArray();
                    jArray.put(jObjectArray);
                } else {
                    jArray.put(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jArray;
    }
}
