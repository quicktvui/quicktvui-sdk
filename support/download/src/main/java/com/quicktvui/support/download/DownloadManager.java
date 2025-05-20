package com.quicktvui.support.download;

import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DownloadManager implements DownloadListener {

    private static DownloadManager instance;
    protected List<DownloadListener> listenerList =
            Collections.synchronizedList(new ArrayList<>());

    protected Map<Download, DownloadTask> downloadTaskMap =
            Collections.synchronizedMap(new HashMap<>());

    private File downloadCacheDir;

    private DownloadConfiguration configuration;

    private DownloadManager() {
    }

    public static DownloadManager getInstance() {
        synchronized (DownloadManager.class) {
            if (instance == null) {
                instance = new DownloadManager();
            }
        }
        return instance;
    }

    public void init(DownloadConfiguration configuration) {
        this.configuration = configuration;
        this.downloadCacheDir = configuration.downloadCacheDir;
        if (L.DEBUG) {
            L.logD("#---------init---------->>>" + configuration);
        }
    }

    public void download(Download download) {
        if (download == null
                || TextUtils.isEmpty(download.getFileUrl())
                || TextUtils.isEmpty(download.getFileName())) {
            if (L.DEBUG) {
                L.logD("#---------download-----参数错误----->>>" + download);
            }
            return;
        }

        if (downloadTaskMap != null
                && downloadTaskMap.containsKey(download)) {
            if (L.DEBUG) {
                L.logD("#---------download------已经存在下载任务----->>>" + download);
            }
            return;
        }

        //
        File downloadFile = new File(downloadCacheDir, download.getFileName());

        if (L.DEBUG) {
            L.logD("#---------download------下载文件----->>>" + downloadFile.getAbsolutePath());
        }

        download.setFile(downloadFile);
        //
        DownloadTask downloadTask = new DownloadTask(this.configuration);
        downloadTask.setDownloadListener(this);
        downloadTask.download(download);
        //
        downloadTaskMap.put(download, downloadTask);

        //
        if (L.DEBUG) {
            L.logD(download + "#---------download-----END------>>>" + downloadTaskMap.size());
        }
    }

    public void start(Download download) {
        if (downloadTaskMap != null && download != null &&
                !TextUtils.isEmpty(download.getFileUrl())) {
            DownloadTask downloadTask = downloadTaskMap.get(download);
            if (downloadTask != null) {
                if (L.DEBUG) {
                    L.logD("#------------start---------->>>" + download);
                }
                downloadTask.start();
            }
        } else {
            if (L.DEBUG) {
                L.logD("#------------start----没有找到任务------>>>" + download);
            }
        }
    }

    public void stop(Download download) {
        if (downloadTaskMap != null && download != null &&
                !TextUtils.isEmpty(download.getFileUrl())) {
            DownloadTask downloadTask = downloadTaskMap.get(download);
            if (downloadTask != null) {
                if (L.DEBUG) {
                    L.logD("#------------stop---------->>>" + download);
                }
                downloadTask.stop();
            }
        } else {
            if (L.DEBUG) {
                L.logD("#------------stop----没有找到任务------>>>" + download);
            }
        }
    }

    public void cancel(Download download) {
        if (L.DEBUG) {
            L.logD("#---------cancel------START----->>>" + download);
        }
        if (downloadTaskMap != null && download != null) {
            DownloadTask downloadTask = downloadTaskMap.get(download);
            downloadTask.cancel();
            downloadTask.setDownloadListener(null);
            downloadTaskMap.remove(download);
        } else {
            if (L.DEBUG) {
                L.logD("#------------cancel----没有找到任务------>>>" + download);
            }
        }
        if (L.DEBUG) {
            L.logD("#---------cancel------END----->>>" + downloadTaskMap.size());
        }
    }

    public void registerDownloadListener(DownloadListener listener) {
        if (listener != null && !listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void unregisterDownloadListener(DownloadListener listener) {
        if (listener != null) {
            listenerList.remove(listener);
        }
    }


    /**
     * 下载状态
     */
    @Override
    public void onDownloadStatusChanged(DownloadStatus<DownloadMessage> downloadStatus) {
        if (L.DEBUG) {
            L.logD("#---------onDownloadStatusChanged-------->>>" + downloadStatus);
        }
        if (listenerList != null && listenerList.size() > 0) {
            for (DownloadListener listener : listenerList) {
                try {
                    listener.onDownloadStatusChanged(downloadStatus);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载进度
     */
    @Override
    public void onDownloadProgressChanged(DownloadStatus<DownloadProgress> progressStatus) {
        if (L.DEBUG) {
            L.logD("#---------onDownloadProgressChanged-------->>>" + progressStatus);
        }
        if (listenerList != null && listenerList.size() > 0) {
            for (DownloadListener listener : listenerList) {
                try {
                    listener.onDownloadProgressChanged(progressStatus);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void release() {
        if (L.DEBUG) {
            L.logD("#---------release-------->>>");
        }
        if (this.listenerList != null) {
            this.listenerList.clear();
        }
    }
}
