package com.quicktvui.support.player.manager.player;

import android.view.View;

import com.quicktvui.support.player.manager.decode.Decode;
import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.manager.PlayerConfiguration;
import com.quicktvui.support.player.manager.model.IPlayerDimension;
import com.quicktvui.support.player.manager.model.IVideoUrl;
import com.quicktvui.support.player.manager.volume.IPlayerVolume;

import java.util.List;

import com.quicktvui.support.player.manager.aspect.AspectRatio;


public interface IPlayer {

    void init(PlayerConfiguration configuration);

    /**
     * 注册回调
     *
     * @param callback
     */
    void registerPlayerCallback(IPlayerCallback callback);

    /**
     * 取消注册
     *
     * @param callback
     */
    void unregisterPlayerCallback(IPlayerCallback callback);

    /**
     * 播放
     *
     * @param url
     */
    void play(IVideoUrl url);

    /**
     * 开始
     */
    void start(long progress);

    /**
     * 开始
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 重新开始
     */
    void resume();

    /**
     * 停止
     */
    void stop();

    /**
     * 重置
     */
    void reset();

    /**
     *
     */
    void release();

    /**
     * 快进快退
     *
     * @param msec
     */
    void seekTo(long msec);

    //-----------------------------------------

    /**
     * 设置播放画面比例
     *
     * @param aspectRatio
     */
    void setAspectRatio(AspectRatio aspectRatio);

    /**
     * 获取播放画面比例
     */
    AspectRatio getCurrentAspectRatio();

    /**
     * 获取所有的画面比例
     *
     * @return
     */
    List<AspectRatio> getAllAspectRatio();

    //-----------------------------------------

    /**
     * 设置播放速率
     *
     * @param rate
     */
    void setPlayRate(float rate);

    /**
     * 获取当前的播放比率
     */
    float getCurrentPlayRate();

    /**
     * 获取所有的播放比率
     *
     * @return
     */
    List<Float> getAllPlayRate();

    //-----------------------------------------

    /**
     * 获取视频的所有的清晰度
     *
     * @return
     */
    List<Definition> getAllDefinition();

    /**
     * 获取当前的清晰度
     *
     * @return
     */
    Definition getCurrentDefinition();

    /**
     * 设置清晰度
     *
     * @param definition
     */
    void setDefinition(Definition definition);

    //-----------------------------------------

    //-----------------------------------------

    List<Decode> getDecodeList();

    Decode getCurrentDecode();

    void setDecode(Decode decode);
    //-----------------------------------------

    /**
     * 获取视频的播放总时长
     *
     * @return
     */
    long getDuration();

    /**
     * 获取视频当前的播放时长
     *
     * @return
     */
    long getCurrentPosition();

    /**
     * 获取视频缓冲的百分比
     *
     * @return
     */
    long getBufferPercentage();

    /**
     * 获取播放器的宽度
     *
     * @return
     */
    int getPlayerWidth();

    /**
     * 获取播放器的高度
     *
     * @return
     */
    int getPlayerHeight();

    /**
     * 获取播放器的view
     *
     * @return
     */
    View getPlayerView();

    /**
     * 使用的播放器类型
     *
     * @return
     */
    PlayerType getPlayerType();

    /**
     * 获取正在播放的地址
     *
     * @return
     */
    IVideoUrl getPlayUrl();

    /**
     * 大小屏切换
     *
     * @param fullScreen
     */
    void changeToFullScreen(boolean fullScreen);

    /**
     * 是否正在播放
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 是否在暂停状态
     *
     * @return
     */
    boolean isPaused();

    /**
     * 是否是全屏
     *
     * @return
     */
    boolean isFullScreen();

    /**
     * 设置播放器的大小
     *
     * @param playerDimension
     */
    void setPlayerDimension(IPlayerDimension playerDimension);

    /**
     * 设置播放器尺寸
     *
     * @param width
     * @param height
     */
    void setPlayerSize(int width, int height);

    /**
     * 获取播放器尺寸配置
     *
     * @return
     */
    IPlayerDimension getPlayerDimension();

    /**
     * 设置左右声道的音量
     *
     * @param playerVolume
     */
    void setVolume(IPlayerVolume playerVolume);

    /**
     * 获取系统音量
     *
     * @return
     */
    IPlayerVolume getVolume();

    /**
     * 设置是否可用，默认可用
     *
     * @param enabled
     */
    void setEnabled(boolean enabled);

    /**
     * 是否可用
     *
     * @return
     */
    boolean isEnabled();

    /**
     * 设置播放器是否停止
     *
     * @param stopped
     */
    void setStopped(boolean stopped);

    /**
     * 判断播放器是否停止
     *
     * @return
     */
    boolean isStopped();

}