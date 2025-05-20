package com.quicktvui.support.player.ijk.player;

import static com.quicktvui.support.ijk.base.misc.ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT;
import static com.quicktvui.support.player.ijk.player.IjkVideoView.RENDER_SURFACE_VIEW;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.support.player.ijk.aspect.IJKAspectRatioMapper;
import com.quicktvui.support.player.ijk.setting.Settings;
import com.quicktvui.support.player.ijk.utils.AudioChannelType;
import com.quicktvui.support.player.ijk.utils.CommonUtils;
import com.quicktvui.support.player.ijk.utils.MetadataUtils;
import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.quicktvui.support.player.manager.aspect.AspectRatio;
import com.quicktvui.support.player.manager.callback.CallbackNotifier;
import com.quicktvui.support.player.manager.decode.Decode;
import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.manager.PlayerConfiguration;
import com.quicktvui.support.player.manager.model.IPlayerDimension;
import com.quicktvui.support.player.manager.model.IVideoUrl;
import com.quicktvui.support.player.manager.player.IPlayer;
import com.quicktvui.support.player.manager.player.IPlayerCallback;
import com.quicktvui.support.player.manager.player.PlayerError;
import com.quicktvui.support.player.manager.player.PlayerInfo;
import com.quicktvui.support.player.manager.player.PlayerStatus;
import com.quicktvui.support.player.manager.player.PlayerStatusEnum;
import com.quicktvui.support.player.manager.player.PlayerStatusParams;
import com.quicktvui.support.player.manager.player.PlayerType;
import com.quicktvui.support.player.manager.utils.Preconditions;
import com.quicktvui.support.player.manager.volume.IPlayerVolume;
import com.quicktvui.support.player.manager.volume.PlayerVolumeModel;
import com.quicktvui.support.ijk.base.IMediaPlayer;
import com.quicktvui.support.ijk.base.IjkMediaPlayer;
import com.quicktvui.support.ijk.base.IjkTimedText;
import com.quicktvui.support.ijk.base.misc.ITrackInfo;
import com.quicktvui.support.ijk.base.misc.IjkTrackInfo;
import com.quicktvui.support.ijk.base.misc.AndroidTrackInfo;
import com.quicktvui.support.ijk.base.misc.IMediaFormat;

public class IjkVideoPlayer implements IPlayer {

    private static final String TAG = "IjkVideoPlayer";
    public static final String EXTRA_KEY_ASPECT_RATIO = "DEFAULT_ASPECT_RATIO";
    public static final String EXTRA_KEY_LEFT_VOLUME = "DEFAULT_LEFT_VOLUME";
    public static final String EXTRA_KEY_RIGHT_VOLUME = "DEFAULT_RIGHT_VOLUME";
    public static final String EXTRA_KEY_PLAYER_OPTIONS = "DEFAULT_PLAYERS_OPTIONS";
    public static final String EXTRA_KEY_PLAYER_CONFIGURATIONS = "DEFAULT_PLAYERS_CONFIGURATIONS";
    public static final String EXTRA_KEY_PLAYER_EXTRA = "EXTRA_KEY_PLAYER_EXTRA";

    protected IjkVideoView videoView;

    protected IVideoUrl url;
    protected List<IPlayerCallback> listenerList = new CopyOnWriteArrayList<IPlayerCallback>();

    private Settings playerSettings;
    protected PlayerConfiguration configuration;
    protected Context context;

    private long historyPoint = 0;
    private boolean isVideoStarted = false;

    private boolean usingTransparentBackground = false;

    private FrameLayout playerRootView;

    /**
     * 开始时间
     */
    private long startTime = 0;
    private long preparedTime = 0;
    private long playingTime = 0;

    private IPlayerVolume playerVolume;

    private List<Float> playRateList = new ArrayList<>();

    private IjkLibManager ijkLibManager;
    private Handler handler;
    private String esPackageName;

    @Override
    public void init(PlayerConfiguration configuration) {
        init(configuration, null);
    }

    public void init(PlayerConfiguration configuration, String esPackageName) {
        this.configuration = configuration;
        this.context = configuration.getContext();
        this.handler = new Handler(Looper.getMainLooper());
        //
        initPlayerRootView(context);
        this.esPackageName = esPackageName;
    }


    public void setPlayerSettings(Settings playerSettings) {
        this.playerSettings = playerSettings;
    }

    //
    public void init() {
        notifyPlayerInitializeSuccess();
    }

    private void notifyPlayerInitializeSuccess() {
        PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
        playerStatus.status = PlayerStatusEnum.PLAYER_STATE_INITIALIZE_SUCCESS;
        notifyAllListeners(playerStatus);
    }

    private void notifyPlayerInitializeError() {
        PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
        playerStatus.status = PlayerStatusEnum.PLAYER_STATE_INITIALIZE_ERROR;
        notifyAllListeners(playerStatus);
    }

