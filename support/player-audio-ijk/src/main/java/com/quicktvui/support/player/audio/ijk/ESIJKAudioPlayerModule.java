package com.quicktvui.support.player.audio.ijk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.player.audio.bean.PlayInfoBean;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.support.ijk.base.IMediaPlayer;

import com.quicktvui.support.player.ijk.player.IjkLibManager;
import com.quicktvui.support.player.manager.player.PlayerStatusEnum;
        import com.quicktvui.support.player.manager.base.PlayerBaseView;

/**
 *
 */
@ESKitAutoRegister
public class ESIJKAudioPlayerModule implements IEsModule, IEsInfo,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnSeekCompleteListener {

    public enum Events {
        EVENT_ON_PLAYER_STATUS_CHANGED("onESAudioPlayerStatusChanged"),
        EVENT_ON_PLAYER_ERROR("onESAudioPlayerError"),
        EVENT_ON_PLAYER_INFO("onESAudioPlayerInfo"),
        EVENT_ON_PLAYER_RATE_CHANGED("onESAudioPlayRateChanged");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private ESIJKAudioPlayer mediaPlayer;
    private Context context;
    private IjkLibManager ijkLibManager;
    private Handler handler;
    private PlayInfoBean playInfoBean;

    @Override
    public void init(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        EsMap esMap = new EsMap();
        esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_PLAYBACK_COMPLETED.ordinal());
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        EsMap eventMap = new EsMap();
        eventMap.pushInt(PlayerBaseView.EVENT_PROP_ERROR_CODE, i1);
        eventMap.pushString(PlayerBaseView.EVENT_PROP_ERROR_MESSAGE, i + "");
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_ERROR.toString(), eventMap);
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        EsMap esMap = new EsMap();
        esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_PREPARED.ordinal());
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        switch (i) {
            //
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                EsMap esMap = new EsMap();
                esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_BUFFER_START.ordinal());
                EsProxy.get().sendNativeEventTraceable(this,
                        Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
                break;
            //
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                EsMap map = new EsMap();
                map.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_BUFFER_END.ordinal());
                EsProxy.get().sendNativeEventTraceable(this,
                        Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), map);
                break;
            default:
                break;
        }
        //onESMediaPlayerInfo
        EsMap infoMap = new EsMap();
        infoMap.pushInt(PlayerBaseView.EVENT_PROP_INFO_CODE, i);
        infoMap.pushString(PlayerBaseView.EVENT_PROP_INFO_MESSAGE, i1 + "");
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_INFO.toString(), infoMap);
        return false;
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        EsMap esMap = new EsMap();
        esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_SEEK_COMPLETED.ordinal());
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
    }

    public void initMediaPlayer() {
        initIjkPlayerLibrary();
    }

    private void initIjkMediaPlayer() {
        try {
            try {
                if (this.mediaPlayer != null) {
                    this.stop();
                    this.release();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            this.mediaPlayer = new ESIJKAudioPlayer(context, 2); // 走ijk
            mediaPlayer.setEsPackageName(EsProxy.get().getEsPackageName(this));
            this.mediaPlayer.setOnBufferingUpdateListener(this);
            this.mediaPlayer.setOnCompletionListener(this);
            this.mediaPlayer.setOnErrorListener(this);
            this.mediaPlayer.setOnPreparedListener(this);
            this.mediaPlayer.setOnInfoListener(this);
            this.mediaPlayer.setOnSeekCompleteListener(this);
            //
            EsMap esMap = new EsMap();
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_SUCCESS.ordinal());
            EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);

            if (playInfoBean != null && playInfoBean.isHadPlay()) {
                play2(playInfoBean.getUrl(), playInfoBean.getExtraInfo());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            //
            EsMap esMap = new EsMap();
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR.ordinal());
            EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }
    }

    private void initIjkPlayerLibrary() {
        this.ijkLibManager = IjkLibManager.getInstance();
        this.ijkLibManager.init(context);
        this.ijkLibManager.loadLibrary(false, new IjkLibManager.IIjkLibLoadCallback() {
            @Override
            public void onLibraryLoadSuccess() {
                if (L.DEBUG) {
                    L.logD("#------onLibraryLoadSuccess----->>>>>");
                }
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (L.DEBUG) {
                                L.logD("#------onLibraryLoadSuccess----->>>>>");
                            }
                            initIjkMediaPlayer();
                        }
                    });
                } else {
                    if (L.DEBUG) {
                        L.logD("#----onLibraryLoadSuccess--handler is null------>>>>>");
                    }
                    EsMap esMap = new EsMap();
                    esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR.ordinal());
                    EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
                }
            }

            @Override
            public void onLibraryLoadError(Throwable e) {
                e.printStackTrace();
                if (L.DEBUG) {
                    L.logD("#----onLibraryLoadError------->>>>>");
                }
                EsMap esMap = new EsMap();
                esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR.ordinal());
                EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
            }
        });
    }

    public void play(String url) {
        this.play2(url, null);
    }

    public void play2(String url, EsMap extraInfo) {
        if (mediaPlayer != null) {
            mediaPlayer.setDataSource(url, extraInfo);
            if (playInfoBean != null) playInfoBean.reset();
        } else {
            if (playInfoBean == null) playInfoBean = new PlayInfoBean();
            playInfoBean.setHadPlay(true);
            playInfoBean.setUrl(url);
            playInfoBean.setExtraInfo(extraInfo);
        }
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            //
            EsMap esMap = new EsMap();
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_PAUSED.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.resume();
            //
            EsMap esMap = new EsMap();
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_RESUMED.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }
    }

    public void seekTo(int msec) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(msec);
            //
            EsMap esMap = new EsMap();
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_SEEK_START.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            //
            EsMap esMap = new EsMap();
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_BEFORE_STOP.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
            mediaPlayer.stop();
            //
            EsMap map = new EsMap();
            map.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_STOP.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), map);
        }
    }

    public void reset() {

    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void isPlaying(EsPromise esPromise) {
        boolean isPlaying = false;
        if (mediaPlayer != null) {
            isPlaying = mediaPlayer.isPlaying();
        }
        esPromise.resolve(isPlaying);
    }

    public void isPaused(EsPromise esPromise) {
        boolean isPaused = false;
        if (mediaPlayer != null) {
            isPaused = mediaPlayer.isPaused();
        }
        esPromise.resolve(isPaused);
    }

    public void getDuration(EsPromise esPromise) {
        try {
            long duration = -1;
            if (mediaPlayer != null) {
                duration = mediaPlayer.getDuration();
                if (L.DEBUG) {
                    L.logD("#-------getDuration-------->>>>>" + duration);
                }
            }
            esPromise.resolve(duration);
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.reject(-1);
        }
    }

    public void getCurrentPosition(EsPromise esPromise) {
        try {
            long position = -1;
            if (mediaPlayer != null) {
                position = mediaPlayer.getCurrentPosition();
                if (L.DEBUG) {
                    L.logD("#-------getCurrentPosition-------->>>>>" + position);
                }
            }
            esPromise.resolve(position);
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.reject(-1);
        }
    }

    public void getBufferPercentage(EsPromise esPromise) {
        try {
            int percentage = -1;
            if (mediaPlayer != null) {
                percentage = mediaPlayer.getBufferPercentage();
            }
            esPromise.resolve(percentage);
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.reject(-1);
        }
    }

    public void setPlayRate(float speed) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setSpeed(speed);
                EsMap esMap = new EsMap();
                esMap.pushString(PlayerBaseView.EVENT_PROP_PLAY_RATE, speed + "");
                EsProxy.get().sendNativeEventTraceable(this,
                        Events.EVENT_ON_PLAYER_RATE_CHANGED.toString(), esMap);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getCurrentPlayRate(EsPromise esPromise) {
        float speed = 1;
        if (mediaPlayer != null) {
            speed = mediaPlayer.getSpeed();
        }
        esPromise.resolve(speed);
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public void setLeftRightVolume(float leftVolume, float rightVolume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    public void getLeftVolume(EsPromise esPromise) {
        float leftVolume = -1;
        if (mediaPlayer != null) {
            leftVolume = mediaPlayer.getLeftVolume();
        }
        esPromise.resolve(leftVolume);
    }

    public void getRightVolume(EsPromise esPromise) {
        float rightVolume = -1;
        if (mediaPlayer != null) {
            rightVolume = mediaPlayer.getRightVolume();
        }
        esPromise.resolve(rightVolume);
    }

    @Override
    public void destroy() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (playInfoBean != null)
                playInfoBean.reset();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        /*try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, BuildConfig.ES_KIT_BUILD_TAG_COUNT);
            map.pushString(IEsInfo.ES_PROP_INFO_PACKAGE_NAME, BuildConfig.LIBRARY_PACKAGE_NAME);
            map.pushString(IEsInfo.ES_PROP_INFO_CHANNEL, BuildConfig.ES_KIT_BUILD_TAG_CHANNEL);
            map.pushString(IEsInfo.ES_PROP_INFO_BRANCH, BuildConfig.ES_KIT_BUILD_TAG);
            map.pushString(IEsInfo.ES_PROP_INFO_COMMIT_ID, BuildConfig.ES_KIT_BUILD_TAG_ID);
            map.pushString(IEsInfo.ES_PROP_INFO_RELEASE_TIME, BuildConfig.ES_KIT_BUILD_TAG_TIME);
            map.pushBoolean(ES_PROP_PLAYER_IJK_DYNAMICALLY_LOAD_SO, BuildConfig.SUPPORT_DYNAMICALLY_LOAD_SO);
            map.pushBoolean(ES_PROP_ASYNC_INIT, BuildConfig.SUPPORT_ASYNC_INIT);
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        promise.resolve(map);
    }
}
