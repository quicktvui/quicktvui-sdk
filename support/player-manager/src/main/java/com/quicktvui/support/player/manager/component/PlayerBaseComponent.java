package com.quicktvui.support.player.manager.component;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.player.manager.log.PLog;
import com.quicktvui.support.player.manager.volume.IPlayerVolume;
import com.tencent.mtt.hippy.utils.ExtendUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import com.quicktvui.support.player.manager.aspect.AspectRatio;
import com.quicktvui.support.player.manager.base.PlayerBaseView;
import com.quicktvui.support.player.manager.decode.Decode;
import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.model.IPlayerDimension;
import com.quicktvui.support.player.manager.model.IVideoUrl;
import com.quicktvui.support.player.manager.model.PlayerDimensionModel;
import com.quicktvui.support.player.manager.model.VideoUrlModel;
import com.quicktvui.support.player.manager.player.IPlayer;
import com.quicktvui.support.player.manager.volume.PlayerVolumeModel;

public abstract class PlayerBaseComponent implements IEsComponent<PlayerBaseView> {

    protected static final String TAG = "EsVideoPlayer";

    protected static final String OP_INIT = "init";
    //IJK设置播放器类型
    protected static final String OP_SET_PLAYER_TYPE = "setPlayerType";
    //IJK使用硬解码
    protected static final String OP_SET_USING_HARDWARE_DECODER = "setUsingHardwareDecoder";

    protected static final String OP_PLAY = "play";
    protected static final String OP_START = "start";
    protected static final String OP_PAUSE = "pause";
    protected static final String OP_RESUME = "resume";
    protected static final String OP_SEEK_TO = "seekTo";
    protected static final String OP_STOP = "stop";
    protected static final String OP_RESET = "reset";
    protected static final String OP_RELEASE = "release";
    protected static final String OP_STOP_FORCE = "stopForce";

    protected static final String OP_CHANGE_TO_FULL_SCREEN = "changeToFullScreen";
    protected static final String OP_IS_FULL_SCREEN = "isFullScreen";

    protected static final String OP_SET_ENABLED = "setEnabled";
    protected static final String OP_IS_ENABLED = "isEnabled";

    protected static final String OP_SET_STOPPED = "setStopped";
    protected static final String OP_IS_STOPPED = "isStopped";

    protected static final String OP_IS_PLAYING = "isPlaying";
    protected static final String OP_IS_PAUSED = "isPaused";

    protected static final String OP_GET_DURATION = "getDuration";
    protected static final String OP_GET_CURRENT_POSITION = "getCurrentPosition";
    protected static final String OP_GET_BUFFER_PERCENTAGE = "getBufferPercentage";

    protected static final String OP_SET_PLAYER_SIZE = "setPlayerSize";
    protected static final String OP_SET_LAYOUT = "setPlayerLayout";

    protected static final String OP_SET_PLAYER_DIMENSION = "setPlayerDimension";


    //-------------------------------------------------------------------------
    //刷新android view层级结构
    //requestPlayerViewLayout
    protected static final String OP_PLAYER_VIEW_REQUEST_LAYOUT = "requestPlayerViewLayout";
    //requestLayout
    protected static final String OP_VIEW_REQUEST_LAYOUT = "requestLayout";
    //requestCustomLayout
    protected static final String OP_REQUEST_CUSTOM_LAYOUT = "requestCustomLayout";
    //requestCustomSizeLayout
    protected static final String OP_REQUEST_CUSTOM_SIZE_LAYOUT = "requestCustomSizeLayout";
    //-------------------------------------------------------------------------
    protected static final String OP_GET_LOCATION_ON_SCREEN = "getLocationOnScreen";
    protected static final String OP_GET_LOCATION_IN_WINDOW = "getLocationInWindow";
    //-------------------------------------------------------------------------

