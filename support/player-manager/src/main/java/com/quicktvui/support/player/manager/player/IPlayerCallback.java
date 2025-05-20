package com.quicktvui.support.player.manager.player;

import com.quicktvui.support.player.manager.decode.Decode;
import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.model.IPlayerDimension;

import java.util.List;

import com.quicktvui.support.player.manager.aspect.AspectRatio;

public interface IPlayerCallback {

    /**
     * 播放器状态改变
     *
     * @param playerStatus
     */
    void onPlayerStatusChanged(PlayerStatus playerStatus);

    /**
     * 播放器错误
     */

    void onPlayerError(PlayerError playerError);


    /**
     * 播放器信息
     *
     * @param playerInfo
     */
    void onPlayerInfo(PlayerInfo playerInfo);

    //-----------------------------------------------

    /**
     * 清晰度改变
     *
     * @param definition
     */
    void onDefinitionChanged(Definition definition);

    /**
     * 所有的清晰度
     */
    void onAllDefinitionChanged(List<Definition> definitionList);

    //-----------------------------------------------

    /**
     * 解码
     *
     * @param decode
     */
    void onDecodeChanged(Decode decode);

    /**
     *
     */
    void onAllDecodeChanged(List<Decode> decodeList);

    //-----------------------------------------------

    /**
     * 屏幕比例改变
     *
     * @param aspectRatio
     */
    void onAspectRatioChanged(AspectRatio aspectRatio);

    /**
     * 所有的屏幕比例
     *
     * @param aspectRatioList
     */
    void onAllAspectRatioChanged(List<AspectRatio> aspectRatioList);
    //-----------------------------------------------

    /**
     * 播放速率
     *
     * @param rate
     */
    void onPlayRateChanged(float rate);

    /**
     * 播放速率
     *
     * @param RateList
     */
    void onAllPlayRateChanged(List<Float> RateList);
    //-----------------------------------------------

    /**
     * 播放器的尺寸改变
     *
     * @param playerViewSize
     */
    void onPlayerDimensionChanged(IPlayerDimension playerViewSize);

    /**
     * 播放进度回调
     *
     * @param currentPosition
     * @param duration
     */
    void onPlayerProgressChanged(long currentPosition, long duration);


    /**
     * 进入全屏
     */
    void onEnterFullScreen();

    /**
     * 退出全屏
     */
    void onExitFullScreen();

    /**
     * 音量变化
     *
     * @param leftVolume
     * @param rightVolume
     */
    void onPlayerVolumeChanged(float leftVolume, float rightVolume);
}
