package com.quicktvui.support.data.shared;


import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.support.core.module.sp.AndroidSharedPreferencesManager;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 */
public class ESSharedDataManager {

    private static ESSharedDataManager instance;

    private Context context;

    private ESSharedDataManager() {
    }

    public static ESSharedDataManager getInstance() {
        synchronized (ESSharedDataManager.class) {
            if (instance == null) {
                instance = new ESSharedDataManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public boolean getBoolean(String selfPackageName, String sharedPackageName, String key, boolean defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            ESSharedData sharedData = getSharedData(sharedPackageName, key);
            if (sharedData != null && sharedData.getData() instanceof Boolean) {
                if (isSelfData(selfPackageName, sharedPackageName) //
                        || (sharedData.getMode() >= ESSharedMode.MODE_WORLD_READABLE)) {
                    return (Boolean) sharedData.getData();
                } else {
                    if (L.DEBUG) {
                        L.logD("#------getBoolean-------没有读权限-------------->>>");
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public int getInt(String selfPackageName, String sharedPackageName, String key, int defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            ESSharedData sharedData = getSharedData(sharedPackageName, key);
            if (sharedData != null && sharedData.getData() instanceof Integer) {
                if (isSelfData(selfPackageName, sharedPackageName) //
                        || (sharedData.getMode() >= ESSharedMode.MODE_WORLD_READABLE)) {
                    return (Integer) sharedData.getData();
                } else {
                    if (L.DEBUG) {
                        L.logD("#------getInt-------没有读权限-------------->>>");
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public long getLong(String selfPackageName, String sharedPackageName, String key, long defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            ESSharedData sharedData = getSharedData(sharedPackageName, key);
            if (sharedData != null && sharedData.getData() instanceof String) {
                if (isSelfData(selfPackageName, sharedPackageName) //
                        || (sharedData.getMode() >= ESSharedMode.MODE_WORLD_READABLE)) {
                    return Long.parseLong((String) sharedData.getData());
                } else {
                    if (L.DEBUG) {
                        L.logD("#------getLong-------没有读权限-------------->>>");
                    }
                }
            } else {
                if (L.DEBUG) {
                    L.logD("#------getLong-------类型错误-------------->>>");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public String getString(String selfPackageName, String sharedPackageName, String key, String defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            ESSharedData sharedData = getSharedData(sharedPackageName, key);
            if (sharedData != null && sharedData.getData() instanceof String) {
                if (isSelfData(selfPackageName, sharedPackageName) //
                        || (sharedData.getMode() >= ESSharedMode.MODE_WORLD_READABLE)) {
                    return (String) sharedData.getData();
                } else {
                    if (L.DEBUG) {
                        L.logD("#------getString-------没有读权限-------------->>>");
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public EsArray getArray(String selfPackageName, String sharedPackageName, String key, EsArray defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }

        try {
            ESSharedData sharedData = getSharedData(sharedPackageName, key);
            if (sharedData != null && sharedData.getData() instanceof EsArray) {
                if (isSelfData(selfPackageName, sharedPackageName) //
                        || (sharedData.getMode() >= ESSharedMode.MODE_WORLD_READABLE)) {
                    return (EsArray) sharedData.getData();
                } else {
                    if (L.DEBUG) {
                        L.logD("#------getArray-------没有读权限-------------->>>");
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public EsMap getMap(String selfPackageName, String sharedPackageName, String key, EsMap defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            ESSharedData sharedData = getSharedData(sharedPackageName, key);
            if (sharedData != null && sharedData.getData() instanceof EsMap) {
                if (isSelfData(selfPackageName, sharedPackageName) //
                        || (sharedData.getMode() >= ESSharedMode.MODE_WORLD_READABLE)) {
                    return (EsMap) sharedData.getData();
                } else {
                    if (L.DEBUG) {
                        L.logD("#------getMap-------没有读权限-------------->>>");
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public boolean putBoolean(String selfPackageName, String sharedPackageName, String key, boolean value, int mode) {
        return putSharedData(selfPackageName, sharedPackageName, key, value, ESSharedDataType.ES_SHARED_DATA_TYPE_BOOLEAN, mode);
    }

    public boolean putInt(String selfPackageName, String sharedPackageName, String key, int value, int mode) {
        return putSharedData(selfPackageName, sharedPackageName, key, value, ESSharedDataType.ES_SHARED_DATA_TYPE_INT, mode);
    }

    public boolean putLong(String selfPackageName, String sharedPackageName, String key, long value, int mode) {
        return putSharedData(selfPackageName, sharedPackageName, key, value + "", ESSharedDataType.ES_SHARED_DATA_TYPE_LONG, mode);
    }

    public boolean putString(String selfPackageName, String sharedPackageName, String key, String value, int mode) {
        return putSharedData(selfPackageName, sharedPackageName, key, value, ESSharedDataType.ES_SHARED_DATA_TYPE_STRING, mode);
    }

    public boolean putArray(String selfPackageName, String sharedPackageName, String key, EsArray value, int mode) {
        return putSharedData(selfPackageName, sharedPackageName, key, value, ESSharedDataType.ES_SHARED_DATA_TYPE_ARRAY, mode);
    }

    public boolean putMap(String selfPackageName, String sharedPackageName, String key, EsMap value, int mode) {
        return putSharedData(selfPackageName, sharedPackageName, key, value, ESSharedDataType.ES_SHARED_DATA_TYPE_MAP, mode);
    }

    public boolean isSelfData(String selfPackageName, String sharedPackageName) {
        return !TextUtils.isEmpty(selfPackageName) //
                && !TextUtils.isEmpty(sharedPackageName) //
                && selfPackageName.equals(sharedPackageName);
    }

    private ESSharedData getSharedData(String sharedPackageName, String key) {
        if (L.DEBUG) {
            L.logD("#------getSharedData-----start---->>>sharedPackageName:" + sharedPackageName + "   key:" + key);
        }
        if (TextUtils.isEmpty(sharedPackageName)) {
            return null;
        }
        AndroidSharedPreferencesManager sharedPreferencesManager = new AndroidSharedPreferencesManager();
        sharedPreferencesManager.init(context);
        sharedPreferencesManager.initSharedPreferences(sharedPackageName + "_shared_data");
        String sharedDataString = sharedPreferencesManager.getString(key, null);
        if (L.DEBUG) {
            L.logD("#-----------getSharedData-----string------>>>" + sharedDataString);
        }
        ESSharedData sharedData = stringToSharedData(sharedDataString);
        if (L.DEBUG) {
            L.logD("#-----------getSharedData-----end------>>>" + sharedData);
        }
        return sharedData;
    }

    public boolean putSharedData(String selfPackageName, String sharedPackageName, //
                                 String key, Object value, int mmapSharedDataType, int mode) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return false;
        }

        AndroidSharedPreferencesManager sharedPreferencesManager = new AndroidSharedPreferencesManager();
        sharedPreferencesManager.init(context);
        sharedPreferencesManager.initSharedPreferences(sharedPackageName + "_shared_data");
        String sharedDataString = sharedPreferencesManager.getString(key, null);

        if (L.DEBUG) {
            L.logD("#-----putSharedData-------获取存储的值--value--------->>>" + sharedDataString);
        }

        if (TextUtils.isEmpty(sharedDataString)) {
            if (isSelfData(selfPackageName, sharedPackageName)) {
                ESSharedData sharedData = new ESSharedData();
                sharedData.setData(value);
                sharedData.setType(mmapSharedDataType);
                sharedData.setMode(mode);
                saveSharedData(sharedPreferencesManager, key, sharedData);
                if (L.DEBUG) {
                    L.logD("#-----putSharedData-------自己数据，保存新值----------->>>");
                }
                return true;
            } else {
                if (L.DEBUG) {
                    L.logD("#-----putSharedData-------非自己的数据，不可写---------->>>");
                }
            }
            return false;
        }
        ESSharedData sharedData = stringToSharedData(sharedDataString);
        if (sharedData == null) {
            if (L.DEBUG) {
                L.logD("#-----putSharedData-------解析已存值错误----------->>>");
            }
            return false;
        }

        if (isSelfData(selfPackageName, sharedPackageName)) {
            if (L.DEBUG) {
                L.logD("#-----putSharedData-------自己数据，更新保存新值---------->>>");
            }
            sharedData.setData(value);
            sharedData.setMode(mode);//自己改权限
            saveSharedData(sharedPreferencesManager, key, sharedData);
            return true;
        }
        //
        else {
            if (sharedData.getMode() >= ESSharedMode.MODE_WORLD_WRITEABLE) {
                if (L.DEBUG) {
                    L.logD("#-----putSharedData-----其他人数据--有权限写----------->>>");
                }
                sharedData.setData(value);
                saveSharedData(sharedPreferencesManager, key, sharedData);
                return true;
            } else {
                if (L.DEBUG) {
                    L.logD("#----putSharedData-----其他人数据--没有权限写----------->>>");
                }
            }
        }
        return false;
    }

    private void saveSharedData(AndroidSharedPreferencesManager preferencesManager, //
                                String key, ESSharedData sharedData) {
        try {
            String value = sharedDataToString(sharedData);
            if (!TextUtils.isEmpty(value)) {
                preferencesManager.putString(key, value);
                if (L.DEBUG) {
                    L.logD("#---------saveSharedData--success--->>>");
                }
            } else {
                if (L.DEBUG) {
                    L.logD("#---------saveSharedData--error---value is null---->>>");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (L.DEBUG) {
                L.logD("#---------saveSharedData--error--->>>");
            }
        }
    }

    private ESSharedData stringToSharedData(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            ESSharedData sharedData = new ESSharedData();
            sharedData.setMode(jsonObject.optInt("mode"));
            sharedData.setType(jsonObject.optInt("type"));

            Object dataObj = jsonObject.get("data");


            if (dataObj instanceof JSONObject) {
                if (L.DEBUG) {
                    L.logD("#---------stringToSharedData--JSONObject--->>>");
                }
                EsMap esMap = new EsMap();
                esMap.pushJSONObject((JSONObject) dataObj);
                sharedData.setData(esMap);
            }
            //
            else if (dataObj instanceof JSONArray) {
                if (L.DEBUG) {
                    L.logD("#---------stringToSharedData--JSONArray--->>>" + dataObj);
                }
                EsArray esArray = new EsArray();
                esArray.pushJSONArray((JSONArray) dataObj);
                sharedData.setData(esArray);
            }
            //
            else {
                if (L.DEBUG) {
                    L.logD("#---------stringToSharedData--other--->>>");
                }
                sharedData.setData(dataObj);
            }
            return sharedData;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private String sharedDataToString(ESSharedData sharedData) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mode", sharedData.getMode());
            jsonObject.put("type", sharedData.getType());
            Object data = sharedData.getData();
            if (data instanceof EsArray) {
                jsonObject.put("data", ((EsArray) data).toJSONArray());
            } else if (data instanceof EsMap) {
                jsonObject.put("data", ((EsMap) data).toJSONObject());
            } else {
                jsonObject.put("data", data);
            }
            return jsonObject.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void release() {
        if (L.DEBUG) {
            L.logD("#---------release----->>>");
        }
    }
}