    //设置默认宽高
    protected static final String OP_SET_DEFAULT_PLAYER_WIDTH = "setDefaultPlayerWidth";
    protected static final String OP_SET_DEFAULT_PLAYER_HEIGHT = "setDefaultPlayerHeight";
    protected static final String OP_SET_FULL_PLAYER_WIDTH = "setFullPlayerWidth";
    protected static final String OP_SET_FULL_PLAYER_HEIGHT = "setFullPlayerHeight";

    //倍速
    protected static final String OP_SET_PLAY_RATE = "setPlayRate";
    protected static final String OP_GET_CURRENT_PLAY_RATE = "getCurrentPlayRate";
    protected static final String OP_GET_ALL_PLAY_RATE = "getAllPlayRate";


    //清晰度
    protected static final String OP_SET_DEFINITION = "setDefinition";
    protected static final String OP_GET_ALL_DEFINITION = "getAllDefinition";
    protected static final String OP_GET_CURRENT_DEFINITION = "getCurrentDefinition";


    //画面比例
    protected static final String OP_SET_ASPECT_RATIO = "setAspectRatio";
    protected static final String OP_GET_ALL_ASPECT_RATIO = "getAllAspectRatio";
    protected static final String OP_GET_CURRENT_ASPECT_RATIO = "getCurrentAspectRatio";


    //解码
    protected static final String OP_SET_DECODE = "setDecode";
    protected static final String OP_GET_ALL_DECODE = "getAllDecode";
    protected static final String OP_GET_CURRENT_DECODE = "getCurrentDecode";

    //音量
    protected static final String OP_GET_LEFT_VOLUME = "getLeftVolume";
    protected static final String OP_GET_RIGHT_VOLUME = "getRightVolume";
    protected static final String OP_SET_LEFT_VOLUME = "setLeftVolume";
    protected static final String OP_SET_RIGHT_VOLUME = "setRightVolume";
    protected static final String OP_SET_VOLUME = "setVolume";
    protected static final String OP_SET_LEFT_RIGHT_VOLUME = "setLeftRightVolume";

    //是否支持版权号
    protected static final String OP_IS_SUPPORT_COPYRIGHT = "isSupportCopyright";

    //---------------------------广告独享-------------START-------------------
    //点击播放器广告view
    protected static final String OP_CLICK_PLAYER_VIEW = "clickPlayerView";
    protected static final String OP_SET_POINT_AD_PROGRESS = "setPointADProgress";
    protected static final String OP_SET_RELEASE_POINT_AD = "releasePointAD";
    //广告的可以跳过的时间
    protected static final String OP_AD_CAN_EXIT_TIME = "getADCanExitTime";
    //---------------------------广告独享-------------END-------------------

    //------------------------------IJK特有的方法--------------------------------------

    protected static final String OP_GET_BIT_RATE = "getBitRate";
    protected static final String OP_GET_TCP_SPEED = "getTcpSpeed";
    protected static final String OP_SET_OPTION_LONG = "setOptionLong";
    protected static final String OP_SET_OPTION_STRING = "setOptionString";
    //--------------------------------------------------------------------
    protected static final String OP_SET_PLAYER_TRANSLATION_X = "setPlayerTranslationX";
    protected static final String OP_SET_PLAYER_TRANSLATION_Y = "setPlayerTranslationY";
    protected static final String OP_SET_PLAYER_TRANSLATION_Z = "setPlayerTranslationZ";
    protected static final String OP_SET_PLAYER_TRANSLATION_XY = "setPlayerTranslationXY";
    protected static final String OP_SET_PLAYER_TRANSLATION_XYZ = "setPlayerTranslationXYZ";
    //--------------------------------------------------------------------

    protected static final String OP_ANDROID_INVOKE_VUE = "androidInvokeVue";

    //--------------------------------------------------------------------

    // TODO 勿动！！！！！
    //region TODO 插件化的时候用于获取插件的context，需要想办法优化下逻辑

    private static final Map<String, String> sMappingComponentFromWitchPlugin = new ConcurrentHashMap<>();

    public static void addPluginMapping(@NonNull String className, @NonNull String packageName) {
        sMappingComponentFromWitchPlugin.put(className, packageName);
    }

