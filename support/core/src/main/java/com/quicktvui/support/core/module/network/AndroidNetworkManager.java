package com.quicktvui.support.core.module.network;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class AndroidNetworkManager {

    private static final String TAG = "AndroidNetworkManager";

    private static AndroidNetworkManager instance;

    private Context context;
    protected List<NetworkListener> listenerList = Collections.synchronizedList(new ArrayList<>());
    private ConnectivityManager connectivityManager;

    private AndroidNetworkReceiver networkReceiver = new AndroidNetworkReceiver();

    private boolean isReceiverRegistered;

    private AndroidNetworkManager() {
    }

    public static AndroidNetworkManager getInstance() {
        synchronized (AndroidNetworkManager.class) {
            if (instance == null) {
                instance = new AndroidNetworkManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            if (!isReceiverRegistered) {

                Log.e(TAG, "-------网络状态监听----------注册监听开始--------->>>>");

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(CONNECTIVITY_ACTION);
                intentFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
                intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
                intentFilter.addAction("eskit.sdk.action.NETWORK_CONNECTED");
                intentFilter.addAction("eskit.sdk.action.NETWORK_DISCONNECTED");
                this.context.registerReceiver(networkReceiver, intentFilter);
                isReceiverRegistered = true;

                Log.e(TAG, "-------网络状态监听----------注册监听结束------>>>>");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            if (networkReceiver != null) {
                Log.e(TAG, "-------网络状态监听------destroy----取消注册监听------->>>>");
                context.unregisterReceiver(networkReceiver);
                isReceiverRegistered = false;
            }
        } catch (Throwable e) {
            L.logWF("" + e.getMessage());
            if (L.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public void onConnectivityChanged(Context context) {
        try {
            if (connectivityManager == null) {
                this.connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            NetworkInfo networkInfo = getActiveNetworkInfo();
            Log.e(TAG, "-------网络状态监听------onConnectivityChanged-------->>>>" + networkInfo);
            notifyConnectivityChanged(networkInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void mockNetworkConnected(Context context) {
        try {
            NetworkInfo networkInfo = getActiveNetworkInfo();
            Log.e(TAG, "-------网络状态监听------mockNetworkConnected-------->>>>" + networkInfo);
            notifyConnectivityChanged(networkInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void mockNetworkDisconnected(Context context) {
        Log.e(TAG, "-------网络状态监听------mockNetworkDisconnected-------->>>>");
        notifyConnectivityChanged(null);
    }

    public NetworkInfo getActiveNetworkInfo() {
        try {
            if (connectivityManager == null) {
                return null;
            }
            return connectivityManager.getActiveNetworkInfo();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerNetworkListener(NetworkListener listener) {
        Log.e(TAG, "-------网络状态监听------registerNetworkListener--------->>>>" + listener);
        try {
            if (listener != null && !listenerList.contains(listener)) {
                listenerList.add(listener);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void unregisterNetworkListener(NetworkListener listener) {
        Log.e(TAG, "-------网络状态监听------unregisterNetworkListener--------->>>>" + listener);
        try {
            if (listener != null) {
                listenerList.remove(listener);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void notifyConnectivityChanged(NetworkInfo networkInfo) {
        if (listenerList != null && listenerList.size() > 0) {
            for (NetworkListener listener : listenerList) {
                try {
                    listener.onConnectivityChanged(networkInfo);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface NetworkListener {
        void onConnectivityChanged(NetworkInfo networkInfo);
    }
}
