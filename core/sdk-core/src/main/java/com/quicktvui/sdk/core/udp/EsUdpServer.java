package com.quicktvui.sdk.core.udp;

import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.CMD_CUSTOM;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.CMD_EVENT;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.CMD_PING;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.CMD_SEARCH;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_THIRD_TYPE;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.PROTOCOL_VERSION;

import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.protocol.EsProtocolDispatcher;
import com.quicktvui.sdk.core.protocol.Protocol_2;
import com.sunrain.toolkit.utils.AppUtils;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.thread.Executors;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Create by weipeng on 2022/04/18 10:47
 * Describe SDK内置UDP服务
 * 主要用于接收DLNA/mDNS发来的消息
 */

public class EsUdpServer extends AbstractUdpServer implements IEsRemoteEventCallback {

    private InetAddress mThirdClientAddress;
    private int mThirdClientPort;
    private boolean mIsNeedWarpContent;

    @Override
    protected void onCreateUdpServer(UdpServerConfig config) {
        config.setPortSearchStart(Constants.SERVER_PORT_UDP);
        super.onCreateUdpServer(config);
    }

    @Override
    protected void onReceiveData(DatagramPacket packet) throws Exception {
        if (L.DEBUG) L.logD("receive event from local udp");
        String json = toString(packet);
        JSONObject jo = toJson(json);
        if (jo == null) return;
        int version = jo.optInt("version", 0);
        if (version > 0) {
            L.logIF("protocol ver: " + version);
        }
        if (jo.has(K_THIRD_TYPE)) { // From 微信的消息
            int type = jo.getInt(K_THIRD_TYPE);
            if (L.DEBUG) L.logD("type:" + type);
            switch (type) {
                case CMD_PING: {
                    JSONObject data = new JSONObject();
                    data.put(K_THIRD_TYPE, 0);
                    data.put("pkg", EsContext.get().getContext().getPackageName());
                    byte[] bytes = data.toString().getBytes("UTF-8");
                    EsUdpServer.get().send(new DatagramPacket(bytes, bytes.length, packet.getAddress(), packet.getPort()));
                }
                break;
                case CMD_SEARCH:
                    String id = EsContext.get().getClientId();
                    String name = EsContext.get().getDeviceName();
                    JSONObject data = new JSONObject();
                    data.put("id", id);
                    data.put("name", name);
                    data.put("sdk_ver_name", EsProxy.get().getEsKitVersionName());
                    data.put("sdk_ver_code", EsProxy.get().getEsKitVersionCode());
                    data.put("version", PROTOCOL_VERSION);
                    data.put("pkg", EsContext.get().getContext().getPackageName());
                    data.put("host_ver_name", AppUtils.getAppVersionName());
                    data.put("host_ver_code", AppUtils.getAppVersionCode());
                    data.put("running", Protocol_2.getRunningInfo());
                    data.put("dongle", Protocol_2.getDongleInfo());
                    jo.put("data", data);
                    byte[] bytes = jo.toString().getBytes("UTF-8");
                    EsUdpServer.get().send(new DatagramPacket(bytes, bytes.length, packet.getAddress(), packet.getPort()));
                    break;
                case CMD_EVENT:
                case CMD_CUSTOM:
                    mIsNeedWarpContent = true;
                    mThirdClientAddress = packet.getAddress();
                    mThirdClientPort = packet.getPort();
                    EsMap from = new EsMap();
                    from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_REMOTE);
                    from.pushObject(Constants.Event.ES_REFERER1, Constants.Event.FROM_UDP);
                    EsProtocolDispatcher.tryDispatcher(from, jo.getJSONObject("data"), this);
                    break;
                default:
                    break;
            }
            return;
        }
        mIsNeedWarpContent = false;
        mThirdClientAddress = packet.getAddress();
        mThirdClientPort = packet.getPort();
        EsMap from = new EsMap();
        from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_REMOTE);
        from.pushObject(Constants.Event.ES_REFERER1, Constants.Event.FROM_UDP);
        EsProtocolDispatcher.tryDispatcher(from, json, this);
    }

    public void replaceIpAndPort(String ip, int port) {
        try {
            mThirdClientAddress = Inet4Address.getByName(ip);
            mThirdClientPort = port;
            mIsNeedWarpContent = true;
            // 告诉三方我的地址
            tellThirdEsAddress();
        } catch (UnknownHostException e) {
            L.logW("replace ip", e);
        }
    }

    private void tellThirdEsAddress() {
        if (L.DEBUG) L.logD("tellThirdAddress");
        onReceiveEvent(EsProtocolDispatcher.K_THIRD_UPDATE, "");
    }

    @Override
    public void onReceiveEvent(String eventName, String eventData) {
        Executors.get().execute(() -> sendToRemoteAsync(eventName, eventData));
    }

    private void sendToRemoteAsync(String eventName, String eventData) {
        try {
            JSONObject data = new JSONObject();
            data.put("action", eventName);
            data.put("args", eventData);

            if (L.DEBUG) L.logD("send data:" + "\nneedWrapContent: " + mIsNeedWarpContent
                    + "\naddress: " + mThirdClientAddress
                    + "\nport: " + mThirdClientPort
                    + "\naction: " + eventName
                    + "\nargs: " + eventData
            );

            byte[] bytes;
            if (mIsNeedWarpContent) {
                JSONObject wrapper = new JSONObject();
                wrapper.put("type", CMD_EVENT);
                wrapper.put("data", data);
                bytes = wrapper.toString().getBytes("UTF-8");
            } else {
                bytes = data.toString().getBytes("UTF-8");
            }
            send(new DatagramPacket(bytes, bytes.length, mThirdClientAddress, mThirdClientPort));
        } catch (Exception e) {
            L.logW("send to remote", e);
        }
    }


    //region 单例

    private static final class EsUdpServerHolder {
        private static final EsUdpServer INSTANCE = new EsUdpServer();
    }

    public static EsUdpServer get() {
        return EsUdpServerHolder.INSTANCE;
    }

    private EsUdpServer() {
    }

    //endregion


}
