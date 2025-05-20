package com.quicktvui.sdk.core.internal.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sunrain.toolkit.utils.FileUtils;

import java.io.File;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.assets.AssetBundleManager;
import com.quicktvui.sdk.core.assets.AssetData;
import com.quicktvui.sdk.core.assets.AssetLoadCallback;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.utils.TaskProgressManager;

/**
 * <br>
 * 从指定文件/文件夹加载快应用
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-27 18:38
 */
public class FileRpkLoader extends AbstractLoader {

    public FileRpkLoader(@NonNull EsData app) {
        super(app);
    }

    @Nullable
    @Override
    protected File getFromLocalCache() throws Exception {
        FileUtils.delete(getCodeDir());
        return null;
    }

    @Nullable
    @Override
    public File getFromServer() throws Exception {
        String uri = mAppData.getAppDownloadUrl();

        if (uri.endsWith(Constants.PATH_RPK_SUFFIX)
                || uri.endsWith(Constants.PATH_ZIP_SUFFIX)) {

            AssetData data = new AssetData(
                    mAppData.getEsPackage(),
                    0.1F,
                    uri);

            File codeDir = AssetBundleManager.loadAppAsync(data,new AssetLoadCallback(){
                @Override
                public void downloading(int progress) {
                    TaskProgressManager.getInstance().updateTaskProgress("download_rpk", progress);
                }
            });
            setCodeDir(codeDir);
        } else {
            setCodeDir(new File(uri.substring(7)));
        }
        return mRpk.getCodeDir();
    }
}
