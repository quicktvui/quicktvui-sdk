package com.quicktvui.support.download;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
@ESKitAutoRegister
public class ESDownloadModule implements IEsModule, IEsInfo, DownloadListener {

    //
    public static final String EVENT_PROP_DOWNLOAD_ID = "id";
    public static final String EVENT_PROP_DOWNLOAD_FILE_URL = "fileUrl";
    public static final String EVENT_PROP_DOWNLOAD_FILE_MD5 = "fileMD5";
    public static final String EVENT_PROP_DOWNLOAD_FILE_NAME = "fileName";
    public static final String EVENT_PROP_DOWNLOAD_FILE_TYPE = "fileType";
    public static final String EVENT_PROP_DOWNLOAD_FILE_PATH = "filePath";
    public static final String EVENT_PROP_DOWNLOAD_FILE_LENGTH = "fileLength";
    public static final String EVENT_PROP_DOWNLOAD_DOWNLOAD_LENGTH = "downloadLength";
    public static final String EVENT_PROP_DOWNLOAD_STATE = "state";
    public static final String EVENT_PROP_DOWNLOAD_CODE = "code";
    public static final String EVENT_PROP_DOWNLOAD_MESSAGE = "message";
    public static final String EVENT_PROP_DOWNLOAD = "download";
    public static final String EVENT_PROP_DOWNLOAD_PARAMS = "params";
    public static final String EVENT_PROP_DOWNLOAD_PARAMS_HEADER = "header";

    //下载进度
    public static final String EVENT_PROP_DOWNLOAD_DOWNLOAD_FILE_SIZE = "downloadSize";
    public static final String EVENT_PROP_DOWNLOAD_TOTAL_FILE_SIZE = "totalSize";

    private DownloadManager downloadManager;
    private Context context;

    @Override
    public void init(Context context) {
        this.context = context;
    }

    /**
     * 初始化
     */
    public void initDefaultDownload() {
        downloadManager = DownloadManager.getInstance();

        String rootPath = EsProxy.get().getEsAppPath(this);
        String miniProgramFilePath = rootPath + File.separator + "download";

        if (L.DEBUG) {
            L.logD("#-------initDefaultDownload------>>>>>" + miniProgramFilePath);
        }
        //
        File downloadCacheDir = new File(miniProgramFilePath);
        if (!downloadCacheDir.exists()) {
            downloadCacheDir.mkdirs();
        }
        DownloadConfiguration configuration = new DownloadConfiguration
                .Builder(context)
                .setDownloadCacheDir(downloadCacheDir)
                .build();
        downloadManager.init(configuration);
        downloadManager.registerDownloadListener(this);
    }

    /**
     * 初始化
     */
    public void initDownload(String downloadPath, long interpolator) {
        if (L.DEBUG) {
            L.logD("#-----initDownload--------->>>>>" + "downloadPath:" + downloadPath + "----" + "interpolator:" + interpolator + "----");
        }

        DownloadConfiguration.Builder builder =
                new DownloadConfiguration.Builder(context);
        //
        if (!TextUtils.isEmpty(downloadPath)) {
            String rootPath = EsProxy.get().getEsAppPath(this);
            String miniProgramFilePath = rootPath + downloadPath;

            if (L.DEBUG) {
                L.logD("#-----initDownload--------->>>>>" +
                        "miniProgramFilePath:" + miniProgramFilePath);
            }
            //
            File downloadCacheDir = new File(miniProgramFilePath);
            if (!downloadCacheDir.exists()) {
                downloadCacheDir.mkdirs();
            }
            builder.setDownloadCacheDir(downloadCacheDir);
        }
        //
        if (interpolator > 0) {
            builder.setInterpolator(interpolator);
        }
        DownloadConfiguration configuration = builder.build();
        downloadManager = DownloadManager.getInstance();
        downloadManager.init(configuration);
        downloadManager.registerDownloadListener(this);
    }


