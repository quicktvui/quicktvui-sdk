package com.quicktvui.sdk.core.internal.loader;

import static com.quicktvui.sdk.core.utils.CommonUtils.checkIsSafeFile;

import com.sunrain.toolkit.utils.FileIOUtils;
import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.utils.PathHelper;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-28 09:58
 */
public class RpkInfo {

    private final EsData mAppData;
    private final File mAppDir;
    private File mCodeDir;
    private String mVersion;
    private String mMd5;

    private final EsMap mRpkConfig = new EsMap();

    public RpkInfo(EsData data) {
        this.mAppData = data;
        this.mAppDir = PathHelper.getAppPath(data);
        FileUtils.createOrExistsDir(mAppDir);

        initEnvironment();
    }

    private void initEnvironment() {
        File[] files = mAppDir.listFiles();
        if (files == null) return;

        if (files.length > 1) {
            Arrays.sort(files, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
        }

        for (File file : files) {
            if(checkIsSafeFile(file)) continue;
            if (mCodeDir == null) {
                File codeDir = new File(mAppDir, file.getName() + File.separator + Constants.PATH_RPK_CODE);
                File indexJs = new File(codeDir, Constants.FILE_JS_INDEX);
                if (indexJs.exists()) {
                    setCodeDir(codeDir);
                } else { // 无效了
                    FileUtils.delete(file);
                }
            } else {
                boolean canDelete = true;
                List<EsData> runningApps = EsViewManager.get().getRunningApps();
                for (EsData app : runningApps) {
                    if (Objects.equals(app.getEsPackage(), mAppData.getEsPackage())) {
                        canDelete = false;
                        break;
                    }
                }
                if (canDelete) {
                    L.logDF("rm ver: " + file.getAbsolutePath());
                    FileUtils.delete(file);
                }
            }
        }
    }

    public File getAppDir() {
        return mAppDir;
    }

    public File getCodeDir() {
        return mCodeDir;
    }

    public void setCodeDir(File dir) {
        this.mCodeDir = dir;
        File versionDir = dir.getParentFile();
        this.mVersion = versionDir.getName();
        File md5Dir = new File(versionDir.getParentFile(), Constants.PATH_RPK_MD5);
        if (md5Dir.exists()) {
            File[] files = md5Dir.listFiles();
            if (files != null && files.length > 0) {
                this.mMd5 = files[0].getName();
            }
        }
        mAppData.setEsVersion(mVersion);
        try {
            updateRpkConfig();
        } catch (Exception e) {
            L.logWF("get rpk config fail, " + e.getMessage());
        }
    }

    private void updateRpkConfig() throws Exception {
        File configFile = new File(mCodeDir, Constants.FILE_PACKAGE_JSON);
        if (configFile.exists()) {
            mRpkConfig.pushJSONObject(
                    new JSONObject(
                            FileIOUtils.readFile2String(configFile)
                    )
            );
        }
    }

    public EsMap getRpkConfig() {
        return mRpkConfig;
    }

    public String getVersionName() {
        return mVersion;
    }

    public String getMd5() {
        return mMd5;
    }

    public EsData getAppData() {
        return mAppData;
    }
}
