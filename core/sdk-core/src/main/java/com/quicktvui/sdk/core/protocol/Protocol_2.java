package com.quicktvui.sdk.core.protocol;

import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_APP_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD_CLOSE;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD_QUERY;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD_QUERY_DONGLE_INFO;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD_QUERY_ERROR;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD_QUERY_RUNNING_APPS;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD_QUERY_TOP_APP;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_CMD_REMOTE_CONTROL;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_NATIVE_APP_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ARGS_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_FROM;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_PACKAGE_SCHEME;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_PACKAGE_V2;

import android.content.Intent;
import android.text.TextUtils;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.model.ThirdEvent;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.EsManager;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.mediasession.EsMediaController;
import com.quicktvui.sdk.core.module.EsNativeModule;
import com.quicktvui.sdk.core.utils.Am;
import com.quicktvui.sdk.core.utils.EsDataFactory;
import com.quicktvui.sdk.core.utils.EsIntent;
import com.quicktvui.sdk.core.utils.KeyEventUtil;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.sunrain.toolkit.utils.ReflectUtils;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

/**
 * 2.0版本协议解析
 * <p>
 * Create by weipeng on 2022/04/18 15:15
 * Describe 就写一解析
 * {
 * "action": "start_es",
 * "pkg": "es.com.fitness.tv",
 * "from": "tv.danmaku.bili",
 * "args": {},
 * "exp": {}
 * }
 */
public class Protocol_2 {

    /**
     * 来自于MediaSession协议
     **/
    public static final String FROM_MULTICAST_CHECK = "es_media_session";

    /**
     * 2.0版本协议解析
     **/
    public static void dispatch(EsMap evtParams, JSONObject jo) throws Exception {
        L.logIF("protocol 2.0 " + jo);
        String action = jo.optString(K_ACTION_V2);
        if (TextUtils.isEmpty(action)) {
            L.logEF("action is empty");
            return;
        }

        // 兼容1.0
        if (jo.has(K_PACKAGE_SCHEME)) {
            jo.put(K_PACKAGE_V2, jo.opt(K_PACKAGE_SCHEME));
        }

        String from = jo.optString(K_FROM);
        if (TextUtils.isEmpty(from)) {
            L.logEF("三方调用需传'from'字段");
            return;
        }

        // 启动快应用
        if (K_ACTION_ES_APP_V2.equals(action)) {
            String pkg = jo.optString(K_PACKAGE_V2);
            if (TextUtils.isEmpty(pkg)) {
                L.logEF("pkg is empty");
                return;
            }
            EsData data = EsDataFactory.create(jo);
            if (data == null) {
                L.logEF("协议解析失败");
                return;
            }
            setLastIsDebug(data);

            // ------- 埋点 -------- //
            EsMap exp = data.getExp();
            if (exp == null) {
                exp = new EsMap();
            }

            // 将所有启动参数添加到exp
            exp.pushString(EsProtocolDispatcher.K_ALL_PARAMS, jo.toString());

            try {
                String[] newFrom = from.split(",");
                int fromIndex = 0;
                for (String key : Constants.Event.ES_REFERER_LIST) {
                    if (evtParams.containsKey(key)) continue;
                    if (fromIndex >= newFrom.length) break;
                    evtParams.pushObject(key, newFrom[fromIndex]);
                    fromIndex++;
                }
                exp.pushMap(K_FROM, evtParams);
                data.setExp(exp);
            } catch (Exception e) {
                L.logW("start es app", e);
            }
            // ------- 埋点 -------- //

            if (L.DEBUG) L.logD("create from factory: " + data);
            EsManager.get().start(data);
            accessSpecialIntentWithFrom(from);
            return;
        }

        // 启动原生应用
        if (K_ACTION_NATIVE_APP_V2.equals(action)) {
            if (jo.has(K_ARGS_V2)) {
                Object arg = jo.get(K_ARGS_V2);
                if (arg instanceof String) {
                    Am am = new Am();
                    EsIntent intent = am.makeIntent((String) arg);
                    am.execute(EsContext.get().getContext(), intent);
                }
            }
            return;
        }

        // 执行命令
        if (K_ACTION_ES_CMD.equals(action)) {
            if (L.DEBUG) L.logD("命令模式");
            executeCommand(from, jo.optJSONObject(K_ARGS_V2));
            return;
        }

        // 抛给VUE
        EsMap args = null;
        if (jo.has(K_ARGS_V2)) {
            args = new EsMap();
            Object obj = jo.get(K_ARGS_V2);

            JSONObject arg = MapperUtils.tryMapperObject2JsonObject(obj);
            if (arg != null) {
                args.pushJSONObject(arg);
            } else {
                args.pushObject("init", obj);
            }
        }
        if (L.DEBUG) L.logD("send event to vue: " + action + " args:" + args);
        EsViewManager.get().sendNativeEventTop(action, args);
    }

    /**
     * 兼容debug状态下的跳转
     **/
    private static void setLastIsDebug(EsData data) {
        EsViewManager vm = EsViewManager.get();
        if (vm == null) return;
        EsData last = vm.getEsAppData();
        if (last == null) return;
        String lastUri;
        if (TextUtils.isEmpty((lastUri = last.getAppDownloadUrl()))) return;
        if (!lastUri.contains(":38989")) return;
        if (!Objects.equals(data.getEsPackage(), last.getEsPackage())) return;
        L.logWF("restore last debug info");
        data.setAppDownloadUrl(lastUri);
    }

    /**
     * 处理特殊来源
     **/
    private static void accessSpecialIntentWithFrom(String from) {
        if (TextUtils.isEmpty(from)) return;
        if (from.contains(FROM_MULTICAST_CHECK)) {
            L.logIF("media session, attach callback");
            EsMediaController.get().attachEventCallback();
        }
    }

