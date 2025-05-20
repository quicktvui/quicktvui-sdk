package com.quicktvui.sdk.core.internal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.IDiskCacheManager;
import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.IEsTraceable;
import com.quicktvui.sdk.base.ISoManager;
import com.quicktvui.sdk.base.ITakeOverKeyEventListener;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.IEsProxy;
import com.quicktvui.sdk.base.display.IDisplayManager;
import com.quicktvui.sdk.base.model.ScreenInfo;
import com.quicktvui.sdk.base.model.ThirdEvent;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.core.adapter.EsImageLoaderAdapter;
import com.quicktvui.sdk.core.display.ESDisplayManager;
import com.quicktvui.sdk.core.protocol.EsProtocolDispatcher;
import com.quicktvui.sdk.core.udp.EsUdpServer;
import com.quicktvui.sdk.core.ui.BrowserBaseActivity;
import com.quicktvui.sdk.core.utils.CommonUtils;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.adapter.image.HippyDrawable;
import com.tencent.mtt.hippy.adapter.image.HippyImageLoader;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.ExtendUtil;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



/**
 * 操作接口
 * <p>
 * Create by weipeng on 2022/03/02 15:32
 */
public class EsProxyImpl implements IEsProxy {

    private SoftReference<IDiskCacheManager> mDiskManagerCache;
    private SoftReference<IDisplayManager> mDisplayManagerCache;

    private SoftReference<HippyImageLoader> mGlobalImageLoaderCache;

    /**
     * 启动缓存清理任务
     **/
    public void startEsCacheCleaner() {
        IDiskCacheManager manager = getDiskCacheManager();
        if (manager instanceof EsDiskCacheManager) {
            ((EsDiskCacheManager) manager).startAutoClearTask();
        }
    }

    @Nullable
    @Override
    public Activity getTopActivity() {
        return EsViewManager.get().getTopActivity();
    }

    @Nullable
    @Override
    public Activity getCurrentActivity(IEsTraceable traceable) {
        IEsViewer view = EsViewManager.get().findPageWithObject(traceable);
        if (view != null) {
            Context context = view.getAppContext();
            if (context instanceof Activity) {
                return (Activity) context;
            }
        }
        return null;
    }

    @Override
    public @Nullable
    Context getContext() {
        return EsContext.get().getContext();
    }

    @Override
    public @Nullable
    Context getContext(String pkgName) {
        return EsContextFinder.get().findContext(pkgName);
    }

    @Override
    public @Nullable
    String getEsPackageName(IEsTraceable traceable) {
        IEsViewer view = EsViewManager.get().findPageWithObject(traceable);
        return view == null ? null : view.getEsData().getEsPackage();
    }

    @Nullable
    @Override
    public String getChannel() {
        return EsContext.get().getAppChannel();
    }

    @Nullable
    @Override
    public String getEsAppPath(IEsTraceable traceable) {
        IEsViewer view = EsViewManager.get().findPageWithObject(traceable);
        if (view != null) {
            File fileDir = new File(view.getTaskContainer().getAppRootDir(), Constants.PATH_APP_FILES);
            if (!fileDir.exists()) fileDir.mkdirs();
            if (L.DEBUG) L.logD("getEsAppPath: " + fileDir);
            return fileDir.getAbsolutePath();
        }
        return null;
    }

    @Nullable
    @Override
    public String getEsAppRuntimePath(IEsTraceable traceable) {
        File appRuntimeDir = EsViewManager.get().getAppRuntimeDir(traceable);
        if (L.DEBUG) L.logD("getEsAppPath: " + appRuntimeDir);
        return appRuntimeDir != null ? appRuntimeDir.getAbsolutePath() : null;
    }

    @Nullable
    @Override
    public String getEsAppRuntimePath(int engineId) {
        File appRuntimeDir = EsViewManager.get().getAppRuntimeDir(engineId);
        return appRuntimeDir != null ? appRuntimeDir.getAbsolutePath() : null;
    }

    @Override
    public boolean isDebugModel() {
        IEsViewer topViewer = EsViewManager.get().getTopViewer();
        if (topViewer != null) {
            EsData esData = topViewer.getEsData();
            return esData != null && esData.isDebug();
        }
        return false;
    }

    @Override
    public String getDebugServer() {
        IEsViewer topViewer = EsViewManager.get().getTopViewer();
        if (topViewer != null) {
            EsData esData = topViewer.getEsData();
            if (esData != null && esData.isDebug()) return esData.getAppDownloadUrl();
        }
        return "";
    }

