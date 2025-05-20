package com.quicktvui.sdk.core.engine;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sunrain.toolkit.bolts.tasks.Task;
import com.sunrain.toolkit.bolts.tasks.TaskCompletionSource;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.thread.Executors;
import com.tencent.mtt.hippy.HippyEngine;
import com.tencent.mtt.hippy.HippyInstanceLifecycleEventListener;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * <br>
 * 引擎池管理
 * <br>
 * <br>
 * Created by WeiPeng on 2023-09-18 10:05
 */
public class EsEnginePool {

    private final int POOL_SIZE_APP_ENGINE = 1;
    private final int POOL_SIZE_CARD_ENGINE = 2;

    private final ArrayBlockingQueue<EsEngine> mAppEnginePool = new ArrayBlockingQueue<>(POOL_SIZE_APP_ENGINE);
    private final ArrayBlockingQueue<EsEngine> mCardEnginePool = new ArrayBlockingQueue<>(POOL_SIZE_CARD_ENGINE);

    private RuntimeManager mRuntimeManager;

    /**
     * FIXME 回收引擎重复利用 (重复使用几次后，无法加载module)
     **/
    public void releaseEngine(EsEngine engine) {
        if (engine.getEngineType() == EsEngine.TYPE_APP) {
            mAppEnginePool.offer(engine);
            if (L.DEBUG) L.logD("put: " + mAppEnginePool.size());
        }
    }

    /**
     * 创建Debug引擎
     **/
    public EsEngine createDebugEngine(@NonNull String debugServer, @NonNull EsEngine.OnHippyEngineRestartListener listener) throws Exception {
        checkNotMainThread();
        return EngineCreator.createWithDebugRuntime(debugServer, listener);
    }

    /**
     * 创建引擎
     **/
    public EsEngine createHippyEngine(int engineType, @Nullable File runtimeFile, @NonNull EsMap vueConfig) throws Exception {
        checkNotMainThread();
        return createEngineWithType(engineType, runtimeFile, vueConfig);
    }

    /**
     * 创建应用引擎
     **/
    private EsEngine createEngineWithType(int engineType, @Nullable File runtimeFile, @NonNull EsMap vueConfig) throws Exception {
        if (runtimeFile != null && runtimeFile.exists()) {
            return EngineCreator.createWithCustomRuntime(engineType, runtimeFile);
        }
        ArrayBlockingQueue<EsEngine> poolQueue;
        int poolSize;
        if (engineType == EsEngine.TYPE_CARD) {
            poolQueue = mCardEnginePool;
            poolSize = POOL_SIZE_CARD_ENGINE;
        } else {
            poolQueue = mAppEnginePool;
            poolSize = POOL_SIZE_APP_ENGINE;
        }
        if (poolQueue.size() == 0) {
            poolQueue.put(EngineCreator.createWithPublicRuntime(engineType, mRuntimeManager, vueConfig));
        } else if (poolQueue.size() == 1) {
            Executors.get().execute(() -> {
                for (int i = 0; i < poolSize; i++) {
                    try {
                        poolQueue.put(EngineCreator.createWithPublicRuntime(engineType, mRuntimeManager, vueConfig));
                    } catch (Exception e) {
                        L.logW("put engine 2 pool", e);
                    }
                }
            });
        }
        return poolQueue.take();
    }

    public void clear() {
        synchronized (mAppEnginePool) {
            for (EsEngine engine : mAppEnginePool) {
                engine.destroy();
            }
            mAppEnginePool.clear();
        }

        synchronized (mCardEnginePool) {
            for (EsEngine engine : mCardEnginePool) {
                engine.destroy();
            }
            mCardEnginePool.clear();
        }
        if (mRuntimeManager != null) {
            mRuntimeManager.clear();
        }
    }

    private static final class EngineCreator {

        public static EsEngine createWithCustomRuntime(int engineType, File file) throws Exception {
            L.logIF("use custom runtime");
            return create(new EsEngine(engineType) {
                @Override
                protected void onBeforeCreateEngine(HippyEngine.EngineInitParams params) throws Exception {
                    if (L.DEBUG) L.logD("coreJSFilePath: " + file.getAbsolutePath());
                    params.coreJSFilePath = file.getAbsolutePath();
                }
            });
        }

