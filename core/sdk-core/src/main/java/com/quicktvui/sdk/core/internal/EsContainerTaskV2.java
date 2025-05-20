package com.quicktvui.sdk.core.internal;

import static com.quicktvui.sdk.core.internal.Constants.ERR_OFFLINE;
import static com.quicktvui.sdk.core.internal.Constants.ERR_PRINT;
import static com.quicktvui.sdk.core.internal.Constants.ERR_TIME_OUT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.quicktvui.sdk.base.EsEmptyCallback;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.ISoManager;
import com.quicktvui.sdk.base.ITakeOverKeyEventListener;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.core.R;
import com.quicktvui.sdk.core.adapter.EsEngineExceptionHandlerAdapter;
import com.quicktvui.sdk.core.adapter.EsImageLoaderAdapter;
import com.quicktvui.sdk.core.adapter.EsSoLoaderAdapter;
import com.quicktvui.sdk.core.engine.EsEngine;
import com.quicktvui.sdk.core.engine.EsEnginePool;
import com.quicktvui.sdk.core.entity.InfoEntity;
import com.quicktvui.sdk.core.internal.loader.ApiRpkLoader;
import com.quicktvui.sdk.core.internal.loader.DebugRpkLoader;
import com.quicktvui.sdk.core.internal.loader.IRpkLoader;
import com.quicktvui.sdk.core.internal.loader.NexusRpkLoader;
import com.quicktvui.sdk.core.internal.loader.RpkInfo;
import com.quicktvui.sdk.core.internal.loader.RpkLoaderFactory;
import com.quicktvui.sdk.core.internal.loader.TaskEntity;
import com.quicktvui.sdk.core.module.EsNativeModule;
import com.quicktvui.sdk.core.utils.CommonUtils;
import com.quicktvui.sdk.core.utils.ESExecutors;
import com.quicktvui.sdk.core.utils.TaskProgressManager;
import com.sunrain.toolkit.bolts.tasks.CancellationTokenSource;
import com.sunrain.toolkit.bolts.tasks.Continuation;
import com.sunrain.toolkit.bolts.tasks.Task;
import com.sunrain.toolkit.bolts.tasks.TaskCompletionSource;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.net.HttpRequest;
import com.tencent.mtt.hippy.HippyEngine;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.adapter.exception.HippyExceptionHandlerAdapter;
import com.tencent.mtt.hippy.adapter.image.HippyImageLoader;
import com.tencent.mtt.hippy.common.HippyJsException;
import com.tencent.mtt.hippy.utils.PixelUtil;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Create by weipeng on 2022/03/21 19:59
 */
public class EsContainerTaskV2 {
    private final ScheduledExecutorService scheduler = java.util.concurrent.Executors.newScheduledThreadPool(1);
    private final Executor THREAD_BG = ESExecutors.RPK_THREAD;
    private final Executor THREAD_UI = Task.UI_THREAD_EXECUTOR;

    private Context mContext;
    private IEsAppLoadCallback mEsLoadCallback;

    private IRpkLoader mLoader;
    private EsEngine mEngine;

    private CancellationTokenSource mTCS;

    private boolean mDynamicSo;

    public EsContainerTaskV2(Context context, IEsAppLoadCallback callback) {
        this.mContext = context;
        this.mEsLoadCallback = callback;
        mDynamicSo = CommonUtils.isEnableDynamicSo();
    }

    public void startLoad(EsData data) {
        TaskProgressManager.getInstance().reset();
        TaskProgressManager.getInstance().addTask("download_so", 30);
        TaskProgressManager.getInstance().addTask("download_rpk", 70);

        if (L.DEBUG) L.logD("load start");
        mTCS = new CancellationTokenSource();

        mLoader = RpkLoaderFactory.createLoader(data);
        L.logIF("data.getAppDownloadUrl() is " + mLoader.getAppData().getAppDownloadUrl());

        if (mLoader instanceof ApiRpkLoader) {
            ((ApiRpkLoader) mLoader).setLoadProxy(InitConfig.getDefault().getEsRpkLoadProxy());
        }
        loadEsContentTask();
    }