    public static @Nullable
    String getPluginPackage(String className) {
        return sMappingComponentFromWitchPlugin.get(className);
    }

    //endregion

    private Context context;

    public Context getContext() {
        return this.context;
    }

    /**
     * 设置默认宽度
     *
     * @param baseView
     * @param defaultPlayerWidth
     */
    @EsComponentAttribute
    public void defaultPlayerWidth(final PlayerBaseView baseView, int defaultPlayerWidth) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController-------setDefaultPlayerWidth------>>>>>" + defaultPlayerWidth);
        }
        if (baseView != null && baseView.getPlayer() != null) {
            IPlayer player = baseView.getPlayer();
            IPlayerDimension playerDimension = player.getPlayerDimension();
            if (playerDimension != null) {
                playerDimension.setDefaultPlayerWidth(defaultPlayerWidth);
            }
        } else {
            if (PLog.isLoggable(PLog.DEBUG)) {
                PLog.d(TAG, "#PlayerViewBaseController-------setDefaultPlayerWidth---baseView == null--->>>>>");
            }
        }
    }

    /**
     * 设置默认高度
     *
     * @param baseView
     * @param defaultPlayerHeight
     */
    @EsComponentAttribute
    public void defaultPlayerHeight(final PlayerBaseView baseView, int defaultPlayerHeight) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController-------setDefaultPlayerHeight------>>>>>" + defaultPlayerHeight);
        }

        if (baseView != null && baseView.getPlayer() != null) {
            IPlayer player = baseView.getPlayer();
            IPlayerDimension playerDimension = player.getPlayerDimension();
            if (playerDimension != null) {
                playerDimension.setDefaultPlayerHeight(defaultPlayerHeight);
            }
        }
    }

    @EsComponentAttribute
    public void fullPlayerWidth(final PlayerBaseView baseView, int fullPlayerWidth) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController-------setFullPlayerWidth------>>>>>" + fullPlayerWidth);
        }

        if (baseView != null && baseView.getPlayer() != null) {
            IPlayer player = baseView.getPlayer();
            IPlayerDimension playerDimension = player.getPlayerDimension();
            if (playerDimension != null) {
                playerDimension.setFullPlayerWidth(fullPlayerWidth);
            }
        }
    }

    @EsComponentAttribute
    public void fullPlayerHeight(final PlayerBaseView baseView, int fullPlayerHeight) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#IJKVideoViewController-------fullPlayerHeight------>>>>>" + fullPlayerHeight);
        }

        if (baseView != null && baseView.getPlayer() != null) {
            IPlayer player = baseView.getPlayer();
            IPlayerDimension playerDimension = player.getPlayerDimension();
            if (playerDimension != null) {
                playerDimension.setFullPlayerHeight(fullPlayerHeight);
            }
        }
    }

    @Override
    public PlayerBaseView createView(Context context, EsMap initParams) {
        this.context = context;
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------createViewImpl----start--->>>>>");
        }
