
package com.quicktvui.support.player.audio.ijk;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.support.ijk.base.AndroidMediaPlayer;
import com.quicktvui.support.ijk.base.IMediaPlayer;
import com.quicktvui.support.ijk.base.IjkMediaPlayer;
import com.quicktvui.support.ijk.base.TextureMediaPlayer;
import com.quicktvui.support.ijk.base.misc.IMediaDataSource;
import com.quicktvui.support.player.audio.utils.Settings;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.quicktvui.support.player.ijk.player.FileMediaDataSource;
import com.quicktvui.support.player.ijk.player.IjkMediaOption;
import com.quicktvui.support.player.ijk.utils.FFConcatUtils;

public class ESIJKAudioPlayer {

    //    private boolean logEnable = BuildConfig.DEBUG;
    private boolean logEnable = L.DEBUG;

    private String TAG = "ESIJKMediaPlayer";
    private Uri mUri;
    private Map<String, String> mHeaders;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    // All the stuff we need for playing and showing a video
    private IMediaPlayer mMediaPlayer = null;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing

    private Context mAppContext;
    private Settings mSettings;

    private float leftVolume = 1f;
    private float rightVolume = 1f;

    private EsMap extraInfo; // 可包含 1、loop 2、options 3、cache   playerType、cover2FFConcat

    private boolean looping;
    private List<IjkMediaOption> optionList;

    private static final String COVER_2_FF_CONCAT = "cover2FFConcat";
    private static final String PLAYER_TYPE = "playerType";
    private static final String LOOP = "looping";
    private static final String PLAYER_OPTIONS = "playerOptions";

    private boolean cover2FFConcat;

    private String esPackageName;

    public ESIJKAudioPlayer(Context context) {
        init(context);
    }

    public ESIJKAudioPlayer(Context context, int playerType) {
        init(context);
        mSettings.setPlayerType(playerType);
    }

    public void setEsPackageName(String esPackageName) {
        this.esPackageName = esPackageName;
    }

    private void init(Context context) {
        mAppContext = context.getApplicationContext();
        mSettings = new Settings();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;

    }

