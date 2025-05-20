package com.bumptech.glide.load.data;

import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.net.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * 替换连接，设置代理
 * <p>
 */
public class EsHttpUrlConnectionFactory implements HttpUrlFetcher.HttpUrlConnectionFactory {

    private final String proxyHostName;
    private final int proxyPort;

    public EsHttpUrlConnectionFactory(String proxyHostName, int proxyPort) {
        this.proxyHostName = proxyHostName;
        this.proxyPort = proxyPort;
    }

    @Override
    public HttpURLConnection build(URL url) throws IOException {
        HttpURLConnection connection = createHttpURLConnection(url);
        HttpRequest.trustAllCerts(connection);
        HttpRequest.trustAllHosts(connection);
        return connection;
    }

    private HttpURLConnection createHttpURLConnection(URL url) throws IOException {
        if(!TextUtils.isEmpty(proxyHostName) && proxyPort != 0){
            if (L.DEBUG){
                L.logIF("glide with proxy " + proxyHostName + ":" + proxyPort);
            }
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostName, proxyPort));
            return (HttpURLConnection) url.openConnection(proxy);
        }
        return (HttpURLConnection) url.openConnection();
    }
}
