package com.quicktvui.support.download;

public interface DownloadListener {
    void onDownloadStatusChanged(DownloadStatus<DownloadMessage> downloadStatus);

    void onDownloadProgressChanged(DownloadStatus<DownloadProgress> progressStatus);
}
