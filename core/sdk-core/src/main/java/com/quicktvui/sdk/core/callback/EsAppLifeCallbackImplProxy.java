package com.quicktvui.sdk.core.callback;

import com.quicktvui.sdk.core.EsData;

/**
 * <br>
 *
 * <br>
 */
public class EsAppLifeCallbackImplProxy implements EsAppLifeCallback {

    private final EsAppLifeCallbackImpl mOrigin;

    public EsAppLifeCallbackImplProxy(EsAppLifeCallbackImpl origin) {
        mOrigin = origin;
    }

    @Override
    public void onEsAppCreate(EsData data) {
        if (mOrigin != null) {
            mOrigin.onEsAppCreate(data);
        }
    }

    @Override
    public void onEsAppStart(EsData data) {
        if (mOrigin != null) {
            mOrigin.onEsAppStart(data);
        }
    }

    @Override
    public void onEsAppResume(EsData data) {
        if (mOrigin != null) {
            mOrigin.onEsAppResume(data);
        }
    }

    @Override
    public void onEsAppPause(EsData data) {
        if (mOrigin != null) {
            mOrigin.onEsAppPause(data);
        }
    }

    @Override
    public void onEsAppStop(EsData data) {
        if (mOrigin != null) {
            mOrigin.onEsAppStop(data);
        }
    }

    @Override
    public void onEsAppRenderSuccess(EsData data) {
        if (mOrigin != null) {
            mOrigin.onEsAppRenderSuccess(data);
        }
    }

    @Override
    public void onEsAppRenderFailed(EsData data, int errCode, String message) {
        if (mOrigin != null) {
            mOrigin.onEsAppRenderFailed(data, errCode, message);
        }
    }

    @Override
    public void onEsAppDestroy(EsData data) {
        if (mOrigin != null) {
            mOrigin.onEsAppDestroy(data);
        }
    }

}
