package com.quicktvui.sdk.core.mediasession.module;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;

import com.sunrain.toolkit.utils.Utils;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.sdk.core.internal.Constants;

/**
 * time：2024-10-29-16:40
 * author:wapple
 * describe:EsMediaSessionModule
 */
@ESKitAutoRegister
public class EsMediaSessionModule implements IEsModule {
    MediaBrowserCompat mediaBrowser;

    @Override
    public void init(Context context) {
    }

    /**
     * 提供module拉起的方法
     *
     * @param from 包名
     */
    public void bindServiceSelf(String from) {
        if (L.DEBUG) L.logD("md EsMediaSessionModule bindServiceSelf:"+from);
        Context context = Utils.getApp();
        Bundle rootHints = new Bundle();
        rootHints.putString(Constants.EVT_LINK_FROM_K, from);
        mediaBrowser = new MediaBrowserCompat(context,
                new ComponentName(context.getPackageName(), Constants.LINK_COMPONENT_CLASS_NAME),
                new MediaBrowserCompat.ConnectionCallback(),
                rootHints); // optional Bundle
        mediaBrowser.disconnect();
        mediaBrowser.connect();
    }

    /**
     * 退出页面调用
     */
    public void unBindService() {

        if (mediaBrowser != null) {
            mediaBrowser.disconnect();
            mediaBrowser = null;
        }
    }

    @Override
    public void destroy() {
        unBindService();
    }
}
