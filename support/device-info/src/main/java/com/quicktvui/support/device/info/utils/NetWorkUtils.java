package com.quicktvui.support.device.info.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NetWorkUtils {

    private static final String TAG = "NetWorkUtils";
    private static final int WIFI_FREQUENCY_2G = 1;
    private static final int WIFI_FREQUENCY_4G = 2;
    private static final int WIFI_FREQUENCY_5G = 3;
    private static final int WIFI_FREQUENCY_6G = 4;

    /**
     * 判断是否有网络连接，并不代表可以数据访问
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * 数据流量是否打开
     *
     * @param context
     * @return
     */
    public static boolean isMobileEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断当前网络是否可以数据访问(测试不可靠)
     *
     * @param context
     * @return
     */
    public static boolean isNetSystemUsable(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("ping -c 3 www.baidu.com");
                return 0 == process.waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否是 WIFI 网络
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    /**
     * 判断当前网络详细类型
     *
     * @param context
     * @return
     */
    public static String getNetWorkType(Context context) {
        if (!isNetworkConnected(context)) {
            return "NONE";
        }
        if (isWifi(context)) {
            return "WIFI";
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "IDEN";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO_0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO_A";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO_B";
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "EHRPD";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPAP";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                case TelephonyManager.NETWORK_TYPE_NR:
                    return "5G";
                default:
                    break;
            }
        }
        return "NONE";
    }

    /**
     * 网络信息
     *
     * @param context
     * @return
     */
    public static void getNetWorkInfo(Context context, List<Pair<String, String>> list) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            list.add(new Pair<>("NET Type Name", networkInfo.getTypeName()));
            String subName = networkInfo.getSubtypeName();
            if (!TextUtils.isEmpty(subName)) {
                list.add(new Pair<>("NET SUB NAME", subName));
            }
            list.add(new Pair<>("NET NAME", networkInfo.getExtraInfo()));
        }
    }

    public static String getDnsServer() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = systemProperties.getMethod("get", String.class);

            // 获取默认 DNS 服务器地址
            String dns1 = (String) getMethod.invoke(null, "net.dns1");
            String dns2 = (String) getMethod.invoke(null, "net.dns2");
            String result = "DNS1: " + dns1 + ", DNS2: " + dns2;
            String resultValue = dns1 + "," + dns2;
