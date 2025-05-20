package com.quicktvui.sdk.core.utils;

import com.sunrain.toolkit.utils.SPUtils;

import java.lang.ref.SoftReference;

/**
 * Create by weipeng on 2022/03/01 20:26
 */
public class SpUtils {

    //region Basic

    private static final String NAME_SP = "es_app";

    private static SoftReference<SPUtils> sSp;

    private static SPUtils getSp() {
        SPUtils sp;
        if (sSp == null || (sp = sSp.get()) == null) {
            sp = SPUtils.getInstance(NAME_SP);
            sSp = new SoftReference<>(sp);
        }
        return sp;
    }

    private static final String SP_KEY_CID = "cid";
    private static final String SP_DEFAULT_STRING = "";
    private static final int SP_DEFAULT_INT = 0;

    /**
     * 保存CID
     **/
    public static void saveClientId(String value) {
        getSp().put(SP_KEY_CID, value);
    }

    /**
     * 获取CID
     **/
    public static String getClientId() {
        return getSp().getString(SP_KEY_CID, SP_DEFAULT_STRING);
    }

    //endregion

    //region 埋点

    private static final String SP_KEY_LAUNCH_TIME = "s_time";

    /**
     * 保存上次使用时间
     **/
    public static void saveLastStartTime() {
        getSp().put(SP_KEY_LAUNCH_TIME, System.currentTimeMillis());
    }

    /**
     * 获取上次使用时间
     **/
    public static long getLastStartTime() {
        return getSp().getLong(SP_KEY_LAUNCH_TIME, 0L);
    }


    //endregion

    //region 用户协议

    private static final String SP_KEY_PRIVACY_POLICY_PKG = "pp_pkg";
    private static final String SP_KEY_PRIVACY_POLICY_SHOW = "pp_show";

    /**
     * 保存同意用户协议
     **/
    public static void savePrivacyPolicy(String pkg, boolean agree) {
        getSp().put(SP_KEY_PRIVACY_POLICY_PKG, pkg);
        getSp().put(SP_KEY_PRIVACY_POLICY_SHOW, agree);
    }

    /**
     * 获取用户是否同意隐私协议
     **/
    public static boolean getPrivacyPolicyShow() {
        return getSp().getBoolean(SP_KEY_PRIVACY_POLICY_SHOW, false);
    }

    /**
     * 获取用户同意隐私协议的包名
     **/
    public static String getPrivacyPolicyShowPackage() {
        return getSp().getString(SP_KEY_PRIVACY_POLICY_PKG, null);
    }

    //endregion

    //region So

    private static final String SP_KEY_SO_PATH = "so_path_#";
    private static final String SP_KEY_SO_DOWNLOAD_TIME = "so_download_time_#";

    public static String getSoPath(String tag) {
        return getSp().getString(SP_KEY_SO_PATH.replace("#", tag), SP_DEFAULT_STRING);
    }

    public static void saveSoPath(String tag, String path) {
        getSp().put(SP_KEY_SO_PATH.replace("#", tag), path);
    }

    public static long getSoDownloadTime(String tag) {
        return getSp().getLong(SP_KEY_SO_DOWNLOAD_TIME.replace("#", tag), SP_DEFAULT_INT);
    }

    public static void saveSoDownloadTime(String tag, long time) {
        getSp().put(SP_KEY_SO_DOWNLOAD_TIME.replace("#", tag), time);
    }

    //endregion

    //region runtime

    private static final String SP_KEY_RUNTIME_VERSION = "rt_ver_#";
    private static final String SP_KEY_RUNTIME_PATH = "rt_path_#";
    private static final String SP_KEY_RUNTIME_LAST_UPDATE_TIME = "rt_update_time_#";

    /**
     * 缓存runtime版本
     **/
    public static void saveRuntimeVersion(String tag, float version) {
        getSp().put(SP_KEY_RUNTIME_VERSION.replace("#", tag), version);
    }

    /**
     * 获取本地缓存runtime版本
     **/
    public static float getRuntimeVersion(String tag) {
        return getSp().getFloat(SP_KEY_RUNTIME_VERSION.replace("#", tag), SP_DEFAULT_INT);
    }

    /**
     * 缓存runtime路径
     **/
    public static void saveRuntimePath(String tag, String path) {
        getSp().put(SP_KEY_RUNTIME_PATH.replace("#", tag), path);
    }

    /**
     * 获取本地缓存runtime路径
     **/
    public static String getRuntimePath(String tag) {
        return getSp().getString(SP_KEY_RUNTIME_PATH.replace("#", tag), SP_DEFAULT_STRING);
    }

    /**
     * 缓存runtime更新时间
     **/
    public static void saveRuntimeUpdateTime(String tag, long time) {
        getSp().put(SP_KEY_RUNTIME_LAST_UPDATE_TIME.replace("#", tag), time);
    }

    /**
     * 获取本地缓存runtime更新时间
     **/
    public static long getRuntimeUpdateTime(String tag) {
        return getSp().getLong(SP_KEY_RUNTIME_LAST_UPDATE_TIME.replace("#", tag), SP_DEFAULT_INT);
    }

    /**
     * 存储rpk更新弹窗标记
     */
    public static void saveRpkUpdateTag(String packageName, String versionName) {
        getSp().put(packageName, versionName);
    }

    /**
     * 获取rpk更新弹窗标记
     */
    public static String getRpkUpdateTag(String packageName) {
        return getSp().getString(packageName, SP_DEFAULT_STRING);
    }

    //endregion

}
