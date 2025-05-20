package com.quicktvui.sdk.core.mediasession.manager.impl;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.core.mediasession.callback.EsCommonMediaSessionCallback;
import com.quicktvui.sdk.core.mediasession.manager.IServiceManager;

/**
 * <br>
 * Walleve
 * <br>
 */
public class MCommonServiceImpl implements IServiceManager {

    protected PlaybackStateCompat.Builder mPlaybackStateCompatBuilder;
    protected EsCommonMediaSessionCallback mCallback;

    @Override
    public void attachToSession(MediaSessionCompat session) {
        mPlaybackStateCompatBuilder = createPlaybackState();
        session.setPlaybackState(mPlaybackStateCompatBuilder.build());
        mCallback = createCallback();
        session.setCallback(mCallback);
    }

    @SuppressLint("RestrictedApi")
    private void printSession(MediaSessionCompat session) {
        if (!L.DEBUG) return;
        StringBuilder sb = new StringBuilder("mds session\n------------------------");
        try {
            sb.append("\ncalling pkg:").append(session.getCallingPackage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sb.append("\ncontroller pkg:").append(session.getController().getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sb.append("\nremote client:").append(session.getRemoteControlClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sb.append("\nsession token:").append(session.getSessionToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.logD(sb.append("\n------------------------").toString());
    }

    protected PlaybackStateCompat.Builder createPlaybackState() {
        return new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_STOP |
                                PlaybackStateCompat.ACTION_REWIND |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_FAST_FORWARD |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SEEK_TO |
                                PlaybackStateCompat.ACTION_SET_RATING |
                                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM |
                                PlaybackStateCompat.ACTION_PLAY_FROM_URI |
                                PlaybackStateCompat.ACTION_SET_REPEAT_MODE |
                                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                );
    }

    protected EsCommonMediaSessionCallback createCallback() {
        return new EsCommonMediaSessionCallback();
    }

    @Override
    public void handleActionFromVueToService(MediaSessionCompat session, String event, Bundle extras) {
        printSession(session);
    }

    @NonNull
    @Override
    public String toString() {
        return "MCommonService";
    }
}
