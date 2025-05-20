package com.quicktvui.support.imageloader;

import com.quicktvui.sdk.base.args.EsMap;


public class Request {
    public String url;
    public int width;
    public int height;
    public int priority;
    public boolean circle;
    public boolean fullQuality;
    public boolean useMemoryCache;
    // 0: PREFER_RGB_565, 1: PREFER_ARGB_8888
    public int format = 0;

    public Request(EsMap params) {
        url = params.getString("url");
        width = params.getInt("width");
        height = params.getInt("height");
        priority = params.getInt("priority");
        circle = params.getBoolean("circle");
        fullQuality = params.getBoolean("fullQuality");
        format = params.getInt("format");
        useMemoryCache = !params.containsKey("memoryCache") || params.getBoolean("memoryCache");
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", priority=" + priority +
                ", priority=" + priority +
                ", format=" + format +
                ", fullQuality=" + fullQuality +
                '}';
    }
}