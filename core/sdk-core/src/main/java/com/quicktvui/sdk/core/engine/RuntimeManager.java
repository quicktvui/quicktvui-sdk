package com.quicktvui.sdk.core.engine;

import android.content.Context;
import android.text.format.DateUtils;

import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.utils.SpUtils;
import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.log.L;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <br>
 * Vendor版本管理
 * <br>
 * <br>
 * Created by WeiPeng on 2023-09-18 14:40
 */
public class RuntimeManager {

    private final long TIME_CACHE = DateUtils.HOUR_IN_MILLIS * 2;

    // 内存缓存当前使用的Runtime版本
    private final ConcurrentMap<String, VRuntime> mCachedRuntimeVersions = new ConcurrentHashMap<>();

    /** 网络缓存runtime **/
    private VRuntime fetchRuntimeFormServer(String runtimePackage) throws Exception {
        L.logIF("update runtime");
        return EsContext.get().getEsApiAdapter().fetchRuntimeInfo(runtimePackage);
    }

    public synchronized VRuntime getLatestRuntime(Context context, String runtimePackage) throws Exception {
        long currentTime = System.currentTimeMillis();
        VRuntime runtime = getRuntimeFromCache(runtimePackage);

        if (runtime == null     // 没有缓存
                || (currentTime - runtime.updateTime) > TIME_CACHE      // 缓存超时
                || !FileUtils.isFileExists(runtime.path)                // 文件不存在
        ) {
            runtime = fetchRuntimeFormServer(runtimePackage);
            saveRuntimeToCache(runtime);
            mCachedRuntimeVersions.put(runtimePackage, runtime);
        }

        return runtime;
    }

    public synchronized void invalidateRuntime(String runtimePackage) {
        mCachedRuntimeVersions.remove(runtimePackage);
        SpUtils.saveRuntimeUpdateTime(runtimePackage, -1);
    }

    /**
     * 从内存或磁盘获取缓存
     **/
    private VRuntime getRuntimeFromCache(String runtimePackage) {
        // 内存缓存
        VRuntime runtime = mCachedRuntimeVersions.get(runtimePackage);
        if (runtime == null) {
            // 磁盘缓存
            long updateTime = SpUtils.getRuntimeUpdateTime(runtimePackage);
            if (updateTime > 0) {
                L.logIF("use cache runtime");
                runtime = new VRuntime();
                runtime.version = SpUtils.getRuntimeVersion(runtimePackage);
                runtime.path = SpUtils.getRuntimePath(runtimePackage);
                runtime.updateTime = SpUtils.getRuntimeUpdateTime(runtimePackage);
            }
        }
        return runtime;
    }

    /**
     * 保存runtime使用信息
     **/
    private void saveRuntimeToCache(VRuntime runtime) {
        SpUtils.saveRuntimePath(runtime.packageName, runtime.path);
        SpUtils.saveRuntimeVersion(runtime.packageName, runtime.version);
        SpUtils.saveRuntimeUpdateTime(runtime.packageName, runtime.updateTime);
    }

    public void clear() {
        mCachedRuntimeVersions.clear();
    }

    public static final class VRuntime {
        public String packageName;
        public float version;
        public String path;
        public long updateTime;
    }
}
