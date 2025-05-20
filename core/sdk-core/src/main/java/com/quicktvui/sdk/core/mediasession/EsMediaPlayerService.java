package com.quicktvui.sdk.core.mediasession;

import android.content.Intent;
import android.support.v4.media.MediaBrowserCompat;

import com.sunrain.toolkit.utils.log.L;

import java.util.List;

/**
 * Create by weipeng on 2022/06/19 10:31
 * Describe
 */
public class EsMediaPlayerService extends BaseMediaSessionPlayerService {

    @Override
    public void onCreate() {
        super.onCreate();
        if (L.DEBUG) L.logD("mds create");
        EsMediaController.get().setCurrentService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    protected List<MediaBrowserCompat.MediaItem> getCurrentMediaList() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EsMediaController.get().setCurrentService(null);
    }
}
