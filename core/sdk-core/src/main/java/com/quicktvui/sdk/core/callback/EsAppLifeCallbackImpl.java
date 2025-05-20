package com.quicktvui.sdk.core.callback;

import android.support.annotation.CallSuper;

import com.quicktvui.sdk.core.EsData;
import com.sunrain.toolkit.utils.log.L;

/**
 * <br>
 *
 * <br>
 */
public class EsAppLifeCallbackImpl implements EsAppLifeCallback {

    @CallSuper
    @Override
    public void onEsAppCreate(EsData data) {
        if (L.DEBUG) L.logD("onEsAppCreate data: " + data.getEsPackage() + " " + this.getClass().getName());
    }

    @CallSuper
    @Override
    public void onEsAppStart(EsData data) {
        if (L.DEBUG) L.logD("onEsAppStart data: " + data.getEsPackage());
    }

    @CallSuper
    @Override
    public void onEsAppResume(EsData data) {
        if (L.DEBUG) L.logD("onEsAppResume data: " + data.getEsPackage());
    }

    @Override
    public void onEsAppPause(EsData data) {
        if (L.DEBUG) L.logD("onEsAppPause data: " + data.getEsPackage());
    }

    @Override
    public void onEsAppRenderSuccess(EsData data) {
        if (L.DEBUG) L.logD("onEsAppRenderSuccess data: " + data.getEsPackage());
    }

    @Override
    public void onEsAppRenderFailed(EsData data, int errCode, String message) {
        if (L.DEBUG)
            L.logD("onEsAppRenderFailed data: " + data.getEsPackage() + ", errCode: " + errCode + ", message: " + message);
    }

    @CallSuper
    @Override
    public void onEsAppStop(EsData data) {
        if (L.DEBUG) L.logD("onEsAppStop data: " + data.getEsPackage());
    }

    @CallSuper
    @Override
    public void onEsAppDestroy(EsData data) {
        if (L.DEBUG) L.logD("onEsAppDestroy data: " + data.getEsPackage());
    }
}
