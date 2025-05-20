package com.quicktvui.sdk.core.mediasession.callback;

import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_FAST_FORWARD;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PAUSE;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PLAY;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PLAY_FROM_MEDIA_ID;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PLAY_FROM_SEARCH;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PLAY_FROM_URI;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PREPARE;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PREPARE_FROM_MEDIA_ID;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PREPARE_FROM_SEARCH;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_PREPARE_FROM_URI;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_REWIND;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_SEEK_TO;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_SET_LOVE;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_SET_REPEAT_MODEL;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_SET_SHUFFLE_MODEL;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_SKIP_TO_NEXT;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_SKIP_TO_PREVIOUS;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_SKIP_TO_QUEUE_ITEM;
import static com.quicktvui.sdk.core.internal.Constants.LINK_ACTION_STOP;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.core.mediasession.EsMediaController;

/**
 * Create by weipeng on 2022/06/19 12:23
 * Describe  公共的回调方法，统一为sdk的action
 */
public class EsCommonMediaSessionCallback extends MediaSessionCompat.Callback {


    private String mPackageName;

    public EsCommonMediaSessionCallback() {
    }

    public EsCommonMediaSessionCallback(String packageName) {
        mPackageName = packageName;
    }

    /**
     * 当控制器向该会话发送自定义命令时调用。 会话的所有者可以处理自定义命令，但不是必需的
     * 对应MediaControllerCompat-sendCommand
     *
     * @param command The command name. 自定义命令可以是action
     * @param extras  Optional parameters for the command, may be null.
     * @param cb      A result receiver to which a result may be sent by the command, may be null.  可以暂时不关注
     */
    @Override
    public void onCommand(String command, Bundle extras, ResultReceiver cb) {
        if (L.DEBUG) L.logD("onCommand: " + command + " , " + extras);
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(command, extras);
    }


    /***
     * 接受到来自客户端的事件消息,对应MediaControllerCompat-sendCustomAction方法
     * @param action The action that was originally sent in the
     *            {@link PlaybackStateCompat.CustomAction}.
     * @param extras Optional extras specified by the
     *            {@link MediaControllerCompat}.
     */
    @Override
    public void onCustomAction(String action, Bundle extras) {
        if (L.DEBUG) L.logD("onCustomAction: " + action + "  " + extras);
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(action, extras);
    }

