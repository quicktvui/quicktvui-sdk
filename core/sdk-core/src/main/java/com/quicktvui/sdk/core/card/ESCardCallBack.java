package com.quicktvui.sdk.core.card;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;

public interface ESCardCallBack extends LifecycleObserver {
    //加载开始回调
    void onStartLoad(String cardId, IEsAppLoadHandler iEsAppLoadHandler);

    //代码包view生成成功回调
    void onLoadSuccess(ESCardBean esCardBean);

    //代码包view生成失败回调
    void onLoadFailed(ESCardBean esCardBean);

    //向vue发送消息
    void sendEvent2Vue(String eventName, EsMap esMap);

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();
}
