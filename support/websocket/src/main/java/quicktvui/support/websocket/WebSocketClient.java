package quicktvui.support.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketClient extends WebSocketListener {

    private final int mSocketId;
    private WebSocketImpl.EventListener mListener;

    private WebSocket mWebSocket;

    public WebSocketClient(int socketId, WebSocketImpl.EventListener listener) {
        mSocketId = socketId;
        mListener = listener;
    }

    public void disconnect() {
        if(mWebSocket == null) return;
        mWebSocket.close(1000, "disconnect");
        mWebSocket = null;
        mListener = null;
    }

    public void send(String text) {
        if(mWebSocket == null) return;
        mWebSocket.send(text);
    }

    public void send(byte... bytes) {
        if(mWebSocket == null) return;
        mWebSocket.send(ByteString.of(bytes));
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        mWebSocket = webSocket;
        if(mListener != null) mListener.onConnect(mSocketId);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        if(mListener != null) mListener.onDisconnect(mSocketId);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if(mListener != null) mListener.onMessage(mSocketId, text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        if(mListener != null) mListener.onMessage(mSocketId, bytes.utf8());
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if(mListener != null) mListener.onConnectError(mSocketId, t.getMessage());
    }
}
