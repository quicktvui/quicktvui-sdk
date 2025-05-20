package com.quicktvui.sdk.core;

import com.quicktvui.sdk.core.adapter.DefaultRpkLoaderAdapter;
import com.quicktvui.sdk.core.callback.EsAppLifeCallbackImpl;
import com.quicktvui.sdk.core.ext.loadproxy.IEsRpkLoadProxy;
import com.sunrain.toolkit.bolts.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by weipeng on 2022/04/25 21:00
 * Describe
 */
public class InitConfig {

    public static final int FLAG_ENABLE_UDP = 1;                    // 开启UDP
    public static final int FLAG_ENABLE_WS = 1 << 1;                // 开启WebSocket
    public static final int FLAG_DYNAMIC_SO = 1 << 2;               // 自动下载so
    public static final int FLAG_SKIP_COUNT_ACTIVE = 1 << 3;        // 跳过报活埋点
    public static final int FLAG_WATCH_CRASH = 1 << 4;              // 检测崩溃
    public static final int FLAG_API_ENC = 1 << 5;                  // 接口加密
    public static final int FLAG_DYNAMIC_SO_INJECT = 1 << 6;        // 采用Inject方式加载so
    public static final int FLAG_CHECK_UPGRADE = 1 << 7;            // 宿主升级检测
    public static final int FLAG_DISABLE_ANALYSIS = 1 << 8;         // 屏蔽
    public static final int FLAG_NO_EXIT_ON_CLICK_HOME = 1 << 9;    // Home键后关闭应用
    public static final int FLAG_SAVE_LOG = 1 << 10;                // 保存log

    private String appId;                        // 后台申请的ID
    private String channel;                      // SDK渠道
    private boolean debug = false;               // 打印全部log
    private String customServer;                 // 自定义API接口: 切换测试环境或私有化部署
    private String customTrackServer;            // 自定义埋点接口: 切换测试环境或私有化部署
    private String proxyHostName;                // 自定义代理域名
    private int proxyPort;                       // 自定义代理端口
    private int flags;                           // SDK flags
    private String deviceName;                   // 自定义设备名称
    private List<Object> apiProviderList;        // 原生ApiProvider列表
    private String deviceId;                     // 自定义DeviceID
    private EsKitInitCallback sdkInitCallback;
    private EsAppLifeCallbackImpl esAppLifeCallback;

    private List<Interceptor<EsData>> sdkInitInterceptorList;
    private List<Interceptor<EsData>> appStartInterceptorList;

    private IEsRpkLoadProxy mEsRpkLoadProxy;

    private String mRepository;
    private String mBcCode; // 牌照code

    private DefaultRpkLoaderAdapter mRpkLoaderAdapter;

    private InitConfig() {
        // 默认开启UDP和WS
        setFlags(FLAG_API_ENC);
    }

    private static final InitConfig DEFAULT = new InitConfig();

    public static InitConfig getDefault() {
        return DEFAULT;
    }

    /**
     * 官网申请的appId
     **/
    public InitConfig setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getAppId() {
        return this.appId;
    }

    /**
     * 使用的渠道
     **/
    public InitConfig setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public String getChannel() {
        return this.channel;
    }

    /**
     * 自定义服务器地址
     **/
    public InitConfig setCustomServer(String server) {
        this.customServer = server;
        return this;
    }

    public String getCustomServer() {
        return customServer;
    }

    public String getCustomTrackServer() {
        return customTrackServer;
    }

    public InitConfig setCustomTrackServer(String server) {
        this.customTrackServer = server;
        return this;
    }

    public InitConfig setProxyHostName(String proxyHostName) {
        this.proxyHostName = proxyHostName;
        return this;
    }

    public String getProxyHostName() {
        return proxyHostName;
    }

    public InitConfig setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * 是否显示全部debug信息。为提高性能，建议正式发版为false
     **/
    public InitConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean getDebug() {
        return this.debug;
    }

    /**
     * 设置flags
     **/
    public InitConfig setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public InitConfig addFlags(int flags) {
        this.flags |= flags;
        return this;
    }

    public InitConfig removeFlags(int flags) {
        this.flags &= ~flags;
        return this;
    }

    public int getFlags() {
        return flags;
    }

    public InitConfig setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public InitConfig addApiProvider(Object provider) {
        if (apiProviderList == null) apiProviderList = new ArrayList<>();
        apiProviderList.add(provider);
        return this;
    }

    public List<Object> getApiProviderList() {
        return apiProviderList;
    }

    public InitConfig setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public InitConfig setSdkInitCallback(EsKitInitCallback callback) {
        this.sdkInitCallback = callback;
        return this;
    }

    public EsKitInitCallback getSdkInitCallback() {
        return sdkInitCallback;
    }

    public InitConfig setAppLifeCallback(EsAppLifeCallbackImpl callback) {
        this.esAppLifeCallback = callback;
        return this;
    }

    public EsAppLifeCallbackImpl getAppLifeCallback() {
        return esAppLifeCallback;
    }

    public IEsRpkLoadProxy getEsRpkLoadProxy() {
        return mEsRpkLoadProxy;
    }

    public InitConfig setEsRpkLoadProxy(IEsRpkLoadProxy proxy) {
        this.mEsRpkLoadProxy = proxy;
        return this;
    }

    public String repository() {
        return mRepository;
    }

    public InitConfig setRepository(String repository) {
        this.mRepository = repository;
        return this;
    }

    public DefaultRpkLoaderAdapter getRpkLoaderAdapter() {
        if (mRpkLoaderAdapter == null) {
            mRpkLoaderAdapter = new DefaultRpkLoaderAdapter();
        }
        return mRpkLoaderAdapter;
    }

    public InitConfig setRpkLoaderAdapter(DefaultRpkLoaderAdapter adapter) {
        this.mRpkLoaderAdapter = adapter;
        return this;
    }

    //region 拦截器

    public List<Interceptor<EsData>> getAppStartInterceptorList() {
        return appStartInterceptorList;
    }

    public InitConfig addAppStartInterceptor(Interceptor<EsData> interceptor) {
        if (appStartInterceptorList == null) {
            appStartInterceptorList = new ArrayList<>(5);
        }
        appStartInterceptorList.add(interceptor);
        return this;
    }

    public InitConfig removeAppStartInterceptor(Interceptor<EsData> interceptor) {
        if (appStartInterceptorList != null) {
            appStartInterceptorList.remove(interceptor);
        }
        return this;
    }

    public List<Interceptor<EsData>> getSdkInitInterceptorList() {
        return sdkInitInterceptorList;
    }

    public InitConfig addSdkInitInterceptor(Interceptor<EsData> interceptor) {
        if (sdkInitInterceptorList == null) {
            sdkInitInterceptorList = new ArrayList<>(5);
        }
        sdkInitInterceptorList.add(interceptor);
        return this;
    }

    public InitConfig removeSdkInitInterceptor(Interceptor<EsData> interceptor) {
        if (sdkInitInterceptorList != null) {
            sdkInitInterceptorList.remove(interceptor);
        }
        return this;
    }

    public String getBcCode() {
        return mBcCode;
    }

    public InitConfig setBcCode(String bcCode) {
        this.mBcCode = bcCode;
        return this;
    }

    //endregion
}
