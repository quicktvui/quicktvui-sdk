/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quicktvui.support.player.ijk.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.ijk.base.AndroidMediaPlayer;
import com.quicktvui.support.ijk.base.IMediaPlayer;
import com.quicktvui.support.ijk.base.IjkMediaPlayer;
import com.quicktvui.support.ijk.base.IjkTimedBitmap;
import com.quicktvui.support.ijk.base.IjkTimedText;
import com.quicktvui.support.ijk.base.TextureMediaPlayer;
import com.quicktvui.support.ijk.base.misc.IMediaDataSource;
import com.quicktvui.support.ijk.base.misc.ITrackInfo;
import com.quicktvui.support.player.ijk.R;
import com.quicktvui.support.player.ijk.service.MediaPlayerService;
import com.quicktvui.support.player.ijk.setting.Settings;
import com.quicktvui.support.player.ijk.utils.PlayerThreadTools;
import com.quicktvui.support.player.ijk.utils.TimedHelper;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.quicktvui.support.player.manager.log.PLog;

public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl {

    private boolean logEnable = L.DEBUG;

    private String TAG = "IjkVideoView";
    // settable by the client
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
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    private IMediaPlayer mMediaPlayer = null;
    // private int         mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener;
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener; // add by zhousuqiang
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener; // add by zhousuqiang
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    /** Subtitle rendering widget overlaid on top of the video. */
    // private RenderingWidget mSubtitleWidget;

    /**
     * Listener for changes to subtitle data, used to redraw when needed.
     */
    // private RenderingWidget.OnChangedListener mSubtitlesChangedListener;

    private Context mAppContext;
    private Settings mSettings;
    private IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private float leftVolume = 1f;
    private float rightVolume = 1f;

    private List<IjkMediaOption> optionList;

    //背景透明
    private boolean usingTransparentBackground = false;

    //
    private boolean looping = false;

    private TimedHelper timedHelper;

    private EsMap extraInfo;

    private String esPackageName;

    public IjkVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    public void setEsPackageName(String esPackageName, Settings settings) {
        this.esPackageName = esPackageName;
        this.mSettings = settings;

        initBackground();
    }


    // REMOVED: onMeasure
    // REMOVED: onInitializeAccessibilityEvent
    // REMOVED: onInitializeAccessibilityNodeInfo
    // REMOVED: resolveAdjustedSize

    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();

//        initBackground();
//        initRenders();

        mVideoWidth = 0;
        mVideoHeight = 0;
        // REMOVED: getHolder().addCallback(mSHCallback);
        // REMOVED: getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        if(isFocusable()) {
//            requestFocus();
//        }
        // REMOVED: mPendingSubtitleTracks = new Vector<Pair<InputStream, MediaFormat>>();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null)
                mMediaPlayer.setDisplay(null);

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            removeView(renderUIView);

            if (timedHelper != null) {
                timedHelper.removeView(this);
            }
        }

        if (renderView == null)
            return;

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = mRenderView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);

        if (mSettings.getTimedType() > IjkMediaPlayer.TIMED_ONLY_TEXT && timedHelper == null) {
            timedHelper = new TimedHelper(mAppContext);
            timedHelper.addView(IjkVideoView.this);
        }
    }

    /**
     * 设置音量
     *
     * @param volume
     * @param v
     */
    public void setVolume(float volume, float v) {
        leftVolume = volume;
        rightVolume = v;

        if (mMediaPlayer != null) {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView----setVolume------->>>>>" + volume + ":" + v);
            }
            mMediaPlayer.setVolume(volume, v);
        } else {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView----setVolume---mMediaPlayer IS NULL---->>>>>" + volume + ":" + v);
            }
        }
    }

    /**
     * 左声道音量
     *
     * @return
     */
    public float getLeftVolume() {
        return leftVolume;
    }

    /**
     * 右声道音量
     *
     * @return
     */
    public float getRightVolume() {
        return leftVolume;
    }

    public void setRender(int render) {

        if (logEnable) {
            Log.e(TAG, "#IjkVideoView----setRender------->>>>>" + render);
        }

        switch (render) {
            case RENDER_NONE:
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView----RENDER_NONE------->>>>>");
                }
                setRenderView(null);
                break;
            case RENDER_TEXTURE_VIEW: {
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView----RENDER_TEXTURE_VIEW------->>>>>");
                }
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    int playerWidth = mMediaPlayer.getVideoWidth();
                    int playerHeight = mMediaPlayer.getVideoHeight();
                    renderView.setVideoSize(playerWidth, playerHeight);

                    if (logEnable) {
                        Log.e(TAG, "#IjkVideoView--------RENDER_TEXTURE_VIEW--->>>>>playerWidth:" + playerWidth + "---playerHeight-->>>" + playerHeight);
                    }

                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mCurrentAspectRatio);
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView----RENDER_SURFACE_VIEW------->>>>>");
                }
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);

                if (usingTransparentBackground) {
                    renderView.setBackgroundColor(Color.TRANSPARENT);
                    renderView.setZOrderOnTop(true);
                    renderView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                }
                break;
            }
            default:
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView----invalid---render---->>>>>");
                }
                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", render));
                break;
        }
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path, EsMap extraInfo) {
        if (logEnable) {
            Log.e(TAG, "#IjkVideoView--------setVideoPath--->>>>>" + path);
        }

        this.extraInfo = extraInfo;
        setVideoURI(Uri.parse(path));
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        if (logEnable) {
            Log.e(TAG, "#IjkVideoView--------setVideoURI--->>>>>" + uri);
        }
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    // REMOVED: addSubtitleSource
    // REMOVED: mPendingSubtitleTracks

    public void stopPlayback() {
        if (logEnable) {
            Log.e(TAG, "#IjkVideoView--------stopPlayback--->>>>>");
        }

        stop(false);
    }

    private static final List<WeakReference<IMediaPlayer>> playerList = new ArrayList<>();

    public void stop(boolean force) {
        if (logEnable) {
            Log.e(TAG, "#IjkVideoView--------stop--playerList->>>>>" + playerList.size());
            Iterator<WeakReference<IMediaPlayer>> iterator = playerList.iterator();
            while (iterator.hasNext()) {
                WeakReference<IMediaPlayer> next = iterator.next();
                IMediaPlayer iMediaPlayer = next.get();
                if (iMediaPlayer == null) {
                    iterator.remove();
                }
            }
            Log.e(TAG, "#IjkVideoView--------stop--playerList2->>>>>" + playerList.size());
        }
        if (logEnable) {
            Log.e(TAG, "#IjkVideoView--------stop--->>>>>");
        }
        if (mCurrentState == STATE_IDLE && !force) {
            Log.e(TAG, "#IjkVideoView---stop---已经是:IDLE状态-->>>>>");
            return;
        }
        if (mMediaPlayer != null) {

            long t = 0;
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView--------mMediaPlayer.stop()--->>>>>" + mMediaPlayer.hashCode());
                t = System.currentTimeMillis();
            }
            mMediaPlayer.stop();
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView--------mMediaPlayer.stop()2--->>>>>"
                        + (System.currentTimeMillis() - t) + " " + mMediaPlayer.hashCode());
            }
            IMediaPlayer temPlayer = mMediaPlayer;
            if (logEnable) {
                playerList.add(new WeakReference<>(temPlayer));
            }
            PlayerThreadTools.getInstance().delegateMethod(() -> {
                long t1 = 0;
//                temPlayer.reset();
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView--------mMediaPlayer.release()--->>>>>" + temPlayer.hashCode());
                    t1 = System.currentTimeMillis();
                }
                temPlayer.release();
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView--------mMediaPlayer.release()2--->>>>>" + (System.currentTimeMillis() - t1)
                            + " " + temPlayer.hashCode());
                }
            });
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
            // 清除监听
            resetListeners();

            // 增加原release中的逻辑
            try {
                if (mRenderView != null) {
                    mRenderView.removeRenderCallback(mSHCallback);
                    mRenderView = null;
                    removeAllViews();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (mHandler != null) {
                isPositionListening = false;
                mHandler.removeCallbacks(mRunnable);
                mHandler = null;
                mRunnable = null;
            }
        }
    }

    private void openVideo() {
        if (logEnable) {
            Log.e(TAG, mUri + "\n-------1-------openVideo------------->>>>>>>\n" + mSurfaceHolder);
        }
        if (mUri == null || mSurfaceHolder == null || mUri.toString().equals("null")) {
            // not ready for playback just yet, will try again later
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);

        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (logEnable) {
            Log.e(TAG, "---------2-----openVideo------------->>>>>>>");
        }

        try {
            int playerType = mSettings.getPlayerType();
            if (logEnable) {
                Log.e(TAG, "--------------播放器类型-----获取播放类型-------->>>>>>>" + playerType);
            }
            mMediaPlayer = createPlayer(playerType);

            // TODO: create SubtitleController in MediaPlayer, but we need
            // a context for the subtitle renderers
            final Context context = getContext();
            // REMOVED: SubtitleController

            // REMOVED: mAudioSession
            mMediaPlayer.setOnTimedTextListener(mTimedTextListener);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
//            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener); // add by zhousuqiang
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
            if (logEnable)
                Log.d(TAG, "scheme:" + scheme);

            if (Build.VERSION.SDK_INT >= 23 &&
                    mSettings.getUsingMediaDataSource() &&
                    (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            } else if (!TextUtils.isEmpty(scheme) &&
                    scheme.equalsIgnoreCase("asset") && mUri.toString().length() > 8) {
                AssetManager assets = mAppContext.getAssets();
                String fileName = mUri.toString().substring(8);
                AssetFileDescriptor assetFileDescriptor = assets.openFd(fileName);
                if (mMediaPlayer instanceof AndroidMediaPlayer) {
                    ((AndroidMediaPlayer) mMediaPlayer).setDataSource(assetFileDescriptor.getFileDescriptor(),
                            assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                } else {
                    mMediaPlayer.setDataSource(new AssetDataSource(assetFileDescriptor));
                }
            } else { // 貌似目前只走else逻辑
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            }
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);

            if (logEnable) {
                Log.e(TAG, "--------------setLooping------------->>>>>>>" + looping);
            }
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

            // REMOVED: mPendingSubtitleTracks

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
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    IMediaPlayer.OnTimedTextListener mTimedTextListener =
            new IMediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
                    if (iMediaPlayer == null || ijkTimedText == null) {
                        return;
                    }
                    String original = ijkTimedText.getText();
                    String result = TimedHelper.formatTimedText(original);
                    ijkTimedText.setText(result);
                    if (logEnable) {
                        Log.e(TAG, "#IjkVideoView--------onTimedText--->>>>>ijkTimedText:" + original);
                        Log.e(TAG, "#IjkVideoView--------onTimedText--->>处理后>>ijkTimedText:" + result);
                    }
                    try {
                        if (mOnTimedTextListener != null) {
                            mOnTimedTextListener.onTimedText(iMediaPlayer, ijkTimedText);
                        }

                        if (TextUtils.isEmpty(original) && timedHelper != null) {
                            timedHelper.timedEnd();
                        }

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTimedBitmap(IMediaPlayer iMediaPlayer, IjkTimedBitmap bitmap) {
                    if (logEnable) {
                        Log.e(TAG, "#IjkVideoView--------onTimedBitmap--->>>>>hadBitmap:" + (bitmap.getBitmap() != null));
                        Log.e(TAG, "#IjkVideoView--------onTimedBitmap--->>>>>width、height:"
                                + bitmap.getWidth() + "、" + bitmap.getHeight());
                    }

//                    if (timedHelper == null) {
//                        timedHelper = new TimedHelper(mAppContext);
//                        timedHelper.addView(IjkVideoView.this);
//                    }

//                    timedHelper.setBitmap(bitmap.getBitmap());
                    timedHelper.setBitmap(bitmap, esPackageName);

                }
            };

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    try {
                        mVideoWidth = mp.getVideoWidth();
                        mVideoHeight = mp.getVideoHeight();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    if (logEnable) {
                        Log.e(TAG, "#IjkVideoView--------onVideoSizeChanged-" +
                                "-->>>>>mVideoWidth:" + mVideoWidth + "----->>>" + mVideoHeight);
                    }

                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                        requestLayout();
                    }

                    if (mOnVideoSizeChangedListener != null) {
                        mOnVideoSizeChangedListener.onVideoSizeChanged(mp, mVideoWidth, mVideoHeight, sarNum, sarDen);
                    }
                }
            };

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            mCurrentState = STATE_PREPARED;

            if (logEnable && mMediaPlayer instanceof IjkMediaPlayer) {
                Log.d(TAG, "#IjkVideoView----onPrepared------->>>>>getVideoDecoder: " + ((IjkMediaPlayer) mMediaPlayer).getVideoDecoder());
            }

            // Get the capabilities of the player for this stream
            // REMOVED: Metadata

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }

            try {
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView----" +
                            "----onPrepared--->>>>>mVideoWidth:" + mVideoWidth + "----->>>" + mVideoHeight);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                mVideoWidth = 0;
                mVideoHeight = 0;
            }
            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView--------onPrepared--->>>>>seekToPosition:" + seekToPosition);
                }
                seekTo(seekToPosition);
            } else {
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView--------onPrepared---seekToPosition == 0--->>>>>");
                }
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                        // We didn't actually change the size (it was already at the size
                        // we need), so we won't get a "surface changed" callback, so
                        // start the video here instead of in the callback.
                        if (mTargetState == STATE_PLAYING) {
                            start();
                        }
                    } else {
                        if (mTargetState == STATE_PLAYING) {
                            start();
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    start();
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
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    return onInfo(mp, arg1, String.valueOf(arg2));
                }

                @Override
                public boolean onInfo(IMediaPlayer mp, int arg1, String arg2) {
                    if (logEnable) {
                        Log.e(TAG, "#IjkVideoView--------onInfo----->>>>>arg1:" + arg1 + ",arg2:" + arg2);
                    }
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            mVideoRotationDegree = Integer.parseInt(arg2);
                            if (logEnable)
                                Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                            if (mRenderView != null)
                                mRenderView.setVideoRotation(Integer.parseInt(arg2));
                            break;
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            if (logEnable)
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

                    /* Otherwise, pop up an error dialog so the user knows that
                     * something bad has happened. Only try and pop up the dialog
                     * if we're attached to a window. When we're going away and no
                     * longer have a window, don't bother showing the user an error.
                     */
                    if (getWindowToken() != null) {
                        Resources r = mAppContext.getResources();
                        int messageId;

                        if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                            messageId = R.string.VideoView_error_text_invalid_progressive_playback;
                        } else {
                            messageId = R.string.VideoView_error_text_unknown;
                        }

//                        new AlertDialog.Builder(getContext())
//                                .setMessage(messageId)
//                                .setPositiveButton(R.string.VideoView_error_button,
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int whichButton) {
//                                            /* If we get here, there is no onError listener, so
//                                             * at least inform them that the video is over.
//                                             */
//                                                if (mOnCompletionListener != null) {
//                                                    mOnCompletionListener.onCompletion(mMediaPlayer);
//                                                }
//                                            }
//                                        })
//                                .setCancelable(false)
//                                .show();
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;

//                    if (logEnable) {
//                        Log.e(TAG, "#IjkVideoView: onBufferingUpdate--> percent : " + percent);
//                    }

                    // add by zhousuqiang
                    if (null != mOnBufferingUpdateListener) {
                        mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
                    }
                }
            };

    // add by zhousuqiang
    IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView: onSeekComplete");
            }
            if (null != mOnSeekCompleteListener) {
                mOnSeekCompleteListener.onSeekComplete(mp);
            }
        }
    };

    // add by zhousuqiang
    public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener
                                                     onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    // add by zhousuqiang
    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener
                                                  onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;
    }

    public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener
                                                      mOnVideoSizeChangedListener) {
        this.mOnVideoSizeChangedListener = mOnVideoSizeChangedListener;
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

    public void setOnTimedTextListener(IMediaPlayer.OnTimedTextListener l) {
        mOnTimedTextListener = l;
    }

    // REMOVED: mSHCallback
    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }

            if (logEnable) {
                Log.e(TAG, "#IjkVideoView--------onSurfaceChanged--->>>>>holder:" + holder + ",format:" + format + ",width :" + w + ",height:" + h);
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    if (logEnable) {
                        Log.e(TAG, "#IjkVideoView--------onSurfaceChanged---seekTo--->>>>>" + mSeekWhenPrepared);
                    }
                    seekTo(mSeekWhenPrepared);
                }
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView--------onSurfaceChanged---start--->>>>>holder:" + holder);
                }
                start();
            } else {
                if (logEnable) {
                    Log.e(TAG, "#IjkVideoView--------onSurfaceChanged--has no ValidSize->>>>>holder:" + holder);
                }
            }
        }

        @Override
        public void onSurfaceCreated(IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            if (logEnable) {
                Log.e(TAG, "#IjkVideoView--------onSurfaceCreated--->>>>>holder:" + holder
                        + "----->>>>width:" + width +
                        "----->>>>height:" + height);
            }

            mSurfaceHolder = holder;
            //
            if (mMediaPlayer != null) {
                bindSurfaceHolder(mMediaPlayer, holder);
            }
            //
            else {
                if (logEnable)
                    Log.e(TAG, "#IjkVideoView--------onSurfaceCreated---openVideo--->>>>");
                openVideo();
            }
        }

        @Override
        public void onSurfaceDestroyed(IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }

            if (logEnable) {
                Log.e(TAG, "#IjkVideoView--------onSurfaceDestroyed---->>>>>holder:" + holder);
            }
            mOnBufferingUpdateListener = null;
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
            releaseWithoutStop();
        }
    };

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    public void release(boolean cleartargetstate) {
        releaseForce(cleartargetstate, false);
    }

    /*
     * release the media player in any state
     */
    public void releaseForce(boolean cleartargetstate, boolean force) {
        if (logEnable) {
            Log.e(TAG, "#IjkVideoView------start--release---->>>>>holder:" + cleartargetstate);
        }
        if (mCurrentState == STATE_IDLE && !force) {
            Log.e(TAG, "#IjkVideoView---release---已经是:IDLE状态-->>>>>");
            return;
        }
        if (mMediaPlayer != null) {

            mMediaPlayer.reset();
            mMediaPlayer.release();

            mMediaPlayer.setDisplay(null);
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView----end----release---->>>>>holder:" + cleartargetstate);
            }
            // 清除监听
            resetListeners();
        }

        try {
            if (mRenderView != null && cleartargetstate) {
                mRenderView.removeRenderCallback(mSHCallback);
                mRenderView = null;
                removeAllViews();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (mHandler != null) {
            isPositionListening = false;
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
            mRunnable = null;
        }
    }

    @Override
    public void start() {
        if (logEnable) {
            Log.e(TAG, mMediaPlayer + "#IjkVideoView------start--->>>>>" + mCurrentState);
        }

        if (isInPlaybackState()) {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView----1----start--->>>>>");
            }
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            //设置音量
            setVolume(leftVolume, rightVolume);
        } else {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView------2--start--->>>>>");
            }
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView----pause------->>>>>");
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
            Log.e(TAG, "#IjkVideoView----resume------->>>>>");
        }
        openVideo();
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            int pos = (int) mMediaPlayer.getCurrentPosition();
//            if (logEnable) {
//                Log.e(TAG, "#IjkVideoView---getCurrentPosition---进度-->>>>>" + pos);
//            }
            return pos;
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (logEnable) {
            Log.e(TAG, "#IjkVideoView--1--seekTo------->>>>>" + msec);
        }
        if (isInPlaybackState()) {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView--2--seekTo---播放器快进---->>>>>" + msec);
            }

            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            if (logEnable) {
                Log.e(TAG, "#IjkVideoView--3--seekTo------->>>>>" + msec);
            }
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    /**
     * 判断是否在暂停状态
     *
     * @return
     */
    public boolean isPaused() {
        return isInPausedState();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    // add by zhousuqiang private --> public
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

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    // REMOVED: getAudioSessionId();
    // REMOVED: onAttachedToWindow();
    // REMOVED: onDetachedFromWindow();
    // REMOVED: onLayout();
    // REMOVED: draw();
    // REMOVED: measureAndLayoutSubtitleWidget();
    // REMOVED: setSubtitleWidget();
    // REMOVED: getSubtitleLooper();

    //-------------------------
    // Extend: Aspect Ratio
    //-------------------------

    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};

    private int mCurrentAspectRatio = s_allAspectRatio[0];

    /**
     * 设置画面比例
     *
     * @param aspectRatio
     */
    public void setAspectRatio(int aspectRatio) {
        mCurrentAspectRatio = aspectRatio;
        if (mRenderView != null) {
            if (PLog.isLoggable(PLog.DEBUG)) {
                PLog.d(TAG, this + "#--------setAspectRatio--->>>>>aspectRatio:" + aspectRatio);
            }
            mRenderView.setAspectRatio(aspectRatio);
        }
    }

    /**
     * 获取当前的画面比例
     *
     * @return
     */
    public int getCurrentAspectRatio() {
        return mCurrentAspectRatio;
    }

    //-------------------------
    // Extend: Render
    //-------------------------
    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    public IMediaPlayer createPlayer(int playerType) {
        IMediaPlayer mediaPlayer = null;
        if (logEnable)
            Log.e(TAG, "#IjkVideoView----createPlayer------>>>>>" + playerType);
        switch (playerType) {
            case Settings.PV_PLAYER__AndroidMediaPlayer: {
                if (logEnable)
                    Log.e(TAG, "#IjkVideoView----createPlayer---AndroidMediaPlayer--->>>>>" + playerType);
                mediaPlayer = new AndroidMediaPlayer();
            }
            break;
            case Settings.PV_PLAYER__IjkExoMediaPlayer: {
                if (logEnable)
                    Log.e(TAG, "#IjkVideoView----createPlayer---ExoMediaPlayer--->>>>>" + playerType);
//                mediaPlayer = new ExoMediaPlayer(mAppContext);
            }
            break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
                if (logEnable)
                    Log.e(TAG, "#IjkVideoView----createPlayer---IjkMediaPlayer--->>>>>" + playerType);
            default: {
                IjkMediaPlayer ijkMediaPlayer = null;
                if (mUri != null) {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    if (logEnable)
                        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
                    else
                        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_ERROR);
                    int optionCategory = mSettings.getOptionCategory(esPackageName);
                    if (logEnable)
                        Log.e(TAG, "#IjkVideoView----optionCategory------>>>>>" + optionCategory);
                    switch (optionCategory) {
                        case Settings.IJK_OPTION_TYPE_FULL_FAST:
                        case Settings.IJK_OPTION_TYPE_BUFFER_FAST:
                        case Settings.IJK_OPTION_TYPE_ANALYZE_FAST:
                            setFastOptions(ijkMediaPlayer, optionCategory);
                            break;
                        case Settings.IJK_OPTION_TYPE_TRADITION:
                        default:
                            setTraditionOptions(ijkMediaPlayer);
                            break;
                    }
                    // concat相关
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                            "ijkmediadatasource,concat,ffconcat,async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,sdp,rtp,rtmp,rtsp,rtmpt,tcp,tls,udp,ijkurlhook,data");
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);

                    TimedHelper.setIjkOption(ijkMediaPlayer, mSettings);

                    if (logEnable)
                        Log.i(TAG, "#---IjkMediaPlayer------setOptionList------>>>>>optionList:" + optionList);
                    try {
                        if (optionList != null && optionList.size() > 0) {
                            for (IjkMediaOption option : optionList) {
                                int category = option.getCategory();
                                if (category < 1 || category > 4) {
                                    continue;
                                }
                                try {
                                    switch (option.getType()) {
                                        case IjkMediaOption.IJK_MEDIA_OPTION_TYPE_LONG:
                                            ijkMediaPlayer.setOption(category, option.getName(), option.getLongValue());
                                            break;
                                        case IjkMediaOption.IJK_MEDIA_OPTION_TYPE_STRING:
                                            ijkMediaPlayer.setOption(category, option.getName(), option.getStringValue());
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

    // 之前的option设置
    private void setTraditionOptions(IjkMediaPlayer ijkMediaPlayer) {
        if (mSettings.getUsingMediaCodec()) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);
        }

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1); // 丢帧
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0); // 是否自动播放

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48); // 完全关闭循环滤波

        // 暂时开启变调   关闭可提高性能，但是会导致倍速失效
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,sdp,rtp,rtmp,rtsp,rtmpt,tcp,tls,udp,ijkurlhook,data");

        //rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video");
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316);
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
//                    //会导致花屏
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240L);
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
//                    //关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);
//                    ijkMediaPlayer.getTcpSpeed()
    }

    // 0为之前设置，1为全部优化版本，2为不关闭buffer的优化版本，3为不设置analyzeduration版本
    private void setFastOptions(IjkMediaPlayer ijkMediaPlayer, int cateType) {
        setFastCommonOptions(ijkMediaPlayer);
        if (cateType != 2) { // 码率比较大的情况，不能关闭buffer
            // 暂停输出直到停止后读取足够的数据包 默认1  不开启缓存
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
        }
        if (cateType != 3) { // 设置这个参数会导致有些视频播放失败，分析时间不足
            // 设置探测输入的分析时长，单位：微秒，详情见：libavformat/options_table.h，通常设置 1 达到首屏秒开效果
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
        }
    }

    // 起播优化参数
    private void setFastCommonOptions(IjkMediaPlayer ijkMediaPlayer) {
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,sdp,rtp,rtmp,rtsp,rtmpt,tcp,tls,udp,ijkurlhook,data");
        // 能走硬解的都走硬解
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);
        // 目前发现会导致seek后有机率播放之前声音，故关闭
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        // 暂时开启变调   关闭可提高性能，但是会导致倍速失效
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        // 不查询stream_info，直接使用 默认为1  导致没有声音（非原画），考虑做成动态：原画设为0，其他设为1
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "find_stream_info", 0);

        // 是否自动播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        // 等待开始之后才绘制 tc8000上会导致C层崩溃，暂时去掉
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "render-wait-start", 1);
        //是否断线重连
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);

        // 设置最长分析时长
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
        // 设置探测输入的分析时长，单位：微秒，详情见：libavformat/options_table.h，通常设置 1 达到首屏秒开效果
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1); // 特殊处理
        //播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10);

        // 关闭seek精准定位，提高seek速度（但会导致seek不精准）｜ 1打开 0关闭
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 0);
        //设置seekTo能够快速seek到指定位置并播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
        //seek 默认超时时间5*1000 ms
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "accurate-seek-timeout", 500);
        // 暂停输出直到停止后读取足够的数据包 默认1  不开启缓存？
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L); // 特殊处理
        // 通过立即清理数据包来减少等待时长  // 在每个数据包之后启用 I/O 上下文的刷新
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
        // 丢帧
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        // 完全关闭循环滤波
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        // 关闭range检测
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        //最大缓冲大小,单位kb 默认15M 不受packet-buffering影响?
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024);
        // 关闭packet-buffering后不生效，同理还有个packet-buffer-size
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 0);
        // 内存中缓存大小 默认32KB  影响起播速度，缓存这么多才行，不受packet-buffering影响  1316
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 64 * 1024);
        // 默认值 50000，范围2～50000
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", "50");

        //http重定向https
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        // 去掉音频
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "an", 1);

