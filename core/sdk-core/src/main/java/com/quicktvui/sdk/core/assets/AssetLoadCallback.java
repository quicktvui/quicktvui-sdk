package com.quicktvui.sdk.core.assets;

import java.io.File;

/**
 * <br>
 * 资源加载切入点
 * <br>
 * <br>
 * Created by WeiPeng on 2023-10-09 17:47
 */
public class AssetLoadCallback {

    /**
     * 下载之前
     **/
    public String beforeDownload(String url) {
        return url;
    }

    /**
     * 下载之后
     **/
    public File afterDownload(File file) {
        return file;
    }

    /**
     * 下载中
     **/
    public void downloading(int progress) {
    }
}
