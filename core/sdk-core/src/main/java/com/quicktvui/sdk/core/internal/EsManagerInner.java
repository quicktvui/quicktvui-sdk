package com.quicktvui.sdk.core.internal;

import static com.quicktvui.sdk.core.utils.CommonUtils.isEnableUdp;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.quicktvui.base.ui.ESBaseConfigManager;
import com.quicktvui.base.ui.graphic.BaseBorderDrawable;
import com.quicktvui.base.ui.graphic.BaseBorderDrawableProvider;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.image.IEsImageLoader;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.EsKitInitCallback;
import com.quicktvui.sdk.core.EsKitInitCallbackProxy;
import com.quicktvui.sdk.core.EsKitStatus;
import com.quicktvui.sdk.core.IEsManager;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.core.tookit.ESToolkitManager;
import com.quicktvui.sdk.core.udp.EsUdpServer;
import com.quicktvui.sdk.core.utils.AutoUtils;
import com.quicktvui.sdk.core.utils.ESExecutors;
import com.quicktvui.sdk.core.utils.EskitLazyInitHelper;
import com.quicktvui.sdk.core.utils.PluginUtils;
import com.sunrain.toolkit.bolts.interceptor.Interceptor;
import com.sunrain.toolkit.bolts.tasks.Task;
import com.sunrain.toolkit.bolts.tasks.TaskCompletionSource;
import com.sunrain.toolkit.utils.Utils;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.utils.ContextHolder;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.utils.UIThreadUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.quicktvui.support.imageloader.EsGlideImageLoader;

/**
 * Create by weipeng on 2022/03/01 13:39
 */
public final class EsManagerInner implements IEsManager,ComponentCallbacks2 {

    private EsKitStatus mSdkStatus = EsKitStatus.STATUS_IDLE;

    private volatile boolean mInitOnce = true;

    //region 对外提供的方法

    @Override
    public void init(Application context, InitConfig config) {
        if (context == null) throw new RuntimeException("context is null");
        if (config == null) throw new RuntimeException("config is null");
        mSdkStatus = EsKitStatus.STATUS_INIT;
//        TimeCheckUtil init = TimeCheckUtil.getOrCreate("CheckInit");
        config.setSdkInitCallback(new EsKitInitCallbackProxy(config.getSdkInitCallback()) {
            @Override
            public void onEsKitInitSuccess() {
                AutoUtils.init(context);
                // 自动组件注册
                AutoUtils.autoRegister();
                // 自动初始化
                AutoUtils.autoInit(context, config);

                EsContext.get().setImageLoader(new EsGlideImageLoader());

                super.onEsKitInitSuccess();
            }
        });
        initCommon(context);
//        init.printLog("common");
        ESExecutors.START_THREAD.execute(this::asyncInit);
//        init.end();
    }

    @Override
    public EsKitStatus getSdkInitStatus() {
        return mSdkStatus;
    }

    @Override
    public void setImageLoader(IEsImageLoader imageLoader) {
        EsContext.get().setImageLoader(imageLoader);
    }

    @Override
    public void registerComponent(String... classNames) {
        EsComponentManager.get().registerComponents(classNames);
    }

    @Override
    public void registerModule(String... classNames) {
        EsComponentManager.get().registerModules(classNames);
    }

    @Override
    public void start(EsData data) {
        EskitLazyInitHelper.initIfNeed();
        if (!isSdkInit()) return;
        if (!isEsDataValid(data)) return;
        ESExecutors.START_THREAD.execute(() -> asyncStartApp(data));
    }

    @Override
    public void load(Context context, EsData data, IEsAppLoadCallback callback) {
        PluginUtils.assertIsInstanceOfActivity(context);
        EskitLazyInitHelper.initIfNeed();
        if (!isSdkInit()) return;
        if (!isEsDataValid(data)) return;
        ESExecutors.START_THREAD.execute(() -> asyncLoadAppToActivity(context, data, callback));
    }

    @Override
    public void load(Context context, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        PluginUtils.assertIsInstanceOfFragmentActivity(context);
        EskitLazyInitHelper.initIfNeed();
        if (!isSdkInit()) return;
        if (!isEsDataValid(data)) return;
        ESExecutors.START_THREAD.execute(() -> asyncLoadAppToFragmentActivity(context, containerLayoutId, data, callback));
    }

    @Override
    public void loadV2(FragmentActivity activity, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        EskitLazyInitHelper.initIfNeed();
        if (!isSdkInit()) return;
        if (!isEsDataValid(data)) return;
        ESExecutors.START_THREAD.execute(() -> asyncLoadAppToFragmentActivityV2(activity, containerLayoutId, data, callback));
    }

    private boolean isSdkInit() {
        if (mSdkStatus == EsKitStatus.STATUS_IDLE) {
            L.logEF("need call init: EsManager.init()");
            return false;
        }
        return true;
    }

