package com.quicktvui.support.longimage.utils;

import android.view.View;

import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;

/**
 * @auth: njb
 * @date: 2022/11/13 13:03
 * @desc: 长图事件工具类
 */
public class LongImageEventUtils {
    public static final String LONG_IMAGE_EVENT = "onLongImageEvent";//vue中定义的方法
    public static final String LONG_IMAGE_ON_READY = "onReady";
    public static final String LONG_IMAGE_ON_IMAGE_LOADED= "onImageLoaded";
    public static final String LONG_IMAGE_ON_PREVIEW_LOAD_ERROR= "onPreviewLoadError";
    public static final String LONG_IMAGE_ON_IMAGE_LOAD_ERROR= "onImageLoadError";
    public static final String LONG_IMAGE_ON_TILE_LOAD_ERROR= "onTileLoadError";
    public static final String LONG_IMAGE_ON_PREVIEW_RELEASED= "onPreviewReleased";

    /**
     * 给longImageView发送事件
     */
    public static void sendLongImageEvent(View longImageView, String eventName, HippyMap map) {
        HippyMap params = new HippyMap();
        params.pushString("eventName", eventName);
        params.pushMap("params", map);
        if (longImageView != null) {
            HippyViewEvent event = new HippyViewEvent(LONG_IMAGE_EVENT);
            event.send(longImageView, params);
        }
    }
}
