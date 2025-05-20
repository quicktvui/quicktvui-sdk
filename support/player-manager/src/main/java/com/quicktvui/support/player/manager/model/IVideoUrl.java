package com.quicktvui.support.player.manager.model;

import com.quicktvui.support.player.manager.definition.Definition;

/**
 * 播放地址
 */
public interface IVideoUrl {

    /**
     * 地址的id
     *
     * @return
     */
    String getId();

    /**
     * 播放地址
     *
     * @return
     */
    String getUrl();

    /**
     * 设置地址
     *
     * @param url
     */
    void setUrl(String url);

    /**
     * 当前地址的清晰度
     *
     * @return
     */
    Definition getDefinition();

    /**
     * 视频的总长度
     *
     * @return
     */
    long getDuration();

    /**
     * 扩展字段
     *
     * @return
     */
    Object getExtra();
}
