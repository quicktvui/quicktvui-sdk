package com.quicktvui.sdk.core.engine;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.base.ui.ESBaseConfigManager;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.core.adapter.DefaultHippyLogAdapter;
import com.quicktvui.sdk.core.adapter.EsEngineExceptionHandlerAdapter;
import com.quicktvui.sdk.core.adapter.EsImageLoaderAdapter;
import com.quicktvui.sdk.core.adapter.EsSoLoaderAdapter;
import com.quicktvui.sdk.core.adapter.HttpsAdapter;
import com.quicktvui.sdk.core.adapter.ProxyHttpAdapter;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsConfigManager;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.internal.loader.IRpkLoader;
import com.quicktvui.sdk.core.internal.loader.RpkInfo;
import com.quicktvui.sdk.core.pm.InnerProvider;
import com.quicktvui.sdk.core.utils.CommonUtils;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.HippyAPIProvider;
import com.tencent.mtt.hippy.HippyEngine;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.adapter.http.HippyHttpAdapter;
import com.tencent.mtt.hippy.modules.javascriptmodules.EventDispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <br>
 * 对HippyEngine的包装
 * <br>
 * <br>
 * Created by WeiPeng on 2023-09-18 10:59
 */
public class EsEngine {

    public static final int TYPE_DEBUG = 0;
    public static final int TYPE_APP = 1;
    public static final int TYPE_CARD = 2;

    public static final String ROOT_COMPONENT_NAME = "EsApp";
    public static final String CODE_CACHE_TAG = "common";

    private final int mEngineType;
    private HippyEngine mInstance;

    public EsEngine(int engineType) {
        mEngineType = engineType;
    }

    public int getEngineType() {
        return mEngineType;
    }

    public HippyEngine getInstance() {
        return mInstance;
    }

//    public void preloadModule(String filePath, HippyEngine.ModuleListener listener) {
//        mInstance.preloadModule(new HippyFileBundleLoader(filePath), listener);
//    }

    public HippyRootView loadModule(Context context, IRpkLoader loader, HippyEngine.ModuleListener listener) {
        EsData appData = loader.getAppData();
        RpkInfo rpkInfo = loader.getRpkInfo();
        HippyEngine.ModuleLoadParams loadParams = new HippyEngine.ModuleLoadParams();
        loadParams.context = context;
        loadParams.componentName = ROOT_COMPONENT_NAME;
        loadParams.jsFilePath = new File(rpkInfo.getCodeDir(), Constants.FILE_JS_INDEX).getAbsolutePath();
        loadParams.esBaseConfigManager = createConfigManager(rpkInfo.getRpkConfig());
        loadParams.baseBorderDrawableProvider = EsContext.get().getBorderDrawableProvider();
        if (appData.getArgs() != null) {
            loadParams.jsParams = MapperUtils.esMap2HpMap(appData.getArgs());
        }

        // TODO 自定义Context
//        ESInstanceContext instanceContext = new ESInstanceContext(context);
//        instanceContext.setModuleParams(loadParams);
//        loadParams.hippyContext = instanceContext;

        return mInstance.loadModule(loadParams, listener);
    }

    private ESBaseConfigManager createConfigManager(EsMap configData) {
        EsConfigManager configManager = new EsConfigManager();
        if(configData == null) return configManager;
        if (EsContext.get().getEsBaseConfigManager() != null) {
            //此处为runtime配置了默认的packageJson解析，那么将先使用package.json，没配置的属性则使用default
//            Log.v("EsConfigManager", "runtime配置了默认的packageJson解析---------");
            configManager.setShakeSelf(EsContext.get().getEsBaseConfigManager().IsShakeSelf());
            configManager.setListShakeSelf(EsContext.get().getEsBaseConfigManager().IsListShakeSelf());
            configManager.setFocusBorderType(EsContext.get().getEsBaseConfigManager().getFocusBorderType());
            configManager.doConfigs(configData.toString());
        } else {
            //说明runtime没有配置默认的packageJson解析，那么就使用package.json
//            Log.v("EsConfigManager", "没有配置默认的packageJson解析---------");
            configManager.doConfigs(configData.toString());
        }
        return configManager;
    }