    /**
     * 快进。
     */
    @Override
    public void onFastForward() {
        if (L.DEBUG) L.logD("onFastForward");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_FAST_FORWARD, extras);
    }

    /**
     * 暂停。
     */
    @Override
    public void onPause() {
        if (L.DEBUG) L.logD("onPause");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PAUSE, extras);
    }


    /**
     * 开始播放的请求
     */
    @Override
    public void onPlay() {
        if (L.DEBUG) L.logD("onPlay");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PLAY, extras);
    }


    /**
     * 处理请求以播放由您的应用提供的特定mediaId
     *
     * @param mediaId
     * @param extras
     */
    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        if (L.DEBUG) {
            L.logD("onPlayFromMediaId: " + mediaId + "  " + extras);
        }
        Bundle bundle = new Bundle();
        if (extras != null) {

            extras.putString("mediaId", mediaId);
            bundle = extras;
        } else {
            bundle.putString("mediaId", mediaId);
        }
        bundle.putString("packageName", mPackageName);

        EsMediaController.get().processActionFromSession(LINK_ACTION_PLAY_FROM_MEDIA_ID, bundle);
    }


    /**
     * 覆盖以处理从搜索查询开始播放的请求。
     * 一个空的查询表示该应用可能播放任何音乐。 实施应该尝试做出关于玩什么的明智选择
     *
     * @param query
     * @param extras
     */
    @Override
    public void onPlayFromSearch(String query, Bundle extras) {
        if (L.DEBUG) L.logD("onPlayFromSearch " + query + "  " + extras);
        Bundle bundle = new Bundle();
        if (extras != null) {
            extras.putString("query", query);
            bundle = extras;
        } else {
            bundle.putString("query", query);
        }
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PLAY_FROM_SEARCH, bundle);
    }


    /**
     * 重写以处理请求以播放由URI表示的特定媒体项目
     *
     * @param uri
     * @param extras
     */
    @Override
    public void onPlayFromUri(Uri uri, Bundle extras) {
        if (L.DEBUG) L.logD("onPlayFromUri " + uri + "  " + extras);
        Bundle bundle = new Bundle();
        if (extras != null) {
            extras.putString("uri", uri.toString());
            bundle = extras;
        } else {
            bundle.putString("uri", uri.toString());
        }
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PLAY_FROM_URI, bundle);
    }

    /**
     * 重写以处理准备播放的请求。 在准备过程中，会话不应该保持音频焦点，以便允许其他会话无缝播放。 准备完成后，播放状态应更新为STATE_PAUSED 。
     */
    @Override
    public void onPrepare() {
        if (L.DEBUG) L.logD("onPrepare");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PREPARE, extras);

    }


    /**
     * 重写以处理准备由URI表示的特定媒体项目的请求。 在准备过程中，会话不应该保持音频焦点，以便允许其他会话无缝播放。
     * 准备完成后，播放状态应更新为STATE_PAUSED 。
     * 准备好的内容的播放应该从执行onPlay() 。
     * 覆盖onPlayFromUri(Uri, Bundle)以处理未经准备就开始播放的请求。。
     *
     * @param uri
     * @param extras
     */
    @Override
    public void onPrepareFromUri(Uri uri, Bundle extras) {
        Bundle bundle = new Bundle();
        if (extras != null) {
            extras.putString("uri", uri.toString());
            bundle = extras;
        } else {
            bundle.putString("uri", uri.toString());
        }
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PREPARE_FROM_URI, bundle);
    }


    /**
     * 重写以处理从搜索查询准备播放的请求。 一个空的查询表明应用程序可以准备任何音乐。
     * 实施应该尝试做出关于玩什么的明智选择。 在准备过程中，会话不应该保持音频焦点，以便允许其他会话无缝播放。
     * 准备完成后，播放状态应更新为STATE_PAUSED 。
     * 准备好的内容的播放应该从执行onPlay() 。 覆盖onPlayFromSearch(String, Bundle)以处理未经准备就开始播放的请求。
     *
     * @param query
     * @param extras
     */
    @Override
    public void onPrepareFromSearch(String query, Bundle extras) {
        Bundle bundle = new Bundle();
        if (extras != null) {
            extras.putString("query", query);
            bundle = extras;
        } else {
            bundle.putString("query", query);
        }
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PREPARE_FROM_SEARCH, bundle);
    }

    /**
     * 覆盖处理请求以准备播放由您的应用提供的特定mediaId。 在准备过程中，会话不应该保持音频焦点，以便允许其他会话无缝播放。
     * 准备完成后，播放状态应更新为STATE_PAUSED 。
     * 准备内容的回放应该在执行onPlay() 。 覆盖onPlayFromMediaId(String, Bundle)以处理未经准备就开始播放的请求。。
     *
     * @param mediaId
     * @param extras
     */
    @Override
    public void onPrepareFromMediaId(String mediaId, Bundle extras) {
        Bundle bundle = new Bundle();
        if (extras != null) {
            extras.putString("mediaId", mediaId);
            bundle = extras;
        } else {
            bundle.putString("mediaId", mediaId);
        }
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_PREPARE_FROM_MEDIA_ID, bundle);
    }


    /**
     * 重写以处理请求快退。
     */
    @Override
    public void onRewind() {
        if (L.DEBUG) L.logD("onRewind");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_REWIND, extras);
    }


    /**
     * 重写以处理请求，以毫秒为单位寻找特定的位置。
     *
     * @param position New position to move to, in milliseconds.
     */
    @Override
    public void onSeekTo(long position) {
        if (L.DEBUG) L.logD("onSeekTo " + position);
        Bundle bundle = new Bundle();
        bundle.putLong("position", position);
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_SEEK_TO, bundle);
    }


    /**
     * 重写以处理被评价的项目。
     *
     * @param rating The rating being set.
     */
    @Override
    public void onSetRating(RatingCompat rating) {
        if (L.DEBUG) L.logD("onSetRating " + rating);
        Bundle bundle = new Bundle();
        int ratingStyle = rating.getRatingStyle();
        bundle.putBoolean("isLove", ratingStyle == RatingCompat.RATING_HEART);
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_SET_LOVE, bundle);
    }

    /**
     * 覆盖处理请求以跳到下一个媒体项目。
     */
    @Override
    public void onSkipToNext() {
        if (L.DEBUG) L.logD("onSkipToNext");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_SKIP_TO_NEXT, extras);
    }


    /**
     * 覆盖处理请求以跳过前一个媒体项目
     */
    @Override
    public void onSkipToPrevious() {
        if (L.DEBUG) L.logD("onSkipToPrevious");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_SKIP_TO_PREVIOUS, extras);
    }


    /**
     * 重写以处理请求跳转到特定队列项。
     *
     * @param id
     */
    @Override
    public void onSkipToQueueItem(long id) {
        if (L.DEBUG) L.logD("onSkipToQueueItem " + id);
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_SKIP_TO_QUEUE_ITEM, bundle);
    }


    /**
     * 重写以处理请求停止播放。
     */
    @Override
    public void onStop() {
        if (L.DEBUG) L.logD("onStop");
        Bundle extras = new Bundle();
        extras.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_STOP, extras);
    }


    @Override
    public void onSetRepeatMode(int repeatMode) {
        if (L.DEBUG) L.logD("onSetRepeatMode " + repeatMode);
        Bundle bundle = new Bundle();
        bundle.putInt("model", repeatMode);
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_SET_REPEAT_MODEL, bundle);
    }

    @Override
    public void onSetShuffleMode(int shuffleMode) {
        if (L.DEBUG) L.logD("onSetShuffleMode: " + shuffleMode);
        Bundle bundle = new Bundle();
        bundle.putInt("model", shuffleMode);
        bundle.putString("packageName", mPackageName);
        EsMediaController.get().processActionFromSession(LINK_ACTION_SET_SHUFFLE_MODEL, bundle);
    }

}
