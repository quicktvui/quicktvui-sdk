package quicktvui.support.websocket;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;


@ESKitAutoRegister
public class WebSocketModule implements IEsModule {

    private static final String WEB_SOCKET_EVENT_NAME = "WebSocketEvents";

    private static final String PARAM_KEY_SOCKET_ID = "id";
    private static final String PARAM_KEY_CODE = "code";
    private static final String PARAM_KEY_REASON = "reason";
    private static final String PARAM_KEY_SOCKET_URL = "url";

    private static final String PARAM_KEY_TYPE = "type";
    private static final String PARAM_KEY_EVENT = "event";
    private static final String PARAM_KEY_DATA = "data";

    private static final String EVENT_TYPE_ON_OPEN = "connect";
    private static final String EVENT_TYPE_ON_CLOSE = "disconnect";
    private static final String EVENT_TYPE_ON_MESSAGE = "onMessage";
    private static final String EVENT_TYPE_ON_ERROR = "connect_error";

    private IEsWebSocketModule mWebSocket;

    @Override
    public void init(Context context) {

    }

    public void connect(EsMap param, EsPromise promise){
        String url;
        if (param == null
                || TextUtils.isEmpty((url = param.getString(PARAM_KEY_SOCKET_URL)))) {
            PromiseHolder.create(promise)
                    .put(PARAM_KEY_CODE, -1)
                    .put(PARAM_KEY_REASON, "没有 'url' 参数")
                    .sendSuccess();
            return;
        }
        mWebSocket = new WebSocketImpl();
        mWebSocket.connect(url, new IEsWebSocketModule.EventListener() {
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
                mWebSocket.destroy(socketId);
                EsMap map = new EsMap();
                map.pushString(PARAM_KEY_REASON, msg);
                sendSocketEvent2Js(socketId, EVENT_TYPE_ON_ERROR, map);
                mWebSocket = null;
            }

            @Override
            public void onDisconnect(int socketId) {
                mWebSocket.destroy(socketId);
                sendSocketEvent2Js(socketId, EVENT_TYPE_ON_CLOSE, null);
                mWebSocket = null;
            }

            @Override
            public void onMessage(int socketId, String msg) {
                sendSocketEvent2Js(socketId, EVENT_TYPE_ON_MESSAGE, msg);
            }

            private void sendSocketEvent2Js(int socketId, String eventType, Object data) {
                if (L.DEBUG) L.logD("send event 2 js:" + eventType + ", " + data);
                EsMap map = new EsMap();
                map.pushInt(PARAM_KEY_SOCKET_ID, socketId);
                map.pushString(PARAM_KEY_TYPE, eventType);
                map.pushObject(PARAM_KEY_DATA, data);
                EsProxy.get().sendNativeEventTop(WEB_SOCKET_EVENT_NAME, map);
            }
        });
    }

    public void send(EsMap param) {
        if(mWebSocket != null){
            mWebSocket.send(param.getInt(PARAM_KEY_SOCKET_ID),
                    param.getString(PARAM_KEY_DATA));
        }
    }

    public void destroy(EsMap param) {
        if(mWebSocket != null){
            mWebSocket.destroy(param.getInt(PARAM_KEY_SOCKET_ID));
        }
        mWebSocket = null;
    }

    @Override
    public void destroy() {

    }
}
