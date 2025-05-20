package com.quicktvui.support.device.info.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.quicktvui.support.device.info.R;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GatewayUtils {

    /**
     * 获取内网 IPv6
     *
     * @param name
     * @return
     */
    public static String getHostIpv6(String name) {

        String hostIp = "";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if ((ia instanceof Inet6Address) && TextUtils.equals(name, ni.getName()) && !ia.isLoopbackAddress()) {
                        String address = ia.getHostAddress().toLowerCase();
                        if (address.indexOf('%') > -1) {
                            address = address.substring(0, address.indexOf('%'));
                        }
                        hostIp = address;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostIp;

    }

    /**
     * 获取 ipv4 地址
     *
     * @return
     */
    public static Map<String, String> getIp(Context context) {

        Map<String, String> map = new HashMap<>();
        try {
            boolean vpn = isVpn(context);
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();//ia.toString().contains("wlan0")
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                boolean isWlan0 = ni.getName().contains("wlan0");
                Log.d("test", "getIp: ------------->"+ni.getName());
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    if (vpn) {
                        // vpn ip
                        if (!ia.isLoopbackAddress() && !ia.isSiteLocalAddress()) {
                            map.put("vpn", TextUtils.isEmpty(ia.getHostAddress()) ? Constants.UNKNOWN : ia.getHostAddress());
                        } else if (!ia.isLoopbackAddress() && ia.isSiteLocalAddress()) {
                            map.put("en0", TextUtils.isEmpty(ia.getHostAddress()) ? Constants.UNKNOWN : ia.getHostAddress());
                            map.put("network_name", TextUtils.isEmpty(ni.getName()) ? Constants.UNKNOWN : ni.getName());
                        }
                    } else {
                        // vpn 关闭，数据流量
                        if (!ia.isLoopbackAddress() && ia.isSiteLocalAddress() && !isGatewayIp(ia.getHostAddress())) {
                            if (isWlan0){
                                map.put("wlan0",TextUtils.isEmpty(ia.getHostAddress()) ? Constants.UNKNOWN : ia.getHostAddress());
                                map.put("wlan0_ipv6", TextUtils.isEmpty(ni.getName()) ? Constants.UNKNOWN : ni.getName());
                            }
                            map.put("en0", TextUtils.isEmpty(ia.getHostAddress()) ? Constants.UNKNOWN : ia.getHostAddress());
                            map.put("network_name", TextUtils.isEmpty(ni.getName()) ? Constants.UNKNOWN : ni.getName());
                            // 内网 ipv6
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;

    }

    public static boolean isGatewayIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }
        // 检查是否是合法的 IPv4 地址
        if (!ipAddress.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            return false;
        }
//        return ipAddress.matches("^\\d+\\.\\d+\\.\\d+\\.1$"); // 匹配 xxx.xxx.xxx.1
        String[] parts = ipAddress.split("\\.");
        int firstOctet = Integer.parseInt(parts[0]);
        int secondOctet = Integer.parseInt(parts[1]);
        int thirdOctet = Integer.parseInt(parts[2]);
        int fourthOctet = Integer.parseInt(parts[3]);

        // 仅检测私有 IP 地址范围
        boolean isPrivateIp = (firstOctet == 10) || // 10.0.0.0 - 10.255.255.255
                (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) || // 172.16.0.0 - 172.31.255.255
                (firstOctet == 192 && secondOctet == 168); // 192.168.0.0 - 192.168.255.255

        return isPrivateIp && (fourthOctet == 1); // 末尾是 .1
    }


    /**
     * 获取代理信息
     *
     * @param context
     * @param list
     * @return
     */
    public static void getProxyInfo(Context context, List<Pair<String, String>> list) {

        String proxyAddress = System.getProperty("http.proxyHost");
        String port = System.getProperty("http.proxyPort");
        int proxyPort = Integer.parseInt((port != null ? port : "-1"));
        boolean isProxy = (!TextUtils.isEmpty(proxyAddress) && (proxyPort != -1));
        try {
            if (isVpn(context)) {
                list.add(new Pair<>(context.getString(R.string.net_proxy), "vpn"));
            } else if (isProxy) {
                list.add(new Pair<>(context.getString(R.string.net_proxy), "proxy"));
                list.add(new Pair<>(context.getString(R.string.net_proxy_host), proxyAddress));
                list.add(new Pair<>(context.getString(R.string.net_proxy_port), port));
            } else {
                list.add(new Pair<>(context.getString(R.string.net_proxy), "false"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean isVpn(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(17);
            return networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getMacAddress(Context context) {

//        AndroidDeviceManager deviceManager = AndroidDeviceManager.getInstance();
//        String ethMac = deviceManager.getAndroidDevice().getEthMac();
//        String wifiMac = deviceManager.getAndroidDevice().getWifiMac();
        String macAddress = Constants.UNKNOWN;
        WifiInfo wifiInfo = getWifiInfo(context);
        if (wifiInfo != null) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (EsProxy.get().checkSelfPermission(EsProxy.get().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EsProxy.get().getContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
                if (ContextCompat.checkSelfPermission(EsProxy.get().getTopActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Objects.requireNonNull(EsProxy.get().getTopActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return "";
                } else {
                    macAddress = wifiInfo.getMacAddress();
                }
            } else {
                macAddress = getMac();
            }*/
            macAddress = getMac();
        }
        return macAddress;
    }

    /**
     * 通过网卡获取 Mac
     *
     * @return
     */
    private static String getMac() {

        String mac = Constants.UNKNOWN;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                if ("wlan0".equals(ni.getName())) {
                    byte[] hardwareAddress = ni.getHardwareAddress();
                    if (hardwareAddress == null || hardwareAddress.length == 0) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hardwareAddress) {
                        sb.append(String.format("%02X:", b));
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;

    }

    /**
     * 拿到bssid
     *
     * @param context
     * @return
     */
    public static String getBssid(Context context) {
        String macAddress = Constants.UNKNOWN;
        WifiInfo wifiInfo = getWifiInfo(context);
        if (wifiInfo != null) {
            String bssid = wifiInfo.getBSSID();
            if (TextUtils.isEmpty(bssid)) {
                macAddress = Constants.UNKNOWN;
            } else {
                macAddress = bssid;
            }
        }
        return macAddress;
    }

    /**
     * 获取广播 ID
     *
     * @param context
     * @return
     */
    public static String getSsid(Context context) {
//        String ssid = Constants.UNKNOWN;
        String ssid = "";
        WifiInfo wifiInfo = getWifiInfo(context);
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID().replace("\"", "");
            if (ssid.contains("unknown")) {//可能隐藏了ssid 无法获取
                ssid = "";
                WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (manager != null) {
                    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        WifiInfo wifi = manager.getConnectionInfo();
                        String wifiSSID = wifi.getSSID();
                        if (!TextUtils.isEmpty(wifiSSID) && wifiSSID.contains("unknown")){
                            return "";
                        }
//                        Log.d("yuyang", "wifiSSID--------"+wifiSSID);
                        return wifiSSID.replace("\"", "");
                    }

                    // 依赖 ACCESS_FINE_LOCATION、ACCESS_WIFI_STATE 权限，需要 WIFI 打开，TODO Android Q 之后 WiFi 扫描获取 BSSID 为随机生成
                    List<ScanResult> scanResults = manager.getScanResults();
                    if (scanResults != null && !scanResults.isEmpty()) {
                        for (ScanResult scanResult : scanResults) {
                            ssid = scanResult.SSID;
                            break;
                        }
                    }
                    try {
                        // 依赖 CHANGE_WIFI_STATE 权限
                        manager.startScan();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ssid;//<unknown ssid>
    }

    /**
     * 获取WifiInfo
     *
     * @param context
     * @return
     */
    public static WifiInfo getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo();
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public static Pair<Integer, Integer> getMobileSignal(Context context) {
        int dbm = -1;
        int level = 0;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            List<CellInfo> cellInfoList;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (tm == null) {
                    return new Pair<>(dbm, level);
                }
                cellInfoList = tm.getAllCellInfo();
                if (null != cellInfoList) {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo instanceof CellInfoGsm) {
                            CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthGsm.getDbm();
                            level = cellSignalStrengthGsm.getLevel();
                        } else if (cellInfo instanceof CellInfoCdma) {
                            CellSignalStrengthCdma cellSignalStrengthCdma =
                                    ((CellInfoCdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthCdma.getDbm();
                            level = cellSignalStrengthCdma.getLevel();
                        } else if (cellInfo instanceof CellInfoLte) {
                            CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthLte.getDbm();
                            level = cellSignalStrengthLte.getLevel();

                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            if (cellInfo instanceof CellInfoWcdma) {
                                CellSignalStrengthWcdma cellSignalStrengthWcdma =
                                        ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                                dbm = cellSignalStrengthWcdma.getDbm();
                                level = cellSignalStrengthWcdma.getLevel();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(dbm, level);
    }

}