    /**
     * 启动快应用的所有任务入口
     */
    private void loadEsContentTask() {
        TaskEntity taskEntity = new TaskEntity();
        Task.forResult(taskEntity)
                .onSuccess(CHECK_NETWORK, THREAD_BG, mTCS.getToken())//检查网络是否可用
                .onSuccess(PREPARE_SO_FROM_INTERNET, THREAD_BG, mTCS.getToken())//更新、下载so文件
                .continueWith((Continuation<TaskEntity, Void>) task -> {
                    if (task.isFaulted()) {
                        esContentTaskError(task);
                        return null;
                    }

                    TaskEntity currentTaskEntity = task.getResult();
                    if (L.DEBUG) L.logD("当前是否有网 is :" + currentTaskEntity.isConnectedNetwork());
                    if (currentTaskEntity.isConnectedNetwork()) {
                        //有网络
                        if (mLoader instanceof ApiRpkLoader) {
                            loadRpkInfo(currentTaskEntity);
                        } else {
                            loadAssetsOrLocalFileRpk(currentTaskEntity);
                        }
                    } else {
                        if (L.DEBUG) L.logD("当前无网状态，直接加载缓存");
                        //无网
                        loadRpkFromCache(currentTaskEntity, true);
                    }
                    return null;
                }, THREAD_UI, mTCS.getToken());
    }

    private void loadRpkInfo(TaskEntity taskEntity) {
        Task.forResult(taskEntity)
                .onSuccess(LOAD_RPK_INFO_FROM_SERVER, THREAD_BG, mTCS.getToken())//获取rpk应用信息

                .continueWith((Continuation<TaskEntity, Void>) task -> {

                    if (task.isFaulted()) {
                        esContentTaskError(task);
                        return null;
                    }

                    TaskEntity currentTaskEntity = task.getResult();
                    InfoEntity infoEntity = currentTaskEntity.getInfoEntity();
                    if (infoEntity.refresh) {//判断是否为强更新
                        L.logIF("force update");
                        //强更新：下载rpk文件
                        downloadRpkFromServer(currentTaskEntity);
                    } else {
                        //弱更新：先走缓存，再检查更新、下载
                        loadRpkFromCache(currentTaskEntity, false);//显示缓存
                    }

                    return null;
                }, THREAD_UI, mTCS.getToken());
    }

    /**
     * 下载rpk文件 并  打开小程序  or  弹窗
     *
     * @param taskEntity
     */
    private void downloadRpkFromServer(TaskEntity taskEntity) {
        Task.forResult(taskEntity)
                .onSuccess(DOWNLOAD_RPK, THREAD_BG, mTCS.getToken())
                .onSuccess(MAKE_RPK_VIEW, THREAD_BG, mTCS.getToken())
                .onSuccess(RENDER_VIEW, THREAD_UI, mTCS.getToken())
                .continueWith((Continuation<TaskEntity, Void>) task -> {
                    if (task.isCancelled()) {
                        L.logWF("load cancel");
                    } else if (task.isFaulted()) {
                        esContentTaskError(task);
                    }
                    // 2024.09.12 断开loading页面监听防止内存泄漏
                    TaskProgressManager.getInstance().setTaskProgressCallback(null);

                    notifyLoadRpkSuccess();

                    TaskEntity taskCompleteEntity = task.getResult();
                    showRpkUpdateDialog(taskCompleteEntity);

                    return null;
                }, THREAD_UI, mTCS.getToken());
    }

    /**
     * 执行debug状态下的rpk文件（file、assets等等）
     */
    private void loadAssetsOrLocalFileRpk(TaskEntity taskEntity) {
        Task.forResult(taskEntity)
                .onSuccess(LOCAL_RPK_FILE, THREAD_BG, mTCS.getToken())
                .onSuccess(MAKE_RPK_VIEW, THREAD_BG, mTCS.getToken())
                .onSuccess(RENDER_VIEW, THREAD_UI, mTCS.getToken())
                .continueWith((Continuation<TaskEntity, Void>) task -> {
                    if (task.isCancelled()) {
                        L.logWF("load cancel");
                    } else if (task.isFaulted()) {
                        esContentTaskError(task);
                    }

                    notifyLoadRpkSuccess();

                    return null;
                }, THREAD_UI, mTCS.getToken());
    }

