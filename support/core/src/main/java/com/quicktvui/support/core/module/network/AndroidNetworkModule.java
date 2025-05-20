package com.quicktvui.support.core.module.network;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.NetworkUtils;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 * 网络
 */
@ESKitAutoRegister
public class AndroidNetworkModule implements IEsModule, IEsInfo, AndroidNetworkManager.NetworkListener {

    private AndroidNetworkManager networkManager;
    private static final String TAG = "AndroidNetworkModule";

    @Override
    public void init(Context context) {
        networkManager = AndroidNetworkManager.getInstance();
        networkManager.init(context);
        networkManager.registerNetworkListener(this);
    }

    /**
     * 网络状态变化
     */
    @Override
    public void onConnectivityChanged(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            Log.e(TAG, "#----网络状态监听---onConnectivityChanged---NOT NULL----->>>>>" + networkInfo);
            EsMap eventMap = networkInfoToEsMap(networkInfo);
            EsProxy.get().sendNativeEventTraceable(this, Events.EVENT_ON_CONNECTIVITY_CHANGED.toString(),
                    eventMap);
        }
        //
        else {
            Log.e(TAG, "#----网络状态监听---onConnectivityChanged---NULL----->>>>>");
            EsMap eventMap = nullNetworkInfoToEsMap();
            EsProxy.get().sendNativeEventTraceable(this, Events.EVENT_ON_CONNECTIVITY_CHANGED.toString(),
                    eventMap);
        }
    }

    /**
     * 获取网络状态
     */
    public void getActiveNetworkInfo(EsPromise promise) {
        NetworkInfo networkInfo = networkManager.getActiveNetworkInfo();
        Log.e(TAG, "#-----网络状态监听--getActiveNetworkInfo-------->>>>>" + networkInfo);
        if (networkInfo != null) {
            EsMap esMap = networkInfoToEsMap(networkInfo);
            promise.resolve(esMap);
        } else {
            EsMap esMap = new EsMap();
            promise.reject(esMap);
        }
    }

    private EsMap networkInfoToEsMap(NetworkInfo networkInfo) {
        EsMap esMap = new EsMap();
        try {
            if (networkInfo != null) {
                esMap.pushInt("type", networkInfo.getType());
                esMap.pushString("typeName", networkInfo.getTypeName());
                esMap.pushInt("subtype", networkInfo.getSubtype());
                esMap.pushInt("state", networkInfo.getState().ordinal());
                esMap.pushString("extraInfo", networkInfo.getExtraInfo());
                esMap.pushBoolean("isAvailable", networkInfo.isAvailable());
                esMap.pushBoolean("isConnected", networkInfo.isConnected());
                esMap.pushBoolean("isConnectedOrConnecting", networkInfo.isConnectedOrConnecting());
                esMap.pushBoolean("isFailover", networkInfo.isFailover());
                esMap.pushBoolean("isRoaming", networkInfo.isRoaming());
                esMap.pushString("extraInfo", networkInfo.getExtraInfo());
                esMap.pushInt("detailedState", networkInfo.getDetailedState().ordinal());
                esMap.pushInt("describeContents", networkInfo.describeContents());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.e(TAG, "#----网络状态监听---networkInfoToEsMap-------->>>>>" + esMap);
        return esMap;
    }

    private EsMap nullNetworkInfoToEsMap() {
        EsMap esMap = new EsMap();
        esMap.pushBoolean("isAvailable", false);
        esMap.pushBoolean("isConnected", false);
        return esMap;
    }

    public void getNetworkType(EsPromise promise) {
        NetworkUtils.NetworkType type = NetworkUtils.NetworkType.NETWORK_UNKNOWN;
        try {
            type = NetworkUtils.getNetworkType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "#---网络状态监听----getNetworkType-------->>>>>" + type);
        promise.resolve(type.getName());
    }

    public void getWifiInfo(EsPromise promise) {
        try {
            Context context = EsProxy.get().getContext();
            WifiManager wm =
                    (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            PromiseHolder.create(promise).put("ssid", wifiInfo.getSSID()).put("strength",
                    WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5)).sendSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            PromiseHolder.create(promise).sendSuccess();
        }
    }

    @Override
    public void destroy() {
        Log.e(TAG, "#---网络状态监听----destroy-------->>>>>");
        if (networkManager != null) {
            networkManager.unregisterNetworkListener(this);
            networkManager.destroy();
        }
    }

    public enum Events {
        EVENT_ON_CONNECTIVITY_CHANGED("onConnectivityChanged");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
            map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }
}