        public static EsEngine createWithDebugRuntime(String server, EsEngine.OnHippyEngineRestartListener listener) throws Exception {
            L.logIF("use debug runtime");
            return create(new EsEngine(EsEngine.TYPE_DEBUG) {
                @Override
                protected void onBeforeCreateEngine(HippyEngine.EngineInitParams params) throws Exception {
                    params.debugMode = true;
                    params.debugServerHost = server;
                }

                @Override
                protected void onAfterCreateEngine(HippyEngine engine) {
                    engine.addRestartListener(() -> listener.onHippyEngineRestart(engine));
                }
            });
        }

        public static EsEngine createWithPublicRuntime(int engineType, RuntimeManager manager, EsMap vueConfig) throws Exception {
            L.logIF("use public " + (engineType == EsEngine.TYPE_APP ? "app" : "card") + " runtime");
            return create(new EsEngine(engineType) {
                @Override
                protected void onBeforeCreateEngine(HippyEngine.EngineInitParams params) throws Exception {

                    // 获取标记后缀
                    String runtimeSuffix = vueConfig == null ? null : vueConfig.getString(Constants.PACKAGE_JSON_K_RUNTIME_SUFFIX);
                    L.logIF("suffix: " + runtimeSuffix);

                    // 拼接后缀 详见 UWHF-4428 例如:
                    // 包名: eskit.runtime.app 后缀: tag1
                    // 拼接后:  eskit.runtime.app.tag1
                    String runtimePackageName = engineType == EsEngine.TYPE_APP ? Constants.ES_VUE_RUNTIME_PKG_APP : Constants.ES_VUE_RUNTIME_PKG_CARD;
                    if (runtimeSuffix != null) {
                        runtimePackageName += "." + runtimeSuffix;
                    }

                    float minVersion = vueConfig.getFloat(Constants.PACKAGE_JSON_K_RUNTIME_MIN_VERSION);
                    L.logIF("minVersion: " + minVersion);

                    RuntimeManager.VRuntime runtime = manager.getLatestRuntime(params.context, runtimePackageName);
                    if (runtime.version < minVersion) {
                        manager.invalidateRuntime(runtimePackageName);
                        runtime = manager.getLatestRuntime(params.context, runtimePackageName);
                    }
                    // 二次验证
                    if (runtime.version < minVersion) {
                        throw new EsException(Constants.ERR_RUNTIME_NOT_SUPPORT, "运行时库版本小于最低要求的版本" + minVersion);
                    }
                    if (L.DEBUG) L.logD("coreJSFilePath: " + runtime.path);
                    params.coreJSFilePath = runtime.path;
                }
            });
        }

        private static EsEngine create(EsEngine engine) throws Exception {
            TaskCompletionSource<Void> result = new TaskCompletionSource<>();
            engine.init((status, msg) -> {
                if (status == HippyEngine.EngineInitStatus.STATUS_OK) {
                    result.setResult(null);
                } else {
                    result.setError(new EsException(status.value(), msg));
                }
            });
            Task<Void> task = result.getTask();
            boolean initSuccess = task.waitForCompletion(20, TimeUnit.SECONDS);
            if (!initSuccess) {
                throw new EsException(Constants.ERR_INIT_TIMEOUT, "engine init timeout");
            }
            if (task.isFaulted()) {
                throw task.getError();
            }
            return engine;
        }

    }

    /**
     * 不能在主线程调用
     **/
    private void checkNotMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new EsException(Constants.ERR_RUNTIME_THREAD, "引擎只运行在子线程创建");
        }
    }

    private void init() {
        mRuntimeManager = new RuntimeManager();
    }

    private static final class HippyEngineLifecycleEventListener implements HippyInstanceLifecycleEventListener {

        private EsEngine mEngine;

        public HippyEngineLifecycleEventListener(EsEngine engine) {
            mEngine = engine;
        }

        @Override
        public void onInstanceLoad(int i) {

        }

        @Override
        public void onInstanceResume(int i) {

        }

        @Override
        public void onInstancePause(int i) {

        }

        @Override
        public void onInstanceDestroy(int i) {
            EsEnginePool.get().releaseEngine(mEngine);
            mEngine = null;
        }
    }

    //region 单例

    private static final class EsEnginePoolHolder {
        private static final EsEnginePool INSTANCE = new EsEnginePool();
    }

    public static EsEnginePool get() {
        return EsEnginePoolHolder.INSTANCE;
    }

    private EsEnginePool() {
        init();
    }

    //endregion

}
