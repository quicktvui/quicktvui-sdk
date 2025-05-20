package com.quicktvui.support.player.audio.android;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

import android.support.annotation.Nullable;

import com.quicktvui.sdk.base.args.EsMap;
import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.quicktvui.support.player.manager.player.PlayerStatusEnum;
import com.quicktvui.support.player.manager.base.PlayerBaseView;

import com.quicktvui.support.player.audio.android.IAudioPlayerService;
import com.quicktvui.support.player.audio.android.IPlayerCallback;


public class ESAudioPlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnSeekCompleteListener {

    protected List<IPlayerCallback> listenerList = Collections.synchronizedList(new ArrayList<>());

    private ESAndroidAudioPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        if (L.DEBUG) {
            L.logD("---------onCreate------->>>>");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    public void initMediaPlayer(boolean looping) {
        try {
            try {
                if (this.mediaPlayer != null) {
                    this.mediaPlayer.stop();
                    this.mediaPlayer.release();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            this.mediaPlayer = new ESAndroidAudioPlayer(this, looping);
            this.mediaPlayer.setOnBufferingUpdateListener(this);
            this.mediaPlayer.setOnCompletionListener(this);
            this.mediaPlayer.setOnErrorListener(this);
            this.mediaPlayer.setOnPreparedListener(this);
            this.mediaPlayer.setOnInfoListener(this);
            this.mediaPlayer.setOnSeekCompleteListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (L.DEBUG) {
            L.logD("---------onDestroy------->>>>");
        }
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IAudioPlayerService.Stub stub = new IAudioPlayerService.Stub() {

        @Override
        public void initAudioPlayer(boolean looping) throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------initAudioPlayer------->>>>");
            }
            initMediaPlayer(looping);
        }

        @Override
        public void play(String url) throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------play------->>>>" + url);
            }
            if (mediaPlayer != null) {
                mediaPlayer.setDataSource(url);
            }
        }

        @Override
        public void start() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------start------->>>>");
            }
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }

        @Override
        public void pause() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------pause------->>>>");
            }
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_PAUSED.ordinal());
            }
        }

        @Override
        public void resume() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------resume------->>>>");
            }
            if (mediaPlayer != null) {
                mediaPlayer.resume();
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_RESUMED.ordinal());
            }
        }

        @Override
        public void seekTo(int msec) throws RemoteException {
            if (mediaPlayer != null) {
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_SEEK_START.ordinal());
                if (L.DEBUG) {
                    L.logD("---------seekTo------->>>>" + msec);
                }
                mediaPlayer.seekTo(msec);
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_SEEK_COMPLETED.ordinal());
            }
        }

        @Override
        public void stop() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------stop------->>>>");
            }
            if (mediaPlayer != null) {
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_BEFORE_STOP.ordinal());
                mediaPlayer.stop();
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_STOP.ordinal());
            }
        }

        @Override
        public void reset() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------reset------->>>>");
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        @Override
        public void release() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------release------->>>>");
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------isPlaying------->>>>");
            }
            boolean isPlaying = false;
            if (mediaPlayer != null) {
                isPlaying = mediaPlayer.isPlaying();
            }
            return isPlaying;
        }

        @Override
        public boolean isPaused() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------isPaused------->>>>");
            }
            boolean isPaused = false;
            if (mediaPlayer != null) {
                isPaused = mediaPlayer.isPaused();
            }
            return isPaused;
        }

        @Override
        public long getDuration() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------getDuration------->>>>");
            }
            try {
                long duration = -1;
                if (mediaPlayer != null) {
                    duration = mediaPlayer.getDuration();
                }
                return duration;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        public long getCurrentPosition() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------getCurrentPosition------->>>>");
            }
            try {
                long position = -1;
                if (mediaPlayer != null) {
                    position = mediaPlayer.getCurrentPosition();
                }
                return position;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public int getBufferPercentage() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------getBufferPercentage------->>>>");
            }
            try {
                int percentage = -1;
                if (mediaPlayer != null) {
                    percentage = mediaPlayer.getBufferPercentage();
                }
                return percentage;
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        public void setPlayRate(float speed) throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------setPlayRate------->>>>" + speed);
            }
            try {
                EsMap esMap = new EsMap();
                esMap.pushString(PlayerBaseView.EVENT_PROP_PLAY_RATE, speed + "");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public float getCurrentPlayRate() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------getCurrentPlayRate------->>>>");
            }
            return 1;
        }

        @Override
        public void setVolume(float volume) throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------setVolume------->>>>"
                        + "volume:" + volume + "---"
                );
            }
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume, volume);
            }
        }

        @Override
        public void setLeftRightVolume(float leftVolume, float rightVolume) throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------setLeftRightVolume------->>>>"
                        + "leftVolume:" + leftVolume + "---"
                        + "rightVolume:" + rightVolume + "---"
                );
            }
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(leftVolume, rightVolume);
            }
        }

        @Override
        public float getLeftVolume() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------getLeftVolume------->>>>");
            }
            float leftVolume = -1;
            if (mediaPlayer != null) {
                leftVolume = mediaPlayer.getLeftVolume();
            }
            return leftVolume;
        }

        @Override
        public float getRightVolume() throws RemoteException {
            if (L.DEBUG) {
                L.logD("---------getRightVolume------->>>>");
            }
            float rightVolume = -1;
            if (mediaPlayer != null) {
                rightVolume = mediaPlayer.getRightVolume();
            }
            return rightVolume;
        }

        @Override
        public void registerPlayerCallback(IPlayerCallback callback) throws RemoteException {
            try {
                if (callback != null && !listenerList.contains(callback)) {
                    listenerList.add(callback);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public void unregisterPlayerCallback(IPlayerCallback callback) throws RemoteException {
            try {
                if (callback != null) {
                    listenerList.remove(callback);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };


    public static void notifyPlayerStatusChanged(List<IPlayerCallback> listenerList,
                                                 int playerStatus) {
        if (listenerList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayerStatusChanged(playerStatus);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyPlayerError(List<IPlayerCallback> listenerList,
                                         int what, int extra) {
        if (listenerList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayerError(what, extra);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyPlayerInfo(List<IPlayerCallback> listenerList,
                                        int code, String message) {
        if (listenerList != null) {
            try {
                for (IPlayerCallback callback : listenerList) {
                    try {
                        callback.onPlayerInfo(code, message);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    //-----------------------------------------------------------
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        notifyPlayerStatusChanged(listenerList,
                PlayerStatusEnum.PLAYER_STATE_PLAYBACK_COMPLETED.ordinal());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        notifyPlayerError(listenerList, what, extra);
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            //
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_BUFFER_START.ordinal());
                break;
            //
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                notifyPlayerStatusChanged(listenerList,
                        PlayerStatusEnum.PLAYER_STATE_BUFFER_END.ordinal());
                break;
            default:
                break;
        }
        notifyPlayerInfo(listenerList, what, extra + "");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        notifyPlayerStatusChanged(listenerList,
                PlayerStatusEnum.PLAYER_STATE_PREPARED.ordinal());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        notifyPlayerStatusChanged(listenerList,
                PlayerStatusEnum.PLAYER_STATE_SEEK_COMPLETED.ordinal());
    }

    //-----------------------------------------------------------
}