    /**
     * 初始化播放器父view
     */
    private void initPlayerRootView(Context context) {
        playerRootView = new FrameLayout(context);
        playerRootView.setFocusable(false);
        playerRootView.setClickable(true);
        playerRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (L.DEBUG) {
                    L.logD(this + "#---------点击------playerRootView------>>>>>" + videoView);
                }

                if (L.DEBUG) {
                    L.logD(this + "#---clickADView----播放器播放的-->>playerView:" + videoView);
                }

                if (videoView != null) { // 这里感觉肯定为null
                    videoView.performClick();
                }
            }
        });
    }

    /**
     * 播放器view
     */
    protected void initPlayerView() {

        try {
            if (videoView != null) {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-------initPlayerView---videoView != null--->>>>>");
                }
                videoView.stopPlayback();
                videoView.release(true);
                videoView = null;
            }
            //
            else {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-------initPlayerView---videoView == null--->>>>>");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        videoView = new IjkVideoView(context);
        videoView.setEsPackageName(esPackageName, playerSettings);
        if (L.DEBUG) {
            L.logD("#IjkPlayer------videoView---创建IjkVideoView--->>>>>videoView:" + videoView);
        }
        //是否使用透明背景
        videoView.setUsingTransparentBackground(usingTransparentBackground);

        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-------onError---->>>>>" + i + "---i1" + i1);
                }

                IjkVideoPlayer.this.onError(iMediaPlayer, i, i1);

                PlayerError error = new PlayerError(PlayerType.IJK, i + "", i);
                notifyAllListeners(error);
                return false;
            }
        });
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {

                IjkVideoPlayer.this.onPrepared(iMediaPlayer);

                preparedTime = System.currentTimeMillis();

                if (L.DEBUG) {
                    try {
                        if (L.DEBUG) {
                            L.logD("#IjkPlayer-------onPrepared---->>>>>historyPoint:" + historyPoint + "--TIME_COST--->>>" + (preparedTime - startTime));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                //快进
                if (historyPoint > 0) {
                    seekTo(historyPoint);
                }
                PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
                playerStatus.status = PlayerStatusEnum.PLAYER_STATE_PREPARED;
                notifyAllListeners(playerStatus);
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                return onInfo(iMediaPlayer, i, String.valueOf(i1));
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, String i1) {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer----setOnInfoListener----播放状态->>>>>" + i);
                }

                switch (i) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
                        playerStatus.status = PlayerStatusEnum.PLAYER_STATE_BUFFER_START;
                        notifyAllListeners(playerStatus);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        PlayerStatus status = new PlayerStatus(PlayerType.IJK);
                        status.status = PlayerStatusEnum.PLAYER_STATE_BUFFER_END;
                        notifyAllListeners(status);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        try {
                            playingTime = System.currentTimeMillis();
                            if (L.DEBUG) {
                                L.logD("#IjkPlayer-------onPlaying---->>>>>" + System.currentTimeMillis() + "--TIME_COST--->>>" + (playingTime - preparedTime));
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        PlayerStatus playingStatus = new PlayerStatus(PlayerType.IJK);
                        playingStatus.status = PlayerStatusEnum.PLAYER_STATE_PLAYING;
                        notifyAllListeners(playingStatus);
//                        break; 现在部分快应用在使用这个info
                    default:
                        notifyPlayerInfo(i, i1);
                        break;
                }
                return false;
            }
        });
        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer----onCompletion----->>>>>");
                }
                PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
                playerStatus.status = PlayerStatusEnum.PLAYER_STATE_PLAYBACK_COMPLETED;
                notifyAllListeners(playerStatus);
            }
        });
        videoView.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//                if (L.DEBUG) {
