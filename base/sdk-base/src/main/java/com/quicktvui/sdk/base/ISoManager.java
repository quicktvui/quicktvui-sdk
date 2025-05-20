package com.quicktvui.sdk.base;

import java.io.File;

/**
 *
 */
public interface ISoManager {

    interface Callback {
        void onSuccess();
        void onError(EsException e);
    }

    interface Callback2 extends Callback{
        void onDownloadProgress(int progress);
    }

    /** 开启ClassLoaderHack **/
    void enableHackMode(boolean enable);

    /** 下载So **/
    void prepareSoFiles(String soTag, boolean cacheFistModel, Callback callback);

    /** 获取So的下载路径 **/
    File getSoFilePath(String soTag);

    /** 加载So **/
    void loadLibrary(String soTag, String libName);

}