    public void setVolume(float volume, float v) {
        leftVolume = volume;
        rightVolume = v;

        if (mMediaPlayer != null) {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer----setVolume------->>>>>" + volume + ":" + v
                        + " " + mMediaPlayer.hashCode() + " " + hashCode());
            }
            mMediaPlayer.setVolume(volume, v);
        } else {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer----setVolume---mMediaPlayer IS NULL---->>>>>" + volume + ":" + v
                        + " " + hashCode());
            }
        }
    }

    public float getLeftVolume() {
        return leftVolume;
    }

    public float getRightVolume() {
        return rightVolume;
    }

    public void setDataSource(String path, EsMap map) {
        if (logEnable) {
            Log.e(TAG, "#ESIJKMediaPlayer--------setDataSource--->>>>>" + path);
        }
        this.extraInfo = map;
        dealExtra();
        this.mUri = Uri.parse(path);
        this.openMedia();
    }

    private void dealExtra() {
        cover2FFConcat = false;
        looping = false;
        if (extraInfo != null) {
            if (extraInfo.containsKey(COVER_2_FF_CONCAT)) {
                cover2FFConcat = extraInfo.getBoolean(COVER_2_FF_CONCAT);
            }
            if (extraInfo.containsKey(PLAYER_TYPE)) {
                mSettings.setPlayerType(extraInfo.getInt(PLAYER_TYPE));
            }
            if (extraInfo.containsKey(LOOP)) {
                looping = extraInfo.getBoolean(LOOP);
            }
            if (extraInfo.containsKey(PLAYER_OPTIONS)) {
                EsArray optionsArray = extraInfo.getArray(PLAYER_OPTIONS);
                if (optionsArray != null && optionsArray.size() > 0) {
                    optionList = new ArrayList<>(optionsArray.size());
                    for (int i = 0; i < optionsArray.size(); i++) {
                        EsMap optionObj = optionsArray.getMap(i);
                        if (optionObj != null) {
                            IjkMediaOption ijkMediaOption = new IjkMediaOption();
                            int type = optionObj.getInt("type");
                            int category = optionObj.getInt("category");
                            String name = optionObj.getString("name");
                            ijkMediaOption.setType(type);
                            ijkMediaOption.setCategory(category);
                            ijkMediaOption.setName(name);
                            switch (type) {
                                case 0:
                                    long longValue = optionObj.getLong("value");
                                    ijkMediaOption.setLongValue(longValue);
                                    break;
                                case 1:
                                    String stringValue = optionObj.getString("value");
                                    ijkMediaOption.setStringValue(stringValue);
                                    break;
                            }
                            optionList.add(ijkMediaOption);
                        }
                    }
                    if (L.DEBUG) {
                        L.logD("#---------解析optionList--------->>>>>>" + optionList);
                    }
                }

            }
        } else {
            optionList = null;
        }
    }

    public void stop() {
        if (logEnable) {
            Log.e(TAG, "#ESIJKMediaPlayer--------stopPlayback--->>>>>");
        }
        if (mMediaPlayer != null) {

            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    private void openMedia() {
        if (logEnable) {
            Log.e(TAG, mUri + "\n-------1-------openMedia------------->>>>>>>\n");
        }
        if (mUri == null) {
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release();

        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (logEnable) {
            Log.e(TAG, "---------2-----openMedia------------->>>>>>>");
        }

        String scheme = mUri.getScheme();
        Log.d(TAG, "scheme:" + scheme);
        if (Build.VERSION.SDK_INT >= 23 &&
                mSettings.getUsingMediaDataSource() &&
                (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
            realOpen(true, null);
        } else if (cover2FFConcat) {
            String url = FFConcatUtils.cover2Concat(mAppContext, mUri.toString());
            if (url != null) {
                realOpen(false, Uri.parse(url));
            } else {
                realOpen(false, mUri);
            }
                // 异步可能导致线程池问题
//            FFConcatUtils.cover2ConcatAsync(mAppContext, mUri.toString(), new FFConcatUtils.CoverListener() {
//                @Override
//                public void onSuccess(String url) {
//                    realOpen(false, Uri.parse(url));
//                }
//
//                @Override
//                public void onFail(String message) {
//                    realOpen(false, mUri);
//                }
//            });
        } else {
            realOpen(false, mUri);
        }
    }

    private void realOpen(boolean isFile, Uri uri) {
        try {
            mMediaPlayer = createPlayer(mSettings.getPlayerType());

            // REMOVED: mAudioSession
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mCurrentBufferPercentage = 0;

            if (isFile) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            } else {
                mMediaPlayer.setDataSource(mAppContext, uri, mHeaders);
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);

            //循环播放
            mMediaPlayer.setLooping(looping);

            try {
                mMediaPlayer.prepareAsync();
                if (logEnable) {
                    Log.e(TAG, "--------------prepareAsync------------->>>>>>>");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mOnPreparedListener2 != null) {
                mOnPreparedListener2.onPrepared(ESIJKAudioPlayer.this);
            }

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                if (logEnable) {
                    Log.e(TAG, "#ESIJKMediaPlayer--------onPrepared--->>>>>seekToPosition:" + seekToPosition);
                }
                seekTo(seekToPosition);
            } else {
                if (logEnable) {
                    Log.e(TAG, "#ESIJKMediaPlayer--------onPrepared---seekToPosition == 0--->>>>>");
                }
            }
        }
    };


    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer mp) {
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                    if (mOnCompletionListener2 != null) {
                        mOnCompletionListener2.onCompletion(ESIJKAudioPlayer.this);
                    }
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    if (logEnable) {
                        Log.e(TAG, "#ESIJKMediaPlayer--------onInfo----->>>>>arg1:" + arg1 + ",arg2:" + arg2);
                    }
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    if (mOnInfoListener2 != null) {
                        mOnInfoListener2.onInfo(ESIJKAudioPlayer.this, arg1, arg2);
                    }
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                            Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;

                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }
                    if (mOnErrorListener2 != null) {
                        if (mOnErrorListener2.onError(ESIJKAudioPlayer.this, framework_err, impl_err)) {
                            return true;
                        }
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                    if (null != mOnBufferingUpdateListener) {
                        mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
                    }
                    if (mOnBufferingUpdateListener2 != null) {
                        mOnBufferingUpdateListener2.onBufferingUpdate(ESIJKAudioPlayer.this, percent);
                    }
                }
            };

    IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if (null != mOnSeekCompleteListener) {
                mOnSeekCompleteListener.onSeekComplete(mp);
            }
            if (mOnSeekCompleteListener2 != null) {
                mOnSeekCompleteListener2.onSeekComplete(ESIJKAudioPlayer.this);
            }
        }
    };

    public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener
                                                     onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener
                                                  onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;
    }

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    /*
     * release the media player in any state
     */
    public void release() {
        if (logEnable) {
            Log.e(TAG, "#ESIJKMediaPlayer------start--release---->>>>>holder:");
        }
        if (mMediaPlayer != null) {

//            if (CacheSetting.getInstance().isUseCache()) {
//                mLocalProxyVideoControl.releaseLocalProxyResources();
//            }

            mMediaPlayer.reset();
            mMediaPlayer.release();

            mMediaPlayer.setDisplay(null);
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = STATE_IDLE;
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer----end----release---->>>>>holder:");
            }
        }
    }

    public void start() {
        if (logEnable) {
            Log.e(TAG, mMediaPlayer + "#ESIJKMediaPlayer------start--->>>>>" + mCurrentState);
        }

        if (isInPlaybackState()) {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer----1----start--->>>>>");
            }
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            //设置音量
            setVolume(leftVolume, rightVolume);
        } else {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer------2--start--->>>>>");
            }
        }
        mTargetState = STATE_PLAYING;
    }

    public void pause() {
        if (isInPlaybackState()) {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer----pause------->>>>>");
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void resume() {
        if (logEnable) {
            Log.e(TAG, "#ESIJKMediaPlayer----resume------->>>>>");
        }
        openMedia();
    }

    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            int pos = (int) mMediaPlayer.getCurrentPosition();
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer---getCurrentPosition---进度-->>>>>" + pos);
            }
            return pos;
        }
        return 0;
    }

    public void seekTo(int msec) {
        if (logEnable) {
            Log.e(TAG, "#ESIJKMediaPlayer--1--seekTo------->>>>>" + msec);
        }
        if (isInPlaybackState()) {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer--2--seekTo---播放器快进---->>>>>" + msec);
            }

            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer--3--seekTo------->>>>>" + msec);
            }
            mSeekWhenPrepared = msec;
        }
    }

    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    public boolean isPaused() {
        return isInPausedState();
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    public boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    public boolean isInPausedState() {
        return (mMediaPlayer != null &&
                mCurrentState == STATE_PAUSED);
    }

    public IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer = null;

        switch (playerType) {
            case Settings.PV_PLAYER__AndroidMediaPlayer: {
                AndroidMediaPlayer androidMediaPlayer = new AndroidMediaPlayer();
                mediaPlayer = androidMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
            default: {
                IjkMediaPlayer ijkMediaPlayer = null;
                if (mUri != null) {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_ERROR);

                    if (mSettings.getUsingOpenSLES()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                    }

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,rtp,tcp,tls,udp,ijkurlhook,data");

                    // concat相关
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "concat,ffconcat,async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,sdp,rtp,rtmp,rtsp,rtmpt,tcp,tls,udp,ijkurlhook,data");
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);

                    // 设置最大缓冲大小 默认为15M，现设置为1M
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024);

                    try {
                        if (optionList != null && optionList.size() > 0) {
                            for (IjkMediaOption option : optionList) {
                                try {
                                    switch (option.getType()) {
                                        case IjkMediaOption.IJK_MEDIA_OPTION_TYPE_LONG:
                                            ijkMediaPlayer.setOption(option.getCategory(), option.getName(), option.getLongValue());
                                            break;
                                        case IjkMediaOption.IJK_MEDIA_OPTION_TYPE_STRING:
                                            ijkMediaPlayer.setOption(option.getCategory(), option.getName(), option.getStringValue());
                                            break;
                                    }
                                    if (logEnable)
                                        Log.i(TAG, "#---------setOption------>>>>>" + option);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                mediaPlayer = ijkMediaPlayer;
            }
            break;
        }

        if (mSettings.getEnableDetachedSurfaceTextureView()) {
            mediaPlayer = new TextureMediaPlayer(mediaPlayer);
        }

        return mediaPlayer;
    }

    public void setSpeed(float speed) {
        try {
            if (mMediaPlayer instanceof IjkMediaPlayer) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
                ijkMediaPlayer.setSpeed(speed);
                if (logEnable)
                    Log.e(TAG, "#Audio----setSpeed---ijk---->>>>>" + speed);
            } else if (mMediaPlayer instanceof AndroidMediaPlayer) {
                ((AndroidMediaPlayer) mMediaPlayer).setSpeed(speed);
                if (logEnable)
                    Log.e(TAG, "#Audio----setSpeed---android---->>>>>" + speed);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public float getSpeed() {
        try {
            if (mMediaPlayer instanceof IjkMediaPlayer) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
                return ijkMediaPlayer.getSpeed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 1f;
    }

    public long getBitRate() {
        try {
            if (mMediaPlayer instanceof IjkMediaPlayer) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
                return ijkMediaPlayer.getBitRate();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getTcpSpeed() {
        try {
            if (mMediaPlayer instanceof IjkMediaPlayer) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
                return ijkMediaPlayer.getTcpSpeed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public IMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private OnCompletionListener mOnCompletionListener2;
    private OnPreparedListener mOnPreparedListener2;
    private OnSeekCompleteListener mOnSeekCompleteListener2;
    private OnBufferingUpdateListener mOnBufferingUpdateListener2;
    private OnErrorListener mOnErrorListener2;
    private OnInfoListener mOnInfoListener2;

    public void setOnCompletionListener2(OnCompletionListener mOnCompletionListener2) {
        this.mOnCompletionListener2 = mOnCompletionListener2;
    }

    public void setOnPreparedListener2(OnPreparedListener mOnPreparedListener2) {
        this.mOnPreparedListener2 = mOnPreparedListener2;
    }

    public void setOnSeekCompleteListener2(OnSeekCompleteListener mOnSeekCompleteListener2) {
        this.mOnSeekCompleteListener2 = mOnSeekCompleteListener2;
    }

    public void setOnBufferingUpdateListener2(OnBufferingUpdateListener mOnBufferingUpdateListener2) {
        this.mOnBufferingUpdateListener2 = mOnBufferingUpdateListener2;
    }

    public void setOnErrorListener2(OnErrorListener mOnErrorListener2) {
        this.mOnErrorListener2 = mOnErrorListener2;
    }

    public void setOnInfoListener2(OnInfoListener mOnInfoListener2) {
        this.mOnInfoListener2 = mOnInfoListener2;
    }

    public interface OnInfoListener {
        boolean onInfo(ESIJKAudioPlayer audioPlayer, int i, int i1);
    }

    public interface OnErrorListener {
        boolean onError(ESIJKAudioPlayer audioPlayer, int i, int i1);
    }

    public interface OnSeekCompleteListener {
        void onSeekComplete(ESIJKAudioPlayer audioPlayer);
    }

    public interface OnBufferingUpdateListener {
        void onBufferingUpdate(ESIJKAudioPlayer audioPlayer, int i);
    }

    public interface OnCompletionListener {
        void onCompletion(ESIJKAudioPlayer audioPlayer);
    }

    public interface OnPreparedListener {
        void onPrepared(ESIJKAudioPlayer audioPlayer);
    }
}
