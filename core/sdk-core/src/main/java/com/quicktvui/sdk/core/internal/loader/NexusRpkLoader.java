package com.quicktvui.sdk.core.internal.loader;

import static com.quicktvui.sdk.core.utils.CommonUtils.getRepositoryHost;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.net.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.assets.AssetBundleManager;
import com.quicktvui.sdk.core.assets.AssetData;
import com.quicktvui.sdk.core.assets.AssetLoadCallback;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.utils.HttpRequestUtils;
import com.quicktvui.sdk.core.utils.NexusRepo;
import com.quicktvui.sdk.core.utils.TaskProgressManager;
import com.quicktvui.sdk.base.EsException;

/**
 * <br>
 * NEXUS服务获取RPK
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-28 13:33
 */
public class NexusRpkLoader extends AbstractLoader {

    private final NexusRepo mNexusRepo;

    public NexusRpkLoader(@NonNull EsData data) {
        super(data);
        mNexusRepo = NexusRepo.from(getRepositoryHost(data))
                .withPackage(data.getEsPackage());
    }

    @Nullable
    @Override
    protected File getFromLocalCache() throws Exception {
        if (!TextUtils.isEmpty(mWantVersion)) {
            String latestVersion = getLatestVersionName();
            if (!Objects.equals(mWantVersion, latestVersion)) {
                return null;
            }
        }

        File localCache = super.getFromLocalCache();

//        if (localCache == null) { // 没有加载过的话，先从assets下找
//            Context context = Utils.getApp();
//            String[] assetsFiles = context.getAssets().list(Constants.PATH_RPK_ASSETS);
//            if (assetsFiles != null) {
//                for (String fileName : assetsFiles) {
//                    if (fileName.contains(mAppData.getEsPackage())) {
//                        EsData clone = mAppData.clone();
//                        clone.setAppLoadUri("assets://" + Constants.PATH_RPK_ASSETS + "/" + fileName);
//                        AssetsRpkLoader assetsRpkLoader = new AssetsRpkLoader(clone);
//                        File fromServer = assetsRpkLoader.getFromServer();
//                        setCodeDir(assetsRpkLoader.getCodeDir());
//                        return fromServer;
//                    }
//                }
//            }
//        }

        if (localCache != null) {
            String minVersion = mAppData.getEsMinVersion();
            if (!TextUtils.isEmpty(minVersion)) {
                RpkVersion current = RpkVersion.parse(getLatestVersionName());
                RpkVersion minVer = RpkVersion.parse(minVersion);
                if (current.isGreaterOrEqualsThan(minVer)) {
                    return localCache;
                }
            }
        }
        L.i("loader", "local cache validate");
        return null;
    }

    @Nullable
    @Override
    public File getFromServer() throws Exception {
        try {
            return tryGetFromServer();
        } catch (Exception e) {
            if (!mHasLocalCache) throw e;
        }
        return null;
    }

    private File tryGetFromServer() throws Exception {
        String pkg = mAppData.getEsPackage();
        JSONObject rpkInfo = requestJson(mNexusRepo.getOuterMetaUrl());
        String ver = mWantVersion;
        if (!TextUtils.isEmpty(ver)) {
            checkVersionValidate(rpkInfo, ver);
        } else { // 获取最新版本
            ver = rpkInfo.optString(Constants.Nexus.Meta.K_LATEST);
        }

        JSONObject rpkDetail = requestJson(mNexusRepo.getInnerMetaUrl(ver));

        AssetData data = new AssetData(pkg, 0L,
                mNexusRepo.getInnerUrl(ver, rpkDetail.optString(Constants.Nexus.Meta.K_APP_PATH))
        ).md5(rpkDetail.optString(Constants.Nexus.Meta.K_APP_MD5))
                .versionStr(ver)
                .autoRemove(false);

        if (L.DEBUG) L.logD("rpk: " + data.url);

        File codeDir = AssetBundleManager.loadAppAsync(data, new AssetLoadCallback() {
            @Override
            public File afterDownload(File file) {
//                File decodeFile = new File(file.getParent(), "2_" + file.getName());
//                EsDecode.decryptRpk(file, decodeFile);
//                FileUtils.delete(file);
//                return decodeFile;
                return file;
            }

            @Override
            public void downloading(int progress) {
                TaskProgressManager.getInstance().updateTaskProgress("download_rpk", progress);
            }
        });

        if (!mHasLocalCache) { // 没加载过
            setCodeDir(codeDir);
            return codeDir;
        }

        return null;
    }

    private void checkVersionValidate(JSONObject rpkInfo, String ver) {
        JSONArray versions = rpkInfo.optJSONArray(Constants.Nexus.Meta.K_VERSIONS);
        boolean isContains = false;
        if (versions != null) {
            int size = versions.length();
            for (int i = 0; i < size; i++) {
                if (Objects.equals(versions.optString(i), ver)) {
                    isContains = true;
                    break;
                }
            }
        }

        if (!isContains) {
            throw new EsException(Constants.ERR_APP_NOT_FOUND, "no version " + ver);
        }
    }

    public static @NonNull JSONObject requestJson(String url) throws Exception {
        if (L.DEBUG) L.logD("requestJson url: " + url);
        HttpRequest req = HttpRequestUtils.wrapper(HttpRequest.get(url));
        int code = req.code();
        if (code == 200) {
            return new JSONObject(req.body());
        }
        if (code == 404) {
            throw new EsException(Constants.ERR_APP_NOT_FOUND, req.message());
        }
        throw new EsException(Constants.ERR_SERVER, req.message()).setReasonCode(req.code());
    }

}
