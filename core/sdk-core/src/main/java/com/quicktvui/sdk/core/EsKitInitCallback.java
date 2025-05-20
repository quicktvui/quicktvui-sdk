package com.quicktvui.sdk.core;

/**
 * SDK初始化成功回调
 * Create by weipeng on 2022/06/16 10:02
 * Describe
 */
public interface EsKitInitCallback {
    /** 开始初始化 **/
    default void onEsKitInitStart(){}
    /** 初始化成功 **/
    void onEsKitInitSuccess();
    /** 初始化失败 **/
    default void onEsKitInitError(int code){}
}
