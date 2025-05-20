package com.quicktvui.sdk.base;

import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by weipeng on 2022/04/09 17:30
 */
public interface EsEventPacket {

    /**
     * 获取事件名称
     */
    String getEventName();

    EsMap getEventData();

    void postValue(EsMap data);

}