//            Log.d("test", "getDnsServer: ------------->" + result);
//            Log.d("test", "getDnsServer: ------------->" + resultValue);
            return resultValue;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getDnsServer:无法获取dns");
            return "";
        }
    }

    public static String getDnsByConnectivityManager(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) return "";

            LinkProperties linkProperties = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    linkProperties = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork());
                }
            }
            if (linkProperties == null) return "";

            List<InetAddress> dnsServers = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dnsServers = linkProperties.getDnsServers();
            }
            StringBuilder dnsList = new StringBuilder();
            for (InetAddress dns : dnsServers) {
                dnsList.append(dns.getHostAddress()).append(",");
            }
            return dnsList.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /*public static String is5GSupported(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "WiFi 不可用";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int wifiStandard = wifiInfo.getWifiStandard();

            switch (wifiStandard) {
                case WifiInfo.WIFI_STANDARD_LEGACY:
                    return "WiFi 3 或更低（802.11a/b/g）";
                case WifiInfo.WIFI_STANDARD_11N:
                    return "WiFi 4（802.11n）";
                case WifiInfo.WIFI_STANDARD_11AC:
                    return "WiFi 5（802.11ac）";
                case WifiInfo.WIFI_STANDARD_11AX:
                    return "WiFi 6（802.11ax）";
                case WifiInfo.WIFI_STANDARD_11BE:
                    return "WiFi 7（802.11be）";
                default:
                    return "未知 WiFi 标准";
            }
        } else {
            return "仅支持 Android 11+ 获取 WiFi 版本";
        }
    }*/

    public static int getWiFiFrequency(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "WiFi 不可用");
            return -1;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int frequency = 0; // 获取 WiFi 频率（MHz）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //大于5.0
            frequency = wifiInfo.getFrequency();
            if (frequency < 2400) {
//                Log.d(TAG, "getWiFiFrequency: ------------>" + "2.4GHz 频段（可能是 WiFi 4 或更低）");
                return WIFI_FREQUENCY_2G;
            } else if (frequency >= 2400 && frequency < 2500) {
//                Log.d(TAG, "getWiFiFrequency: ------------>" + "2.4GHz 频段（可能是 WiFi 4 或更低）");
                return WIFI_FREQUENCY_4G;
            } else if (frequency >= 4900 && frequency < 5900) {
//                Log.d(TAG, "getWiFiFrequency: ------------>" + "5GHz 频段（可能是 WiFi 5 或 WiFi 6）");
                return WIFI_FREQUENCY_5G;
            } else if (frequency >= 5900 && frequency < 7100) {
//                Log.d(TAG, "getWiFiFrequency: ------------>" + "6GHz 频段（WiFi 6E 或 WiFi 7）");
                return WIFI_FREQUENCY_6G;
            } else {
//                Log.d(TAG, "getWiFiFrequency: ------------>" + "未知 WiFi 频段");
                return -1;
            }
        } else {
            String connectedSSID = wifiInfo.getSSID(); // 当前连接的 WiFi SSID（可能需要处理引号）
            // 获取所有可见的 WiFi 列表
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult result : scanResults) {
                if (result.SSID.equals(connectedSSID)) { // 匹配当前连接的 WiFi
                    frequency = result.frequency;
                    if (frequency < 2400) {
//                        Log.d(TAG, "getWiFiFrequency: ------------>" + "2.4GHz 频段（可能是 WiFi 4 或更低）");
                        return WIFI_FREQUENCY_2G;
                    } else if (frequency >= 2400 && frequency < 2500) {
//                        Log.d(TAG, "getWiFiFrequency: ------------>" + "2.4GHz 频段（可能是 WiFi 4 或更低）");
                        return WIFI_FREQUENCY_4G;
                    } else if (frequency >= 4900 && frequency < 5900) {
//                        Log.d(TAG, "getWiFiFrequency: ------------>" + "5GHz 频段（可能是 WiFi 5 或 WiFi 6）");
                        return WIFI_FREQUENCY_5G;
                    } else if (frequency >= 5900 && frequency < 7100) {
//                        Log.d(TAG, "getWiFiFrequency: ------------>" + "6GHz 频段（WiFi 6E 或 WiFi 7）");
                        return WIFI_FREQUENCY_6G;
                    } else {
//                        Log.d(TAG, "getWiFiFrequency: ------------>" + "未知 WiFi 频段");
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    // 定义回调接口
    public interface SpeedCallBack {
        void onTaskCompleted(String speed);
    }

    public static void getWifiSpeed(SpeedCallBack callback) {
        long initialRxBytes = TrafficStats.getTotalRxBytes();
        long initialTxBytes = TrafficStats.getTotalTxBytes();
        long initialTime = System.currentTimeMillis();

        /*Log.d("yuyang", "initialRxBytes:总下行速率 " + initialRxBytes);
        Log.d("yuyang", "initialRxBytes:总上行速率 " + initialTxBytes);
        Log.d("yuyang", "initialTime:当前时间戳" + initialTime);*/


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 延迟执行的操作
                long finalRxBytes = TrafficStats.getTotalRxBytes();//finalRxBytes/1000/1000*8/1000
                long finalTxBytes = TrafficStats.getTotalTxBytes();
                long finalTime = System.currentTimeMillis();

                /*Log.d("yuyang", "1s延迟后 initialRxBytes:总下行速率 " + initialRxBytes);
                Log.d("yuyang", "1s延迟后 initialRxBytes:总上行速率 " + initialTxBytes);
                Log.d("yuyang", "1s延迟后 initialTime:当前时间戳" + initialTime);*/
                long rxBytes = finalRxBytes - initialRxBytes;
                long txBytes = finalTxBytes - initialTxBytes;
                long timeInterval = finalTime - initialTime;

                /*Log.d("yuyang", "1s延迟后差值 rxBytes:总下行速率 " + rxBytes);
                Log.d("yuyang", "1s延迟后差值 txBytes:总上行速率 " + txBytes);
                Log.d("yuyang", "1s延迟后差值 timeInterval:两次时间戳绝对值" + timeInterval);*/
                // 计算网速并显示在界面上
                double downloadSpeed = FormatTools.formatNetSpeedMB(rxBytes * 8, timeInterval);//真实下载
                double uploadSpeed = FormatTools.formatNetSpeedMB(txBytes * 8, timeInterval);//真实上传
                /*double downloadSpeed = (rxBytes * 8) / (timeInterval / 1000.0); // 下载速度，单位：bps
                double uploadSpeed = (txBytes * 8) / (timeInterval / 1000.0); // 上传速度，单位：bps*/

                /*Log.d("yuyang", "1s延迟后差值 downloadSpeed 下载速率 " + downloadSpeed);
                Log.d("yuyang", "1s延迟后差值 uploadSpeed:上传速率 " + uploadSpeed);*/

                double value;
                if (downloadSpeed > uploadSpeed) {
                    value = downloadSpeed * 8;
                } else {
                    value = uploadSpeed * 8;
                }
                if (value > 0) {
                    int speed = (int) value;
                    //Log.d("yuyang", "1s延迟后差值带宽速率*8 带宽  " + speed);
                    callback.onTaskCompleted(String.valueOf(speed));
                } else {
                    callback.onTaskCompleted("");
                }
            }
        }, 1000); // 延迟 1 秒
    }

    /*public static String getWifiSpeed(Context context) {
        WifiInfo wifiInfo = GatewayUtils.getWifiInfo(context);
        if (wifiInfo != null) {
            if (wifiInfo.getLinkSpeed() < 0) { //有线
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    Network network = cm.getActiveNetwork();
                    if (network != null) {
                        NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                        if (nc != null) {
                            int downloadSpeed = nc.getLinkDownstreamBandwidthKbps();
                            int uploadSpeed = nc.getLinkUpstreamBandwidthKbps();
                            Log.d("NetSpeed", "Down: " + downloadSpeed + " Kbps, Up: " + uploadSpeed + " Kbps");
                            if (downloadSpeed / 1000 > 0) {
                                return downloadSpeed / 1000 + " Mbps";
                            }
                        }
                    }
                }
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    int networkType = telephonyManager.getDataNetworkType();
                    int estimatedSpeed = getMobileNetworkSpeed(networkType);
                    if (estimatedSpeed > 0) {
                        return estimatedSpeed + " Mbps";
                    }
                }
            } else { //无线
                return wifiInfo.getLinkSpeed() + " Mbps";
            }
        }
        return "0Mbps";
    }*/

    public static String getWiredRate(Context context) {
        WifiInfo wifiInfo = GatewayUtils.getWifiInfo(context);
        if (wifiInfo != null) {
            if (wifiInfo.getLinkSpeed() < 0) { //有线
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    Network network = cm.getActiveNetwork();
                    if (network != null) {
                        NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                        if (nc != null) {
                            int downloadSpeed = nc.getLinkDownstreamBandwidthKbps();
                            int uploadSpeed = nc.getLinkUpstreamBandwidthKbps();
//                            Log.d("yuyang", "Down: " + downloadSpeed + " Kbps, Up: " + uploadSpeed + " Kbps");
                            if (downloadSpeed / 1000 > 0) {
                                return downloadSpeed / 1000 + " Mbps";
                            }
                        }
                    }
                } else {
                    try {
                        File file = new File("/sys/class/net/eth0/speed");
                        if (file.exists()) {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String value = br.readLine();
                            br.close();
//                            Log.d("yuyang", "speed: ------------------>" + value);
                            if (!TextUtils.isEmpty(value)) {
                                return value + " Mbps";
                            } else {
                                return "";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "100 Mbps";
    }

    public static String getWirelessRate(Context context) {
        WifiInfo wifiInfo = GatewayUtils.getWifiInfo(context);
        if (wifiInfo != null) {
            if (wifiInfo.getLinkSpeed() > 0) { //无线
                return wifiInfo.getLinkSpeed() + " Mbps";
            }
        }
        return "";
    }

    private static int getMobileNetworkSpeed(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_LTE: // 4G
                return 100000; // 100 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP: // 3G+
                return 21000; // 21 Mbps
            case TelephonyManager.NETWORK_TYPE_EDGE: // 2G
                return 236; // 236 Kbps
            default:
                return -1;
        }
    }

    //private static final String IP_SERVICE_URL = "https://api.ipify.org";
    //private static final String IP_SERVICE_URL = "https://www.ip138.com/";
    private static final String IP_SERVICE_URL = "http://checkip.amazonaws.com/";
    private static final String IP_SERVICE_URL2 = "https://www.baidu.com/";

    // 使用 OkHttp 获取外网IP
    public static void getExternalIPWithOkHttp(final IPCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(IP_SERVICE_URL).build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String ip = response.body().string();
                        if (ip.contains("\n")) {
                            ip = ip.replace("\n", "");
                        }
                        if (callback != null) {
                            callback.onResult(ip); // 回调返回IP
                        }
                    } else {
                        if (callback != null) {
                            callback.onResult(null);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "获取外网回调失败: ------>" + e.getMessage());
                    if (callback != null) {
                        callback.onResult(null);
                    }
                }
            }
        }).start();
    }

    // 回调接口，用于接收IP结果
    public interface IPCallback {
        void onResult(String value);

        default void onError() {
        }
    }


    public static void checkHttpsInterface(final IPCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(IP_SERVICE_URL2).build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // 支持 HTTPS
                        callback.onResult("HTTPS");
//                        Log.d("yuyang", "Supports HTTPS");
                    } else {
                        callback.onResult("");
                    }
                } catch (Exception e) {
                    // 出现异常，说明可能不支持 HTTPS
                    Log.e("error", e.getMessage());
                    callback.onError();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getNetworkProtocol() {
        /*Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Log.d(TAG, "getNetworkProtocol: ------------>" + provider.getName());
        }*/
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        if (sslSocketFactory != null) {
            // 支持 SSL/TLS 协议
            Log.d("yuyang", "Supports SSL,TLS");
            return "SSL,TLS";
        } else {
            // 不支持 SSL/TLS 协议
            Log.d("yuyang", "not Supports SSL,TLS");
        }
        return "";
    }

    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X:", b));
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
