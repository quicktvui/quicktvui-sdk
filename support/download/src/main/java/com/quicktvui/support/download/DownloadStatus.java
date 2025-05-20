package com.quicktvui.support.download;

/**
 *
 */
public class DownloadStatus<T> {

    private DownloadState state;
    private Download download;

    private T data;

    public DownloadState getState() {
        return state;
    }

    public void setState(DownloadState state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        this.download = download;
    }


    @Override
    public String toString() {
        return "DownloadStatus{" +
                "state=" + state +
                ", download=" + download +
                ", data=" + data +
                '}';
    }
}
