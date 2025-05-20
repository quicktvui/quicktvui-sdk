package com.quicktvui.support.player.manager.player;


/**
 * 顺序不可更改
 */
public enum PlayerStatusEnum {
    PLAYER_STATE_IDLE,//
    PLAYER_STATE_PREPARING,//正在准备
    PLAYER_STATE_PREPARED,//准备完毕
    PLAYER_STATE_BUFFER_START,//正在缓冲
    PLAYER_STATE_BUFFER_END,//正在缓冲
    PLAYER_STATE_PLAYING,//正在播放
    PLAYER_STATE_SEEK_START,//快进开始
    PLAYER_STATE_SEEK_COMPLETED,//快进完成
    PLAYER_STATE_PAUSED,//暂停
    PLAYER_STATE_RESUMED,//继续播放
    PLAYER_STATE_BEFORE_STOP,//停止之前
    PLAYER_STATE_STOP,//停止
    PLAYER_STATE_PLAYBACK_COMPLETED,//播放完毕
    PLAYER_STATE_ERROR,//错误
    PLAYER_STATE_VIDEO_SIZE_CHANGED,//视频尺寸变化
    PLAYER_STATE_PLAYER_VIEW_CHANGED,//播放布局变化

    //广告
    PLAYER_STATE_AD_START,//广告开始
    PLAYER_STATE_AD_END,//广告结束
    PLAYER_STATE_AD_SKIP,//广告跳过
    PLAYER_STATE_AD_PAUSED,//广告暂停

    //鉴权通过
    PLAYER_STATE_AUTHORIZED,//tvbc播放器鉴权

    //设置播放倍速成功
    PLAYER_STATE_SET_PLAY_RATE_SUCCESS, //设置播放倍速报错
    PLAYER_STATE_SET_PLAY_RATE_ERROR,

    //广告
    PLAYER_STATE_AD_RESUMED,//广告RESUME
    PLAYER_STATE_AD_LOADED,//
    PLAYER_STATE_AD_TIME,//广告倒计时时间

    //
    PLAYER_STATE_PLAYER_INITIALIZED,//播放器初始化完毕
    //
    PLAYER_STATE_TIMED_TEXT_CHANGED,//TimedText
    //
    PLAYER_STATE_PLAYER_CLICKABLE,//播放器view是否可以点击

    //----------------播放器初始化------------------
    PLAYER_STATE_INITIALIZE_SUCCESS,//已经初始化了
    PLAYER_STATE_INITIALIZE_ERROR,//初始化错误

}
