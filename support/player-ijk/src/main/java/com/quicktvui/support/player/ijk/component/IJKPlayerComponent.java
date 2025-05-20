package com.quicktvui.support.player.ijk.component;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.core.EsProxy;

import java.util.HashMap;
import java.util.Map;

import com.quicktvui.support.player.ijk.utils.CommonUtils;
import com.quicktvui.support.player.ijk.utils.FormatTools;
import com.quicktvui.support.player.ijk.player.IjkVideoPlayer;
import com.quicktvui.support.player.ijk.setting.Settings;
import com.quicktvui.support.player.manager.base.PlayerBaseView;
import com.quicktvui.support.player.manager.component.PlayerBaseComponent;
import com.quicktvui.support.player.manager.log.PLog;
import com.quicktvui.support.player.manager.manager.PlayerConfiguration;
import com.quicktvui.support.player.manager.model.IPlayerDimension;
import com.quicktvui.support.player.manager.model.IVideoUrl;
import com.quicktvui.support.player.manager.model.PlayerDimensionModel;
import com.quicktvui.support.player.manager.model.VideoUrlModel;
import com.quicktvui.support.player.manager.player.IPlayer;

/**
 * ijk播放器
 */
@ESKitAutoRegister
public class IJKPlayerComponent extends PlayerBaseComponent {

    protected static final String OP_GET_TRACK_INFO = "getTrackInfo";
    protected static final String OP_SELECT_TRACK = "selectTrack";
    protected static final String OP_GET_SELECT_TRACK = "getSelectTrack";
    protected static final String OP_DE_SELECT_TRACK = "deselectTrack";
    protected static final String OP_OPTION_CATEGORY = "setOptionCategory";
    protected static final String OP_GET_TCP_SPEED = "getTcpSpeed";
    protected static final String OP_GET_BIT_RATE = "getBitRate";
    protected static final String OP_START_POSITION_LISTENER = "startPositionListener";
    protected static final String OP_STOP_POSITION_LISTENER = "stopPositionListener";

    protected static final String OP_SET_DATA_SOURCE = "setSubDataSource";
    protected static final String OP_CLOSE_TIMED_FILE = "closeTimedFile";

    protected static final String OP_SET_PLAYER_OPTION = "setPlayerOption";
    protected static final String OP_SET_PLAYER_OPTIONS = "setPlayerOptions";
    protected static final String OP_GET_PLAYER_OPTION = "getPlayerOption";
    protected static final String OP_GET_PLAYER_OPTIONS = "getPlayerOptions";
    protected static final String OP_SET_USING_MEDIA_DATASOURCE = "setUsingMediaDataSource";

    private Settings settings;

    @Override
    protected IPlayer initPlayer(Context context, PlayerBaseView playerParentView) {
        IPlayerDimension playerViewSize = new PlayerDimensionModel.Builder(context).setFullScreen(false).setDefaultPlayerWidth(848).setDefaultPlayerHeight(476).build();
        PlayerConfiguration playerConfiguration = new PlayerConfiguration.Builder(context).setPlayerDimension(playerViewSize).build();
        IjkVideoPlayer player = new IjkVideoPlayer();
        player.init(playerConfiguration, EsProxy.get().getEsPackageName(this));
        playerParentView.setPlayer(player);

        settings = new Settings();
        player.setPlayerSettings(settings);

        return player;
    }

    /**
     * 设置字幕类型
     *
     * @param baseView  播放器组件View
     * @param timedType 字幕类型：0-关闭，1-只打开文本字幕，2-全部打开，3-只打开图形字幕
     */
    @EsComponentAttribute
    public void timedType(final PlayerBaseView baseView, int timedType) {
        settings.setTimedType(timedType);
    }

    @EsComponentAttribute
    public void subChi(final PlayerBaseView baseView, boolean subChinese) {
        settings.setSubChinese(subChinese);
    }

    @EsComponentAttribute
    public void subIndex(final PlayerBaseView baseView, int index) {
        settings.setSubIndex(index);
    }

    @EsComponentAttribute
    public void audioChi(final PlayerBaseView baseView, boolean audioChinese) {
        settings.setAudioChinese(audioChinese);
    }

    @EsComponentAttribute
    public void audioIndex(final PlayerBaseView baseView, int index) {
        settings.setAudioIndex(index);
    }

