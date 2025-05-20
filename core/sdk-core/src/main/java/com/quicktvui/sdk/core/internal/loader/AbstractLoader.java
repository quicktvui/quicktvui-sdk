package com.quicktvui.sdk.core.internal.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.internal.Constants;
import com.sunrain.toolkit.utils.Utils;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;

/**
 * <br>
 * 抽象加载
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-27 15:57
 */
public abstract class AbstractLoader implements IRpkLoader {

    protected final EsData mAppData;
    protected final RpkInfo mRpk;
    protected final String mWantVersion;
    protected boolean mHasLocalCache;
    protected boolean mIsForceReload;
    protected RecordInfo mRecordInfo;

    public AbstractLoader(@NonNull EsData app) {
        this.mAppData = app;
        this.mWantVersion = app.getEsVersion();
        this.mRpk = new RpkInfo(app);

        mRecordInfo = new RecordInfo();
        mRecordInfo.pkg = app.getEsPackage();
        mRecordInfo.versionName = mRpk.getVersionName();

        if (L.DEBUG) {
            L.logD("load ------- " + mAppData.getEsPackage());
        }
    }

    protected String getLatestVersionName() {
//        File codeDir = mRpk.getCodeDir();
//        if (codeDir != null) {
//            return codeDir.getParentFile().getName();
//        }
//        return "";
        return mRpk.getVersionName();
    }

    protected String getLatestMd5() {
        return mRpk.getMd5();
    }

    @Nullable
    protected EsData findAssetsPackage() {
        try {
            Context context = Utils.getApp();
            String[] assetsFiles = context.getAssets().list(Constants.PATH_RPK_ASSETS);
            if (assetsFiles != null) {
                for (String fileName : assetsFiles) {
                    if (fileName.contains(mAppData.getEsPackage())) {
                        EsData cloneData = mAppData.clone();
                        try {
                            String[] split = fileName.replace(Constants.PATH_RPK_SUFFIX, "").split("\\-");
                            if(split.length > 1){
                                cloneData.setEsVersion(split[1]);
                            }else {
                                cloneData.setEsVersion("0.1");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        cloneData.setAppLoadUri("assets://" + Constants.PATH_RPK_ASSETS + "/" + fileName);
                        return cloneData;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCodeDir(File dir) {
        mIsForceReload = mHasLocalCache;
        this.mRpk.setCodeDir(tryFixCodeDir(dir));
    }

    private File tryFixCodeDir(File dir) {
        if (new File(dir, Constants.FILE_JS_INDEX).exists()) return dir;

//        File fixDir = new File(dir, Constants.PATH_RPK_CODE);
//        if (new File(fixDir, Constants.FILE_JS_INDEX).exists()) return fixDir;

        throw new EsException(Constants.ERR_ZIP_FILES, "包结构有误");
    }

    @Nullable
    protected File getCodeDir() {
        return mRpk.getCodeDir();
    }

    @Override
    public EsData getAppData() {
        return mAppData;
    }

    @Nullable
    @Override
    public final File getLocalCache() throws Exception {
        File cache = getFromLocalCache();
        mHasLocalCache = cache != null;
        return cache;
    }

    protected File getFromLocalCache() throws Exception {
        return getCodeDir();
    }

    @NonNull
    @Override
    public RpkInfo getRpkInfo() {
        return mRpk;
    }

    @Override
    public RecordInfo getRecordInfo() {
        return mRecordInfo;
    }
}
