package com.quicktvui.support.socketio;

/**
 * Create by weipeng on 2021/08/20 15:20
 */
public interface IEsSocketIOModule {

    void connect(String url, EventListener listener);

    void destroy(int socketId);

    void on(int socketId, String event);

    void emit(int socketId, String event, Object... data);

    void destroy();

    interface EventListener {
        void onConnect(int socketId);
        void onConnectError(int socketId, String msg);
        void onDisconnect(int socketId);
        void onMessage(int socketId, String eventName, String data);
    }
}