    /**
     * 预下载rpk文件
     *
     * @param taskEntity
     */
    private void preDownloadRpk(TaskEntity taskEntity) {
        String versionName = mLoader.getRpkInfo().getVersionName();
        InfoEntity infoEntity = taskEntity.getInfoEntity();
        if(Objects.equals(versionName, infoEntity.esVersion)) return;
        Task.forResult(taskEntity)
                .onSuccess(PRE_DOWNLOAD_RPK, THREAD_BG, mTCS.getToken())
                .continueWith((Continuation<TaskEntity, Void>) task -> {
                    if (task.isCancelled()) {
                        L.logWF("load cancel");
                    } else if (task.isFaulted()) {
                        esContentTaskError(task);
                    }
                    L.logIF("预下载rpk完成");

                    TaskEntity taskCompleteEntity = task.getResult();
                    showRpkUpdateDialog(taskCompleteEntity);

                    return null;
                }, THREAD_UI, mTCS.getToken());
    }

    /**
     * 本地缓存展示rpk
     *
     * @param taskEntity
     * @param isOffline  是否离线状态
     */
    private void loadRpkFromCache(TaskEntity taskEntity, boolean isOffline) {
        Task.forResult(taskEntity)
                .onSuccess(LOAD_FROM_CACHE, THREAD_BG, mTCS.getToken())
                .continueWith((Continuation<TaskEntity, Void>) task -> {
                    if (L.DEBUG) L.logD("rpk file path is：" + task.getResult().getRpkFile());
                    if (task.isCancelled()) {
                        L.logWF("load cancel");
                    } else if (task.isFaulted()) {
                        esContentTaskError(task);
                    }
                    //判断是否有缓存
                    if (L.DEBUG) L.logD("本地rpk缓存目录：" + task.getResult().getRpkFile());
                    if (task.getResult().getRpkFile() == null) {
                        if (isOffline) {
                            //无网并且没有缓存   显示报错
                            if (mEsLoadCallback != null) {
                                mEsLoadCallback.onLoadError(new EsException(ERR_OFFLINE, "网络不可用，请检查网络后重试"));
                                checkNetwork();
                                notifyLoadRpkSuccess();
                            }
                        } else {
                            //有网没有缓存   下载rpk文件
                            downloadRpkFromServer(task.getResult());
                        }
                    } else {
                        //有缓存
                        showCreateViewAndRenderView(task.getResult(), true);
                    }
                    return null;
                }, THREAD_UI, mTCS.getToken());
    }

    /**
     * 创建rpk view 渲染rpk view
     *
     * @param isPreDownload 是否预下载
     */
    private void showCreateViewAndRenderView(TaskEntity taskEntity, boolean isPreDownload) {
        L.logIF("----------+>" + taskEntity.toString() + "  isPreDownload is :" + isPreDownload);
        Task.forResult(taskEntity)
                .onSuccess(MAKE_RPK_VIEW, THREAD_BG, mTCS.getToken())
                .onSuccess(RENDER_VIEW, THREAD_UI, mTCS.getToken())
                .continueWith((Continuation<TaskEntity, Void>) task -> {
                    L.logWF("task local" + task.getResult().getRpkFile());
                    if (task.isCancelled()) {
                        L.logWF("load cancel");
                    } else if (task.isFaulted()) {
                        esContentTaskError(task);
                    }
                    //TODO 2024.09.12 断开loading页面监听防止内存泄漏
                    TaskProgressManager.getInstance().setTaskProgressCallback(null);

                    if (isPreDownload) {
                        preDownloadRpk(task.getResult());
                    }

                    notifyLoadRpkSuccess();

                    L.logIF("load end");
                    return null;
                }, THREAD_UI, mTCS.getToken());
    }