//                    L.logD("#IjkPlayer----onBufferingUpdate----->>>>>");
//                }
            }
        });
        videoView.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer----onSeekComplete----->>>>>");
                }
                PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
                playerStatus.status = PlayerStatusEnum.PLAYER_STATE_SEEK_COMPLETED;
                notifyAllListeners(playerStatus);
            }
        });

        videoView.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
                playerStatus.status = PlayerStatusEnum.PLAYER_STATE_VIDEO_SIZE_CHANGED;
                playerStatus.putData(PlayerStatusParams.PLAYER_WIDTH, i);
                playerStatus.putData(PlayerStatusParams.PLAYER_HEIGHT, i1);
                notifyAllListeners(playerStatus);
            }
        });

        //弹幕
        videoView.setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
                PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
                playerStatus.status = PlayerStatusEnum.PLAYER_STATE_TIMED_TEXT_CHANGED;
                if (ijkTimedText != null) {
                    playerStatus.putData(PlayerStatusParams.PLAYER_TIMED_TEXT, ijkTimedText.getText());
                    if (ijkTimedText.getBounds() != null) {
                        Rect rect = ijkTimedText.getBounds();
                        playerStatus.putData(PlayerStatusParams.PLAYER_TIMED_TEXT_LEFT, rect.left);
                        playerStatus.putData(PlayerStatusParams.PLAYER_TIMED_TEXT_TOP, rect.top);
                        playerStatus.putData(PlayerStatusParams.PLAYER_TIMED_TEXT_RIGHT, rect.right);
                        playerStatus.putData(PlayerStatusParams.PLAYER_TIMED_TEXT_BOTTOM, rect.bottom);
                    }
                }
                notifyAllListeners(playerStatus);
            }
        });

        //播放器初始化完毕
        PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
        playerStatus.status = PlayerStatusEnum.PLAYER_STATE_PLAYER_INITIALIZED;
        notifyAllListeners(playerStatus);
    }

    /**
     * 添加播放器view
     *
     * @param playerRootView
     * @param playerView
     */
    protected void addPlayerView(final FrameLayout playerRootView, final View playerView) {

        try {
            if (playerView != null) {
                playerView.setFocusable(false);
                playerView.setClickable(true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (L.DEBUG) {
                L.logD(playerRootView + "#IjkPlayer----addPlayerView----->>>>>" + playerView);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (playerView != null) {
                playerRootView.removeAllViews();
                playerRootView.addView(playerView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            } else {
                try {
                    L.logD(playerRootView + "#IjkDynamicPlayer-----<<<<addPlayerView>>>>---playerView is null------->>>>>>" + playerView);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        //播放器布局变化
        notifyPlayerSizeChanged();
    }

    private void notifyPlayerSizeChanged() {
        if (L.DEBUG) {
            L.logD("#IjkPlayer-----------notifyLayoutChanged--------->>>>>");
        }

        //播放器布局变化
        PlayerStatus playerStatusPreparing = new PlayerStatus(PlayerType.IJK);
        playerStatusPreparing.status = PlayerStatusEnum.PLAYER_STATE_PLAYER_VIEW_CHANGED;
        CallbackNotifier.notifyPlayerStatusChanged(listenerList, playerStatusPreparing);
    }

    /**
     * onPrepared
     *
     * @param iMediaPlayer
     */
    protected void onPrepared(IMediaPlayer iMediaPlayer) {

    }

    /**
     * onError
     *
     * @param iMediaPlayer
     * @param i
     * @param i1
     */
    public void onError(IMediaPlayer iMediaPlayer, int i, int i1) {

    }

    /**
     * onInfo
     *
     * @param iMediaPlayer
     * @param i
     * @param i1
     */
    public void onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        notifyPlayerInfo(i, i1 + "");
    }


    /**
     * 播放器设置
     */
    private void initPlayerLibrary() {
        if (playerSettings.getPlayerType() == Settings.PV_PLAYER__IjkMediaPlayer)
            try {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-------IjkMediaPlayer.native_profileBegin---->>>>>");
                }
                IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            } catch (Throwable e) {
                e.printStackTrace();
            }
    }

    @Override
    public void registerPlayerCallback(IPlayerCallback callback) {
        Preconditions.checkNotNull(callback);
        if (!listenerList.contains(callback)) {
            listenerList.add(callback);
        }
    }

    @Override
    public void unregisterPlayerCallback(IPlayerCallback callback) {
        listenerList.remove(callback);
    }

    @Override
    public void play(IVideoUrl url) {
        if (L.DEBUG) {
            L.logD("#IjkPlayer-------play---->>>>>" + url);
        }
        initIjkPlayerLibrary(url);
    }

    private void playVideoUrl(IVideoUrl url) {
        if (L.DEBUG) {
            L.logD("#IjkPlayer------playVideoUrl----->>>>>" + url);
        }
        this.url = url;
        startTime = System.currentTimeMillis();
        if (L.DEBUG) {
            L.logD("#IjkPlayer----TIME_COST---play---startTime->>>>>" + startTime);
        }

        /**
         * 初始化播放器view
         */
        initPlayerView();

        /**
         * 添加view
         */
        addPlayerView(playerRootView, videoView);

        /**
         * so
         */
        initPlayerLibrary();

        //额外参数
        Object extraObj = url.getExtra();
        EsMap extraInfo = null;
        if (L.DEBUG) {
            L.logD("#IjkPlayer------play--->>>>>extraObj:" + extraObj);
        }
        //默认画面比例
        try {
            if (extraObj != null && extraObj instanceof Map) {
                Map<String, Object> extraMap = ((Map) extraObj);
                int aspectRatio = -1;
                try {
                    Object aspectRatioObj = extraMap.get(EXTRA_KEY_ASPECT_RATIO);
                    aspectRatio = (Integer) aspectRatioObj;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (aspectRatio >= 0 && videoView != null) {
                    if (L.DEBUG) {
                        L.logD("#IjkPlayer------play---setAspectRatio默认画面比例--->>>>>aspectRatio:" + aspectRatio);
                    }
                    videoView.setAspectRatio(aspectRatio);
                }

                //2.音量
                float leftVolume = 1f;
                float rightVolume = 1f;
                try {
                    Object leftVolumeObj = extraMap.get(EXTRA_KEY_LEFT_VOLUME);
                    String leftVolumeStr = (String) leftVolumeObj;
                    leftVolume = Float.parseFloat(leftVolumeStr);
                    if (leftVolume < 0f || leftVolume > 1f) {
                        leftVolume = 1f;
                    }
                    Object rightVolumeObj = extraMap.get(EXTRA_KEY_RIGHT_VOLUME);
                    String rightVolumeStr = (String) rightVolumeObj;
                    rightVolume = Float.parseFloat(rightVolumeStr);
                    if (rightVolume < 0f || rightVolume > 1f) {
                        rightVolume = 1f;
                    }

                    if (L.DEBUG) {
                        L.logD("#IjkPlayer--1-play--Volume-->>>>>leftVolume:" + leftVolume + "---->>>rightVolume:" + rightVolume);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (L.DEBUG) {
                    L.logD("#IjkPlayer-2--play--Volume-->>>>>leftVolume:" + leftVolume + "---->>>rightVolume:" + rightVolume);
                }
                IPlayerVolume volume = new PlayerVolumeModel.Builder().setLeftVolume(leftVolume).setRightVolume(rightVolume).build();
                //
                setVolume(volume);

                //3.播放器参数
                Object playerOptionsObj = extraMap.get(EXTRA_KEY_PLAYER_OPTIONS);
                if (playerOptionsObj instanceof EsArray) {
                    EsArray optionsArray = (EsArray) playerOptionsObj;
                    if (optionsArray.size() > 0) {
                        List<IjkMediaOption> optionList = new ArrayList<>(optionsArray.size());
                        for (int i = 0; i < optionsArray.size(); i++) {
                            EsMap optionObj = optionsArray.getMap(i);
                            if (optionObj != null) {
                                optionList.add(CommonUtils.getIjkMediaOption(optionObj));
                            }
                        }
                        if (L.DEBUG) {
                            L.logD("#---------解析optionList--------->>>>>>" + optionList);
                        }
                        if (!optionList.isEmpty()) {
                            videoView.setOptionList(optionList);
                        }
                    }
                }

                //4.播放器配置参数
                Object playerConfigObj = extraMap.get(EXTRA_KEY_PLAYER_CONFIGURATIONS);
                if (playerConfigObj instanceof EsMap) {
                    EsMap playerConfigMap = (EsMap) playerConfigObj;
                    //1.loop play
                    boolean loop = playerConfigMap.getBoolean("loop");
                    videoView.setLooping(loop);

                    //2.render type
                    int render = playerConfigMap.getInt("render");
                    videoView.setRender(render);
                } else {
                    videoView.setRender(RENDER_SURFACE_VIEW);
                }

                // 额外信息
                Object tempInfo = extraMap.get(EXTRA_KEY_PLAYER_EXTRA);
                if (tempInfo instanceof EsMap && ((EsMap) tempInfo).size() > 0) {
                    extraInfo = (EsMap) tempInfo;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // Apollo统计暂用
        PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
        playerStatus.status = PlayerStatusEnum.PLAYER_STATE_PREPARING;
        notifyAllListeners(playerStatus);

        videoView.setVideoPath(url.getUrl(), extraInfo);

        //播放速率
        initPlayRateList();
        //画面比例
        initAspectRatioList();

        try {
            //如果已经调用了start
            if (isVideoStarted) {
                this.start(this.historyPoint);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initIjkPlayerLibrary(IVideoUrl url) {
        this.ijkLibManager = IjkLibManager.getInstance();
        this.ijkLibManager.init(context);
        this.ijkLibManager.loadLibrary(playerSettings, new IjkLibManager.IIjkLibLoadCallback() {
            @Override
            public void onLibraryLoadSuccess() {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-----onLibraryLoadSuccess---->>>>>" + context);
                }

                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (L.DEBUG) {
                                L.logD("#IjkPlayer------context is activity------>>>>>");
                            }
                            playVideoUrl(url);
                        }
                    });
                } else {
                    if (L.DEBUG) {
                        L.logD("#IjkPlayer------context is not activity------>>>>>");
                    }
                    PlayerError error = new PlayerError(PlayerType.IJK, "main handler is null...", -1);
                    notifyAllListeners(error);
                }
            }

            @Override
            public void onLibraryLoadError(Throwable e) {
                e.printStackTrace();
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-----onLibraryLoadError---->>>>>");
                }
                PlayerError error = new PlayerError(PlayerType.IJK, e.getMessage(), -1);
                notifyAllListeners(error);
            }
        });
    }

    private void initAspectRatioList() {
        if (L.DEBUG) {
            L.logD("#IjkPlayer--------initAspectRatioList--------->>>>>");
        }
        //1.画面比例列表
        List<AspectRatio> aspectRatioList = getAllAspectRatio();
        if (L.DEBUG) {
            L.logD("#IjkPlayer--------initAspectRatioList----aspectRatioList----->>>>>" + aspectRatioList);
        }
        notifyAllListeners(aspectRatioList);

        //2.当前的画面比例
        AspectRatio aspectRatio = getCurrentAspectRatio();
        if (L.DEBUG) {
            L.logD("#IjkPlayer--------initAspectRatioList----aspectRatio----->>>>>" + aspectRatio);
        }
        notifyAllListeners(aspectRatio);
    }

    /**
     * 初始化播放速率
     */
    private void initPlayRateList() {
        playRateList.clear();
        playRateList.add(0.5f);
        playRateList.add(0.75f);
        playRateList.add(1f);
        playRateList.add(1.2f);
        playRateList.add(1.25f);
        playRateList.add(1.5f);
        playRateList.add(1.75f);
        playRateList.add(2f);
        playRateList.add(2.5f);

        notifyAllPlayRateChanged(playRateList);
        notifyPlayRateChanged(1f);
    }

    private void notifyAllPlayRateChanged(List<Float> rateList) {
        CallbackNotifier.notifyPlayerAllPlayRateChanged(listenerList, rateList);
    }

    private void notifyPlayRateChanged(float playRate) {
        CallbackNotifier.notifyPlayerPlayRateChanged(listenerList, playRate);
    }

    @Override
    public boolean isPlaying() {
        if (videoView != null) {
            boolean isPlaying = videoView.isPlaying();
            return isPlaying;
        } else {
            if (L.DEBUG) {
                L.logD("#IjkPlayer--------videoView is null--->>>>>");
            }
        }
        return false;
    }

    @Override
    public boolean isPaused() {
        if (videoView != null) {
            boolean isPaused = videoView.isPaused();
//             if (L.DEBUG) {
//                L.logD( "#IjkPlayer--------isPaused--->>>>>" + isPaused);
//            }
            return isPaused;
        } else {
            if (L.DEBUG) {
                L.logD("#IjkPlayer--------videoView is null--->>>>>");
            }
        }
        return false;
    }

    @Override
    public void start(long progress) {
        if (L.DEBUG) {
            L.logD("#IjkPlayer----1---start---->>>>>" + progress);
        }
        isVideoStarted = true;
        if (videoView != null) {
            videoView.start();
        }
        //历史记录
        historyPoint = progress;

        try {
            if (videoView != null && videoView.isPlaying()) {
                PlayerStatus playingStatus = new PlayerStatus(PlayerType.IJK);
                playingStatus.status = PlayerStatusEnum.PLAYER_STATE_PLAYING;
                notifyAllListeners(playingStatus);
            }
            if (L.DEBUG) {
                L.logD("#IjkPlayer----2---start---->>>>>");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (L.DEBUG) {
            L.logD("#IjkPlayer-------start---->>>>>");
        }
        start(0);
    }

    @Override
    public void pause() {
        if (videoView != null) {
            videoView.pause();
            PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
            playerStatus.status = PlayerStatusEnum.PLAYER_STATE_PAUSED;
            notifyAllListeners(playerStatus);
        }
    }

    @Override
    public void resume() {
        if (videoView != null) {
            videoView.resume();
        }
    }

    @Override
    public void stop() {
        if (L.DEBUG) {
            L.logD("#IjkPlayer-------stop---START->>>>>");
        }
        stopForce(false);
    }

    public void stopForce(boolean force) {
        if (L.DEBUG) {
            L.logD("#IjkPlayer-------stopForce---START->>>>>");
        }
        try {
            historyPoint = 0;
            isVideoStarted = false;
            PlayerStatus playerStatus = new PlayerStatus(getPlayerType());
            playerStatus.status = PlayerStatusEnum.PLAYER_STATE_BEFORE_STOP;
            notifyAllListeners(playerStatus);

            if (videoView != null) {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-------stop---videoView != null--->>>>>");
                }
                videoView.stop(force);
                videoView.releaseForce(true, force);

                //
                releaseIJKPlayer();
            }
            //
            else {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer-------stop---videoView == null--->>>>>");
                }
            }
            //
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }

            playerStatus.status = PlayerStatusEnum.PLAYER_STATE_STOP;
            notifyAllListeners(playerStatus);

            if (L.DEBUG) {
                L.logD("#IjkPlayer-------stop---END->>>>>");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (playerRootView != null) {
                playerRootView.removeAllViews();

                if (L.DEBUG) {
                    L.logD("#IjkPlayer-----stop--videoView---置空->>>>>" + videoView);
                }
                videoView = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        historyPoint = 0;
        isVideoStarted = false;
    }

    @Override
    public void release() {
        historyPoint = 0;
        isVideoStarted = false;
        try {
            if (playerRootView != null) {
                playerRootView.removeAllViews();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void releaseIJKPlayer() {
        if (playerSettings.getPlayerType() == Settings.PV_PLAYER__IjkMediaPlayer)
            try {
                if (L.DEBUG) {
                    L.logD("#IjkPlayer----IjkMediaPlayer.native_profileEnd()-->>>>>");
                }
                IjkMediaPlayer.native_profileEnd();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
    }

    @Override
    public void seekTo(long msec) {
        if (L.DEBUG) {
            L.logD("#--------seekTo--->>>>>" + msec);
        }
        if (videoView != null) {
            videoView.seekTo((int) msec);
        }
        PlayerStatus playerStatus = new PlayerStatus(PlayerType.IJK);
        playerStatus.status = PlayerStatusEnum.PLAYER_STATE_SEEK_START;
        notifyAllListeners(playerStatus);
    }

    /**
     * 设置当前的画面比例
     *
     * @param aspectRatio
     */
    @Override
    public void setAspectRatio(AspectRatio aspectRatio) {
        if (L.DEBUG) {
            L.logD(this + "#--------setAspectRatio--->>>>>aspectRatio:" + aspectRatio);
        }
        int ar = IJKAspectRatioMapper.generateAspectRatio(aspectRatio);
        if (ar < 0) {
            return;
        }
        if (videoView != null) {
            if (L.DEBUG) {
                L.logD("#--------setAspectRatio--->>>>>ar:" + ar);
            }
            videoView.setAspectRatio(ar);
        }
        notifyAllListeners(aspectRatio);
        notifyPlayerSizeChanged();
    }

    /**
     * 获取当前的画面比例
     *
     * @return
     */
    @Override
    public AspectRatio getCurrentAspectRatio() {
        if (videoView != null) {
            int ar = videoView.getCurrentAspectRatio();
            AspectRatio aspectRatio = IJKAspectRatioMapper.generateAspectRatio(ar);
            if (L.DEBUG) {
                L.logD("#--------getCurrentAspectRatio--->>>>>aspectRatio:" + aspectRatio);
            }
            return aspectRatio;
        }
        return null;
    }

    /**
     * 获取所有的画面比例
     *
     * @return
     */
    @Override
    public List<AspectRatio> getAllAspectRatio() {
        List<AspectRatio> aspectRatioList = IJKAspectRatioMapper.generateAllAspectRatio();
        if (L.DEBUG) {
            L.logD("#--------getAllAspectRatio--->>>>>aspectRatioList:" + aspectRatioList);
        }
        return aspectRatioList;
    }

    @Override
    public void setPlayRate(float rate) {
        if (videoView != null) {
            videoView.setSpeed(rate);
            CallbackNotifier.notifyPlayerPlayRateChanged(listenerList, rate);
        }
    }

    @Override
    public float getCurrentPlayRate() {
        if (videoView != null) {
            float speed = videoView.getSpeed();
            return speed;
        }
        return 1f;
    }

    @Override
    public List<Float> getAllPlayRate() {
        return playRateList;
    }

    @Override
    public List<Definition> getAllDefinition() {
        return null;
    }

    @Override
    public Definition getCurrentDefinition() {
        return null;
    }

    @Override
    public long getDuration() {
        try {
            if (videoView == null) {
                return 0;
            }
            long duration = videoView.getDuration();
            if (L.DEBUG) {
                L.logD(this + "----getDuration--->>>>>>>>>" + duration);
            }
            return duration;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        try {
            if (videoView == null) {
                return 0;
            }
            long currentPosition = videoView.getCurrentPosition();
            if (L.DEBUG) {
                L.logD(this + "----getCurrentPosition--->>>>>>>>>" + currentPosition);
            }
            return currentPosition;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getBufferPercentage() {
        try {
            if (videoView == null) {
                return 0;
            }
            long bufferPercentage = videoView.getBufferPercentage();
            if (L.DEBUG) {
                L.logD(this + "----getBufferPercentage--->>>>>>>>>" + bufferPercentage);
            }
            return bufferPercentage;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setDefinition(Definition definition) {

    }

    @Override
    public List<Decode> getDecodeList() {
        return null;
    }

    @Override
    public Decode getCurrentDecode() {
        return null;
    }

    @Override
    public void setDecode(Decode decode) {

    }

    @Override
    public int getPlayerWidth() {
        if (videoView == null) {
            return 0;
        }
        return videoView.getWidth();
    }

    @Override
    public int getPlayerHeight() {
        if (videoView == null) {
            return 0;
        }
        return videoView.getHeight();
    }

    @Override
    public View getPlayerView() {
        return playerRootView;
    }

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.IJK;
    }

    @Override
    public IVideoUrl getPlayUrl() {
        if (L.DEBUG) {
            L.logD(this + "#-------getPlayUrl---->>>>>" + url);
        }
        return url;
    }

    /**
     * 进入全屏退出全屏
     *
     * @param fullScreen
     */
    @Override
    public void changeToFullScreen(boolean fullScreen) {
        if (L.DEBUG) {
            L.logD("#IjkPlayer-------changeToFullScreen---->>>>>" + fullScreen);
        }
        if (configuration != null) {
            configuration.setFullScreen(fullScreen);
        }
        playerDimensionChanged(fullScreen);
    }

    @Override
    public boolean isFullScreen() {
        return configuration != null && configuration.isFullScreen();
    }

    /**
     * 设置播放器的尺寸
     *
     * @param playerDimension
     */
    @Override
    public void setPlayerDimension(IPlayerDimension playerDimension) {
        if (configuration != null) {
            configuration.setPlayerDimension(playerDimension);
        }
        try {
            playerDimensionChanged(playerDimension.isFullScreen());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayerSize(int width, int height) {
        if (L.DEBUG) {
            L.logD(this + "#--------setPlayerSize--->>>>>width:" + width + "---->>>height:" + height);
        }

        try {
            if (playerRootView != null) {
                ViewGroup.LayoutParams layoutParams = playerRootView.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                playerRootView.setLayoutParams(layoutParams);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (videoView != null) {
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                videoView.setLayoutParams(layoutParams);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fullScreen
     */
    private void playerDimensionChanged(boolean fullScreen) {
        if (playerRootView == null) {
            return;
        }

        try {
            if (fullScreen) {
                //root view
                ViewGroup.LayoutParams rootLayoutParams = playerRootView.getLayoutParams();
                rootLayoutParams.width = configuration.getPlayerDimension().getFullPlayerWidth();
                rootLayoutParams.height = configuration.getPlayerDimension().getFullPlayerHeight();
                playerRootView.setLayoutParams(rootLayoutParams);

                //video view
                if (videoView != null) {
                    ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                    layoutParams.width = configuration.getPlayerDimension().getFullPlayerWidth();
                    layoutParams.height = configuration.getPlayerDimension().getFullPlayerHeight();
                    videoView.setLayoutParams(layoutParams);
                }

                if (L.DEBUG) {
                    L.logD(this + "#----changeToFullScreen--------fullScreen---->>>>>" + configuration.getPlayerDimension());
                }
            } else {
                //root view
                ViewGroup.LayoutParams rootLayoutParams = playerRootView.getLayoutParams();
                if (rootLayoutParams != null) {
                    rootLayoutParams.width = configuration.getPlayerDimension().getDefaultPlayerWidth();
                    rootLayoutParams.height = configuration.getPlayerDimension().getDefaultPlayerHeight();
                    playerRootView.setLayoutParams(rootLayoutParams);
                }

                //video view
                if (videoView != null) {
                    ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.width = configuration.getPlayerDimension().getDefaultPlayerWidth();
                        layoutParams.height = configuration.getPlayerDimension().getDefaultPlayerHeight();
                        videoView.setLayoutParams(layoutParams);
                    }
                }

                if (L.DEBUG) {
                    L.logD(this + "#--------changeToFullScreen----small---->>>>>" + configuration.getPlayerDimension());
                }
            }

            if (videoView != null) {
                videoView.requestLayout();
                videoView.invalidate();
            }
            playerRootView.requestLayout();
            playerRootView.invalidate();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public IPlayerDimension getPlayerDimension() {
        if (configuration != null) {
            return configuration.getPlayerDimension();
        }
        return null;
    }

    /**
     * 设置左右声道的音量
     *
     * @param playerVolume
     */
    @Override
    public void setVolume(IPlayerVolume playerVolume) {
        this.playerVolume = playerVolume;
        if (videoView != null && playerVolume != null) {
            if (L.DEBUG) {
                L.logD("#IjkPlayer------------setVolume--------->>>>>" + playerVolume);
            }
            videoView.setVolume(playerVolume.getLeftVolume(), playerVolume.getRightVolume());
            //回调
            CallbackNotifier.notifyPlayerVolumeChanged(listenerList, playerVolume);
        } else {
            if (L.DEBUG) {
                L.logD("#IjkPlayer------------setVolume-----videoView IS NULL---->>>>>" + playerVolume);
            }
        }
    }

    @Override
    public IPlayerVolume getVolume() {
        if (playerVolume == null) {
            return new PlayerVolumeModel.Builder().build();
        }
        //
        else {
            return playerVolume;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public void setStopped(boolean stopped) {

    }

    public long getBitRate() {
        try {
            if (videoView != null) {
                return videoView.getBitRate();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getTcpSpeed() {
        try {
            if (videoView != null) {
                return videoView.getTcpSpeed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isUsingTransparentBackground() {
        return usingTransparentBackground;
    }

    public void setUsingTransparentBackground(boolean usingTransparentBackground) {
        this.usingTransparentBackground = usingTransparentBackground;
    }


    /**
     * 获取轨道信息
     *
     * @param promise 回调
     * @param sysType 系统播放器获取等级 1：默认  2：增加格式（有反射，影响性能）  3：增加声道信息（解析url，更加耗性能）
     */
    public void getTrackInfo(EsPromise promise, int sysType) {
        new Thread(() -> {
            if (sysType == 3 &&
                    playerSettings.getPlayerType() == Settings.PV_PLAYER__AndroidMediaPlayer &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                MetadataUtils.getAndroidTracksInfo(url.getUrl(), promise);
            } else {
                getTrackInfo(promise, sysType == 2);
            }
        }).start();
    }

    public void getTrackInfo(EsPromise promise, boolean more) {
        EsArray array = new EsArray();
        if (videoView != null) {
            ITrackInfo[] trackInfo = videoView.getTrackInfo();
            if (trackInfo == null) {
                promise.resolve(array);
                return;
            }
            for (int i = 0; i < trackInfo.length; i++) {
                ITrackInfo iTrackInfo = trackInfo[i];
                EsMap map = new EsMap();
                int trackType = iTrackInfo.getTrackType();
                map.pushBoolean(MetadataUtils.STREAM_KEY_SEEK_FLAG, true);
                map.pushInt(MetadataUtils.STREAM_KEY_TRACK_TYPE, trackType);
                map.pushString(MetadataUtils.STREAM_KEY_LANGUAGE, iTrackInfo.getLanguage());
                if (iTrackInfo instanceof IjkTrackInfo) {
                    map.pushInt(MetadataUtils.STREAM_KEY_INDEX, iTrackInfo.getStreamIndex());
                    map.pushString(MetadataUtils.STREAM_KEY_TITLE, iTrackInfo.getTitle());
                    if (trackType == MEDIA_TRACK_TYPE_TIMEDTEXT) {
                        map.pushInt(MetadataUtils.STREAM_KEY_SUB_TYPE, iTrackInfo.getSubType());
                    }
                    // 增加更多详细信息
                    IMediaFormat format = iTrackInfo.getFormat();
                    map.pushString(MetadataUtils.STREAM_KEY_CODEC, format.getString(MetadataUtils.IJK_STREAM_KEY_CODE_NAME));
                    if (trackType == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                        int channels = format.getInteger(MetadataUtils.IJK_STREAM_KEY_CHANNELS);
                        String channelName = AudioChannelType.getChannelName(channels);
                        map.pushInt(MetadataUtils.STREAM_KEY_CHANNELS, channels);
                        map.pushString(MetadataUtils.STREAM_KEY_CHANNEL_NAME, channelName);
                    } else if (trackType == ITrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                        map.pushInt(MetadataUtils.STREAM_KEY_VIDEO_WIDTH, format.getInteger(MetadataUtils.IJK_STREAM_KEY_WIDTH));
                        map.pushInt(MetadataUtils.STREAM_KEY_VIDEO_HEIGHT, format.getInteger(MetadataUtils.IJK_STREAM_KEY_HEIGHT));
                    }
                } else {
                    map.pushInt(MetadataUtils.STREAM_KEY_INDEX, i);
                    if (more)
                        MetadataUtils.getAndroidTrackInfo((AndroidTrackInfo) iTrackInfo, map);
                }

                array.pushMap(map);
            }
            promise.resolve(array);
            if (L.DEBUG) {
                L.logD("#ijkVideoPlayer--------getTrackInfo--->>>>>" + array);
            }
            return;
        }

        promise.resolve(array);
    }

    public void selectTack(int trackIndex) {
        if (videoView != null) {
            videoView.selectTrack(trackIndex);
        }
    }

    public void deselectTack(int trackIndex) {
        if (videoView != null) {
            videoView.deselectTrack(trackIndex);
        }
    }

    public int getSelectedTrack(int trackType) {
        if (videoView != null) {
            return videoView.getSelectedTrack(trackType);
        }
        return -1;
    }

    private String subUrl;

    public void setSubDataSource(String url, EsMap extraInfo) {
        subUrl = url;
        if (videoView != null) {
            videoView.setSubDataSource(url, extraInfo);
        }
    }

    public void closeTimedFile() {
        subUrl = null;
        if (videoView != null) {
            videoView.closeTimedFile();
        }
    }

    public void setOptions(List<IjkMediaOption> options) {
        if (videoView != null) {
            videoView.setOptions(options);
        }
    }

    public void setOption(IjkMediaOption option) {
        if (videoView != null) {
            videoView.setOption(option);
        }
    }

    public String getOption(String key) {
        if (videoView != null) {
            return videoView.getOption(key);
        }
        return null;
    }

    public EsArray getOptions() {
        if (videoView != null) {
            Map<String, String> options = videoView.getOptions();
            if (options != null) {
                EsArray array = new EsArray();
                Set<Map.Entry<String, String>> entries = options.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    EsMap map = new EsMap();
                    map.pushString(entry.getKey(), entry.getValue());
                    array.pushMap(map);
                }
                return array;
            }
        }
        return null;
    }

    private void notifyAllListeners(AspectRatio aspectRatio) {
        if (L.DEBUG) {
            L.logD("#--------notifyAllListeners--->>>>>" + aspectRatio);
        }
        CallbackNotifier.notifyAspectRatioChanged(listenerList, aspectRatio);
    }

    private void notifyAllListeners(List<AspectRatio> aspectRatioList) {
        if (L.DEBUG) {
            L.logD("#--------notifyAllListeners--->>>>>" + aspectRatioList);
        }
        CallbackNotifier.notifyAspectRatioListChanged(listenerList, aspectRatioList);
    }

    protected void notifyAllListeners(PlayerStatus playerStatus) {
        if (L.DEBUG) {
            L.logD("#BasePlayerManager--------notifyAllListeners--->>>>>" + playerStatus);
        }
        CallbackNotifier.notifyPlayerStatusChanged(listenerList, playerStatus);
    }

    private void notifyAllListeners(PlayerError playerError) {
        CallbackNotifier.notifyPlayerErrorChanged(listenerList, playerError);
    }

    private void notifyPlayerInfo(int code, String message) {
        PlayerInfo playerInfo = new PlayerInfo(PlayerType.IJK, message, code);
        CallbackNotifier.notifyPlayerInfoChanged(listenerList, playerInfo);
    }

    public void startPositionListener(View view) {
        if (videoView != null) {
            videoView.startPositionListener(view);
        }
    }

    public void stopPositionListener() {
        if (videoView != null) {
            videoView.stopPositionListener();
        }
    }
}

