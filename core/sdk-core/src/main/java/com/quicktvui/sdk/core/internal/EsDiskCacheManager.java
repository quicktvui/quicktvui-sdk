package com.quicktvui.sdk.core.internal;

import com.sunrain.toolkit.utils.ConvertUtils;
import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.ThreadUtils;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.thread.Executors;

import java.io.File;
import java.util.List;

import com.quicktvui.sdk.core.EsKitStatus;
import com.quicktvui.sdk.core.EsManager;
import com.quicktvui.sdk.core.engine.EsEnginePool;
import com.quicktvui.sdk.base.EsSingleCallback;
import com.quicktvui.sdk.base.IDiskCacheManager;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.model.LocalCacheInfo;

/**
 *
 */
public class EsDiskCacheManager implements IDiskCacheManager {

    private static volatile boolean mIsDirty = true;

    public static void dirty() {
        L.logIF("cache-manager mark dirty");
        mIsDirty = true;
    }

    @Override
    public void getEsCacheInfo(EsSingleCallback<LocalCacheInfo> callback) {
        CacheScannerTask cacheScannerTask = new CacheScannerTask(callback);
        if (ThreadUtils.isMainThread()) {
            Executors.get().execute(cacheScannerTask);
        } else {
            cacheScannerTask.run();
        }
    }

    @Override
    public void clearAllCache(LocalCacheInfo info) {
        if (info == null) return;
        clearCache(
                info.getRpkCacheInfo(),
                info.getPluginCacheInfo(),
                info.getSoCacheInfo()
        );
    }

    @Override
    public void clearCache(LocalCacheInfo.CacheInfo... infos) {
        if (infos == null || infos.length == 0) return;
        Executors.get().execute(new CacheClearTask(infos));
    }

    private static final class CacheScannerTask implements Runnable {

        private EsSingleCallback<LocalCacheInfo> mCallback;

        public CacheScannerTask(EsSingleCallback<LocalCacheInfo> callback) {
            mCallback = callback;
        }

        @Override
        public void run() {
            EsKitStatus sdkInitStatus = EsManager.get().getSdkInitStatus();
            while (sdkInitStatus != EsKitStatus.STATUS_SUCCESS && sdkInitStatus != EsKitStatus.STATUS_ERROR) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (mCallback == null) return;
            LocalCacheInfo info = new LocalCacheInfo();

            File rpkDir = Constants.getEsAppDir();
            info.setRpkCacheInfo(buildCacheInfo(rpkDir));

            File cardDir = Constants.getEsCardDir();
            info.setCardInfo(buildCacheInfo(cardDir));

            File pluginDir = Constants.getPluginDir();
            info.setPluginCacheInfo(buildCacheInfo(pluginDir));

            File soDir = Constants.getEsSoDir();
            info.setSoCacheInfo(buildCacheInfo(soDir));

            File runtimeDir = Constants.getRuntimeDir();
            info.setRuntimeInfo(buildCacheInfo(runtimeDir));

            mCallback.onCallback(info);

            mCallback = null;
        }

        private LocalCacheInfo.CacheInfo buildCacheInfo(File file) {
            LocalCacheInfo.CacheInfo info = null;
            try {
                if (FileUtils.isFileExists(file)) {
                    info = new LocalCacheInfo.CacheInfo();
                    info.mPath = file.getAbsolutePath();
                    info.mSize = FileUtils.getLength(file);
                }
            } catch (Exception e) {
                L.logW("cal file size", e);
            }
            return info;
        }

    }

    private static final class CacheClearTask implements Runnable {

        private LocalCacheInfo.CacheInfo[] mTargets;

        public CacheClearTask(LocalCacheInfo.CacheInfo... targets) {
            this.mTargets = targets;
        }

        @Override
        public void run() {
            for (LocalCacheInfo.CacheInfo info : mTargets) {
                if (info == null) continue;
                try {
                    FileUtils.delete(info.mPath);
                } catch (Exception e) {
                    L.logW("del cache", e);
                }
            }
            mTargets = null;
        }
    }

    //region 自动清理

    public void startAutoClearTask() {
        L.logIF("cache-manager start auto clear");
        Executors.get().execute(new AutoClearTask());
    }

