package com.quicktvui.sdk.core.mdns;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import com.sunrain.toolkit.utils.NetworkUtils;
import com.sunrain.toolkit.utils.log.L;

import java.net.InetAddress;

import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.udp.EsUdpServer;

/**
 * Create by weipeng on 2022/04/21 15:57
 * Describe mDns注册
 */
public class NsdRegisterHelper {

    /** 启动本地mDNS **/
    public static void register() throws Exception{
        final String SERVER_TYPE = "_es._udp.";

        Context context = EsContext.get().getContext();
        if(context == null) return;
        NsdManager nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        if(nsdManager == null) return;
        NsdServiceInfo info = new NsdServiceInfo();
        info.setServiceName(EsContext.get().getDeviceName());
        info.setHost(InetAddress.getByName(NetworkUtils.getIPAddress(true)));
        info.setPort(EsUdpServer.get().getPort());
        info.setServiceType(SERVER_TYPE);
        nsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                L.logWF("mdns reg err, " + errorCode);
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                if (L.DEBUG) L.logD("mdns reg success");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        });
    }
}