//                    //设置第一次唤醒read_thread线程的时间(毫秒)，范围100～5000，默认值100
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "first-high-water-mark-ms", 100);
//                    //设置下一次唤醒read_thread线程的时间(毫秒)，范围100～5000，默认值1000
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "next-high-water-mark-ms", 1000);
//                    //设置最后一次唤醒read_thread线程的时间(毫秒)，范围100～5000，默认值5000
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "last-high-water-mark-ms", 5000);
    }

    //-----------------------------------------------------------------
    public void setSpeed(float speed) {
        try {
            mMediaPlayer.setSpeed(speed);
            if (logEnable)
                Log.e(TAG, "#IjkVideoView----setSpeed------->>>>>" + speed);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public float getSpeed() {
        try {
            if (mMediaPlayer instanceof IjkMediaPlayer) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
                float speed = ijkMediaPlayer.getSpeed();
                if (logEnable)
                    Log.e(TAG, "#IjkVideoView----getSpeed------->>>>>" + speed);
                return speed;
            } else {
                if (logEnable)
                    Log.e(TAG, "#IjkVideoView----getSpeed----不支持--->>>>>");
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

    public void setOptionList(List<IjkMediaOption> optionList) {
        this.optionList = optionList;
        if (logEnable)
            Log.i(TAG, "#----setOptionList------->>>>>" + optionList);
    }
    //-------------------------------------------------------------------------------------------------------------------
    // Extend: Background
    //-------------------------

    private boolean mEnableBackgroundPlay = false;

    private void initBackground() {
        mEnableBackgroundPlay = mSettings.getEnableBackgroundPlay();
        if (mEnableBackgroundPlay) {
            MediaPlayerService.intentToStart(getContext());
            mMediaPlayer = MediaPlayerService.getMediaPlayer();
        }
    }

    public boolean isBackgroundPlayEnabled() {
        return mEnableBackgroundPlay;
    }

    public void enterBackground() {
        MediaPlayerService.setMediaPlayer(mMediaPlayer);
    }

    public void stopBackgroundPlay() {
        MediaPlayerService.setMediaPlayer(null);
    }

    public ITrackInfo[] getTrackInfo() {
        if (mMediaPlayer == null)
            return null;

        if (mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getTrackInfoSubFilter(mSettings.getTimedType());
        }

        return mMediaPlayer.getTrackInfo();
    }


    /**
     * 获取IMediaPlayer
     *
     * @return
     */
    public IMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void selectTrack(int stream) {
        MediaPlayerCompat.selectTrack(mMediaPlayer, stream);
        if (logEnable)
            Log.i(TAG, "#----selectTrack------->>>>>" + stream);
    }

    public void deselectTrack(int stream) {
        if (stream == getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT) && timedHelper != null) {
            timedHelper.timedEnd();
        }

        MediaPlayerCompat.deselectTrack(mMediaPlayer, stream);

        if (logEnable) {
            Log.i(TAG, "#----deselectTrack------->>>>>" + stream);
        }

    }

    public int getSelectedTrack(int trackType) {
        return MediaPlayerCompat.getSelectedTrack(mMediaPlayer, trackType);
    }

    public void setSubDataSource(String url, EsMap extraInfo) {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            if (timedHelper != null) {
                timedHelper.timedEnd();
            }
        }
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            if (logEnable) {
                Log.i(TAG, "#----setSubDataSource------->>>>>" + url);
            }
            ((IjkMediaPlayer) mMediaPlayer).setSubDataSource(url);
        }
    }

    public void closeTimedFile() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            if (timedHelper != null) {
                timedHelper.timedEnd();
            }
        }
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            if (logEnable) {
                Log.i(TAG, "#----closeTimedFile------->>>>>");
            }

            ((IjkMediaPlayer) mMediaPlayer).closeTimedFile();
        }
    }

    public void setOption(IjkMediaOption option) {
        if (option == null) return;
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            if (option.getType() == IjkMediaOption.IJK_MEDIA_OPTION_TYPE_LONG) {
                ((IjkMediaPlayer) mMediaPlayer).
                        setOption(option.getCategory(), option.getName(), option.getLongValue());
            } else if (option.getType() == IjkMediaOption.IJK_MEDIA_OPTION_TYPE_STRING) {
                ((IjkMediaPlayer) mMediaPlayer).
                        setOption(option.getCategory(), option.getName(), option.getStringValue());
            }

        }
    }

    public void setOptions(List<IjkMediaOption> options) {
        if (options == null || options.isEmpty()) return;
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            for (IjkMediaOption option : options) {
                if (option.getType() == IjkMediaOption.IJK_MEDIA_OPTION_TYPE_LONG) {
                    ((IjkMediaPlayer) mMediaPlayer).
                            setOption(option.getCategory(), option.getName(), option.getLongValue());
                } else if (option.getType() == IjkMediaOption.IJK_MEDIA_OPTION_TYPE_STRING) {
                    ((IjkMediaPlayer) mMediaPlayer).
                            setOption(option.getCategory(), option.getName(), option.getStringValue());
                }
            }
        }
    }

    public String getOption(String key) {
        return null;
    }

    public Map<String, String> getOptions() {
        return null;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (mRenderView != null && mRenderView.getView() != null) {
            mRenderView.getView().setVisibility(visibility);
        }
    }

    public void setUsingTransparentBackground(boolean usingTransparentBackground) {
        this.usingTransparentBackground = usingTransparentBackground;
    }

    public boolean isUsingTransparentBackground() {
        return usingTransparentBackground;
    }


    public void setLooping(boolean looping) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, this + "#--------setLooping------>>>>>" + looping);
        }
        this.looping = looping;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, this + "#--------onLayout------>>>>>left:" + left + "---->>>top:" + top + "---->>>right:" + right + "---->>>bottom:" + bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, this + "#--------onMeasure------>>>>>");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, this + "#--------onSizeChanged------>>>>>w:" + w + "---->>>h:" + h + "---->>>oldw:" + oldw + "---->>>oldh:" + oldh);
        }
    }

    private Handler mHandler;
    private Runnable mRunnable;
    private boolean isPositionListening;

    public void startPositionListener(View view) {
        if (isPositionListening || !isInPlaybackState() || view == null) {
            return;
        }

        if (mHandler == null || mRunnable == null) {
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    EsMap map = new EsMap();
                    map.pushLong("position", mMediaPlayer.getCurrentPosition());
                    EsProxy.get().sendUIEvent(view.getId(),
                            "onPlayerPositionChanged", map);
                    mHandler.postDelayed(this, 1000);
                }
            };
        }

        mHandler.post(mRunnable);
    }

    public void stopPositionListener() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        isPositionListening = false;
    }


    private void resetListeners() {
        mOnErrorListener = null;
        mOnPreparedListener = null;
        mOnInfoListener = null;
        mOnCompletionListener = null;
        mOnSeekCompleteListener = null;
        mOnVideoSizeChangedListener = null;
        mOnTimedTextListener = null;
    }
}
