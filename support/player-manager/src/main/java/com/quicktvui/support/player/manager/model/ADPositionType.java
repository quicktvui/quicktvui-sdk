package com.quicktvui.support.player.manager.model;

/**
 * 广告位的类型
 */
public enum ADPositionType {
    //开屏
    AD_POSITION_TYPE_LAUNCH,
    //插屏
    AD_POSITION_TYPE_CHA_PING,
    //退出广告
    AD_POSITION_TYPE_EXIT,
    //精选Banner
    AD_POSITION_TYPE_BANNER,
    //翻转广告
    AD_POSITION_TYPE_FLIP,

    //信息流
    AD_POSITION_TYPE_FLOW,

    //前插
    AD_POSITION_TYPE_START,
    AD_POSITION_TYPE_MIDDLE,
    AD_POSITION_TYPE_END,
    AD_POSITION_TYPE_PAUSED,

    //随心看创意广告
    AD_POSITION_TYPE_PLEASANT,

    //角标广告,打点广告
    AD_POSITION_TYPE_POINT,
}
