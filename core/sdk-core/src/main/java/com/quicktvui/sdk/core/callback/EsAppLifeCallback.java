package com.quicktvui.sdk.core.callback;

import com.quicktvui.sdk.core.EsData;

/**
 * 生命周期回调
 * <p>
 * Create by weipeng on 2022/08/10 10:40
 */
interface EsAppLifeCallback {
    void onEsAppCreate(EsData data);
    void onEsAppStart(EsData data);
    void onEsAppResume(EsData data);
    void onEsAppPause(EsData data);
    void onEsAppStop(EsData data);
    void onEsAppRenderSuccess(EsData data);
    void onEsAppRenderFailed(EsData data, int errCode, String message);
    void onEsAppDestroy(EsData data);
}
