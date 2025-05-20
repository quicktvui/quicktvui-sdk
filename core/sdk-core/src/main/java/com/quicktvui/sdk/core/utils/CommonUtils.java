package com.quicktvui.sdk.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.core.internal.Constants;
import com.sunrain.toolkit.utils.NetworkUtils;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;

/**
 *
 */
public class CommonUtils {

    public static boolean hasNetworkByPing() {
//        String[] targets = new String[]{InitConfig.getDefault().getProxyHostName(), UrlConstants.getBaseUrl()};
        String[] targets = new String[]{InitConfig.getDefault().getProxyHostName()};
        for (String target : targets) {
            if (TextUtils.isEmpty(target)) continue;
            String host = Uri.parse(target).getHost();
            int count = 5;
            while (count > 0) {
                L.logIF("ping " + host + " " + count);
                if (NetworkUtils.isAvailableByPing(host)) return true;
                count--;
            }
        }
        return false;
    }

    /**
     * 检查设备是否连接了Wi-Fi或网线
     * @param context
     * @return
     */
    public static boolean isConnectedToWifiOrEthernet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network == null) {
                    return false;
                }

                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // 适配老版本
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return networkInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                            networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET;
                }
            }
        }
        return false;
    }

    public static boolean isContainsFlag(int flag) {
        return isContainsFlag(InitConfig.getDefault().getFlags(), flag);
    }

    public static boolean isContainsFlag(int flags, int flag) {
        return (flags & flag) == flag;
    }

    /** 是否开启UDP **/
    public static boolean isEnableUdp(){
        return isContainsFlag(InitConfig.FLAG_ENABLE_UDP);
    }

    /** 是否开启websocket **/
    public static boolean isEnableWebSocket(){
        return isContainsFlag(InitConfig.FLAG_ENABLE_WS);
    }

    /** 是否开启动态So **/
    public static boolean isEnableDynamicSo(){
        return isContainsFlag(InitConfig.FLAG_DYNAMIC_SO);
    }

    /** 是否开启崩溃监控 **/
    public static boolean isEnableCrashWatch(){
        return isContainsFlag(InitConfig.FLAG_WATCH_CRASH);
    }

    /** 是否开启接口加密 **/
    public static boolean isEnableApiEnc() {
        return isContainsFlag(InitConfig.FLAG_API_ENC);
    }

    /** 动态SO注入 **/
    public static boolean isEnableDynamicSoInject() {
        return isContainsFlag(InitConfig.FLAG_DYNAMIC_SO_INJECT);
    }

    /** 开启宿主升级检测 **/
    public static boolean isEnableCheckUpgrade() {
        return isContainsFlag(InitConfig.FLAG_CHECK_UPGRADE);
    }

    /** 是否跳过报活 **/
    public static boolean isSkipActiveCountEvent(){
        return isContainsFlag(InitConfig.FLAG_SKIP_COUNT_ACTIVE);
    }

    /** 是否关闭了所有埋点 **/
    public static boolean isDisableAnalysis(){
        return isContainsFlag(InitConfig.FLAG_DISABLE_ANALYSIS);
    }

    /** 当按下home键的时候不退出应用 **/
    public static boolean isNotExitAppOnClickHomeKey(){
        return isContainsFlag(InitConfig.FLAG_NO_EXIT_ON_CLICK_HOME);
    }

    /** 是否开启XLog **/
    public static boolean isEnableXLog() {
        return isContainsFlag(InitConfig.FLAG_SAVE_LOG);
    }

    /** 获取真实意图的nexus源 **/
    public static String getRepositoryHost(EsData data) {
        String repository = data.getRepository();
        if(!TextUtils.isEmpty(repository)) return repository;
        InitConfig cfg = InitConfig.getDefault();
        return cfg == null ? null : cfg.repository();
    }

    public static boolean checkIsSafeFile(File file){
        if(Constants.PATH_APP_FILES.equals(file.getName())) return true;
        if(Constants.PATH_RPK_MD5.equals(file.getName())) return true;
        return false;
    }

}
