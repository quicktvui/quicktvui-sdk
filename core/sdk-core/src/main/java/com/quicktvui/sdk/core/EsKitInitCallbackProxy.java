package com.quicktvui.sdk.core;

import android.support.annotation.CallSuper;

/**
 * SDK初始化成功回调
 * Create by weipeng on 2022/06/16 10:02
 * Describe
 */
public class EsKitInitCallbackProxy implements EsKitInitCallback {

    private final EsKitInitCallback origin;

    public EsKitInitCallbackProxy(EsKitInitCallback origin) {
        this.origin = origin;
    }

    @CallSuper
    @Override
    public void onEsKitInitStart() {
        if (origin != null) {
            origin.onEsKitInitStart();
        }
    }

    @CallSuper
    @Override
    public void onEsKitInitSuccess() {
        if (origin != null) {
            origin.onEsKitInitSuccess();
        }
    }

    @CallSuper
    @Override
    public void onEsKitInitError(int code) {
        if (origin != null) {
            origin.onEsKitInitError(code);
        }
    }
}