//        Activity activity = EsProxy.get().getCurrentActivity();
        PlayerBaseView view = new PlayerBaseView(context, this);
        IPlayer player = initPlayer(EsProxy.get().getContext(getPluginPackage(getClass().getName())), view);
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#PlayerViewBaseController------createViewImpl---end-->>>>>" + player);
        }
        //保持屏幕常亮
        view.setKeepScreenOn(true);

        return view;
    }

    /**
     * 初始化播放器
     */
    protected abstract IPlayer initPlayer(Context context, PlayerBaseView playerRootView);

    @Override
    public void dispatchFunction(PlayerBaseView view, String functionName, EsArray params, EsPromise promise) {
        if (PLog.isLoggable(PLog.DEBUG)) {
            PLog.d(TAG, "#---dispatchFunction-----promise---->>>>>" + functionName + "---->>>" + Thread.currentThread());
        }
        if (view.getPlayer() == null) {
            return;
        }
        try {
            switch (functionName) {
                case OP_PLAYER_VIEW_REQUEST_LAYOUT:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.requestPlayerViewLayout();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_VIEW_REQUEST_LAYOUT:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.requestLayout();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_INIT:
                    try {
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_PLAY:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String url = params.getString(0);
                            if (PLog.isLoggable(PLog.DEBUG)) {
                                PLog.d(TAG, "#OP_PLAY------url-->>>>>" + url);
                            }
                            if (TextUtils.isEmpty(url)) {
                                return;
                            }
                            IVideoUrl videoUrl = new VideoUrlModel.Builder().setVideoUrl(url).build();
                            view.getPlayer().play(videoUrl);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_START:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            int position = params.getInt(0);
                            if (position > 0) {
                                view.getPlayer().start(position);
                            } else {
                                view.getPlayer().start();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_PAUSE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.getPlayer().pause();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_RESUME:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.getPlayer().resume();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_STOP:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.getPlayer().stop();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_RESET:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.getPlayer().reset();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_RELEASE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.getPlayer().release();
//                            view.release();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SEEK_TO:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            view.getPlayer().seekTo(params.getInt(0));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_ENABLED:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean enabled = params.getBoolean(0);
                            view.getPlayer().setEnabled(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_IS_ENABLED:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean isEnabled = view.getPlayer().isEnabled();
                            if (promise != null) {
                                promise.resolve(isEnabled);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_STOPPED:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean stopped = params.getBoolean(0);
                            view.getPlayer().setStopped(stopped);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_IS_STOPPED:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean isStopped = view.getPlayer().isStopped();
                            if (promise != null) {
                                promise.resolve(isStopped);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_IS_PLAYING:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean isPlaying = view.getPlayer().isPlaying();
                            if (promise != null) {
                                promise.resolve(isPlaying);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_IS_PAUSED:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean isPaused = view.getPlayer().isPaused();
                            if (promise != null) {
                                promise.resolve(isPaused);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_CHANGE_TO_FULL_SCREEN:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean fullScreen = params.getBoolean(0);
                            view.getPlayer().changeToFullScreen(fullScreen);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_PLAYER_SIZE:
                    //Log.i(TAG,"setPlayerSize:"+params);
                    try {
                        if (view != null && view.getPlayer() != null) {
                            int width = params.getInt(0);
                            int height = params.getInt(1);
                            //TODO LIULIPENG
                            view.setPlayerSize(width, height);
                            view.getPlayer().setPlayerSize(width, height);
                            //ExtendUtil.layoutView(view,0,0,width,height);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_LAYOUT:
                    try {
                        if (view.getPlayer() != null) {
                            int x = params.getInt(0);
                            int y = params.getInt(1);
                            int width = params.getInt(2);
                            int height = params.getInt(3);
                            view.setPlayerSize(width, height);
                            view.getPlayer().setPlayerSize(width, height);
                            ExtendUtil.layoutView(view,x,y,width,height);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_IS_FULL_SCREEN:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            boolean isFullScreen = view.getPlayer().isFullScreen();
                            if (promise != null) {
                                promise.resolve(isFullScreen);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_DURATION:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            long duration = view.getPlayer().getDuration();
                            if (promise != null) {
                                promise.resolve(duration);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_CURRENT_POSITION:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            long currentPosition = view.getPlayer().getCurrentPosition();
                            if (promise != null) {
                                promise.resolve(currentPosition);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_BUFFER_PERCENTAGE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            long bufferPercentage = view.getPlayer().getBufferPercentage();
                            if (promise != null) {
                                promise.resolve(bufferPercentage);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------播放器解码------------------------------
                //解码
                case OP_SET_DECODE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                int decodeValue = params.getInt(0);
                                Decode decode = Decode.getDecode(decodeValue);
                                if (decode != null) {
                                    view.getPlayer().setDecode(decode);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_ALL_DECODE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                List<Decode> decodeList = view.getPlayer().getDecodeList();
                                EsArray esArray = new EsArray();
                                if (decodeList != null && decodeList.size() > 0) {
                                    for (Decode decode : decodeList) {
                                        esArray.pushInt(decode.getValue());
                                    }
                                }
                                if (promise != null) {
                                    promise.resolve(esArray);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_CURRENT_DECODE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                Decode decode = view.getPlayer().getCurrentDecode();
                                if (promise != null) {
                                    promise.resolve(decode.getValue());
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------倍速------------------------------
                //倍速
                case OP_SET_PLAY_RATE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String rateString = params.getString(0);
                            try {
                                float rateValue = Float.parseFloat(rateString);
                                if (rateValue >= 0) {
                                    view.getPlayer().setPlayRate(rateValue);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_ALL_PLAY_RATE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                List<Float> playRateList = view.getPlayer().getAllPlayRate();
                                EsArray esArray = new EsArray();
                                if (playRateList != null && playRateList.size() > 0) {
                                    for (float playRate : playRateList) {
                                        esArray.pushString(playRate + "");
                                    }
                                }
                                if (promise != null) {
                                    promise.resolve(esArray);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_CURRENT_PLAY_RATE:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                float playRate = view.getPlayer().getCurrentPlayRate();
                                if (promise != null) {
                                    promise.resolve(playRate + "");
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                //----------------------------清晰度------------------------------
                //清晰度
                case OP_SET_DEFINITION:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                int definitionValue = params.getInt(0);
                                Definition definition = Definition.getDefinition(definitionValue);
                                if (definition != null) {
                                    view.getPlayer().setDefinition(definition);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_ALL_DEFINITION:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                List<Definition> definitionList = view.getPlayer().getAllDefinition();
                                EsArray esArray = new EsArray();
                                if (definitionList != null && definitionList.size() > 0) {
                                    for (Definition definition : definitionList) {
                                        esArray.pushInt(definition.getValue());
                                    }
                                }
                                if (promise != null) {
                                    promise.resolve(esArray);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_CURRENT_DEFINITION:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                Definition definition = view.getPlayer().getCurrentDefinition();
                                if (promise != null) {
                                    promise.resolve(definition.getValue());
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------画面比例------------------------------
                //画面比例
                case OP_SET_ASPECT_RATIO:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                int value = params.getInt(0);
                                AspectRatio aspectRatio = AspectRatio.getAspectRatio(value);
                                if (aspectRatio != null) {
                                    view.getPlayer().setAspectRatio(aspectRatio);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_ALL_ASPECT_RATIO:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                List<AspectRatio> aspectRatioList = view.getPlayer().getAllAspectRatio();
                                EsArray esArray = new EsArray();
                                if (aspectRatioList != null && aspectRatioList.size() > 0) {
                                    for (AspectRatio aspectRatio : aspectRatioList) {
                                        esArray.pushInt(aspectRatio.getValue());
                                    }
                                }

                                if (promise != null) {
                                    promise.resolve(esArray);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_GET_CURRENT_ASPECT_RATIO:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            try {
                                AspectRatio aspectRatio = view.getPlayer().getCurrentAspectRatio();
                                if (promise != null && aspectRatio != null) {
                                    promise.resolve(aspectRatio.getValue());
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------播放器的尺寸------------------------------
                case OP_SET_DEFAULT_PLAYER_WIDTH:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            int value = params.getInt(0);
                            IPlayerDimension playerDimension = view.getPlayer().getPlayerDimension();
                            if (playerDimension != null) {
                                playerDimension.setDefaultPlayerWidth(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DEFAULT_PLAYER_HEIGHT:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            int value = params.getInt(0);
                            IPlayerDimension playerDimension = view.getPlayer().getPlayerDimension();
                            if (playerDimension != null) {
                                playerDimension.setDefaultPlayerHeight(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_FULL_PLAYER_WIDTH:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            int value = params.getInt(0);
                            IPlayerDimension playerDimension = view.getPlayer().getPlayerDimension();
                            if (playerDimension != null) {
                                playerDimension.setFullPlayerWidth(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_FULL_PLAYER_HEIGHT:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            int value = params.getInt(0);
                            IPlayerDimension playerDimension = view.getPlayer().getPlayerDimension();
                            if (playerDimension != null) {
                                playerDimension.setFullPlayerHeight(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_PLAYER_DIMENSION:
                    try {
                        if (view.getPlayer() != null) {

                            try {
                                int defaultWidth = params.getInt(0);
                                int defaultHeight = params.getInt(1);
                                int fullPlayerWidth = params.getInt(2);
                                int fullPlayerHeight = params.getInt(3);
                                boolean fullScreen = params.getBoolean(4);
                                boolean quickUpdate = params.getBoolean(5);

                                if (PLog.isLoggable(PLog.DEBUG)) {
                                    PLog.d(TAG, "#------dispatchFunction---setPlayerDimension--->>>>>" + "defaultWidth:" + defaultWidth + "\n" + "defaultHeight:" + defaultHeight + "\n" + "fullPlayerWidth:" + fullPlayerWidth + "\n" + "fullPlayerHeight:" + fullPlayerHeight + "\n" + "fullScreen:" + fullScreen + "\n");
                                }

                                IPlayerDimension playerDimension = new PlayerDimensionModel.Builder(context).setDefaultPlayerWidth(defaultWidth).setDefaultPlayerHeight(defaultHeight).setFullPlayerWidth(fullPlayerWidth).setFullPlayerHeight(fullPlayerHeight).setFullScreen(fullScreen).build();
                                view.setPlayerDimension(playerDimension, quickUpdate);
                                view.getPlayer().setPlayerDimension(playerDimension);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                //是否支持版权号
                case OP_IS_SUPPORT_COPYRIGHT:
                    try {
                        if (promise != null) {
                            promise.resolve(false);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------播放器音量相关------------------------------
                //获取左声道
                case OP_GET_LEFT_VOLUME:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            IPlayerVolume volume = view.getPlayer().getVolume();
                            if (volume != null && promise != null) {
                                promise.resolve(volume.getLeftVolume());
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //获取右声道
                case OP_GET_RIGHT_VOLUME:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            IPlayerVolume volume = view.getPlayer().getVolume();
                            if (volume != null && promise != null) {
                                promise.resolve(volume.getRightVolume());
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                //设置左声道
                case OP_SET_LEFT_VOLUME:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String volumeString = params.getString(0);
                            if (PLog.isLoggable(PLog.DEBUG)) {
                                PLog.d(TAG, "#--start--Volume--OP_SET_LEFT_VOLUME-->>>>>" + volumeString);
                            }
                            try {
                                float volume = Float.parseFloat(volumeString);
                                IPlayerVolume volumeModel = view.getPlayer().getVolume();
                                if (volumeModel != null && volume >= 0f && volume <= 1f) {
                                    volumeModel.setLeftVolume(volume);
                                    if (PLog.isLoggable(PLog.DEBUG)) {
                                        PLog.d(TAG, "#--end--Volume--OP_SET_LEFT_VOLUME-->>>>>" + volumeModel);
                                    }
                                    view.getPlayer().setVolume(volumeModel);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //设置右声道
                case OP_SET_RIGHT_VOLUME:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String volumeString = params.getString(0);
                            if (PLog.isLoggable(PLog.DEBUG)) {
                                PLog.d(TAG, "#--start--Volume--OP_SET_RIGHT_VOLUME-->>>>>" + volumeString);
                            }
                            try {
                                float volume = Float.parseFloat(volumeString);
                                IPlayerVolume volumeModel = view.getPlayer().getVolume();
                                if (volumeModel != null && volume >= 0f && volume <= 1f) {
                                    volumeModel.setRightVolume(volume);
                                    if (PLog.isLoggable(PLog.DEBUG)) {
                                        PLog.d(TAG, "#--end--Volume--OP_SET_RIGHT_VOLUME-->>>>>" + volumeModel);
                                    }
                                    view.getPlayer().setVolume(volumeModel);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                //设置全声道
                case OP_SET_VOLUME:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String volumeString = params.getString(0);
                            try {
                                float volume = Float.parseFloat(volumeString);
                                if (volume >= 0f && volume <= 1f) {
                                    IPlayerVolume volumeModel = new PlayerVolumeModel.Builder().setLeftVolume(volume).setRightVolume(volume).build();
                                    view.getPlayer().setVolume(volumeModel);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                //设置左右声道
                case OP_SET_LEFT_RIGHT_VOLUME:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String leftVolumeString = params.getString(0);
                            String rightVolumeString = params.getString(1);
                            try {
                                float leftVolume = Float.parseFloat(leftVolumeString);
                                float rightVolume = Float.parseFloat(rightVolumeString);
                                if (leftVolume >= 0f && leftVolume <= 1f && rightVolume >= 0f && rightVolume <= 1f) {
                                    IPlayerVolume volumeModel = new PlayerVolumeModel.Builder().setLeftVolume(leftVolume).setRightVolume(rightVolume).build();
                                    view.getPlayer().setVolume(volumeModel);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_PLAYER_TRANSLATION_X:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String translationString = params.getString(0);
                            float value = Float.parseFloat(translationString);
                            view.setPlayerTranslationX(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_PLAYER_TRANSLATION_Y:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String translationString = params.getString(0);
                            float value = Float.parseFloat(translationString);
                            view.setPlayerTranslationY(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_PLAYER_TRANSLATION_Z:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String translationString = params.getString(0);
                            float value = Float.parseFloat(translationString);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                view.setPlayerTranslationZ(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_PLAYER_TRANSLATION_XY:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String translationXString = params.getString(0);
                            String translationYString = params.getString(1);
                            float valueX = Float.parseFloat(translationXString);
                            float valueY = Float.parseFloat(translationYString);
                            view.setPlayerTranslationXY(valueX, valueY);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_PLAYER_TRANSLATION_XYZ:
                    try {
                        if (view != null && view.getPlayer() != null) {
                            String translationXString = params.getString(0);
                            String translationYString = params.getString(1);
                            String translationZString = params.getString(2);
                            float valueX = Float.parseFloat(translationXString);
                            float valueY = Float.parseFloat(translationYString);
                            float valueZ = Float.parseFloat(translationZString);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                view.setPlayerTranslationXYZ(valueX, valueY, valueZ);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_REQUEST_CUSTOM_SIZE_LAYOUT:
                    try {
                        if (view != null) {
                            int width = params.getInt(0);
                            int height = params.getInt(1);
                            int x = params.getInt(2);
                            int y = params.getInt(3);
                            view.requestCustomLayout(width, height, x, y);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_REQUEST_CUSTOM_LAYOUT:
                    try {
                        if (view != null) {
                            view.requestCustomLayout();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //=========================================================

                case OP_GET_LOCATION_ON_SCREEN:
                    try {
                        if (view != null) {
                            int[] location = new int[2];
                            view.getLocationOnScreen(location);
                            EsMap esMap = new EsMap();
                            esMap.pushInt("locationX", location[0]);
                            esMap.pushInt("locationY", location[1]);
                            if (promise != null) {
                                promise.resolve(esMap);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GET_LOCATION_IN_WINDOW:
                    try {
                        if (view != null) {
                            int[] location = new int[2];
                            view.getLocationInWindow(location);
                            EsMap esMap = new EsMap();
                            esMap.pushInt("locationX", location[0]);
                            esMap.pushInt("locationY", location[1]);
                            if (promise != null) {
                                promise.resolve(esMap);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                //-----------------------------------------------------
                case OP_ANDROID_INVOKE_VUE:
                    try {
                        if (view != null) {
                            String value = params.getString(0);
                            view.androidInvokeVue(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(PlayerBaseView playerBaseView) {
        if (playerBaseView != null) {
            playerBaseView.removeAllViews();
        }
    }

}
