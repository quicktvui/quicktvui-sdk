package com.quicktvui.support.core.module.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * 网络状态变化监听
 */
public class AndroidNetworkReceiver extends BroadcastReceiver {

    private static final String ACTION_NETWORK_CONNECTED = "eskit.sdk.action.NETWORK_CONNECTED";
    private static final String ACTION_NETWORK_DISCONNECTED = "eskit.sdk.action.NETWORK_DISCONNECTED";

    private static final String TAG = AndroidNetworkReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "#----网络状态监听---onReceive-------->>>>>" + intent.getAction());

        //网络变化
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            Log.e(TAG, "#----网络状态监听---CONNECTIVITY_ACTION-------->>>>>");
            try {
                AndroidNetworkManager.getInstance().onConnectivityChanged(context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        //模拟联网
        else if (ACTION_NETWORK_CONNECTED.equals(intent.getAction())) {
            Log.e(TAG, "#----网络状态监听---ACTION_NETWORK_CONNECTED-------->>>>>");
            try {
                AndroidNetworkManager.getInstance().mockNetworkConnected(context);
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        //模拟断网
        else if (ACTION_NETWORK_DISCONNECTED.equals(intent.getAction())) {
            Log.e(TAG, "#----网络状态监听---ACTION_NETWORK_DISCONNECTED-------->>>>>");
            try {
                AndroidNetworkManager.getInstance().mockNetworkDisconnected(context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
