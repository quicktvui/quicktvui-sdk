package com.quicktvui.support.core.nativeevent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.quicktvui.sdk.base.PromiseHolder;

/**
 * Create by weipeng on 2021/12/09 11:46
 *
 * @see InnerEventRunnable#run()
 *
 * return {
 *     connect,
 *     available,
 *     type,
 *     name,
 *     strength
 * }
 *
 * connect      是否连接  boolean  true/false
 * available    是否可用  boolean  true/false
 * type         连接方式  string   unknown 未知   wifi 无线网   eth 网线    mobile 蜂窝
 * name         网络名称  string   仅wifi有
 * strength     信号强度  int      5格强度 (0-4)
 *
 */

public final class NetworkChangeObserver extends BaseChangeObserver {

    private Handler mHandler;

    @Override
    protected void startObserver() {
        mHandler = new Handler();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mContext.registerReceiver(this, filter);
    }

    @Override
    protected void triggerIfNeed() {
    }

    @Override
    protected void onObserverChange(Context context, Intent intent) {

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new InnerEventRunnable(context, this), 500);

    }

    @Override
    protected void stopObserver() {
        if(mHandler != null) mHandler.removeCallbacksAndMessages(null);
        if(mContext != null) mContext.unregisterReceiver(this);
        mHandler = null;
    }

    private static final class InnerEventRunnable implements Runnable{

        private Context context;
        private BaseChangeObserver observer;

        public InnerEventRunnable(Context context, BaseChangeObserver observer) {
            this.context = context;
            this.observer = observer;
        }

        @Override
        public void run() {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean hasNetwork = activeNetwork != null;
            PromiseHolder result = PromiseHolder.create();
            result.put("connect", hasNetwork && activeNetwork.isConnected());
            result.put("available", hasNetwork && activeNetwork.isAvailable());
            if (hasNetwork) {
                int type = activeNetwork.getType();
                switch (type) {
                    case ConnectivityManager.TYPE_WIFI:
                        result.put("type", "wifi");
                        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wm.getConnectionInfo();
                        result.put("name", wifiInfo.getSSID());
                        result.put("strength", WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5));
                        break;
                    case ConnectivityManager.TYPE_ETHERNET:
                        result.put("type", "eth");
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        result.put("type", "mobile");
                        break;
                    default:
                        result.put("type", "unknown");
                        break;
                }
            }

            observer.notifyData(result.getData());

            context = null;
            observer = null;
        }
    }

    //region 单例

    private static final class NetworkObserverHolder {
        private static final NetworkChangeObserver INSTANCE = new NetworkChangeObserver();
    }

    public static NetworkChangeObserver get() {
        return NetworkObserverHolder.INSTANCE;
    }

    private NetworkChangeObserver() {
    }

    //endregion

}