    public void initESDownload(String downloadPath, long interpolator) {
        if (L.DEBUG) {
            L.logD("#-----initDownload--------->>>>>" + "downloadPath:" + downloadPath + "----" + "interpolator:" + interpolator + "----");
        }

        DownloadConfiguration.Builder builder =
                new DownloadConfiguration.Builder(context);
        //
        if (!TextUtils.isEmpty(downloadPath)) {
            if (L.DEBUG) {
                L.logD("#-----initDownload--------->>>>>" +
                        "downloadPath:" + downloadPath);
            }
            //
            File downloadCacheDir = new File(downloadPath);
            if (!downloadCacheDir.exists()) {
                downloadCacheDir.mkdirs();
            }
            builder.setDownloadCacheDir(downloadCacheDir);
        }
        //
        if (interpolator > 0) {
            builder.setInterpolator(interpolator);
        }
        DownloadConfiguration configuration = builder.build();
        downloadManager = DownloadManager.getInstance();
        downloadManager.init(configuration);
        downloadManager.registerDownloadListener(this);
    }

    /**
     * 下载
     */
    public void download(EsMap params) {
        if (downloadManager == null) {
            if (L.DEBUG) {
                L.logD("#---download----downloadManager is not init------>>>>>" + params);
            }
            return;
        }
        Download download = esMapToDownload(params);
        if (download == null) {
            return;
        }
        if (L.DEBUG) {
            L.logD("#-------download-------->>>>>" + params);
        }
        downloadManager.download(download);
    }

    /**
     * 开始下载
     */
    public void start(EsMap params) {
        if (downloadManager == null) {
            if (L.DEBUG) {
                L.logD("#----start---downloadManager is not init------>>>>>" + params);
            }
            return;
        }
        Download download = esMapToDownload(params);
        if (download == null) {
            return;
        }
        if (L.DEBUG) {
            L.logD("#-------start-------->>>>>" + params);
        }

        downloadManager.start(download);
    }

    /**
     * 停止下载
     */
    public void stop(EsMap params) {
        if (downloadManager == null) {
            if (L.DEBUG) {
                L.logD("#----stop---downloadManager is not init------>>>>>" + params);
            }
            return;
        }
        Download download = esMapToDownload(params);
        if (download == null) {
            return;
        }
        if (L.DEBUG) {
            L.logD("#-------stop-------->>>>>" + params);
        }
        downloadManager.stop(download);
    }

    /**
     * 取消
     */
    public void cancel(EsMap params) {
        if (downloadManager == null) {
            if (L.DEBUG) {
                L.logD("#----cancel---downloadManager is not init------>>>>>" + params);
            }
            return;
        }
        Download download = esMapToDownload(params);
        if (download == null) {
            return;
        }
        if (L.DEBUG) {
            L.logD("#-------cancel-------->>>>>" + params);
        }
        downloadManager.cancel(download);
    }

    /**
     * 回收
     */
    public void release() {
        if (downloadManager == null) {
            if (L.DEBUG) {
                L.logD("#---release----downloadManager is not init------>>>>>");
            }
            return;
        }
        if (L.DEBUG) {
            L.logD("#-------release-------->>>>>");
        }
        downloadManager.release();
    }

