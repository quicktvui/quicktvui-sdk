package com.quicktvui.sdk.core.udp;

import com.sunrain.toolkit.utils.NetworkUtils;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.net.AvailablePortFinder;
import com.sunrain.toolkit.utils.thread.Executors;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Create by weipeng on 2022/04/14 15:47
 */
public abstract class AbstractUdpServer implements Runnable {

    private DatagramSocket mSocket;
    private boolean mRunning;
    private byte[] mBuffer;
    private int mPortSearchStart;
    protected String mCurrentIp;
    protected int mCurrentPort;

    /** 启动本地UDP服务 **/
    public void start(){
        Executors.get().execute(this);
    }

    public void stop() {
        if (isRunning()) {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (Exception e) {
                    L.logW("udp stop", e);
                }
            }
        }
        mSocket = null;
        mRunning = false;
    }

    public String getIp() {
        return mCurrentIp;
    }

    public int getPort() {
        return mCurrentPort;
    }

    public void send(DatagramPacket packet) throws IOException {
        if(mSocket == null) return;
        mSocket.send(packet);
    }

    public boolean isRunning() {
        return mRunning;
    }

    protected void onCreateUdpServer(UdpServerConfig config){
        mBuffer = new byte[config.getBufferSize()];
        mPortSearchStart = config.getPortSearchStart();
    }

    protected abstract void onReceiveData(DatagramPacket packet) throws Exception;

    @Override
    public void run() {
        try {
            onCreateUdpServer(new UdpServerConfig());
            mCurrentIp = NetworkUtils.getIPAddress(true);
            mCurrentPort = AvailablePortFinder.getNextAvailable(mPortSearchStart);
            mSocket = new DatagramSocket(mCurrentPort);
            mSocket.setReuseAddress(true);
            mRunning = true;
            if (L.DEBUG) L.logD("start server " + mCurrentIp + " " + mCurrentPort);
            while (isRunning()) {
                DatagramPacket packet = new DatagramPacket(mBuffer, mBuffer.length);
                L.logIF("receiving....");
                mSocket.receive(packet);
                if (packet.getLength() <= 0) continue;
                onReceiveData(packet);
            }
        }catch (Exception e){
            L.logW("udp work", e);
        }
    }

    protected String toString(DatagramPacket packet){
        return new String(packet.getData(), 0, packet.getLength());
    }

    protected JSONObject toJson(String data) {
        try {
            return new JSONObject(data);
        } catch (Exception ignored) {
        }
        return null;
    }
}