    @Override
    public void updateLayout(IEsComponentView componentView) {
        if (componentView == null) return;
        View view = (View) componentView;
        HippyEngineContext engineContext = getEngineContext(view.getContext());
        if (engineContext == null) return;
        engineContext.getDomManager().addNulUITask(() -> {
            RenderNode node = engineContext.getRenderManager().getRenderNode(view.getId());
            if (node == null) return;
            node.updateLayout(node.getX(), node.getY(), node.getWidth(), node.getHeight());
            node.updateViewRecursive();
        });
    }

    @Override
    public void sendUIEvent(int viewId, String eventName, Object params) {
        EsViewManager.get().sendUIEvent(viewId, eventName, params);
    }

    @Override
    public void sendUIEvent(IEsTraceable traceable, int viewId, String eventName, Object params) {
        IEsViewer viewer = EsViewManager.get().findPageWithObject(traceable);
        if (viewer != null) {
            viewer.sendUIEvent(viewId, eventName, params);
        }
    }

    @Override
    public void sendNativeEventTop(String eventName, Object params) {
        EsViewManager.get().sendNativeEventTop(eventName, params);
    }

    @Override
    public void sendNativeEventAll(String eventName, Object params) {
        EsViewManager.get().sendNativeEventAll(eventName, params);
    }

    @Override
    public void sendNativeEventTraceable(IEsTraceable traceable, String eventName, Object params) {
        IEsViewer view = EsViewManager.get().findPageWithObject(traceable);
        if (view != null) {
            view.sendNativeEvent(eventName, params);
        }
    }

    @Override
    public void sendNativeEvent2App(String packageName, String eventName, Object params) {
        IEsViewer viewer = EsViewManager.get().getViewerWithPackage(packageName);
        if (viewer != null) {
            viewer.sendNativeEvent(eventName, params);
        }
    }

    @Override
    public boolean stateContainsAttribute(int[] stateSpecs, int attr) {
        return ExtendUtil.stateContainsAttribute(stateSpecs, attr);
    }

    @Override
    public boolean stateContainsAttribute(int[] stateSpecs, int[] state) {
        return ExtendUtil.stateContainsAttribute(stateSpecs, state);
    }

    @Override
    public boolean isRegisterComponent(String className) {
        return EsComponentManager.get().isRegisterComponent(className);
    }

    @Override
    public boolean isRegisterModule(String className) {
        return EsComponentManager.get().isRegisterModule(className);
    }

    @Override
    public void registerComponent(String... classNames) {
        EsComponentManager.get().injectComponentsToAllContext(classNames);
    }

    @Override
    public void registerComponent(Class<? extends IEsComponent<?>>... clazz) {
        EsComponentManager.get().injectComponentsToAllContext(clazz);
    }

    @Override
    public void registerModule(String... classNames) {
        EsComponentManager.get().injectModulesToAllContext(classNames);
    }

    @Override
    public void registerModule(Class<? extends IEsModule>... clazz) {
        EsComponentManager.get().injectModulesToAllContext(clazz);
    }

    public void registerApiProvider(Object... providers) {
        EsComponentManager.get().injectProvidersToAllContext(providers);
    }

    @Override
    public void loadImageBitmap(EsMap params, EsCallback<Bitmap, Throwable> callback) {
        loadImageBitmap(null, params, callback);
    }

    @Override
    public void loadImageBitmap(IEsTraceable traceable, EsMap data, EsCallback<Bitmap, Throwable> callback) {
        IEsViewer viewer;
        if (traceable == null) {
            viewer = EsViewManager.get().getTopViewer();
        } else {
            viewer = EsViewManager.get().findPageWithObject(traceable);
        }
        if (viewer == null) {
            L.logEF("image load proxy viewer is null");
            return;
        }
        HippyEngineContext engineContext = viewer.getEngineContext();
        HippyImageLoader loaderAdapter;
        if (engineContext == null) {
            loaderAdapter = getGlobalImageLoader();
        } else {
            loaderAdapter = engineContext.getGlobalConfigs().getImageLoaderAdapter();
        }
        loaderAdapter.fetchImage(data.getString("url"), new HippyImageLoader.Callback() {
            @Override
            public void onRequestStart(HippyDrawable drawableTarget) {

            }

            @Override
            public void onRequestSuccess(HippyDrawable drawableTarget) {
                callback.onSuccess(drawableTarget.getBitmap());
            }

            @Override
            public void onRequestFail(Throwable cause, String source) {
                callback.onFailed(cause);
            }
        }, data);
    }

    private HippyImageLoader getGlobalImageLoader() {
        HippyImageLoader loader;
        if (mGlobalImageLoaderCache == null || (loader = mGlobalImageLoaderCache.get()) == null) {
            mGlobalImageLoaderCache = new SoftReference<>((loader = new EsImageLoaderAdapter(EsContext.get().getImageLoader())));
        }
        return loader;
    }