    @Override
    public void dispatchFunction(PlayerBaseView view, String functionName, EsArray params, EsPromise promise) {
        if (ES_OP_GET_ES_INFO.equals(functionName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
        //init
        else if (OP_INIT.equals(functionName)) {
            if (view != null && view.getPlayer() instanceof IjkVideoPlayer) {
                ((IjkVideoPlayer) view.getPlayer()).init();
            }
            //
            promise.resolve(true);
        }
        //play
        else if (OP_PLAY.equals(functionName)) {
            if (view != null && view.getPlayer() != null) {
                //1.
                String url = params.getString(0);
                if (url == null || url.equals("null")) return;
                //2.
                int aspectRatio = params.getInt(1);
                //3.音量
                String leftVolume = params.getString(2);
                String rightVolume = params.getString(3);
                //4.options
                EsArray playerOptions = params.getArray(4);
                //5.播放器类型
                int playerType = params.getInt(5);
                //6.
                boolean playerMediaCodec = params.getBoolean(6);

                //7.播放器配置
                EsMap playerConfig = params.getMap(7);

                // 额外信息
                EsMap extraInfo = params.getMap(8);

                //如果播放器类型没有设置，则不进行任何播放器操作
                switch (playerType) {
                    case Settings.PV_PLAYER__AndroidMediaPlayer:
                    case Settings.PV_PLAYER__IjkExoMediaPlayer:
                    case Settings.PV_PLAYER__ApolloMediaPlayer:
                        settings.setPlayerType(playerType);
                        break;
                    case Settings.PV_PLAYER__IjkMediaPlayer:
                        settings.setPlayerType(playerType);
                        settings.setUsingHardwareDecoder(playerMediaCodec);
                        break;
                }

                if (PLog.isLoggable(PLog.DEBUG)) {
                    PLog.d(TAG, "#播放参数-------->>>>>\n" + "url:" + url + "\n" + "aspectRatio:" + aspectRatio + "\n" + "leftVolume:" + leftVolume + "\n" + "rightVolume:" + rightVolume + "\n" + "playerType:" + playerType + "\n" + "playerOptions:" + playerOptions + "\n" + "playerMediaCodec:" + playerMediaCodec + "\n" + "playerConfig:" + playerConfig);
                }
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                //
                Map<String, Object> extra = new HashMap<>();
                extra.put(IjkVideoPlayer.EXTRA_KEY_ASPECT_RATIO, aspectRatio);
                extra.put(IjkVideoPlayer.EXTRA_KEY_LEFT_VOLUME, leftVolume);
                extra.put(IjkVideoPlayer.EXTRA_KEY_RIGHT_VOLUME, rightVolume);
                extra.put(IjkVideoPlayer.EXTRA_KEY_PLAYER_OPTIONS, playerOptions);
                extra.put(IjkVideoPlayer.EXTRA_KEY_PLAYER_CONFIGURATIONS, playerConfig);
                extra.put(IjkVideoPlayer.EXTRA_KEY_PLAYER_EXTRA, extraInfo);

                //
                IVideoUrl videoUrl = new VideoUrlModel.Builder().setVideoUrl(url).setExtra(extra).build();
                view.getPlayer().play(videoUrl);
            }
        }
        //IJK设置播放器类型
        else if (OP_SET_PLAYER_TYPE.equals(functionName)) {
            try {
                int type = params.getInt(0);
                settings.setPlayerType(type);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        //IJK使用硬解码
        else if (OP_SET_USING_HARDWARE_DECODER.equals(functionName)) {
            try {
                boolean usingHardwareDecoder = params.getBoolean(0);
                settings.setUsingHardwareDecoder(usingHardwareDecoder);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        // 获取轨道信息
        else if (OP_GET_TRACK_INFO.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                int sysType = params.getInt(0);
                ((IjkVideoPlayer) view.getPlayer()).getTrackInfo(promise, sysType);
            }
        }
        // 设置轨道
        else if (OP_SELECT_TRACK.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                int trackIndex = params.getInt(0);
                ((IjkVideoPlayer) view.getPlayer()).selectTack(trackIndex);
            }
        }
        // 获取当前选择的轨道
        else if (OP_GET_SELECT_TRACK.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                int trackType = params.getInt(0);
                int selectedTrack = ((IjkVideoPlayer) view.getPlayer()).getSelectedTrack(trackType);
                promise.resolve(selectedTrack);
            }
        }
        // 取消设置轨道
        else if (OP_DE_SELECT_TRACK.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                int trackIndex = params.getInt(0);
                ((IjkVideoPlayer) view.getPlayer()).deselectTack(trackIndex);
            }
        }
        // 改变option策略
//        else if (OP_OPTION_CATEGORY.equals(functionName)) {
//            if (view.getPlayer() instanceof IjkVideoPlayer) {
//                int optionType = params.getInt(0);
//                ((IjkVideoPlayer) view.getPlayer()).setOptions(optionType);
//            }
//        }
        // 获取视频下载速度
        else if (OP_GET_TCP_SPEED.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                long tcpSpeed = ((IjkVideoPlayer) view.getPlayer()).getTcpSpeed();
                promise.resolve(FormatTools.formatSpeed(tcpSpeed));
            }
        }
        // 获取码率
        else if (OP_GET_BIT_RATE.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                long bitRate = ((IjkVideoPlayer) view.getPlayer()).getBitRate();
                promise.resolve(FormatTools.formatBitRate(bitRate));
            }
        }
        // 开始监听视频进度
        else if (OP_START_POSITION_LISTENER.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                ((IjkVideoPlayer) view.getPlayer()).startPositionListener(view);
            }
        }
        // 关闭进度监听
        else if (OP_STOP_POSITION_LISTENER.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                ((IjkVideoPlayer) view.getPlayer()).stopPositionListener();
            }
        }
        //
        else if (OP_STOP_FORCE.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                boolean force = params.getBoolean(0);
                ((IjkVideoPlayer) view.getPlayer()).stopForce(force);
            }
        }
        // 设置外挂字幕
        else if (OP_SET_DATA_SOURCE.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                String url = params.getString(0);
                EsMap extraInfo = params.getMap(1);

                ((IjkVideoPlayer) view.getPlayer()).setSubDataSource(url, extraInfo);
            }
        }
        // 关闭外挂字幕
        else if (OP_CLOSE_TIMED_FILE.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                ((IjkVideoPlayer) view.getPlayer()).closeTimedFile();
            }
        }
        // 动态设置option
        else if (OP_SET_PLAYER_OPTION.equals(functionName)) {
            EsMap option = params.getMap(0);
            if (option != null && view.getPlayer() instanceof IjkVideoPlayer) {
                ((IjkVideoPlayer) view.getPlayer()).setOption(CommonUtils.getIjkMediaOption(option));
            }
        }
        // 动态设置options
        else if (OP_SET_PLAYER_OPTIONS.equals(functionName)) {
            if (params != null && params.size() > 0 && view.getPlayer() instanceof IjkVideoPlayer) {
                ((IjkVideoPlayer) view.getPlayer()).setOptions(CommonUtils.getIjkMediaOptions(params));
            }
        }
        // 获取指定option
        else if (OP_GET_PLAYER_OPTION.equals(functionName)) {
            if (params != null && params.size() > 0 && view.getPlayer() instanceof IjkVideoPlayer) {
                String value = ((IjkVideoPlayer) view.getPlayer()).getOption(params.getString(0));
                promise.resolve(value != null ? value : "");
            }
        }
        // 获取指定options
        else if (OP_GET_PLAYER_OPTIONS.equals(functionName)) {
            if (view.getPlayer() instanceof IjkVideoPlayer) {
                EsArray options = ((IjkVideoPlayer) view.getPlayer()).getOptions();
                promise.resolve(options);
            }
        }
        // 设置ijk option策略
        else if (OP_OPTION_CATEGORY.equals(functionName)) {
            if (params != null) {
                int type = params.getInt(0);
                settings.setOptionCategory(type);
            }
        }
        // 设置ijk是否使用自定义datasource
        else if (OP_SET_USING_MEDIA_DATASOURCE.equals(functionName)) {
            if (params != null) {
                settings.setUsingMediaDataSource(params.getBoolean(0));
            }
        }
        //
        else {
            super.dispatchFunction(view, functionName, params, promise);
        }
    }

}
