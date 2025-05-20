
package com.quicktvui.support.player.audio.android;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

public class ESAndroidAudioPlayer {

    private boolean logEnable = false;

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
    private MediaPlayer mMediaPlayer = null;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private int mCurrentBufferPercentage;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing

    private Context mAppContext;

    private float leftVolume = 1f;
    private float rightVolume = 1f;
    private boolean looping = false;

    public ESAndroidAudioPlayer(Context context, boolean looping) {
        this.looping = looping;
        init(context);
    }

    private void init(Context context) {
        mAppContext = context.getApplicationContext();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    public void setVolume(float volume, float v) {
        leftVolume = volume;
        rightVolume = v;

        if (mMediaPlayer != null) {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer----setVolume------->>>>>" + volume + ":" + v);
            }
            mMediaPlayer.setVolume(volume, v);
        } else {
            if (logEnable) {
                Log.e(TAG, "#ESIJKMediaPlayer----setVolume---mMediaPlayer IS NULL---->>>>>" + volume + ":" + v);
            }
        }
    }

    public float getLeftVolume() {
        return leftVolume;
    }

    public float getRightVolume() {
        return rightVolume;
    }

    public void setDataSource(String path) {
        if (logEnable) {
            Log.e(TAG, "#ESIJKMediaPlayer--------setDataSource--->>>>>" + path);
        }
        this.mUri = Uri.parse(path);
        this.openMedia();
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

        try {
            mMediaPlayer = createPlayer();

            // REMOVED: mAudioSession
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setLooping(looping);
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
            Log.d(TAG, "scheme:" + scheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);

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
        } finally {
        }
    }

    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
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


    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private MediaPlayer.OnInfoListener mInfoListener =
            new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int arg1, int arg2) {
                    if (logEnable) {
                        Log.e(TAG, "#ESIJKMediaPlayer--------onInfo----->>>>>arg1:" + arg1 + ",arg2:" + arg2);
                    }
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    switch (arg1) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            break;
                        case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            };

    private MediaPlayer.OnErrorListener mErrorListener =
            new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;

                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }
                    return true;
                }
            };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                    if (null != mOnBufferingUpdateListener) {
                        mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
                    }
                }
            };

    MediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (null != mOnSeekCompleteListener) {
                mOnSeekCompleteListener.onSeekComplete(mp);
            }
        }
    };

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener
                                                     onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener
                                                  onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;
    }

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
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
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(MediaPlayer.OnInfoListener l) {
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

    public MediaPlayer createPlayer() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