    private static final class AutoClearTask implements Runnable, EsSingleCallback<LocalCacheInfo> {

        private volatile static boolean isStart = false;

        @Override
        public void run() {
            if (isStart) return;
            while (true) {
                isStart = true;
                try {
                    Thread.sleep(Constants.ES_CACHE_CHECK_INTERVAL);

                    if(!EsDiskCacheManager.mIsDirty) {
                        L.logDF("cache-manager not dirty");
                        continue;
                    }

                    EsDiskCacheManager.mIsDirty = false;

                    if (L.DEBUG) L.logD("----------------------------------------");
                    if (L.DEBUG) L.logD("cache-manager cache checking...");

                    IDiskCacheManager manager = EsProxy.get().getDiskCacheManager();
                    if (manager == null) continue;
                    manager.getEsCacheInfo(this);

                } catch (Exception e) {
                    L.logW("auto clear", e);
                }
            }
        }

        @Override
        public void onCallback(LocalCacheInfo info) {
            check(info);
        }

        private void check(LocalCacheInfo info) {
            if (info == null) {
                L.logEF("获取缓存信息出错!");
                return;
            }
            long totalSize = info.getTotalSize();
            L.logIF("cache-manager size:" + ConvertUtils.byte2FitMemorySize(totalSize, 1));
            if (L.DEBUG) {
                L.logD("rpk: " + ConvertUtils.byte2FitMemorySize(info.getRpkCacheInfo() == null ? 0 : info.getRpkCacheInfo().mSize, 1));
                L.logD("card: " + ConvertUtils.byte2FitMemorySize(info.getCardInfo() == null ? 0 : info.getCardInfo().mSize, 1));
                L.logD("so: " + ConvertUtils.byte2FitMemorySize(info.getSoCacheInfo() == null ? 0 : info.getSoCacheInfo().mSize, 1));
                L.logD("plugin: " + ConvertUtils.byte2FitMemorySize(info.getPluginCacheInfo() == null ? 0 : info.getPluginCacheInfo().mSize, 1));
                L.logD("runtime: " + ConvertUtils.byte2FitMemorySize(info.getRuntimeInfo() == null ? 0 : info.getRuntimeInfo().mSize, 1));
            }

            if (totalSize < Constants.ES_CACHE_MAX_SIZE) return;
            L.logIF("cache-manager trigger clear");

            File[] caches = getLocalVersions();
            File[] runnings = getCurrentRunning();

            // 删除RPK
            cache:
            for (File cache : caches) {
                if (runnings != null && runnings.length > 0) {
                    for (File running : runnings) {
                        if (cache.equals(running)) continue cache;
                    }
                }
                FileUtils.delete(cache);
            }

            IDiskCacheManager manager = EsProxy.get().getDiskCacheManager();

            if (runnings == null || runnings.length == 0) {
                // 删除plugin
                deleteLocalCache(manager, info.getPluginCacheInfo());
                // 删除so
                deleteLocalCache(manager, info.getSoCacheInfo());
                // 删除runtime
                deleteLocalCache(manager, info.getRuntimeInfo());
                EsEnginePool.get().clear();
            }

            L.logIF("clear done.");
        }

        private long deleteLocalCache(IDiskCacheManager manager, LocalCacheInfo.CacheInfo info) {
            if (manager != null && info != null) {
                manager.clearCache(info);
                return info.mSize;
            }
            return 0;
        }

        private File[] getLocalVersions() {
            File dir = Constants.getEsAppDir();
            if (dir.exists()) {
                return dir.listFiles();
            }
            return new File[0];
        }

        private File[] getCurrentRunning() {
            File[] currentRunning = null;
            if (EsManager.get().isEsRunning()) {
                EsViewManager vm = EsViewManager.get();
                if (vm != null) {
                    List<EsViewRecord> tasks = vm.getRunningAppTasks();
                    currentRunning = new File[tasks.size()];
                    for (int i = 0; i < tasks.size(); i++) {
                        try {
                            currentRunning[i] = tasks.get(i).getViewer().getTaskContainer().getAppDir();
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
            return currentRunning;
        }
    }

    //endregion

}
