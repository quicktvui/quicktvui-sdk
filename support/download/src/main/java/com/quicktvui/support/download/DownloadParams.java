package com.quicktvui.support.download;

import java.util.Map;

/**
 *
 */
public class DownloadParams {

    private Map<String, String> header;

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "DownloadParams{" + "header=" + header + '}';
    }
}
