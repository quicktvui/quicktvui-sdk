package com.quicktvui.sdk.core.protocol;

import android.net.Uri;
import android.util.Base64;

import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.utils.EsNativeEvent;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

import java.util.Set;

/**
 * 协议解析入口
 * Create by weipeng on 2022/04/18 15:16
 * Describe
 */
public class EsProtocolDispatcher {

    // 协议版本号
    public static final int PROTOCOL_VERSION = 1;

    // ------------------------ 1.0协议 ------------------------ //
    public static final String K_ACTION_V1 = "name";
    public static final String K_PACKAGE_V1 = "es_package";
    public static final String K_ARGS_V1 = "params";
    public static final String K_EXP = "exp";
    public static final String K_ACTION_ES_APP_V1 = "__AC_MAIN__";
    public static final String K_ACTION_NATIVE_APP_V1 = "__AC_APP__";

    // ------------------------ scheme旧协议 ------------------------ //
    public static final String K_PACKAGE_SCHEME = "es_pkg";

    // ------------------------ 2.0协议 ------------------------ //
    public static final String K_ACTION_V2 = "action";
    public static final String K_PACKAGE_V2 = "pkg";
    public static final String K_ARGS_V2 = "args";

    public static final String K_FROM = "from";
    public static final String K_ALL_PARAMS = "__START_PARAMS__";

    public static final String K_ACTION_ES_APP_V2 = "start_es";
    public static final String K_ACTION_NATIVE_APP_V2 = "start_app";
    public static final String K_ACTION_ES_CMD = "es_cmd";
    public static final String K_ACTION_ES_CMD_REMOTE_CONTROL = "es_remote_control";
    public static final String K_ACTION_ES_CMD_CLOSE = "es_close";
    public static final String K_ACTION_ES_CMD_QUERY = "es_query";
    public static final String K_ACTION_ES_CMD_QUERY_ERROR = "on_query_error";
    public static final String K_ACTION_ES_CMD_QUERY_RUNTIME_INFO = "runtime_info"; // TODO 获取基座信息
    public static final String K_ACTION_ES_CMD_QUERY_TOP_APP = "top_es_app";
    public static final String K_ACTION_ES_CMD_QUERY_RUNNING_APPS = "running_es_apps";
    public static final String K_ACTION_ES_CMD_QUERY_DONGLE_INFO = "dongle_info";

    // ------------------------ DLNA拼接参数 ------------------------ //
    public static final String K_DLNA_ARGS = "es_params";

    // ------------------------ 三方消息 ------------------------ //
    public static final String K_THIRD_TYPE = "type";
    public static final String K_THIRD_UPDATE = "update";
    public static final int CMD_PING = 0;
    public static final int CMD_SEARCH = 1;
    public static final int CMD_EVENT = 2;
    public static final int CMD_CUSTOM = 3;

    /** 尝试解析协议 **/
    public static void tryDispatcher(EsMap evtParams, String jsonData, IEsRemoteEventCallback callback) {
        L.logIF("try dispatch str");
        try {
            tryDispatcher(evtParams, new JSONObject(jsonData), callback);
        } catch (Exception e) {
            L.logW("parse protocol " + jsonData, e);
        }
    }

    /** 尝试解析协议 **/
    public static void tryDispatcher(EsMap evtParams, JSONObject json, IEsRemoteEventCallback callback) {
        L.logIF("try dispatch json");
        try {
            EsContext.get().setRemoteEventCallback(callback);
            if (json.has(K_ACTION_V1)) { // 认为是旧版本协议
                Protocol_1.dispatch(evtParams, json);
                return;
            }
            Protocol_2.dispatch(evtParams, json);
        } catch (Exception e) {
            L.logW("parse protocol " + json, e);
        }
    }

    /** 通过Uri的形式解析协议，多用于dlna传递的播放链接 **/
    public static void tryDispatcherUri(String uriStr) {
        if (L.DEBUG) L.logD(uriStr);
        Uri uri = Uri.parse(uriStr);
        Set<String> names = uri.getQueryParameterNames();
        if (names != null && names.contains(K_DLNA_ARGS)) {
            String base64Data = uri.getQueryParameter(K_DLNA_ARGS);
            if (L.DEBUG) L.logD("uri data:" + base64Data);
            String dataDecode = new String(Base64.decode(base64Data, Base64.URL_SAFE));
            EsMap from = new EsMap();
            from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_REMOTE);
            from.pushObject(Constants.Event.ES_REFERER1, Constants.Event.FROM_DLNA_PROTOCOL);
            tryDispatcher(from, dataDecode, null);
        } else {
            // 将DLNA的Uri通过 EsManager.setNativeEventListener() 回调抛出去
            EsMap data = new EsMap();
            data.pushString("uri", uriStr);
            new EsNativeEvent(Constants.GLOBAL_EVENT.EVT_DISPATCH_DLNA, data, null).handleEvent();
        }
    }
}
