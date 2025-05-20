package com.quicktvui.support.socketio;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;


/**
 * Create by weipeng on 2022/04/19 14:46
 * Describe
 */
@ESKitAutoRegister
public class SocketIoModule implements IEsModule {

    private static final String SOCKET_IO_EVENT_NAME = "SocketIOEvents";

    private static final String PARAM_KEY_SOCKET_ID = "id";
    private static final String PARAM_KEY_CODE = "code";
    private static final String PARAM_KEY_REASON = "reason";
    private static final String PARAM_KEY_SOCKET_URL = "url";

    private static final String PARAM_KEY_TYPE = "type";
    private static final String PARAM_KEY_EVENT = "event";
    private static final String PARAM_KEY_DATA = "data";

    private static final String EVENT_TYPE_ON_OPEN = "connect";
    private static final String EVENT_TYPE_ON_CLOSE = "disconnect";
    private static final String EVENT_TYPE_ON_ERROR = "connect_error";

    private IEsSocketIOModule mSocketIo;


    @Override
    public void init(Context context) {

    }

    public void connect(EsMap param, EsPromise promise) {
        String url;
        if (param == null
                || TextUtils.isEmpty((url = param.getString(PARAM_KEY_SOCKET_URL)))) {
            PromiseHolder.create(promise)
                    .put(PARAM_KEY_CODE, -1)
                    .put(PARAM_KEY_REASON, "没有 'url' 参数")
                    .sendSuccess();
            return;
        }

        mSocketIo = new SocketIOClientImpl();
        mSocketIo.connect(url, new IEsSocketIOModule.EventListener() {
            @Override
            public void onConnect(int socketId) {
                if (L.DEBUG) L.logD("create socket " + socketId);
                PromiseHolder.create(promise)
                        .put(PARAM_KEY_CODE, 0)
                        .put(PARAM_KEY_REASON, "")
                        .put(PARAM_KEY_SOCKET_ID, socketId)
                        .sendSuccess();
                sendSocketEvent2Js(socketId, EVENT_TYPE_ON_OPEN, null);
            }

            @Override
            public void onConnectError(int socketId, String msg) {
                mSocketIo.destroy(socketId);
                EsMap map = new EsMap();
                map.pushString(PARAM_KEY_REASON, msg);
                sendSocketEvent2Js(socketId, EVENT_TYPE_ON_ERROR, map);
            }

            @Override
            public void onDisconnect(int socketId) {
                mSocketIo.destroy(socketId);
                sendSocketEvent2Js(socketId, EVENT_TYPE_ON_CLOSE, null);
            }

            @Override
            public void onMessage(int socketId, String eventName, String data) {
                sendSocketEvent2Js(socketId, eventName, data);
            }

            private void sendSocketEvent2Js(int socketId, String eventType, Object data) {
                if (L.DEBUG) L.logD("send event 2 js:" + eventType + ", " + data);
                EsMap map = new EsMap();
                map.pushInt(PARAM_KEY_SOCKET_ID, socketId);
                map.pushString(PARAM_KEY_TYPE, eventType);
                map.pushObject(PARAM_KEY_DATA, data);
                EsProxy.get().sendNativeEventTop(SOCKET_IO_EVENT_NAME, map);
            }

        });
    }

    public void on(EsMap param) {
        String event = param.getString(PARAM_KEY_EVENT);
        if (TextUtils.isEmpty(event)) {
            L.logEF("Missing 'event' parameter");
            return;
        }

        if (EVENT_TYPE_ON_OPEN.equals(event)
                || EVENT_TYPE_ON_CLOSE.equals(event)
                || EVENT_TYPE_ON_ERROR.equals(event)) {
            L.logWF(String.format("Need not on[%s,%s,%s], register already.",
                    EVENT_TYPE_ON_OPEN, EVENT_TYPE_ON_CLOSE, EVENT_TYPE_ON_ERROR));
            return;
        }

        if (mSocketIo != null) {
            mSocketIo.on(param.getInt(PARAM_KEY_SOCKET_ID), event);
        }
    }

    public void emit(EsMap param) {
        String event = param.getString(PARAM_KEY_EVENT);
        if (TextUtils.isEmpty(event)) {
            L.logEF("Missing 'event' parameter");
            return;
        }

        Object data = param.get(PARAM_KEY_DATA);

        JSONObject jo = new JSONObject();
        if (data instanceof EsMap) {
            jo = ((EsMap) data).toJSONObject();
//            Set<Map.Entry<String, Object>> entries = ((EsMap) data).entrySet();
//            for (Map.Entry<String, Object> entry : entries) {
//                try {
//                    jo.put(entry.getKey(), entry.getValue());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }

        if (mSocketIo != null) {
            mSocketIo.emit(param.getInt(PARAM_KEY_SOCKET_ID), event, jo);
        }
    }

    public void disconnect(EsMap param) {
        if (mSocketIo != null) {
            mSocketIo.destroy(param.getInt(PARAM_KEY_SOCKET_ID));
        }
    }

    @Override
    public void destroy() {
        if (mSocketIo != null) {
            mSocketIo.destroy();
        }
    }
}