    private static void executeCommand(String from, JSONObject cmd) throws Exception {
        if (cmd == null) return;
        String intention = cmd.optString("intention");
        if (L.DEBUG) L.logD("executeCommand:" + intention);
        switch (intention) {
            case K_ACTION_ES_CMD_REMOTE_CONTROL:
                executeRemoteControl(from, cmd.optJSONObject("data"));
                break;
            case K_ACTION_ES_CMD_CLOSE:
                executeClosePage(from, cmd.optJSONObject("data"));
                break;
            case K_ACTION_ES_CMD_QUERY:
                executeQuery(from, cmd.optJSONObject("data"));
                break;
        }
    }

    private static void executeClosePage(String from, JSONObject data) throws Exception {
        if (data == null) return;
        JSONArray pkgs = data.optJSONArray("pkgs");
        if (pkgs == null) return;
        if (Objects.equals(pkgs.getString(0), "all")) {
            EsViewManager.get().finishAllApp();
        } else {
            int length = pkgs.length();
            for (int i = 0; i < length; i++) {
                String pkg = pkgs.getString(i);
                EsViewManager.get().finish(pkg);
            }
        }
    }

    private static void executeRemoteControl(String from, JSONObject data) {
        if (data == null) return;
        int keyCode = data.optInt("keycode", -1);
        if (keyCode <= 0) {
            L.logEF("keycode无效");
            return;
        }
        L.logIF("keycode:" + keyCode);
        KeyEventUtil.sendKeyEvent(keyCode);
    }

    private static void executeQuery(String from, JSONObject data) {
        if (data == null) return;
        String keyword = data.optString("keyword");
        if (L.DEBUG) L.logD("query:" + keyword);

        // 旧版的事件返回并没有前缀on_ , 需要兼容
        // TODO 优化 提供获取基座信息的接口，调用方兼容指令
        final String EVT_RPS_PREFIX = "wechat".equals(from) ? "" : "on_";

        switch (keyword) {
            case K_ACTION_ES_CMD_QUERY_TOP_APP: {
                EsNativeModule.sendEvent2Master(EVT_RPS_PREFIX + K_ACTION_ES_CMD_QUERY_TOP_APP, getTopInfo());
            }
            break;
            case K_ACTION_ES_CMD_QUERY_RUNNING_APPS: {
                EsNativeModule.sendEvent2Master(EVT_RPS_PREFIX + K_ACTION_ES_CMD_QUERY_RUNNING_APPS, getRunningInfo().toString());
            }
            break;
            case K_ACTION_ES_CMD_QUERY_DONGLE_INFO: {
                EsNativeModule.sendEvent2Master(EVT_RPS_PREFIX + K_ACTION_ES_CMD_QUERY_DONGLE_INFO, getDongleInfo().toString());
            }
            break;
            default: {
                EsNativeModule.sendEvent2Master(K_ACTION_ES_CMD_QUERY_ERROR, keyword + " not support");
            }
            break;
        }
    }

    public static String getTopInfo() {
        try {
            EsViewManager vm = EsViewManager.get();
            if (vm != null) {
                EsData data = vm.getEsAppData();
                if (data != null) {
                    return data.getEsPackage();
                }
            }
        } catch (Exception e) {
            L.logW("get top info", e);
        }
        return "";
    }

    public static JSONArray getRunningInfo() {
        JSONArray ja = new JSONArray();
        try {
            EsViewManager vm = EsViewManager.get();
            if (vm != null) {
                List<EsData> apps = vm.getRunningApps();
                for (EsData app : apps) {
                    ja.put(app.getEsPackage());
                }
            }
        } catch (Exception e) {
            L.logW("get running info", e);
        }
        return ja;
    }

    public static JSONObject getDongleInfo() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("sn", "");
            ReflectUtils nanoUsbInstance = ReflectUtils.reflect("com.extscreen.runtime.usb.NanoUsbInfoManager")
                    .method("getInstance");
            String sn = nanoUsbInstance.method("getNanoUsbSnCode").get();
            if (!TextUtils.isEmpty(sn)) {
                jo.put("sn", sn);
            } else {
                nanoUsbInstance.method("initNanoUsbSnCode");
            }

            ReflectUtils NanoUsbDevice = ReflectUtils.reflect("com.extscreen.runtime.usb.NanoUsbDevice");
            jo.put("vid", NanoUsbDevice.field("NANO_USB_DEVICE_VENDOR_ID"));
            jo.put("pid", NanoUsbDevice.field("NANO_USB_DEVICE_PRODUCT_ID"));

        } catch (Exception e) {
            L.logW("get dongle info", e);
        }
        return jo;
    }

    public static boolean dealWithMessenger(Intent intent) {
        if (intent.getData() != null && intent.hasExtra("data")
                && intent.hasExtra("ip")) {
            String data = intent.getStringExtra("data");
            String ip = intent.getStringExtra("ip");
            int port = intent.getIntExtra("port", 0);
            String from = intent.getStringExtra("from");
            if (L.DEBUG) L.logD("receive ip: " + ip);
            if (L.DEBUG) L.logD("receive port: " + port);
            if (L.DEBUG) L.logD("receive data: " + data);
            if (L.DEBUG) L.logD("receive from: " + from);
            ThirdEvent evt = new ThirdEvent();
            evt.ip = ip;
            evt.port = port;
            evt.data = data;
            evt.from = from;
            EsProxy.get().receiveThirdEvent(evt);
            return true;
        }
        return false;
    }

}
