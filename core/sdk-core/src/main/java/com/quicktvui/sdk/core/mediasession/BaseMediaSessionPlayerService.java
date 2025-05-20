package com.quicktvui.sdk.core.mediasession;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.sunrain.toolkit.utils.log.L;

import java.util.List;

import com.quicktvui.sdk.core.mediasession.manager.IServiceManager;
import com.quicktvui.sdk.core.mediasession.manager.ServiceManagerFactory;

/**
 * Create by weipeng on 2022/06/19 10:28
 * Describe
 */
public abstract class BaseMediaSessionPlayerService extends MediaBrowserServiceCompat {

    private static final String TAG = "[-EsMediaPlayerService-]";

    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaSessionCompat mSession;
    private IServiceManager mServiceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if (L.DEBUG) L.logD("mds 媒体服务启动");
        try {
            mSession = new MediaSessionCompat(this, TAG);
            mSession.setActive(true);

            mSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS |
                            MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);

            setSessionToken(mSession.getSessionToken());
        } catch (Exception e) { // 异常 有的设备MediaSession有BUG
            L.logW("mds media session", e);
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        mServiceManager = ServiceManagerFactory.create(clientPackageName, clientUid, rootHints);
        if (L.DEBUG) L.logE("mds onGetRoot@@:"+mServiceManager);
        if(mServiceManager != null) {
            mServiceManager.attachToSession(mSession);
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        }

        return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
        if (MY_EMPTY_MEDIA_ROOT_ID.equals(parentId)) {
            result.sendResult(null);
            return;
        }
        if (L.DEBUG) L.logD("onLoadChildren");

        result.sendResult(getCurrentMediaList());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mSession != null) {
            mSession.setCallback(null);
            mSession.release();
        }
        if (L.DEBUG) L.logD("媒体服务关闭");
    }

    public void sendEvent(String event, Bundle extras) {
        //将sdk的转化成对应的 tcl的  baidu的action等
        if (mServiceManager != null) {
            mServiceManager.handleActionFromVueToService(mSession, event, extras);
        }
    }

    public boolean isActive() {
        return mSession != null && mSession.isActive();
    }

    protected abstract List<MediaBrowserCompat.MediaItem> getCurrentMediaList();

}
