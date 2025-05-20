package com.quicktvui.sdk.core.utils;

import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_APP_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_V2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsPromiseProxy;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by sorosunrain on 2021/05/21 11:05
 */
public class MapperUtils {

    /**
     * 类型匹配 EsClass -> HpClass
     **/
    public static Type[] tryMapperEsClass2HpClass(Type[] paramClss) {
        if (paramClss != null) {
            int len = paramClss.length;
            for (int i = 0; i < len; i++) {
                if (paramClss[i] == EsMap.class) {
                    paramClss[i] = HippyMap.class;
                } else if (paramClss[i] == EsArray.class) {
                    paramClss[i] = HippyArray.class;
                } else if (paramClss[i] == EsPromise.class) {
                    paramClss[i] = Promise.class;
                }
            }
        }
        return paramClss;
    }

    /**
     * 将Hp类型转为Es类型数组[]
     **/
    public static Object[] tryMapperHpData2EsData(Object data[]) {
        if (data != null) {
            int len = data.length;
            for (int i = 0; i < len; i++) {
                data[i] = tryMapperHpData2EsData(data[i]);
            }
        }
        return data;
    }

    /**
     * 将Hp类型转为Es类型
     **/
    public static Object tryMapperHpData2EsData(Object data) {
        if (data instanceof HippyMap) return hpMap2EsMap((HippyMap) data);
        if (data instanceof HippyArray) return hpArray2EsArray((HippyArray) data);
        if (data instanceof Promise) return new EsPromiseProxy((Promise) data);
        return data;
    }

    /**
     * 将Es类型转为Hp类型
     **/
    public static Object tryMapperEsData2HpData(Object data) {
        if (data instanceof EsMap) return esMap2HpMap((EsMap) data);
        if (data instanceof EsArray) return esArray2HpArray((EsArray) data);
        if (data instanceof EsPromise) return ((EsPromise) data).getProxy();
        return data;
    }

    /**
     * 将HippyMap转为EsMap
     **/
    public static EsMap hpMap2EsMap(HippyMap hpMap) {
        EsMap map = new EsMap();

        if (hpMap != null) {
            Set<String> keys = hpMap.keySet();
            for (String key : keys) {
                map.pushObject(key, tryMapperHpData2EsData(hpMap.get(key)));
            }
        }

        return map;
    }

    /**
     * 将HippyArray转为EsArray
     **/
    public static EsArray hpArray2EsArray(HippyArray hpArray) {
        if (L.DEBUG) {
            if (hpArray != null) {
                JSONArray ja = hpArray.toJSONArray();
                if (ja != null) {
                    L.logD("hpArray2EsArray " + ja);
                }
            }
        }
        EsArray array = new EsArray();
        try {
            if (hpArray != null) {
                int size = hpArray.size();
                for (int i = 0; i < size; i++) {
                    array.pushObject(tryMapperHpData2EsData(hpArray.get(i)));
                }
            }
        } catch (Throwable e) {
            L.logW("array mapper", e);
        }
        return array;
    }

    /**
     * 将EsMap转为HippyMap
     **/
    public static HippyMap esMap2HpMap(EsMap esMap) {
        HippyMap map = new HippyMap();

        if (esMap != null) {
            Set<String> keys = esMap.keySet();
            for (String key : keys) {
                map.pushObject(key, tryMapperEsData2HpData(esMap.get(key)));
            }
        }

        return map;
    }

    /**
     * 将EsArray转为HippyArray
     **/
    public static HippyArray esArray2HpArray(EsArray esArray) {
        HippyArray array = new HippyArray();

        if (esArray != null) {
            int size = esArray.size();
            for (int i = 0; i < size; i++) {
                array.pushObject(tryMapperEsData2HpData(esArray.get(i)));
            }
        }

        return array;
    }

