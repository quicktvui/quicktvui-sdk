package com.quicktvui.support.download;

/**
 *
 */
public interface IDownloadTask {
    void download(Download download);

    void start();

    void stop();

    void cancel();

    void setDownloadListener(DownloadListener listener);
}
