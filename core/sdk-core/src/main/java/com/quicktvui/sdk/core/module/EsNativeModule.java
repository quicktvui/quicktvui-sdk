package com.quicktvui.sdk.core.module;

import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_APP_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_FROM;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsNativeEventCallback;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.cover.IEsCoverView;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.EsManager;
import com.quicktvui.sdk.core.engine.ESEvents;
import com.quicktvui.sdk.core.interceptor.LaunchEsPageInterceptor;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsComponentManager;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.internal.IEsViewer;
import com.quicktvui.sdk.core.protocol.EsProtocolDispatcher;
import com.quicktvui.sdk.core.ui.BrowserBaseActivity;
import com.quicktvui.sdk.core.utils.Am;
import com.quicktvui.sdk.core.utils.EsDataFactory;
import com.quicktvui.sdk.core.utils.EsIntent;
import com.quicktvui.sdk.core.utils.EsNativeEvent;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.quicktvui.sdk.core.utils.ReflectExecutor;
import com.quicktvui.sdk.core.utils.SpUtils;
import com.sunrain.toolkit.utils.AppUtils;
import com.sunrain.toolkit.utils.NetworkUtils;
import com.sunrain.toolkit.utils.PropertyUtils;
import com.sunrain.toolkit.utils.SDCardUtils;
import com.sunrain.toolkit.utils.Utils;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.thread.Executors;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Create by weipeng on 2022/03/22 16:24
 */
@ESKitAutoRegister
public class EsNativeModule implements IEsModule {

    @Override
    public void init(Context context) {

    }

    public void killSelf() {
        Process.killProcess(Process.myPid());
    }

    public void getVisiblePageSize(EsPromise promise) {
        EsViewManager manager = EsViewManager.get();
        PromiseHolder.create(promise).put("size", manager.getVisiblePageSize())
                .sendSuccess();
    }

    //region 操作EsData

