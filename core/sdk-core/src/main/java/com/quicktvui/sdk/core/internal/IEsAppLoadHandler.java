package com.quicktvui.sdk.core.internal;

import android.view.KeyEvent;

import com.quicktvui.sdk.base.EsEmptyCallback;

/**
 *
 */
public interface IEsAppLoadHandler {

    /** 调用生命周期 **/
    void onStart();

    /** 调用生命周期 **/
    void onResume();

    /** 调用生命周期 **/
    void onPause();

    /** 调用生命周期 **/
    void onStop();

    /** 调用生命周期 **/
    void onDestroy();

    /** 调用方法 **/
    void dispatchKeyEvent(KeyEvent event);

    /** 调用方法 **/
    boolean onBackPressed(EsEmptyCallback callback);

    /** 向VUE发送自定义事件(如有需要) **/
    void sendEvent(String eventName, Object params);

}