    /**
     * 检测EsData参数有效性
     **/
    private boolean isEsDataValid(EsData data) {
        if (data == null || TextUtils.isEmpty(data.getEsPackage())) {
            L.logEF("data is NULL or pkg is empty");
            return false;
        }
        return true;
    }

    @Override
    public boolean isEsRunning() {
        EsViewManager vm = EsViewManager.get();
        return vm != null && vm.isEsRunning();
    }

    @Override
    public boolean isEsRunning(String pkg) {
        EsViewManager vm = EsViewManager.get();
        return vm != null && vm.isEsRunning(pkg);
    }

    @Override
    public void setBorderDrawableProvider(BaseBorderDrawableProvider<ConcurrentHashMap<Integer, BaseBorderDrawable>> borderDrawableProvider) {
        EsContext.get().setBorderDrawableProvider(borderDrawableProvider);
    }

    @Override
    public void setESBaseConfigManager(ESBaseConfigManager esBaseConfigManager) {
        EsContext.get().setEsBaseConfigManager(esBaseConfigManager);
    }

    //endregion

    //region 合规初始化

    /**
     * 初始化基础库，要保证合规
     **/
    private void initCommon(Application context) {
        // 工具类
        Utils.init(context);
        // LOG
        initLog(context);
        ContextHolder.initAppContext(context);
        EsContext.get().init(context);
        // 调试工具(焦点配置等)
        ESToolkitManager.get().init(context);

        EsProxyImpl proxy = new EsProxyImpl();
        ((EsProxy) EsProxy.get()).setProxy(proxy);
    }

    private void initLog(Context context) {
        Log.d("debug", "init log");
        L.init(InitConfig.getDefault().getDebug());
        LogUtils.enableDebugLog(L.DEBUG);
    }

    //endregion

    //region 异步初始化

    /**
     * 异步初始化
     **/
    private void asyncInit() {
        InitConfig config = InitConfig.getDefault();
        // 执行SDK初始化之前的拦截器
        if (!dispatchInterceptors(null, config.getSdkInitInterceptorList())) {
            L.logWF("sdk init block");
            mSdkStatus = EsKitStatus.STATUS_BLOCK;
            callbackInitError(Constants.INIT.ERROR_BLOCK, config);
            return;
        }

        callbackInitStart(config);

//        TimeCheckUtil timeCheck = TimeCheckUtil.getOrCreate("InitManagers");

        if (TextUtils.isEmpty(EsContext.get().getClientId())) {
            mSdkStatus = EsKitStatus.STATUS_ERROR;
            callbackInitError(Constants.INIT.ERROR_CID, config);
            return;
        }

        if (mInitOnce) {
            mInitOnce = false;

            ((EsProxyImpl) ((EsProxy) EsProxy.get()).getProxy()).startEsCacheCleaner();
//            timeCheck.printLog("proxy");
            registerLowMemoryCallback();
//            timeCheck.printLog("reg callback");
            startUdp();
//            timeCheck.printLog("udp");
        }

        callbackInitSuccess(config);
//        timeCheck.printLog("callback success");

//        timeCheck.end();

    }

    @Override
    public void relieveImageSize(boolean flag) {
        EsContext.get().setRelieveImageSize(flag);
    }


