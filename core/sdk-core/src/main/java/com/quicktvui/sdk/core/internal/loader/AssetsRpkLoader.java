package com.quicktvui.sdk.core.internal.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sunrain.toolkit.utils.FileUtils;

import java.io.File;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.assets.AssetBundleManager;
import com.quicktvui.sdk.core.assets.AssetData;
import com.quicktvui.sdk.core.assets.AssetLoadCallback;
import com.quicktvui.sdk.core.utils.EsDecode;
import com.quicktvui.sdk.core.utils.TaskProgressManager;

/**
 * <br>
 * 从Assets加载快应用
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-28 10:35
 */
public class AssetsRpkLoader extends AbstractLoader {

    public AssetsRpkLoader(@NonNull EsData app) {
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
        AssetData data = new AssetData(
                mAppData.getEsPackage(),
                0F,
                uri).versionStr(mWantVersion);

        File codeDir = AssetBundleManager.loadAppAsync(data, new AssetLoadCallback() {

            private boolean isEnc = false;

            @Override
            public String beforeDownload(String url) {
                isEnc = !TextUtils.isEmpty(url) && url.contains("enc");
                return super.beforeDownload(url);
            }

            @Override
            public File afterDownload(File file) {
                if (isEnc) {
                    // 需要解密zip文件
                    File decodeFile = new File(file.getParent(), "2_" + file.getName());
                    EsDecode.decryptRpk(file, decodeFile);
                    FileUtils.delete(file);
                    return decodeFile;
                }
                return super.afterDownload(file);
            }

            @Override
            public void downloading(int progress) {
                TaskProgressManager.getInstance().updateTaskProgress("download_rpk", progress);
            }
        });

        setCodeDir(codeDir);
        return mRpk.getCodeDir();
    }
}