    private Download esMapToDownload(EsMap params) {
        if (params == null) {
            return null;
        }
        int id = params.getInt(EVENT_PROP_DOWNLOAD_ID);
        String url = params.getString(EVENT_PROP_DOWNLOAD_FILE_URL);
        String md5 = params.getString(EVENT_PROP_DOWNLOAD_FILE_MD5);
        String fileName = params.getString(EVENT_PROP_DOWNLOAD_FILE_NAME);

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        //
        Download download = new Download();
        download.setId(id);
        download.setFileUrl(url);
        download.setFileMD5(md5);
        download.setFileName(fileName);

        //params
        EsMap downloadParamsMap = params.getMap(EVENT_PROP_DOWNLOAD_PARAMS);
        if (downloadParamsMap != null) {
            DownloadParams downloadParams = new DownloadParams();
            try {
                //header
                EsMap headerMap = downloadParamsMap.getMap(EVENT_PROP_DOWNLOAD_PARAMS_HEADER);
                if (headerMap != null && headerMap.size() > 0) {
                    Map<String, String> map = new HashMap<>();
                    for (String key : headerMap.keySet()) {
                        String value = headerMap.getString(key);
                        map.put(key, value);
                    }
                    downloadParams.setHeader(map);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            download.setParams(downloadParams);
        }
        return download;
    }

    private EsMap downloadToEsMap(Download download) {
        EsMap esMap = new EsMap();
        if (download != null) {
            esMap.pushInt(EVENT_PROP_DOWNLOAD_ID, download.getId());
            esMap.pushString(EVENT_PROP_DOWNLOAD_FILE_URL, download.getFileUrl());
            esMap.pushString(EVENT_PROP_DOWNLOAD_FILE_MD5, download.getFileMD5());
            esMap.pushString(EVENT_PROP_DOWNLOAD_FILE_NAME, download.getFileName());

            esMap.pushLong(EVENT_PROP_DOWNLOAD_FILE_LENGTH, download.getFileLength());
            esMap.pushLong(EVENT_PROP_DOWNLOAD_DOWNLOAD_LENGTH, download.getDownloadLength());
            //
            if (download.getFileType() != null) {
                esMap.pushString(EVENT_PROP_DOWNLOAD_FILE_TYPE, download.getFileType());
            }
            if (download.getFile() != null) {
                esMap.pushString(EVENT_PROP_DOWNLOAD_FILE_PATH, download.getFile().getAbsolutePath());
            }
        }
        return esMap;
    }

    @Override
    public void onDownloadStatusChanged(DownloadStatus<DownloadMessage> downloadStatus) {
        if (L.DEBUG) {
            L.logD("#-------onDownloadStatusChanged-------->>>>>" + downloadStatus);
        }
        EsMap eventMap = new EsMap();
        DownloadMessage message = downloadStatus.getData();
        if (message != null) {
            eventMap.pushInt(EVENT_PROP_DOWNLOAD_CODE, message.getCode());
            eventMap.pushString(EVENT_PROP_DOWNLOAD_MESSAGE, message.getMessage());
        }
        EsMap downloadMap = downloadToEsMap(downloadStatus.getDownload());
        eventMap.pushMap(EVENT_PROP_DOWNLOAD, downloadMap);
        eventMap.pushInt(EVENT_PROP_DOWNLOAD_STATE, downloadStatus.getState().ordinal());
        EsProxy.get().sendNativeEventTraceable(this, Events.EVENT_ON_DOWNLOAD_STATUS_CHANGED.toString(), eventMap);
    }

    @Override
    public void onDownloadProgressChanged(DownloadStatus<DownloadProgress> progressStatus) {
        EsMap eventMap = new EsMap();
        DownloadProgress progress = progressStatus.getData();
        if (progress != null) {
            eventMap.pushLong(EVENT_PROP_DOWNLOAD_DOWNLOAD_FILE_SIZE, progress.getDownloadSize());
            eventMap.pushLong(EVENT_PROP_DOWNLOAD_TOTAL_FILE_SIZE, progress.getTotalSize());
        }
        EsMap downloadMap = downloadToEsMap(progressStatus.getDownload());
        eventMap.pushMap(EVENT_PROP_DOWNLOAD, downloadMap);
        eventMap.pushInt(EVENT_PROP_DOWNLOAD_STATE, progressStatus.getState().ordinal());
        EsProxy.get().sendNativeEventTraceable(this, Events.EVENT_ON_DOWNLOAD_STATUS_CHANGED.toString(), eventMap);
    }

    @Override
    public void destroy() {
        if (L.DEBUG) {
            L.logD("#-------destroy-------->>>>>");
        }
        if (downloadManager != null) {
            downloadManager.unregisterDownloadListener(this);
        }
    }

    public enum Events {
        EVENT_ON_DOWNLOAD_STATUS_CHANGED("onDownloadStatusChanged");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        /*try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, BuildConfig.ES_KIT_BUILD_TAG_COUNT);
            map.pushString(IEsInfo.ES_PROP_INFO_PACKAGE_NAME, BuildConfig.LIBRARY_PACKAGE_NAME);
            map.pushString(IEsInfo.ES_PROP_INFO_CHANNEL, BuildConfig.ES_KIT_BUILD_TAG_CHANNEL);
            map.pushString(IEsInfo.ES_PROP_INFO_BRANCH, BuildConfig.ES_KIT_BUILD_TAG);
            map.pushString(IEsInfo.ES_PROP_INFO_COMMIT_ID, BuildConfig.ES_KIT_BUILD_TAG_ID);
            map.pushString(IEsInfo.ES_PROP_INFO_RELEASE_TIME, BuildConfig.ES_KIT_BUILD_TAG_TIME);
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        promise.resolve(map);
    }
}
