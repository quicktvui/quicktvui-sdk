package com.quicktvui.sdk.core.adapter;

import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.adapter.http.HippyHttpRequest;

import java.net.URL;

/**
 * 网络代理
 * <p>
 */
public class ProxyHttpAdapter extends HttpsAdapter {

    private final String proxyHostName;
    private final int proxyPort;

    public ProxyHttpAdapter(String proxyHostName, int proxyPort) {
        this.proxyHostName = proxyHostName;
        this.proxyPort = proxyPort;
    }

    @Override
    protected void configUrlConnection(HippyHttpRequest request, URL url) {
        String hostName = request.getProxyHostName();
        int hostPort = request.getProxyPort();
        if (TextUtils.isEmpty(hostName) && hostPort == 0) {
            request.setProxy(proxyHostName, proxyPort);
            L.logIF("fetch with proxy " + proxyHostName + ":" + proxyPort);
        } else {
            L.logIF("fetch with proxy " + hostName + ":" + hostPort);
        }
    }

}
