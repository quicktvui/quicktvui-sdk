package com.quicktvui.support.socketio;

import android.util.SparseArray;

import com.sunrain.toolkit.utils.log.L;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

/**
 * Create by weipeng on 2021/08/18 20:13
 */
public class SocketIOClientImpl implements IEsSocketIOModule {

    private final SparseArray<SocketIOClient> mSocketClients = new SparseArray<>(1);
    private final AtomicInteger mSocketIds = new AtomicInteger(0);

    @Override
    public void connect(String url, EventListener listener) {

//        IO.Options options = IO.Options.builder()
//                .setMultiplex(true)
//                .setReconnection(true)
//                .setTransports(new String[]{WebSocket.NAME})
//                .build();

        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.transports = new String[]{WebSocket.NAME};
        options.timeout = 5000;

        int socketId = mSocketIds.incrementAndGet();
        Socket socket = IO.socket(URI.create(url), options);
        SocketIOClient client = new SocketIOClient(socketId, socket, listener);
        mSocketClients.append(socketId, client);

        client.connect();
    }

    public void destroy(int socketId) {
        SocketIOClient client = mSocketClients.get(socketId);
        if (client != null) {
            client.disconnect();
            mSocketClients.remove(socketId);
        }
    }

    public void on(int socketId, String event) {
        SocketIOClient client = mSocketClients.get(socketId);
        if (client != null) client.on(event);
    }

    public void emit(int socketId, String event, Object... data) {
        SocketIOClient client = mSocketClients.get(socketId);
        if (client != null) client.emit(event, data);
    }

    @Override
    public void destroy() {
        int size = mSocketClients.size();
        for (int i = 0; i < size; i++) {
            mSocketClients.get(mSocketClients.keyAt(i)).disconnect();
        }
        mSocketClients.clear();
        mSocketIds.set(0);
    }

    private static final class SocketIOClient {

        private final int mSocketId;
        private Socket mSocket;
        private EventListener mListener;

        public SocketIOClient(int socketId, Socket socket, EventListener listener) {
            mSocketId = socketId;
            mSocket = socket;
            mListener = listener;

            onBaseListen();
        }

        public void connect() {
            if (mSocket == null) return;
            mSocket.connect();
        }

        public void disconnect() {
            if (mSocket == null) return;
            mSocket.disconnect();
            mSocket.off();

            if (L.DEBUG) L.logD("destroy socket " + mSocketId);

            mSocket = null;
        }

        public void on(String event) {
            if (mSocket == null) return;
            if (L.DEBUG) L.logD("listen " + event);
            mSocket.off(event);
            EventCallback callback = new EventCallback(event);
            mSocket.on(event, callback);
        }

        public void once(String event, Emitter.Listener listener) {
            if (mSocket == null) return;
            mSocket.once(event, listener);
        }

        public void emit(String event, Object... data) {
            if (!isConnected()) return;
            if (L.DEBUG) L.logD("send " + event + ", " + data);
            mSocket.emit(event, data);
        }

        public boolean isConnected() {
            return mSocket != null && mSocket.connected();
        }

        private void onBaseListen() {

            mSocket.on(Socket.EVENT_CONNECT, args -> {
                if (isConnected()) {
                    if(mListener != null) mListener.onConnect(mSocketId);
                }
            });

            mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                if(mListener != null) mListener.onConnectError(mSocketId, args[0].toString());
            });

            mSocket.on(Socket.EVENT_DISCONNECT, args -> {
                if(mListener != null) mListener.onDisconnect(mSocketId);
            });

        }

        private final class EventCallback implements Emitter.Listener {

            private final String mEventName;

            public EventCallback(String event) {
                mEventName = event;
            }

            @Override
            public void call(Object... args) {
                String data = "{}";
                if (args != null && args.length > 0) {
                    data = args[0].toString();
                }
                if (isConnected()) {
                    if(mListener != null) mListener.onMessage(mSocketId, mEventName, data);
                }
            }

        }

    }
}
