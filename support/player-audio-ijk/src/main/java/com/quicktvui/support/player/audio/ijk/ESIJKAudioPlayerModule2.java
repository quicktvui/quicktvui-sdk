package com.quicktvui.support.player.audio.ijk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.ijk.base.IMediaPlayer;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.support.player.ijk.player.IjkLibManager;
import com.quicktvui.support.player.manager.player.PlayerStatusEnum;
import com.quicktvui.support.player.manager.base.PlayerBaseView;
import com.quicktvui.support.player.ijk.utils.FFConcatUtils;

/**
 *
 */
@ESKitAutoRegister
public class ESIJKAudioPlayerModule2 implements IEsModule, IEsInfo,
        ESIJKAudioPlayer.OnBufferingUpdateListener,
        ESIJKAudioPlayer.OnCompletionListener,
        ESIJKAudioPlayer.OnErrorListener,
        ESIJKAudioPlayer.OnPreparedListener,
        ESIJKAudioPlayer.OnInfoListener,
        ESIJKAudioPlayer.OnSeekCompleteListener {

    protected static final String ES_AUDIO_PLAYER_ID = "id";

    public enum Events {
        EVENT_ON_PLAYER_STATUS_CHANGED("onESAudioPlayerStatusChanged2"),
        EVENT_ON_PLAYER_ERROR("onESAudioPlayerError2"),
        EVENT_ON_PLAYER_INFO("onESAudioPlayerInfo2"),
        EVENT_ON_PLAYER_RATE_CHANGED("onESAudioPlayRateChanged2");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    //    private ESIJKAudioPlayer mediaPlayer;
    private Context context;
    private IjkLibManager ijkLibManager;
    private Handler handler;

    private SparseArray<ESIJKAudioPlayer> mAudioPlayers;

    @Override
    public void init(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onBufferingUpdate(ESIJKAudioPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(ESIJKAudioPlayer mediaPlayer) {
        EsMap esMap = new EsMap();
        esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
        esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_PLAYBACK_COMPLETED.ordinal());
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
    }

    @Override
    public boolean onError(ESIJKAudioPlayer mediaPlayer, int i, int i1) {
        EsMap eventMap = new EsMap();
        eventMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
        eventMap.pushInt(PlayerBaseView.EVENT_PROP_ERROR_CODE, i1);
        eventMap.pushString(PlayerBaseView.EVENT_PROP_ERROR_MESSAGE, i + "");
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_ERROR.toString(), eventMap);
        return false;
    }

    @Override
    public void onPrepared(ESIJKAudioPlayer mediaPlayer) {
        EsMap esMap = new EsMap();
        esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
        esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_PREPARED.ordinal());
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
    }

    @Override
    public boolean onInfo(ESIJKAudioPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            //
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                EsMap esMap = new EsMap();
                esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
                esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_BUFFER_START.ordinal());
                EsProxy.get().sendNativeEventTraceable(this,
                        Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
                break;
            //
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                EsMap map = new EsMap();
                map.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
                map.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_BUFFER_END.ordinal());
                EsProxy.get().sendNativeEventTraceable(this,
                        Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), map);
                break;
            default:
                break;
        }
        //onESMediaPlayerInfo
        EsMap infoMap = new EsMap();
        infoMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
        infoMap.pushInt(PlayerBaseView.EVENT_PROP_INFO_CODE, i);
        infoMap.pushString(PlayerBaseView.EVENT_PROP_INFO_MESSAGE, i1 + "");
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_INFO.toString(), infoMap);
        return false;
    }

    @Override
    public void onSeekComplete(ESIJKAudioPlayer mediaPlayer) {
        EsMap esMap = new EsMap();
        esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
        esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_SEEK_COMPLETED.ordinal());
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
    }

    // vue端初始化入口
    public void initMediaPlayer(EsPromise promise) {
        initIjkPlayerLibrary(promise);
    }

    private void initIjkMediaPlayer(EsPromise promise) {
        try {
//            try {
//                if (this.mediaPlayer != null) {
//                    this.stop();
//                    this.release();
//                }
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//            this.mediaPlayer = new ESIJKAudioPlayer(context, 1); // 走系统播放器

            ESIJKAudioPlayer mediaPlayer = new ESIJKAudioPlayer(context, -1);
            mediaPlayer.setEsPackageName(EsProxy.get().getEsPackageName(this));
            setPlayer(mediaPlayer);
            promise.resolve(mediaPlayer.hashCode());
            mediaPlayer.setOnBufferingUpdateListener2(this);
            mediaPlayer.setOnCompletionListener2(this);
            mediaPlayer.setOnErrorListener2(this);
            mediaPlayer.setOnPreparedListener2(this);
            mediaPlayer.setOnInfoListener2(this);
            mediaPlayer.setOnSeekCompleteListener2(this);
            //
            EsMap esMap = new EsMap();
            esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_SUCCESS.ordinal());
            EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule2.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);

        } catch (Throwable e) {
            e.printStackTrace();
            //
//            EsMap esMap = new EsMap();
//            esMap.pushInt(EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR.ordinal());
//            EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule2.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
            promise.reject(-1);
        }
    }

    private void initIjkPlayerLibrary(EsPromise promise) {
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
                            initIjkMediaPlayer(promise);
                        }
                    });
                } else {
                    if (L.DEBUG) {
                        L.logD("#----onLibraryLoadSuccess--handler is null------>>>>>");
                    }
//                    EsMap esMap = new EsMap();
//                    esMap.pushInt(EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR.ordinal());
//                    EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule2.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
                    promise.reject(-1);
                }
            }

            @Override
            public void onLibraryLoadError(Throwable e) {
                e.printStackTrace();
                if (L.DEBUG) {
                    L.logD("#----onLibraryLoadError------->>>>>");
                }
//                EsMap esMap = new EsMap();
//                esMap.pushInt(EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR.ordinal());
//                EsProxy.get().sendNativeEventTraceable(ESIJKAudioPlayerModule2.this, Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
                promise.reject(-1);
            }
        });
    }

    public void play(String id, String url) {
        this.play2(id, url, null);
    }

    public void play2(String id, String url, EsMap extraInfo) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.setDataSource(url, extraInfo);
        }
    }

    public void start(String id) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pause(String id) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            //
            EsMap esMap = new EsMap();
            esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_PAUSED.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }
    }

    public void resume(String id) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.resume();
            //
            EsMap esMap = new EsMap();
            esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_RESUMED.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }
    }

    public void seekTo(String id, int msec) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(msec);
            //
            EsMap esMap = new EsMap();
            esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_SEEK_START.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
        }
    }

    public void stop(String id) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            //
            EsMap esMap = new EsMap();
            esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
            esMap.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_BEFORE_STOP.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), esMap);
            mediaPlayer.stop();
            //
            EsMap map = new EsMap();
            map.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
            map.pushInt(PlayerBaseView.EVENT_PROP_PLAYER_STATUS, PlayerStatusEnum.PLAYER_STATE_STOP.ordinal());
            EsProxy.get().sendNativeEventTraceable(this,
                    Events.EVENT_ON_PLAYER_STATUS_CHANGED.toString(), map);
        }
    }

    public void reset(String id) {

    }

    public void release(String id) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            removePlayer(mediaPlayer);
            mediaPlayer = null;
        }
    }

    public void isPlaying(String id, EsPromise esPromise) {
        boolean isPlaying = false;
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            isPlaying = mediaPlayer.isPlaying();
        }
        esPromise.resolve(isPlaying);
    }

    public void isPaused(String id, EsPromise esPromise) {
        boolean isPaused = false;
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            isPaused = mediaPlayer.isPaused();
        }
        esPromise.resolve(isPaused);
    }

    public void getDuration(String id, EsPromise esPromise) {
        try {
            long duration = -1;
            ESIJKAudioPlayer mediaPlayer = getPlayer(id);
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

    public void getCurrentPosition(String id, EsPromise esPromise) {
        try {
            long position = -1;
            ESIJKAudioPlayer mediaPlayer = getPlayer(id);
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

    public void getBufferPercentage(String id, EsPromise esPromise) {
        try {
            int percentage = -1;
            ESIJKAudioPlayer mediaPlayer = getPlayer(id);
            if (mediaPlayer != null) {
                percentage = mediaPlayer.getBufferPercentage();
            }
            esPromise.resolve(percentage);
        } catch (Throwable e) {
            e.printStackTrace();
            esPromise.reject(-1);
        }
    }

    public void setPlayRate(String id, float speed) {
        try {
            ESIJKAudioPlayer mediaPlayer = getPlayer(id);
            if (mediaPlayer != null) {
                mediaPlayer.setSpeed(speed);
                EsMap esMap = new EsMap();
                esMap.pushString(ES_AUDIO_PLAYER_ID, String.valueOf(mediaPlayer.hashCode()));
                esMap.pushString(PlayerBaseView.EVENT_PROP_PLAY_RATE, speed + "");
                EsProxy.get().sendNativeEventTraceable(this,
                        Events.EVENT_ON_PLAYER_RATE_CHANGED.toString(), esMap);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getCurrentPlayRate(String id, EsPromise esPromise) {
        float speed = 1;
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            speed = mediaPlayer.getSpeed();
        }
        esPromise.resolve(speed);
    }

    public void setVolume(String id, float volume) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public void setLeftRightVolume(String id, float leftVolume, float rightVolume) {
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    public void getLeftVolume(String id, EsPromise esPromise) {
        float leftVolume = -1;
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            leftVolume = mediaPlayer.getLeftVolume();
        }
        esPromise.resolve(leftVolume);
    }

    public void getRightVolume(String id, EsPromise esPromise) {
        float rightVolume = -1;
        ESIJKAudioPlayer mediaPlayer = getPlayer(id);
        if (mediaPlayer != null) {
            rightVolume = mediaPlayer.getRightVolume();
        }
        esPromise.resolve(rightVolume);
    }

    @Override
    public void destroy() {
        try {
            if (mAudioPlayers != null) {
                ESIJKAudioPlayer mediaPlayer;
                while (mAudioPlayers.size() > 0) {
                    mediaPlayer = mAudioPlayers.valueAt(0);
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    // 移除已处理的键值对，以避免重复遍历
                    mAudioPlayers.removeAt(0);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        FFConcatUtils.clearFFConcatS(context);
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

    private void setPlayer(ESIJKAudioPlayer player) {
        if (mAudioPlayers == null)
            mAudioPlayers = new SparseArray<>();
        mAudioPlayers.put(player.hashCode(), player);
    }

    private ESIJKAudioPlayer getPlayer(String id) {
        if (id == null) return null;
        if (mAudioPlayers != null)
            return mAudioPlayers.get(Integer.parseInt(id));
        return null;
    }

    private void removePlayer(ESIJKAudioPlayer player) {
        if (mAudioPlayers != null)
            mAudioPlayers.remove(player.hashCode());
    }
}