    public void getAllStartParams(EsPromise promise) {
        try {
            EsData data = EsViewManager.get().getEsAppData(this);
            if (data != null) {
                EsMap exp = data.getExp();
                if (exp != null) {
                    String params = exp.getString(EsProtocolDispatcher.K_ALL_PARAMS);
                    if (!TextUtils.isEmpty(params)) {
                        promise.resolve(params);
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve("{}");
    }

    public void setPageTag(String tag) {
        // FIXME: 暂时不支持，因为如果首次启动的时候是standActivity，即使指定了flags和tag，也不会起作用
//        try {
//            EsViewManager.get().getEsAppData().setPageTag(tag);
//        } catch (Exception e) {
//            L.logEF("setPageTagErr " + e.getMessage());
//        }
    }

    @Deprecated
    public void setPageLimit(int limit) {
//        try {
//            EsViewManager.get().getEsAppData().setPageLimit(limit);
//        } catch (Exception e) {
//            L.logEF("setPageLimitErr " + e.getMessage());
//        }
    }

    //endregion

    //region 缓存数据

    public void storeData(String K, EsMap data) {
//        EsViewManager view = EsViewManager.get();
    }

    public void getDiskSize(EsPromise promise) {
        try {
            long total = SDCardUtils.getInternalTotalSize();
            long available = SDCardUtils.getInternalAvailableSize();
            PromiseHolder.create(promise)
                    .put("total", total)
                    .put("available", available)
                    .sendSuccess();
        } catch (Exception e) {
            try {
                PromiseHolder.create(promise).singleData(e.getMessage()).sendFailed();
            } catch (Exception ignore) {
            }
        }
    }

    //endregion

    //region App操作

    public static final String K_PKG = "pkg";
    public static final String K_ARGS = "args";
    public static final String K_FLAGS = "flags";
    public static final String K_PAGE_TAG = "pageTag";
    public static final String K_PAGE_LIMIT = "pageLimit";
    public static final String K_BACKGROUND_COLOR = "backgroundColor";

    // 与 BrowserProxyActivity 对应 !!!!!!!!!!
    public void getSupportSchemes(EsPromise promise) {
        EsArray array = new EsArray();
        array.pushString("esapp://action/start");
        promise.resolve(array);
    }

    public void launchEsPage(EsMap params) {
        if (params == null) return;

        if (params.containsKey(K_PKG)) {
            EsData last = EsViewManager.get().getEsAppData(this);
            if (last != null) {
                LaunchEsPageInterceptor interceptor = last.getInterface(LaunchEsPageInterceptor.NAME);
                if (interceptor != null) {
                    if (interceptor.launchEsPage(last, params)) {
                        return;
                    }
                }
            }
        }

        EsData data = EsDataFactory.create(params.toJSONObject());
        if (data == null) return;
        if (params.containsKey(K_PKG)) {
            EsData last = EsViewManager.get().getEsAppData(this);
            if (last != null) {
                data.setExp(PromiseHolder.create().put(K_FROM, Constants.Event.FROM_ES_APP).getData());
            }
        } else {
            EsData appData = EsViewManager.get().getEsAppData(this);
            if (appData != null) {
                EsData last = appData.clone();
                // 兼容旧版本背景色
                if (data.getBackgroundColor() < 0) {
                    Object _color = params.get(K_BACKGROUND_COLOR);
                    int color = -1;
                    try {
                        if (_color instanceof Number) {
                            color = (int) _color;
                        } else if (_color instanceof String) {
                            color = Color.parseColor((String) _color);
                        }
                        data.setBackgroundColor(color);
                    } catch (Exception e) {
                        L.logW("apply bg color", e);
                    }
                }
                data.setAppPackage(last.getEsPackage());
                data.setEsVersion(last.getEsVersion());
                data.setEsMinVersion(last.getEsMinVersion());
                data.setAppDownloadUrl(last.getAppDownloadUrl());
                data.setAppMd5(last.getAppMd5());
                data.setUseEncrypt(last.isUseEncrypt());
                data.setCheckNetwork(last.isCheckNetwork());
                if (last.getCoverLayoutId() == 0) data.setCoverLayoutId(EsData.SPLASH_NONE, null);
            }
        }
        EsManager.get().start(data);
    }

    public void finish() {
        if (L.DEBUG) L.logD("app_chain call finish");
        IEsViewer viewer = EsViewManager.get().findPageWithObject(this);
        if (viewer != null) {
            viewer.toFinish();
        }
    }

    public void finishAll() {
        if (L.DEBUG) L.logD("app_chain call finish all");
        EsViewManager.get().finishAllAppPage();
    }

    public void launchNativeApp(EsArray params, EsPromise promise) {
        try {
            executeNativeCmd(Am.TYPE_ACTIVITY, parseArgs(params), promise);
        } catch (Exception e) {
            L.logW("launch native app", e);
        }
    }

    /**
     * 执行指令
     * [
     * ['-a', 'action名称'],
     * ['-p', '包名'],
     * ['-d', 'URI']
     * ['-t', 'video/*']
     * ]
     **/
    public void executeNativeCmd(String type, List<String[]> cmd, EsPromise promise) {
        try {
            Am am = new Am();
            EsIntent intent = am.makeIntent(type, cmd);
            boolean isBlockMode = processIntent(intent);

            // 需要本地拦截
            if (isBlockMode) {
                Uri uri = intent.getData();
                JSONObject json = MapperUtils.uri2JsonObject(uri);
                json.put(K_ACTION_V2, K_ACTION_ES_APP_V2);
                EsMap from = new EsMap();
                from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_ES_APP);
                EsProtocolDispatcher.tryDispatcher(from, json, EsContext.get().getRemoteEventCallback());
            } else {
                am.execute(EsContext.get().getContext(), intent);
            }
            new PromiseHolder(promise).sendSuccess();
        } catch (Exception e) {
            new PromiseHolder(promise).message(e.getMessage()).sendFailed();
            L.logW("exec cmd", e);
        }
    }

    /**
     * 是否需要本地拦截启动
     **/
    private boolean processIntent(Intent intent) {
        Uri data;
        String scheme;
        if (intent != null
                && (data = intent.getData()) != null
                && !TextUtils.isEmpty((scheme = data.getScheme()))
                && "esapp".equals(scheme)) {
            String targetPackage = intent.getPackage();
            String myPackage = EsContext.get().getContext().getPackageName();
            if (TextUtils.isEmpty(targetPackage) || targetPackage.equals(myPackage)) {
                intent.setPackage(myPackage);
                return true;
            }
        }
        return false;
    }

    public void launchNativeAppWithPackage(String pkg) {
//    public void launchNativeAppWithPackage(String pkg, EsPromise promise) {
        try {
            Context context = EsContext.get().getContext();
            PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(pkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//            PromiseHolder.create(promise).singleData(true).sendSuccess();
        } catch (Exception e) {
            L.logW("launch native app", e);
//            PromiseHolder.create(promise).singleData(false).message(e.getMessage()).sendSuccess();
        }
    }

    public void sendBroadcast(EsArray params, EsPromise promise) {
        try {
            Am am = new Am();
            EsIntent intent = am.makeIntent(Am.TYPE_BROADCAST, parseArgs(params));
            am.execute(EsContext.get().getContext(), intent);
            if (promise != null) {
                promise.resolve(true);
            }
        } catch (Exception e) {
            L.logW("send broadcast", e);
            if (promise != null) {
                promise.reject(false);
            }
        }
    }

    public void startService(EsArray params) {
        try {
            Am am = new Am();
            EsIntent intent = am.makeIntent(Am.TYPE_SERVICE, parseArgs(params));
            am.execute(EsContext.get().getContext(), intent);
        } catch (Exception e) {
            L.logW("start service", e);
        }
    }

    private List<String[]> parseArgs(EsArray params) {
        List<String[]> lines = new ArrayList<>();
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                EsArray argInLine = params.getArray(i);
                int argLen = argInLine.size();
                String[] args = new String[argLen];
                for (int j = 0; j < argLen; j++) {
                    args[j] = argInLine.getString(j);
                }
                lines.add(args);
            }
        }
        return lines;
    }

    // 兼容老版本
    @Deprecated
    public void getSdkInfo(EsPromise promise) {
        getDeviceInfo(promise);
    }

    public void getDeviceInfo(EsPromise promise) {
        Map<String, String> info = EsContext.get().getDeviceInfo();
        PromiseHolder holder = PromiseHolder.create(promise);
        if (info != null) {
            Set<String> keys = info.keySet();
            holder.put("cid", EsContext.get().getClientId());
            for (String key : keys) {
                holder.put(key, info.get(key));
            }
            holder.put("device_name", EsContext.get().getDeviceName());
            holder.put("device_ip", NetworkUtils.getIPAddress(true));
        }
        holder.sendSuccess();
    }

    /**
     * getESId
     */
    public void getESId(EsPromise promise) {
        try {
            String id = EsContext.get().getClientId();
            promise.resolve(id);
        } catch (Throwable e) {
            L.logW("get esid", e);
        }
    }

    public void isAppInstalled(String packageName, EsPromise promise) {
        boolean installed = AppUtils.isAppInstalled(packageName);
        PromiseHolder.create(promise).singleData(installed).sendSuccess();
    }



    /**
     * [
     *  {
     *     "pkg":"",
     *     "install":true
     *  }
     * ]
     * @param packages
     * @param promise
     */
    public void isAppsInstalled(EsArray packages, EsPromise promise) {

        PackageManager pm = Utils.getApp().getPackageManager();
        EsArray result = new EsArray();

        for (int i = 0; i < packages.size(); i++) {
            EsMap info = new EsMap();
            String pkg = packages.getString(i);
            info.pushString("pkg", pkg);
            try {
                pm.getApplicationInfo(packages.getString(i), 0);
                info.pushBoolean("install", true);
            } catch (PackageManager.NameNotFoundException ignore) {
                info.pushBoolean("install", false);
            }
            result.pushMap(info);
        }

        PromiseHolder.create(promise).singleData(result).sendSuccess();
    }

    public void isAppSystem(String packageName, EsPromise promise) {
        boolean appSystem = AppUtils.isAppSystem(packageName);
        PromiseHolder.create(promise)
                .singleData(appSystem)
                // 兼容老版本
                .put("isSystem", appSystem)
                .sendSuccess();
    }

    public void getAppInfo(String pkgName, EsPromise promise) {
        PromiseHolder.create(promise)
                .singleData(getAppInfo_(pkgName))
                .sendSuccess();
    }

    private EsMap getAppInfo_(String pkgName) {
        EsMap info = new EsMap();
        try {
            PackageManager pm = EsContext.get().getContext().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(pkgName, 0);
            info.pushString("name", ai.loadLabel(pm).toString());
            info.pushString("pkg", pkgName);
            PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
            if (pi != null) info.pushInt("verCode", pi.versionCode);
            if (pi != null) info.pushString("verName", pi.versionName);
            if (pi != null) info.pushLong("installTime", pi.firstInstallTime);
            if (pi != null) info.pushLong("updateTime", pi.lastUpdateTime);
            info.pushBoolean("isSystemApp", (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
        } catch (Exception e) {
            L.logW("get app info", e);
        }
        return info;
    }

    public void getAppList(EsPromise promise) {
//        EsMap data = new EsMap();
        EsArray systemApps = new EsArray();
        EsArray userApps = new EsArray();
        PackageManager pm = EsContext.get().getContext().getPackageManager();

        Intent queryIntent = new Intent(Intent.ACTION_MAIN);
        queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> list = pm.queryIntentActivities(queryIntent, 0);
        for (ResolveInfo info : list) {
            EsMap app = getAppInfo_(info.activityInfo.packageName);
            if (app.getBoolean("isSystemApp")) {
                systemApps.pushMap(app);
            } else userApps.pushMap(app);
        }

        PromiseHolder.create(promise).put("system", systemApps).put("user", userApps)
                .sendSuccess();
    }

    public void getAutoStartAppList(EsPromise promise) {
        PackageManager pm = EsContext.get().getContext().getPackageManager();
        List<ResolveInfo> queryList = pm.queryBroadcastReceivers(new Intent(Intent.ACTION_BOOT_COMPLETED), PackageManager.GET_RESOLVED_FILTER);
        EsArray result = new EsArray();
        if (queryList != null) {
            Map<String, ComponentName> autoStartPackages = new HashMap<>(); // 去重使用
            for (ResolveInfo ri : queryList) {
                autoStartPackages.put(ri.activityInfo.packageName,
                        new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name));
            }

            Set<String> packages = autoStartPackages.keySet();
            for (String pkg : packages) {

                try {
                    ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                    if (pm.getLaunchIntentForPackage(ai.packageName) == null) continue;
                    EsMap app = new EsMap();
                    app.pushString("name", ai.loadLabel(pm).toString());
                    app.pushString("pkg", ai.packageName);
                    PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
                    if (pi != null) app.pushInt("verCode", pi.versionCode);
                    if (pi != null) app.pushString("verName", pi.versionName);
                    app.pushBoolean("system", (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
                    app.pushBoolean("allow", pm.getComponentEnabledSetting(autoStartPackages.get(pkg)) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                    result.pushMap(app);
                } catch (Exception e) {
                    L.logW("get auto start app", e);
                }
            }

        }
        PromiseHolder.create(promise).singleData(result).sendSuccess();
    }

    public void setAutoStartAppSwitch(String packageName, boolean enable, EsPromise promise) {
        boolean success = false;
        if (!TextUtils.isEmpty(packageName)) {
            Intent queryIntent = new Intent(Intent.ACTION_BOOT_COMPLETED);
            queryIntent.setPackage(packageName);
            Context context = EsContext.get().getContext();
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> queryList = pm.queryBroadcastReceivers(queryIntent, PackageManager.GET_DISABLED_COMPONENTS);
            if (queryList != null) {
                if (L.DEBUG) L.logD("setAutoStartAppSwitch:" + packageName);
                for (ResolveInfo ri : queryList) {
                    ActivityInfo ai = ri.activityInfo;
                    ComponentName cN = new ComponentName(ai.packageName, ai.name);
                    int wantState = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                    if (L.DEBUG) L.logD("wantState:" + wantState);
                    if (L.DEBUG) L.logD("set to " + cN);
                    // 检测状态
                    pm.setComponentEnabledSetting(cN, wantState, PackageManager.DONT_KILL_APP);
                    int curState = pm.getComponentEnabledSetting(cN);
                    if (L.DEBUG) L.logD("checkState:" + curState);
                    success = curState == wantState;
                }
            }
        }

        PromiseHolder.create(promise).singleData(success).sendSuccess();
    }

    @Deprecated // 可以使用自定义IEsModule实现
    public void sendNativeEvent(String name, EsMap data, EsPromise promise) {
        int engineId = EsViewManager.get().findEngineIdWithObject(this);
        if (engineId != -1) {
            EsNativeEventCallback nativeEvent = ESEvents.getNativeEvent(engineId);
            if (nativeEvent != null) {
                EsNativeEvent event = new EsNativeEvent(name, data, promise);
                nativeEvent.onEvent(event);
            }
        }
    }

    public void sendRemoteEvent(String eventName, String eventData) {
        sendEvent2Master(eventName, eventData);
    }

    public static void sendEvent2Master(String eventName, String eventData) {
        if (L.DEBUG) L.logD("sendRemoteEvent --> event: " + eventName + ", data: " + eventData);
        IEsRemoteEventCallback callback = EsContext.get().getRemoteEventCallback();
        if (callback == null) {
            L.logWF("未注册事件响应者 忽略");
            return;
        }
        callback.onReceiveEvent(eventName, eventData);
    }

    //endregion

    //region Cover

    public void suspendLoadingView(String msg) {
        toSuspendLoadingView(msg);
    }

    public static void toSuspendLoadingView(String msg) {
        if (L.DEBUG) L.logD("suspendLoadingView:" + msg);
        IEsViewer viewer = EsViewManager.get().getTopViewer();
        if (viewer != null) {
            Context context = viewer.getAppContext();
            if (context instanceof BrowserBaseActivity) {
                IEsCoverView coverView = ((BrowserBaseActivity) context).getCoverView();
                if (coverView != null) {
                    if (L.DEBUG) L.logD("suspend");
                    coverView.suspend(msg);
                }
            }
        }
    }

    public void unSuspendLoadingView() {
        if (L.DEBUG) L.logD("unSuspendLoadingView");
        IEsViewer viewer = EsViewManager.get().getTopViewer();
        if (viewer != null) {
            Context context = viewer.getAppContext();
            if (context instanceof BrowserBaseActivity) {
                IEsCoverView coverView = ((BrowserBaseActivity) context).getCoverView();
                if (coverView != null) {
                    if (L.DEBUG) L.logD("unSuspend");
                    coverView.unSuspend();
                }
            }
        }
    }

    //endregion

    //region Property

    public void getPropString(String key, String def, EsPromise promise) {
        promise.resolve(PropertyUtils.getString(key, def));
    }

    public void getPropInt(String key, int def, EsPromise promise) {
        promise.resolve(PropertyUtils.getInt(key, def));
    }

    public void getPropLong(String key, long def, EsPromise promise) {
        promise.resolve(PropertyUtils.getLong(key, def));
    }

    public void getPropBoolean(String key, boolean def, EsPromise promise) {
        promise.resolve(PropertyUtils.getBoolean(key, def));
    }

    //endregion

    //region MetaData

    public void getApplicationMetaData(EsPromise promise) {
        getApplicationMetaDataWithPackage(Utils.getApp().getPackageName(), promise);
    }

    public void getApplicationMetaDataWithPackage(String packageName, EsPromise promise) {
        EsMap data;
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            data = getMetaData(info.metaData);
        } catch (Exception e) {
            L.logEF("read app meta data err:" + e.getMessage());
            data = new EsMap();
        }
        PromiseHolder.create(promise).put(data).sendSuccess();
    }

    public void getActivityMetaData(String activityName, EsPromise promise) {
        getActivityMetaDataWithPackage(Utils.getApp().getPackageName(), activityName, promise);
    }

    public void getActivityMetaDataWithPackage(String packageName, String activityName, EsPromise promise) {
        EsMap data;
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            ActivityInfo info = pm.getActivityInfo(new ComponentName(packageName, activityName), PackageManager.GET_META_DATA);
            data = getMetaData(info.metaData);
        } catch (Exception e) {
            L.logEF("read app meta data err:" + e.getMessage());
            data = new EsMap();
        }
        PromiseHolder.create(promise).put(data).sendSuccess();
    }

    public void getReceiverMetaData(String receiverName, EsPromise promise) {
        getReceiverMetaDataWithPackage(Utils.getApp().getPackageName(), receiverName, promise);
    }

    public void getReceiverMetaDataWithPackage(String packageName, String receiverName, EsPromise promise) {
        EsMap data;
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            ActivityInfo info = pm.getReceiverInfo(new ComponentName(packageName, receiverName), PackageManager.GET_META_DATA);
            data = getMetaData(info.metaData);
        } catch (Exception e) {
            L.logEF("read app meta data err:" + e.getMessage());
            data = new EsMap();
        }
        PromiseHolder.create(promise).put(data).sendSuccess();
    }

    public void getProviderMetaData(String providerName, EsPromise promise) {
        getProviderMetaDataWithPackage(Utils.getApp().getPackageName(), providerName, promise);
    }

    public void getProviderMetaDataWithPackage(String packageName, String providerName, EsPromise promise) {
        EsMap data;
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            ProviderInfo info = pm.getProviderInfo(new ComponentName(packageName, providerName), PackageManager.GET_META_DATA);
            data = getMetaData(info.metaData);
        } catch (Exception e) {
            L.logEF("read app meta data err:" + e.getMessage());
            data = new EsMap();
        }
        PromiseHolder.create(promise).put(data).sendSuccess();
    }

    public void getServiceMetaData(String serviceName, EsPromise promise) {
        getServiceMetaDataWithPackage(Utils.getApp().getPackageName(), serviceName, promise);
    }

    public void getServiceMetaDataWithPackage(String packageName, String serviceName, EsPromise promise) {
        EsMap data;
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            ServiceInfo info = pm.getServiceInfo(new ComponentName(packageName, serviceName), PackageManager.GET_META_DATA);
            data = getMetaData(info.metaData);
        } catch (Exception e) {
            L.logEF("read app meta data err:" + e.getMessage());
            data = new EsMap();
        }
        PromiseHolder.create(promise).put(data).sendSuccess();
    }

    private EsMap getMetaData(Bundle bundle) {
        EsMap data = new EsMap();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                try {
                    data.pushObject(key, bundle.get(key));
                } catch (Exception e) {
                    L.logEF("read meta data err:" + e.getMessage());
                }
            }
        }
        return data;
    }

    //endregion

    //region Package.json

    public void getPackageJson(EsPromise promise) {
        EsMap packageJson = EsProxy.get().getPackageJson();
        if (packageJson == null) {
            packageJson = new EsMap();
        }
        PromiseHolder.create(promise)
                .put(packageJson).sendSuccess();
    }

    //endregion

    //region 内存

    /**
     * 获取内存信息
     **/
    public void getMemoryInfo(EsPromise promise) {
        ActivityManager.MemoryInfo memInfo = getMemoryInfo(EsContext.get().getContext());
        long m_max = 0L;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            m_max = memInfo.totalMem;
        }
        PromiseHolder.create(promise)
                .put("max", m_max)
                .put("available", memInfo.availMem).sendSuccess();
    }

    private ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi;
    }

    //endregion

    //region 网络

    /**
     * 网络可用
     **/
    public void isNetworkAvailable(EsPromise promise) {
        Executors.get().execute(() -> PromiseHolder.create(promise)
                .singleData(NetworkUtils.isAvailable())
                .sendSuccess());
    }

    /**
     * 网络连接，连接但是可以没有网
     **/
    public void isNetworkConnected(EsPromise promise) {
        PromiseHolder.create(promise).singleData(NetworkUtils.isConnected()).sendSuccess();
    }

    /**
     * 通过dns检测网络是否可用
     **/
    public void isNetworkAvailableByDns(String dns, EsPromise promise) {
        Executors.get().execute(() -> PromiseHolder.create(promise)
                .singleData(NetworkUtils.isAvailableByDns(dns))
                .sendSuccess());
    }

    /**
     * 通过ip检测网络是否可用
     **/
    public void isNetworkAvailableByPing(String ip, EsPromise promise) {
        Executors.get().execute(() -> PromiseHolder.create(promise)
                .singleData(NetworkUtils.isAvailableByPing(ip))
                .sendSuccess());
    }

    /**
     * 判断compoent是否注册
     *
     * @param className
     * @param promise
     */
    public void isESComponentRegistered(String className, EsPromise promise) {
        try {
            boolean isRegistered = EsComponentManager.get().isRegisterComponent(className);
            promise.resolve(isRegistered);
        } catch (Throwable e) {
            L.logW("check reg comp", e);
            promise.reject(e.getMessage());
        }
    }

    /**
     * 判断module是否注册
     *
     * @param className
     * @param promise
     */
    public void isESModuleRegistered(String className, EsPromise promise) {
        try {
            boolean isRegistered = EsComponentManager.get().isRegisterModule(className);
            promise.resolve(isRegistered);
        } catch (Throwable e) {
            L.logW("check reg mod", e);
            promise.reject(e.getMessage());
        }
    }

    //endregion

    //region 反射

    /**
     * 反射类
     **/
    public void callReflect(EsMap params, EsPromise promise) {
        try {
            Object result = new ReflectExecutor(params.toJSONObject()).execute();
            PromiseHolder.create(promise).singleData(result).sendSuccess();
        } catch (Exception e) {
            L.logW("call reft", e);
            PromiseHolder.create(promise)
                    .message(e.getMessage())
                    .sendFailed();
        }
    }

    //endregion

    public void isShowPolicy(String pkg, EsPromise promise) {
        PromiseHolder.create(promise)
                .singleData(!TextUtils.isEmpty(pkg) && pkg.equals(SpUtils.getPrivacyPolicyShowPackage()))
                .sendSuccess();
    }

    /**
     * Vue告诉底层跳到了哪个界面
     **/
    public void onRouteTo(String path) {
        if (L.DEBUG) L.logD("onRouteTo path: " + path);
    }

    @Override
    public void destroy() {

    }
}
