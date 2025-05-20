package com.quicktvui.sdk.core.utils;

import android.text.TextUtils;

import com.quicktvui.sdk.core.InitConfig;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.net.HttpRequest;

/**
 * 说明
 * <p>
 */
public class HttpRequestUtils {

    public static HttpRequest wrapper(HttpRequest request) {
        String proxyHostName = InitConfig.getDefault().getProxyHostName();
        int proxyPort = InitConfig.getDefault().getProxyPort();
        if(!TextUtils.isEmpty(proxyHostName) && proxyPort != 0){
            request.useProxy(proxyHostName, proxyPort);
            L.logIF("request with proxy " + proxyHostName + ":" + proxyPort);
        }
        // 增加默认Header BcCode
        String bcCode = InitConfig.getDefault().getBcCode();
        if (!TextUtils.isEmpty(bcCode)) {
            request.header("bcCode", bcCode);
        }
        return request.connectTimeout(15000).readTimeout(15000).followRedirects(true).trustAllCerts().trustAllHosts().useCaches(false);
    }

}
