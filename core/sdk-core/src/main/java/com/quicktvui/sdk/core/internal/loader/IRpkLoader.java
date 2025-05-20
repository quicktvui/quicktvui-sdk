package com.quicktvui.sdk.core.internal.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.entity.InfoEntity;

/**
 * <br>
 * RPK加载器
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-27 14:13
 */
public interface IRpkLoader {

    String PREFIX_FILE = "file://";
    String PREFIX_ASSETS = "assets://";
    String PREFIX_HTTP = "http";
    String SUFFIX_HTTP = "38989";

    /**
     * 获取缓存
     **/
    @Nullable
    File getLocalCache() throws Exception;

    /**
     * 更新
     **/
    @Nullable
    File getFromServer() throws Exception;

    default File downloadRpk(InfoEntity infoEntity) throws Exception {
        return null;
    }

    default File preDownloadRpk(InfoEntity infoEntity) {
        return null;
    }

    default InfoEntity getRpkInfoFromServer() {
        return null;
    }

    default RecordInfo getRecordInfo() {
        return null;
    }

    /**
     * 获取启动参数
     **/
    EsData getAppData();

    /**
     * 获取当前rpk信息
     **/
    @NonNull
    RpkInfo getRpkInfo();

}
