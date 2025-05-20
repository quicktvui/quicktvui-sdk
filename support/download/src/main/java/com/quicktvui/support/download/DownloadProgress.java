package com.quicktvui.support.download;

/**
 *
 */
public class DownloadProgress {

    private long downloadSize;
    private long totalSize;

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public String toString() {
        return "DownloadProgress{" +
                "downloadSize=" + downloadSize +
                ", totalSize=" + totalSize +
                '}';
    }
}
