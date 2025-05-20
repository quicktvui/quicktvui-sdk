package quicktvui.support.websocket;


public interface IEsWebSocketModule {

    void connect(String url, EventListener listener);

    void destroy(int socketId);

    void send(int socketId, String text);

    void send(int socketId, byte... bytes);

    interface EventListener {

        void onConnect(int socketId);

        void onConnectError(int socketId, String msg);

        void onDisconnect(int socketId);

        void onMessage(int socketId, String msg);
    }
}