    void init(HippyEngine.EngineListener listener) throws Exception {
        HippyEngine.EngineInitParams initParams = getInitParams();
        onBeforeCreateEngine(initParams);

        mInstance = HippyEngine.create(initParams);
        onAfterCreateEngine(mInstance);

        mInstance.initEngine(listener);
        onAfterInitEngine(mInstance);
    }

    private HippyEngine.EngineInitParams getInitParams() {
        HippyEngine.EngineInitParams initParams = new HippyEngine.EngineInitParams();
        initParams.context = EsContext.get().getContext().getApplicationContext();
        initParams.imageLoader = new EsImageLoaderAdapter(EsContext.get().getImageLoader());
        initParams.httpAdapter = createHttpAdapter();
        if (CommonUtils.isEnableDynamicSo()) {
            initParams.soLoader = new EsSoLoaderAdapter();
        }
        initParams.logAdapter = new DefaultHippyLogAdapter();
        initParams.enableLog = L.DEBUG;
        initParams.exceptionHandler = new EsEngineExceptionHandlerAdapter();
//      TODO 设置预加载类库  initParams.jsPreloadFilePath
        registerProviders(initParams);
        return initParams;
    }

    private void registerProviders(HippyEngine.EngineInitParams initParams) {
        if (initParams.providers == null) {
            initParams.providers = new ArrayList<>();
        }
        initParams.providers.add(new InnerProvider());
        List<Object> providerList = InitConfig.getDefault().getApiProviderList();
        if (providerList != null) {
            for (Object p : providerList) {
                initParams.providers.add((HippyAPIProvider) p);
            }
        }
    }

    /**
     * 网络代理
     **/
    private HippyHttpAdapter createHttpAdapter() {
        String proxyHostName = InitConfig.getDefault().getProxyHostName();
        int proxyPort = InitConfig.getDefault().getProxyPort();
        if (!TextUtils.isEmpty(proxyHostName) && proxyPort != 0) {
            return new ProxyHttpAdapter(proxyHostName, proxyPort);
        }
        return new HttpsAdapter();
    }

    /**
     * 可提前修改启动参数
     **/
    protected void onBeforeCreateEngine(HippyEngine.EngineInitParams params) throws Exception {

    }

    /**
     * 可提前注册事件
     **/
    protected void onAfterCreateEngine(HippyEngine engine) {

    }

    protected void onAfterInitEngine(HippyEngine engine) {

    }

    public void sendUIEvent(int tagId, String eventName, Object params) throws Exception {
        if (mInstance != null) {
            HippyEngineContext engineContext = mInstance.getEngineContext();
            if (engineContext != null) {
                if (L.DEBUG)
                    L.logI(engineContext.getEngineId() + " sendUIEvent tagId: " + tagId + ", eventName: " + eventName + ", params: " + params);
                engineContext.getModuleManager().getJavaScriptModule(EventDispatcher.class).receiveUIComponentEvent(tagId, eventName, MapperUtils.tryMapperEsData2HpData(params));
            }
        }
    }

    public void sendNativeEvent(String eventName, Object params) throws Exception {
        if (mInstance != null) {
            HippyEngineContext engineContext = mInstance.getEngineContext();
            if (engineContext != null) {
                if (L.DEBUG)
                    L.logI(engineContext.getEngineId() + " sendNativeEvent eventName: " + eventName + ", params: " + params);
                engineContext.getModuleManager().getJavaScriptModule(EventDispatcher.class).receiveNativeEvent(eventName, MapperUtils.tryMapperEsData2HpData(params));
            }
        }
    }

    public void destroy() {
        ESEvents.off(mInstance.getId());
        mInstance.addRestartListener(null);
        mInstance.destroyEngine();
        mInstance = null;
    }

    public interface OnHippyEngineRestartListener {
        void onHippyEngineRestart(HippyEngine engine);
    }
}