    /**
     * 广播通知rpk加载、渲染完成
     */
    private void notifyLoadRpkSuccess() {
        //页面显示发送广播，解决fix：
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("eskit.action.render.view.success"));
    }

    /**
     * task任务执行失败
     *
     * @param task
     */
    private void esContentTaskError(Task<TaskEntity> task) {
        Exception errException = task.getError();
        L.logWF("check upload", errException);

        EsException exception;
        if (errException instanceof EsException) {
            exception = (EsException) errException;
        } else if (errException instanceof HttpRequest.HttpRequestException) {
            exception = new EsException(ERR_TIME_OUT, errException.getMessage());
        } else {
            exception = new EsException(ERR_PRINT, errException.getMessage());
        }

        int code = exception.getCode();
        int reasonCode = exception.getReasonCode();

        EsViewManager.get().onAppViewRenderFailed(mLoader.getAppData(), reasonCode == 0 ? code : reasonCode, exception.getMessage());

        if (mEsLoadCallback != null) {
            mEsLoadCallback.onLoadError(exception);
        }
    }

    public boolean isNexusLoader() {
        return mLoader instanceof NexusRpkLoader;
    }

    /**
     * 1. 检查网络是否可用
     */
    private final Continuation<TaskEntity, TaskEntity> CHECK_NETWORK = task -> {
        TaskEntity taskEntity = task.getResult();
        taskEntity.setConnectedNetwork(CommonUtils.isConnectedToWifiOrEthernet(mContext.getApplicationContext()));
        return taskEntity;
    };

    /**
     * 开启循环检查网络是否可用
     */
    private void checkNetwork() {
        //每五秒检查一次网络
        scheduler.scheduleAtFixedRate(() -> {
            if (CommonUtils.isConnectedToWifiOrEthernet(mContext)) {
                scheduler.shutdownNow();
                loadEsContentTask();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 下载HP SO
     **/
    private final Continuation<TaskEntity, TaskEntity> PREPARE_SO_FROM_INTERNET = task -> {
        TaskEntity taskEntity = task.getResult();
        if (mDynamicSo) {
            TaskCompletionSource<Object> getSoResult = new TaskCompletionSource<>();
            ISoManager soManager = EsProxy.get().getSoManager();
            soManager.prepareSoFiles(EsSoLoaderAdapter.HP_SO_PKG, false, new ISoManager.Callback2() {
                @Override
                public void onSuccess() {
                    getSoResult.setResult("");
                }

                @Override
                public void onError(EsException e) {
                    getSoResult.setError(e);
                }

                @Override
                public void onDownloadProgress(int progress) {
                    TaskProgressManager.getInstance().updateTaskProgress("download_so", progress);
                }
            });
            Task<Object> t = getSoResult.getTask();
            t.waitForCompletion();
            if (t.isFaulted()) throw t.getError();
        }
        return taskEntity;
    };

    private final Continuation<TaskEntity, TaskEntity> LOAD_FROM_CACHE = task -> {
        TaskEntity taskEntity = task.getResult();
        taskEntity.setRpkFile(mLoader.getLocalCache());
        return taskEntity;
    };

    private final Continuation<TaskEntity, TaskEntity> DOWNLOAD_RPK = task -> {
        TaskEntity taskEntity = task.getResult();
        taskEntity.setRpkFile(mLoader.downloadRpk(taskEntity.getInfoEntity()));
        return taskEntity;
    };

    private final Continuation<TaskEntity, TaskEntity> LOCAL_RPK_FILE = task -> {
        TaskEntity taskEntity = task.getResult();
        taskEntity.setRpkFile(mLoader.getFromServer());
        return taskEntity;
    };

    private final Continuation<TaskEntity, TaskEntity> PRE_DOWNLOAD_RPK = task -> {
        TaskEntity taskEntity = task.getResult();
        taskEntity.setRpkFile(mLoader.preDownloadRpk(taskEntity.getInfoEntity()));
        return taskEntity;
    };

    private final Continuation<TaskEntity, TaskEntity> LOAD_RPK_INFO_FROM_SERVER = task -> {
        TaskEntity taskEntity = task.getResult();
        taskEntity.setInfoEntity(mLoader.getRpkInfoFromServer());
        return taskEntity;
    };

    private final Continuation<TaskEntity, TaskEntity> MAKE_RPK_VIEW = task -> {
        TaskEntity taskEntity = task.getResult();
        File file = taskEntity.getRpkFile();
        if (file == null) {
            if (!(mLoader instanceof DebugRpkLoader)) {
                return null;
            } else if (mEngine != null) { // 防止创建两次
                return null;
            }
        }
        if (mEngine != null) {
            L.logIF("destroy last instance");
            mEngine.destroy();
        }
        L.logIF("mk engine start");

        RpkInfo rpkInfo = mLoader.getRpkInfo();
        if (mLoader instanceof DebugRpkLoader) {
            String debugServer = mLoader.getAppData().getAppDownloadUrl();
            mEngine = EsEnginePool.get().createDebugEngine(debugServer, this::beforeMakeHippyView);
        } else {
            rpkInfo.setCodeDir(file);
            int engineType = mLoader.getAppData().isCard() ? EsEngine.TYPE_CARD : EsEngine.TYPE_APP;
            File runtimeFile = new File(rpkInfo.getCodeDir(), Constants.FILE_JS_VENDOR);
            mEngine = EsEnginePool.get().createHippyEngine(engineType, runtimeFile, rpkInfo.getRpkConfig());
            beforeMakeHippyView(mEngine.getInstance());
        }
        L.logIF("mk engine end");
        HippyRootView hippyRootView = makeHippyView();
        taskEntity.setHippyRootView(hippyRootView);
        return taskEntity;
    };

    private void beforeMakeHippyView(HippyEngine engine) {
        HippyEngineContext engineContext = engine.getEngineContext();

        // 设置图片加载类对应的EngineId
        HippyImageLoader imageLoaderAdapter = engineContext.getGlobalConfigs().getImageLoaderAdapter();
        if (imageLoaderAdapter instanceof EsImageLoaderAdapter) {
            EsData esData = mLoader.getAppData();
            ((EsImageLoaderAdapter) imageLoaderAdapter).setEngineId(engineContext.getEngineId(), esData.isDebug(), esData.getAppDownloadUrl());
        }

        // 设置当前正在运行的app的包名
        HippyExceptionHandlerAdapter exceptionHandler = engineContext.getGlobalConfigs().getExceptionHandler();
        if (exceptionHandler instanceof EsEngineExceptionHandlerAdapter) {
            ((EsEngineExceptionHandlerAdapter) exceptionHandler).setCurrentEsAppPackage(mLoader.getAppData().getEsPackage());
        }

        EsViewManager.get().markView2Engine((IEsViewer) mEsLoadCallback, engineContext);
        EsComponentManager.get().injectModulesAndComponents(engineContext);
    }

    /**
     * 渲染界面
     **/
    private final Continuation<TaskEntity, TaskEntity> RENDER_VIEW = task -> {
        TaskEntity taskEntity = task.getResult();
        HippyRootView view = taskEntity.getHippyRootView();
        if (view != null) {
            L.logIF("mk view success");
            EsViewManager.get().onAppViewRenderSuccess(mLoader.getAppData());
            if (mEsLoadCallback != null) {
                suspendViewWithConfig();
                mEsLoadCallback.onViewLoaded(view);
                // 应用打开回调
                if (mLoader instanceof ApiRpkLoader) {
                    mEsLoadCallback.onAppOpened(mLoader.getRecordInfo());
                }
                showDebugInfo(view);
            }
        }
        return taskEntity;
    };

    /**
     * 等待用户确认 confirm
     **/
    private final Continuation<File, File> REQUEST_USER_CONFIRM = task -> {
        File result = task.getResult();
        if (result == null) return null;
        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(mContext)
                        .setTitle("发现新版本")
                        .setNegativeButton("重新加载", (dialog, which) -> tcs.setResult(true))
                        .setPositiveButton("下次生效", (dialog, which) -> tcs.setResult(false)).show();
            }
        });

        Task<Boolean> userConfirmTask = tcs.getTask();
        userConfirmTask.waitForCompletion();
        return userConfirmTask.getResult() ? result : null;
    };

    /**
     * 显示加载的版本号
     **/
    private void showDebugInfo(View view) {
        if (!L.DEBUG) return;
        if (mLoader == null) return;
        EsData data = mLoader.getAppData();
        if (data == null) return;
        if (data.isDebug()) return;
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) return;

        TextView tv = parent.findViewById(R.id.es_rooview_version_text);
        if (tv == null) {
            tv = createDebugInfoTextView(view.getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            int margin = (int) PixelUtil.dp2px(5F);
            layoutParams.bottomMargin = margin;
            layoutParams.rightMargin = margin;
            parent.addView(tv, layoutParams);
        }
        RpkInfo rpkInfo = mLoader.getRpkInfo();
        tv.setText("v" + rpkInfo.getVersionName() + " (" +
                EsProxy.get().getEsKitVersionCode() + ")");
    }

    /**
     * 显示rpk更新弹窗，1:显示；0:不显示
     *
     * @param infoEntity
     */
    private void showRpkUpdateDialog(TaskEntity infoEntity) {
    }

    private TextView createDebugInfoTextView(Context context) {
        TextView tv = new TextView(context);
        tv.setId(R.id.es_rooview_version_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.eskit_color_white_50));
        tv.setBackgroundColor(ContextCompat.getColor(context, R.color.color_es_default_bg));
        return tv;
    }

    /**
     * 根据配置suspendView
     **/
    private void suspendViewWithConfig() {
        try {
            EsMap splashCfg = mLoader.getRpkInfo().getRpkConfig().getMap(Constants.PACKAGE_JSON_K_SPLASH);
            if (splashCfg != null) {
                String msg = splashCfg.getString(Constants.PACKAGE_JSON_K_SPLASH_MSG);
                if (msg == null) msg = "";
                EsNativeModule.toSuspendLoadingView(msg);
            }
        } catch (Exception e) {
            L.logW("suspend view", e);
        }
    }

    private HippyRootView makeHippyView() {
        return mEngine.loadModule(mContext, mLoader, new HippyEngine.ModuleListener() {
            @Override
            public void onLoadCompleted(HippyEngine.ModuleLoadStatus statusCode, String msg, HippyRootView hippyRootView) {
                if (L.DEBUG) L.logD("render view statusCode: " + statusCode + ", msg: " + msg);
                try {
                    mLoader.getAppData().setLoadState(statusCode.value());
                } catch (Exception ignore) {
                }
            }

            @Override
            public boolean onJsException(HippyJsException e) {
                L.logWF("render view exception: " + (e == null ? "" : e.getMessage() + " " + e.getStack()));
                return false;
            }
        });
    }

    public void deleteApp() {
//        if (mAppData == null) return;
//        String esPkgName = mAppData.getEsPackage();
//        File rpkDir = new File(Constants.getEsAppDir(), esPkgName);
//        Executors.get().execute(() -> {
//            try {
//                FileUtils.delete(rpkDir);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    public void destroy() {
        if (L.DEBUG) L.logD("destroy");
        quit();
    }

    private void quit() {
        if (mEngine != null) {
            mTCS.cancel();
            mEngine.destroy();
            reset();
        }
    }

    private void reset() {
        mEngine = null;
        mContext = null;
        mEsLoadCallback = null;
        mTCS = null;
        mLoader = null;
    }

    public void dispatchKeyEvent(KeyEvent event) {
        ITakeOverKeyEventListener takeOverKey = EsViewManager.get().getTakeOverKeyEventListener(mContext.hashCode());
        if (takeOverKey != null) {
            L.logIF("take_over check: " + takeOverKey);
        }
        if (takeOverKey != null && takeOverKey.dispatchKeyEvent(event)) return;
        EsMap data = new EsMap();
        data.pushInt("action", event.getAction());
        data.pushInt("keyCode", event.getKeyCode());
        data.pushInt("keyRepeat", event.getRepeatCount());
        try {
            sendNativeEvent(Constants.GLOBAL_EVENT.EVT_DISPATCH_KEY, data);
        } catch (Exception e) {
            L.logW("dispatch key", e);
        }
        if (L.DEBUG) L.logD("onDispatchKeyEvent " + data);
    }

    public boolean onBackPressed(EsEmptyCallback callback) {
        return mEngine != null && mEngine.getInstance().onBackPressed(callback::onCallback);
    }

    public void onResume() {
        if (mEngine != null) mEngine.getInstance().onEngineResume();
    }

    public void onPause() {
        if (mEngine != null) mEngine.getInstance().onEnginePause();
    }

    public EsData getEsData() {
        return mLoader == null ? null : mLoader.getAppData();
    }

    public IEsAppLoadCallback getEsLoadCallback() {
        return mEsLoadCallback;
    }

    public HippyEngineContext getEngineContext() {
        return mEngine == null ? null : mEngine.getInstance().getEngineContext();
    }

    public void sendUIEvent(int tagId, String name, Object params) {
        try {
            if (mEngine != null) mEngine.sendUIEvent(tagId, name, params);
        } catch (Exception e) {
            L.logW("ui event", e);
        }
    }

    public File getAppDir() {
        return getAppRootDir();
    }

    public File getAppRootDir() {
        return mLoader == null ? null : mLoader.getRpkInfo().getAppDir();
    }

    public File getAppRuntimeDir() {
        return mLoader == null ? null : mLoader.getRpkInfo().getCodeDir();
    }

    public void sendNativeEvent(String name, Object params) {
        try {
            if (mEngine != null) mEngine.sendNativeEvent(name, params);
        } catch (Exception e) {
            L.logW("native event", e);
        }
    }

    public EsMap getPackageJsonData() {
        return mLoader == null ? null : mLoader.getRpkInfo().getRpkConfig();
    }
}