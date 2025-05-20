package com.quicktvui.support.core.module.mmap;


import static com.quicktvui.support.core.module.mmap.MMapSharedDataType.DATA_TYPE_ARRAY;
import static com.quicktvui.support.core.module.mmap.MMapSharedDataType.DATA_TYPE_BOOLEAN;
import static com.quicktvui.support.core.module.mmap.MMapSharedDataType.DATA_TYPE_INT;
import static com.quicktvui.support.core.module.mmap.MMapSharedDataType.DATA_TYPE_LONG;
import static com.quicktvui.support.core.module.mmap.MMapSharedDataType.DATA_TYPE_MAP;
import static com.quicktvui.support.core.module.mmap.MMapSharedDataType.DATA_TYPE_STRING;

import android.content.Context;
import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;

/**
 *
 */
public class MMapManager {

    private static final String TAG = "MMapManager";

    private static MMapManager instance;

    private Context context;

    //
    private Map<String, Map<String, MMapSharedData>> mmapSharedDataMap
            = Collections.synchronizedMap(new HashMap<>());

    private MMapManager() {
    }

    public static MMapManager getInstance() {
        synchronized (MMapManager.class) {
            if (instance == null) {
                instance = new MMapManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        if (L.DEBUG) {
            L.logD("#-------------init-------------->>>");
        }
    }

    //------------------------------BOOLEAN--------------------------------
    public boolean getBoolean(String selfPackageName, String sharedPackageName, String key, boolean defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            if (mmapSharedDataMap != null && mmapSharedDataMap.containsKey(sharedPackageName)) {
                Map<String, MMapSharedData> sharedDataMap = mmapSharedDataMap.get(sharedPackageName);
                if (sharedDataMap != null
                        && sharedDataMap.containsKey(key)
                        && sharedDataMap.get(key) != null
                        && sharedDataMap.get(key).getData() instanceof Boolean) {
                    if (isSelfData(selfPackageName, sharedPackageName)
                            || (sharedDataMap.get(key).getMode() >= MMapMode.MODE_WORLD_READABLE)) {
                        return (Boolean) sharedDataMap.get(key).getData();
                    } else {
                        if (L.DEBUG) {
                            L.logD("#------getBoolean-------没有读权限-------------->>>");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public boolean putBoolean(String selfPackageName, String sharedPackageName, String key, boolean value, int mode) {
        return putMMapSharedData(selfPackageName, sharedPackageName, key, value, DATA_TYPE_BOOLEAN, mode);
    }

    //---------------------------INT-----------------------------------
    public int getInt(String selfPackageName, String sharedPackageName, String key, int defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            if (mmapSharedDataMap != null && mmapSharedDataMap.containsKey(sharedPackageName)) {
                Map<String, MMapSharedData> sharedDataMap = mmapSharedDataMap.get(sharedPackageName);
                if (sharedDataMap != null
                        && sharedDataMap.containsKey(key)
                        && sharedDataMap.get(key) != null
                        && sharedDataMap.get(key).getData() instanceof Integer) {
                    if (isSelfData(selfPackageName, sharedPackageName)
                            || (sharedDataMap.get(key).getMode() >= MMapMode.MODE_WORLD_READABLE)) {
                        return (Integer) sharedDataMap.get(key).getData();
                    } else {
                        if (L.DEBUG) {
                            L.logD("#-----getInt-------没有读权限--------------->>>");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public boolean putInt(String selfPackageName, String sharedPackageName, String key, int value, int mode) {
        return putMMapSharedData(selfPackageName, sharedPackageName, key, value, DATA_TYPE_INT, mode);
    }

    //-------------------------LONG-------------------------------------
    public long getLong(String selfPackageName, String sharedPackageName, String key, long defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            if (mmapSharedDataMap != null && mmapSharedDataMap.containsKey(sharedPackageName)) {
                Map<String, MMapSharedData> sharedDataMap = mmapSharedDataMap.get(sharedPackageName);
                if (sharedDataMap != null
                        && sharedDataMap.containsKey(key)
                        && sharedDataMap.get(key) != null
                        && sharedDataMap.get(key).getData() instanceof Long) {
                    if (isSelfData(selfPackageName, sharedPackageName)
                            || (sharedDataMap.get(key).getMode() >= MMapMode.MODE_WORLD_READABLE)) {
                        return (Long) sharedDataMap.get(key).getData();
                    } else {
                        if (L.DEBUG) {
                            L.logD("#-----getLong-------没有读权限--------------->>>");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public boolean putLong(String selfPackageName, String sharedPackageName, String key, long value, int mode) {
        return putMMapSharedData(selfPackageName, sharedPackageName, key, value, DATA_TYPE_LONG, mode);
    }

    //---------------------------STRING-----------------------------------
    public String getString(String selfPackageName, String sharedPackageName, String key, String defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            if (mmapSharedDataMap != null && mmapSharedDataMap.containsKey(sharedPackageName)) {
                Map<String, MMapSharedData> sharedDataMap = mmapSharedDataMap.get(sharedPackageName);
                if (sharedDataMap != null
                        && sharedDataMap.containsKey(key)
                        && sharedDataMap.get(key) != null
                        && sharedDataMap.get(key).getData() instanceof String) {
                    if (isSelfData(selfPackageName, sharedPackageName)
                            || (sharedDataMap.get(key).getMode() >= MMapMode.MODE_WORLD_READABLE)) {
                        return (String) sharedDataMap.get(key).getData();
                    } else {
                        if (L.DEBUG) {
                            L.logD("#-----getString-------没有读权限-------------->>>");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public boolean putString(String selfPackageName, String sharedPackageName, String key, String value, int mode) {
        return putMMapSharedData(selfPackageName, sharedPackageName, key, value, DATA_TYPE_STRING, mode);
    }

    //---------------------------ARRAY-----------------------------------
    public EsArray getArray(String selfPackageName, String sharedPackageName, String key, EsArray defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            if (mmapSharedDataMap != null && mmapSharedDataMap.containsKey(sharedPackageName)) {
                Map<String, MMapSharedData> sharedDataMap = mmapSharedDataMap.get(sharedPackageName);
                if (sharedDataMap != null
                        && sharedDataMap.containsKey(key)
                        && sharedDataMap.get(key) != null
                        && sharedDataMap.get(key).getData() instanceof EsArray) {
                    if (isSelfData(selfPackageName, sharedPackageName)
                            || (sharedDataMap.get(key).getMode() >= MMapMode.MODE_WORLD_READABLE)) {
                        return (EsArray) sharedDataMap.get(key).getData();
                    } else {
                        if (L.DEBUG) {
                            L.logD("#------getArray-------没有读权限------------>>>");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public boolean putArray(String selfPackageName, String sharedPackageName, String key, EsArray value, int mode) {
        return putMMapSharedData(selfPackageName, sharedPackageName, key, value, DATA_TYPE_ARRAY, mode);
    }

    //---------------------------MAP-----------------------------------
    public EsMap getMap(String selfPackageName, String sharedPackageName, String key, EsMap defValue) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return defValue;
        }
        try {
            if (mmapSharedDataMap != null && mmapSharedDataMap.containsKey(sharedPackageName)) {
                Map<String, MMapSharedData> sharedDataMap = mmapSharedDataMap.get(sharedPackageName);
                if (sharedDataMap != null
                        && sharedDataMap.containsKey(key)
                        && sharedDataMap.get(key) != null
                        && sharedDataMap.get(key).getData() instanceof EsMap) {
                    if (isSelfData(selfPackageName, sharedPackageName)
                            || (sharedDataMap.get(key).getMode() >= MMapMode.MODE_WORLD_READABLE)) {
                        return (EsMap) sharedDataMap.get(key).getData();
                    } else {
                        if (L.DEBUG) {
                            L.logD("#------getMap-------没有读权限------------>>>");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public boolean putMap(String selfPackageName, String sharedPackageName, String key, EsMap value, int mode) {
        return putMMapSharedData(selfPackageName, sharedPackageName, key, value, DATA_TYPE_MAP, mode);
    }

    public boolean isSelfData(String selfPackageName, String sharedPackageName) {
        return !TextUtils.isEmpty(selfPackageName)
                && !TextUtils.isEmpty(sharedPackageName)
                && selfPackageName.equals(sharedPackageName);
    }


    public boolean putMMapSharedData(String selfPackageName, String sharedPackageName,
                                     String key, Object value, int mmapSharedDataType, int mode) {
        if (TextUtils.isEmpty(selfPackageName) || TextUtils.isEmpty(sharedPackageName)) {
            return false;
        }

        if (mmapSharedDataMap == null) {
            return false;
        }

        try {
            if (!mmapSharedDataMap.containsKey(sharedPackageName)
                    || mmapSharedDataMap.get(sharedPackageName) == null) {
                Map<String, MMapSharedData> sharedDataMap = new HashMap<>();
                mmapSharedDataMap.put(sharedPackageName, sharedDataMap);
            }

            Map<String, MMapSharedData> sharedDataMap = mmapSharedDataMap.get(sharedPackageName);
            MMapSharedData sharedData = sharedDataMap.get(key);
            if (sharedData != null) {
                if (mmapSharedDataType == sharedData.getType()) {
                    //自己应用可写
                    if (isSelfData(selfPackageName, sharedPackageName)) {
                        if (L.DEBUG) {
                            L.logD("#-----putMMapSharedData-------自己应用随便写----------->>>");
                        }
                        sharedData.setData(value);
                        sharedData.setMode(mode);
                        sharedDataMap.put(key, sharedData);
                        return true;
                    }
                    //其他应用可写
                    else {
                        if (sharedData.getMode() == MMapMode.MODE_WORLD_WRITEABLE) {
                            if (L.DEBUG) {
                                L.logD("#-----putMMapSharedData-------有权限写----------->>>");
                            }
                            sharedData.setData(value);
                            sharedDataMap.put(key, sharedData);
                            return true;
                        }
                        //
                        else {
                            if (L.DEBUG) {
                                L.logD("#----putMMapSharedData-------没有权限写----------->>>");
                            }
                        }
                    }
                }
                //数据类型错误
                else {
                    if (L.DEBUG) {
                        L.logD("#---putMMapSharedData-------数据类型错误----------->>>");
                    }
                }
            }
            //init
            else if (isSelfData(selfPackageName, sharedPackageName)) {
                if (L.DEBUG) {
                    L.logD("#---putMMapSharedData-------自己应用随便写----初始化数据----------->>>");
                }
                sharedData = new MMapSharedData();
                sharedData.setData(value);
                sharedData.setMode(mode);
                sharedData.setType(mmapSharedDataType);
                sharedDataMap.put(key, sharedData);
                return true;
            }
            //
            else {
                if (L.DEBUG) {
                    L.logD("#---putMMapSharedData-------其他情况不符合条件--------->>>");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (L.DEBUG) {
                L.logD("#---putMMapSharedData-------异常------->>>");
            }
        }
        if (L.DEBUG) {
            L.logD("#--putMMapSharedData-------执行完毕------>>>");
        }
        return false;
    }

    public void release() {
        if (L.DEBUG) {
            L.logD("#---------release----->>>");
        }
        if (mmapSharedDataMap != null) {
            mmapSharedDataMap.clear();
        }
    }
}
