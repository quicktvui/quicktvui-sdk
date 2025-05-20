package quicktvui.support.websocket;

import android.util.SparseArray;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class WebSocketImpl implements IEsWebSocketModule {

    private final AtomicInteger mSocketIds = new AtomicInteger(0);
    private final SparseArray<WebSocketClient> mClients = new SparseArray<>();
    private OkHttpClient okHttpClient;

    @Override
    public void connect(String url, EventListener listener) {
        if(listener == null) throw new IllegalArgumentException("EventListener不能为空");

        if(okHttpClient == null) okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(url).build();

        int socketId = mSocketIds.incrementAndGet();
        WebSocketClient client = new WebSocketClient(socketId, listener);
        mClients.append(socketId, client);
        okHttpClient.newWebSocket(request, client);
    }

    @Override
    public void destroy(int socketId) {
        WebSocketClient client = mClients.get(socketId);
        if(client != null){
            client.disconnect();
            mClients.remove(socketId);
        }
    }

    @Override
    public void send(int socketId, String text){
        WebSocketClient client = mClients.get(socketId);
        if(client != null) client.send(text);
    }

    @Override
    public void send(int socketId, byte... bytes) {
        WebSocketClient client = mClients.get(socketId);
        if(client != null) client.send(bytes);
    }
}
