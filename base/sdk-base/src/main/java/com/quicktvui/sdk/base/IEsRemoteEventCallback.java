package com.quicktvui.sdk.base;

/**
 * Vue向Remote发送消息回调（DLNA、微信、TCL转微信）
 * Create by weipeng on 2022/04/19 20:32
 * Describe Vue向Remote发送消息（DLNA、微信、TCL转微信）
 */
public interface IEsRemoteEventCallback {
    /** 接收到Vue发送来的事件 **/
    void onReceiveEvent(String eventName, String eventData);
}
