package com.quicktvui.sdk.core.internal.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sunrain.toolkit.utils.FileUtils;

import java.io.File;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.assets.AssetBundleManager;
import com.quicktvui.sdk.core.assets.AssetData;
import com.quicktvui.sdk.core.assets.AssetLoadCallback;
import com.quicktvui.sdk.core.utils.TaskProgressManager;

/**
 * <br>
 * 从URL地址加载
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-28 11:09
 */
public class HttpRpkLoader extends AbstractLoader {

    public HttpRpkLoader(@NonNull EsData app) {
        super(app);
    }

    @Nullable
    @Override
    public File getFromServer() throws Exception {

        float ver = 0.1F;

        File localCache = super.getFromLocalCache();
        FileUtils.delete(localCache);

        String url = mAppData.getAppDownloadUrl();

        AssetData data = new AssetData(
                mAppData.getEsPackage(),
                ver,
                url)
                .autoRemove(false);

        File codeDir = AssetBundleManager.loadAppAsync(data, new AssetLoadCallback() {
            @Override
            public void downloading(int progress) {
                TaskProgressManager.getInstance().updateTaskProgress("download_rpk", progress);
            }
        });

        if (!mHasLocalCache) {
            setCodeDir(codeDir);
            return codeDir;
        }

        return null;
    }
}