    /**
     * 将JSONObject或者json字符串转为EsMap
     **/
    public static EsMap tryMapperJson2EsMap(Object data) {
        if (data != null) {
            try {
                JSONObject jsonObject = tryMapperObject2JsonObject(data);
                if (jsonObject != null) {
                    EsMap esMap = new EsMap();
                    esMap.pushJSONObject(jsonObject);
                    return esMap;
                }
            } catch (Exception e) {
                L.logW("map mapper", e);
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
            L.logW("bundle mapper", e);
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
    public static JSONObject tryMapperBundle2JsonObject(Bundle bundle) {
        JSONObject jo = new JSONObject();
        try {
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object obj = bundle.get(key);
                    if (obj instanceof Bundle) {
                        jo.put(key, tryMapperBundle2JsonObject((Bundle) obj));
                    } else {
                        jo.put(key, tryWrap(obj));
                    }
                }
            }
        } catch (Exception e) {
            L.logW("json mapper", e);
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
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
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

    @Nullable
    public static JSONObject toJsonObject(String str) {
        try {
            return new JSONObject(str);
        } catch (Exception ignore) {
        }
        return null;
    }

    @Nullable
    public static JSONObject intent2JsonObject(Intent intent) {
        if (intent == null) return null;
        Uri uri = intent.getData();
        if (uri != null) {
            return uri2JsonObject(uri);
        }
        return bundle2JsonObject(intent.getExtras());
    }

    public static JSONObject uri2JsonObject(@NonNull Uri uri) {
        // 老的scheme形式
        if ("esapp".equals(uri.getScheme())
                && "action".equals(uri.getHost())) {
            return uri2JsonObjectV1(uri);
        }
        // 新的scheme形式
        return uri2JsonObjectV2(uri);
    }

    /**
     * Uri转JSONObject
     * <br>
     * esapp://action/start?from=cmd&pkg=es.skill.weather.tv&flags=1
     **/
    @Nullable
    public static JSONObject uri2JsonObjectV1(Uri uri) {
        if (uri == null) return null;
        JSONObject jo = null;
        try {
            String path = uri.getLastPathSegment();
            Set<String> keys = uri.getQueryParameterNames();
            if (keys.size() != 0) {
                jo = new JSONObject();
                if ("start".equals(path)) {
                    jo.put(K_ACTION_V2, K_ACTION_ES_APP_V2);
                }
                for (String key : keys) {
                    jo.put(decode(key), decode(uri.getQueryParameter(key)));
                }
            }
        } catch (Exception e) {
            L.logW("uri mapper", e);
        }
        return jo;
    }

    /**
     * -> rpk
     * esapp://es.hello.world?from=cmd
     * esapp://es.hello.world/1.0.3?from=cmd
     * -> nexus
     * esapp://huantv/zoo?repository=http://hun.quickui.com/&from=cmd
     * esapp://huantv/zoo/1.0.0?repository=http://hun.quickui.com/&from=cmd
     **/
    @Nullable
    public static JSONObject uri2JsonObjectV2(Uri uri) {

        try {
            boolean isFromApi = checkIsRequestApi(uri);

            String pkgName = uri.getHost();
            String version;

            List<String> segments = uri.getPathSegments();
            if (isFromApi) {
                version = segments.size() > 0 ? segments.get(0) : "";
            } else {
                pkgName += "/" + segments.get(0);
                version = segments.size() > 1 ? segments.get(1) : "";
            }

            JSONObject jo = new JSONObject();
            jo.put(K_ACTION_V2, K_ACTION_ES_APP_V2);
            jo.put(Constants.EsData.K_PKG, pkgName);
            jo.put(Constants.EsData.K_VER, version);

            Set<String> keys = uri.getQueryParameterNames();
            for (String key : keys) {
                jo.put(decode(key), decode(uri.getQueryParameter(key)));
            }
            return jo;
        } catch (Exception e) {
            L.logDF("" + uri);
            L.logW("uri mapper", e);
        }
        return null;
    }

    private static String decode(String str) throws Exception {
        return URLDecoder.decode(str, "UTF-8");
    }

    private static boolean checkIsRequestApi(Uri uri) {
        return TextUtils.isEmpty(uri.getQueryParameter(Constants.EsData.K_REPO));
    }

    @Nullable
    private static JSONObject bundle2JsonObject(Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) return null;
        Set<String> keys = bundle.keySet();
        JSONObject jo = new JSONObject();
        try {
            for (String key : keys) {
                jo.put(key, bundle.get(key));
            }
        } catch (Exception e) {
            L.logW("json mapper", e);
        }
        return jo;
    }
}