    /**
     * 执行拦截器
     **/
    private boolean dispatchInterceptors(EsData data, List<Interceptor<EsData>> list) {
        if (list != null && list.size() > 0) {
            for (Interceptor<EsData> interceptor : list) {
                TaskCompletionSource<Boolean> ts = new TaskCompletionSource<>();
                try {
                    L.logIF("interceptor exec");
                    interceptor.intercept(data, new Interceptor.Chain(ts));
                    Task<Boolean> t = ts.getTask();
                    t.waitForCompletion();
                    if (!t.getResult()) {
                        L.logIF("interceptor block");
                        return false;
                    }
                } catch (Throwable e) {
                    L.logWF("interceptor exception", e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 启动udp服务
     **/
    private static void startUdp() {
        if (isEnableUdp()) {
            L.logIF("enable udp");
            try {
                EsUdpServer.get().start();
            } catch (Throwable e) {
                L.logW("udp", e);
            }
        }
    }

    //endregion

    //region SDK初始化回调

    /**
     * 回调开始初始化
     **/
    private void callbackInitStart(InitConfig config) {
        EsKitInitCallback initCallback = config.getSdkInitCallback();
        if (initCallback != null) {
            try {
                initCallback.onEsKitInitStart();
            } catch (Exception e) {
                L.logWF("callback init start", e);
            }
        }
    }

    /**
     * 回调初始化成功
     **/
    private void callbackInitSuccess(InitConfig config) {
        mSdkStatus = EsKitStatus.STATUS_SUCCESS;
        EsKitInitCallback initCallback = config.getSdkInitCallback();
        if (initCallback != null) {
            try {
                initCallback.onEsKitInitSuccess();
            } catch (Exception e) {
                L.logWF("callback init success", e);
            }
        }
    }

    /**
     * 回调初始化失败
     **/
    private void callbackInitError(int code, InitConfig config) {
        EsKitInitCallback initCallback = config.getSdkInitCallback();
        if (initCallback != null) {
            try {
                initCallback.onEsKitInitError(code);
            } catch (Exception e) {
                L.logWF("callback init error", e);
            }
        }
    }

    //endregion

    //region 内存检测

    /**
     * 注册内存监听
     **/
    private void registerLowMemoryCallback() {
        Utils.getApp().registerComponentCallbacks(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

    }

    //    static final int TRIM_MEMORY_COMPLETE = 80;             // 嗨！系统内存比较低了哟~
    //    static final int TRIM_MEMORY_MODERATE = 60;             // 嗯？系统内存进一步降低，我们在暗杀名单的中间位置
    //    static final int TRIM_MEMORY_BACKGROUND = 40;           // 咦？系统内存已经很低了，杀手已经开始准备武器了！
    //    static final int TRIM_MEMORY_UI_HIDDEN = 20;            // 哟~ 界面不可见了，是不是考虑预防下杀手？
    //    static final int TRIM_MEMORY_RUNNING_CRITICAL = 15;     // 啊！系统已经派出了杀手，我看到好几个同伴被杀！
    //    static final int TRIM_MEMORY_RUNNING_LOW = 10;          // 擦！系统的杀手对我进行了警告！！！
    //    static final int TRIM_MEMORY_RUNNING_MODERATE = 5;      // 哔！我随时可能被干掉！！！！！！！
    @Override
    public void onTrimMemory(int level) {
        if (L.DEBUG) L.logD("onTrimMemory:" + level);
        EsProxy.get().sendNativeEventAll(
                Constants.GLOBAL_EVENT.EVT_MEMORY_LEVEL_CHANGE, level);
        if (level <= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            clearMemory();
        }
    }

    @Override
    public void onLowMemory() {
        EsProxy.get().sendNativeEventAll(
                Constants.GLOBAL_EVENT.EVT_MEMORY_LOW, "");
        clearMemory();
    }

    private void clearMemory() {
        if (L.DEBUG) L.logW("clearMemoryIfNeed");
    }

    //endregion

    //region 启动快应用

    /**
     * 异步启动
     **/
    private void asyncStartApp(EsData data) {
//        if (!isSdkInitSuccess()) return;
        if (!isCanStartApp(data)) return;
        EsViewManager.get().start(data);
        L.logIF("start end");
    }

    /**
     * 异步加载到Activity
     **/
    private void asyncLoadAppToActivity(Context context, EsData data, IEsAppLoadCallback callback) {
        if (!isSdkInitSuccess()) return;
        if (!isCanStartApp(data)) return;
        UIThreadUtils.runOnUiThread(() -> EsViewManager.get().load(context, 0, data, callback));
        L.logIF("load end");
    }

    /**
     * 异步加载到Activity
     **/
    private void asyncLoadAppToFragmentActivity(Context context, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        if (!isSdkInitSuccess()) return;
        if (!isCanStartApp(data)) return;
        UIThreadUtils.runOnUiThread(() -> EsViewManager.get().load(context, containerLayoutId, data, callback));
        L.logIF("load end");
    }

    private void asyncLoadAppToFragmentActivityV2(FragmentActivity activity, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        if (!isSdkInitSuccess()) return;
        if (!isCanStartApp(data)) return;
        UIThreadUtils.runOnUiThread(() -> EsViewManager.get().loadV2(activity, containerLayoutId, data, callback));
        L.logIF("load end");
    }

    /**
     * 检测SDK是否初始化成功，内部会重新执行一次初始化
     **/
    private boolean isSdkInitSuccess() {
        if (mSdkStatus != EsKitStatus.STATUS_SUCCESS) {
            L.logIF("sdk init retry");
            // 重新初始化
            asyncInit();
        }
        if (mSdkStatus != EsKitStatus.STATUS_SUCCESS) {
            L.logEF("sdk init not success");
            return false;
        }
        return true;
    }

    /**
     * 检测启动是否被拦截
     **/
    private boolean isCanStartApp(EsData data) {
        InitConfig config = InitConfig.getDefault();
        if (!dispatchInterceptors(data, config.getAppStartInterceptorList())) {
            L.logWF("start block");
            return false;
        }
        beforeStartApp(data);
        return true;
    }

    private void beforeStartApp(EsData data) {
        L.logIF("VER: " + EsProxy.get().getEsKitVersionCode());
        L.logDF("start app:" + data);
    }

    //endregion


}