    @Override
    public String getSdkVersionName() {
        return "2.9.3829";
    }

    @Override
    public int getSdkVersionCode() {
        return 3829;
    }

    @Override
    public String getEsKitVersionName() {
        return "2.95";
    }

    @Override
    public double getEsKitVersionCode() {
        return 2.95;
    }

    @Override
    public EsMap getEsKitInfo() {
        EsMap map = new EsMap();
        map.pushString("sdkChannel", "opensource");
        return map;
    }

    @Override
    public void receiveEvent(String eventData, IEsRemoteEventCallback callback) {
        if (L.DEBUG) L.logD("receive event from proxy");
        EsMap from = new EsMap();
        from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_ES_APP);
        EsProtocolDispatcher.tryDispatcher(from, eventData, callback);
    }

    @Override
    public void receiveThirdEvent(ThirdEvent event) {
        if (event == null) return;
        if (TextUtils.isEmpty(event.data)) return;
        if (TextUtils.isEmpty(event.ip)) return;
        if (event.port <= 0) return;
        if (L.DEBUG) L.logD("from proxy");
        EsUdpServer.get().replaceIpAndPort(event.ip, event.port);
        EsMap from = new EsMap();
        from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_REMOTE);
        from.pushObject(Constants.Event.ES_REFERER1, Constants.Event.FROM_PROXY);
        from.pushObject(Constants.Event.ES_REFERER2, event.from);
        EsProtocolDispatcher.tryDispatcher(from, event.data, (action, data) -> {
            if (L.DEBUG) L.logD("send to remote");
            EsUdpServer.get().onReceiveEvent(action, data);
        });
    }

    private HippyEngineContext getEngineContext(Context context) {
        if (!(context instanceof HippyInstanceContext)) {
            L.logEF("Context instance mismatch");
            return null;
        }
        return ((HippyInstanceContext) context).getEngineContext();
    }

    @Override
    public ScreenInfo getScreenInfo() {
        ScreenInfo info = new ScreenInfo();
        Context context = EsContext.get().getContext();
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            info.originDensity = dm.density;
            info.originDensityDip = dm.densityDpi;
            info.originScaleDensity = dm.scaledDensity;
        }
        return info;
    }

    @Override
    public boolean checkSelfPermission(String[] permissions) {
        Context context = EsContext.get().getContext();
        if (context == null) return false;
        if (permissions == null || permissions.length == 0) return true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

    @Override
    public void requestPermission(IEsTraceable traceable, String[] permissions, EsCallback<List<String>, Pair<List<String>, List<String>>> callback) {
        Activity topActivity = getCurrentActivity(traceable);
        if (!(topActivity instanceof BrowserBaseActivity)) {
            callback.onFailed(new Pair<>(Collections.emptyList(), Arrays.asList(permissions)));
            return;
        }

        ((BrowserBaseActivity) topActivity)
                .requestPermission(permissions, callback);
    }

    @Override
    public String getProxyHostName() {
        return InitConfig.getDefault().getProxyHostName();
    }

    @Override
    public int getProxyPort() {
        return InitConfig.getDefault().getProxyPort();
    }

    @Override
    public ISoManager getSoManager() {
        return EsContext.get().getSoManager();
    }

    @Override
    public IDiskCacheManager getDiskCacheManager() {
        IDiskCacheManager manager;
        if (mDiskManagerCache == null || (manager = mDiskManagerCache.get()) == null) {
            mDiskManagerCache = new SoftReference<>((manager = new EsDiskCacheManager()));
        }
        return manager;
    }

    @Override
    public IDisplayManager getDisplayManager() {
        IDisplayManager manager;
        if (mDisplayManagerCache == null || (manager = mDisplayManagerCache.get()) == null) {
            mDisplayManagerCache = new SoftReference<>((manager = new ESDisplayManager()));
        }
        return manager;
    }

    @Override
    public EsMap getPackageJson() {
        EsViewManager vm = EsViewManager.get();
        if (vm != null) {
            IEsViewer viewer = vm.getTopViewer();
            if (viewer != null) {
                EsContainerTaskV2 container = viewer.getTaskContainer();
                if (container != null) {
                    return container.getPackageJsonData();
                }
            }
        }
        return null;
    }

    @Override
    public void setTakeOverKeyEventListener(ITakeOverKeyEventListener listener) {
        EsViewManager vm = EsViewManager.get();
        if (vm != null) {
            Activity activity = vm.getTopActivity();
            if (activity != null) {
                vm.setTakeOverKeyEventListener(activity.hashCode(), listener);
            }
        }
    }

    @Override
    public boolean isContainsFlag(int flag) {
        return CommonUtils.isContainsFlag(flag);
    }
}
