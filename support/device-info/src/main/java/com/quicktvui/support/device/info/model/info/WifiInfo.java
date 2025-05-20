package com.quicktvui.support.device.info.model.info;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Pair;

import com.quicktvui.support.device.info.R;

import java.util.ArrayList;
import java.util.List;


public class WifiInfo {

    //    @AddTrace(name = "WifiInfo.getWifiInfo")
    public static List<Pair<String, String>> getWifiInfo(Context context) {
        boolean enable = false;
        List<Pair<String, String>> list = new ArrayList<>();
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager != null) {
            // 依赖 ACCESS_FINE_LOCATION、ACCESS_WIFI_STATE 权限，需要 WIFI 打开，TODO Android Q 之后 WiFi 扫描获取 BSSID 为随机生成
            List<ScanResult> scanResults = manager.getScanResults();
            enable = manager.isWifiEnabled();
            if (scanResults != null && !scanResults.isEmpty()) {
                for (ScanResult scanResult : scanResults) {
                    list.add(new Pair<>("SSID", scanResult.SSID));
                    list.add(new Pair<>("BSSID", scanResult.BSSID));
                    list.add(new Pair<>("Capabilities", scanResult.capabilities));
                    list.add(new Pair<>("Frequency", scanResult.frequency + ""));
                    list.add(new Pair<>("Level", scanResult.level + ""));
                    list.add(new Pair<>("", ""));
                }
            }
            try {
                // 依赖 CHANGE_WIFI_STATE 权限
                manager.startScan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (list.isEmpty()) {
            if (enable) {
                list.add(new Pair<>("WIFI Scan", context.getString(R.string.usb_not_found)));
            } else {
                list.add(new Pair<>("WIFI Scan", "Please turn on WiFi switch"));
            }
        }
        return list;
    }

}
