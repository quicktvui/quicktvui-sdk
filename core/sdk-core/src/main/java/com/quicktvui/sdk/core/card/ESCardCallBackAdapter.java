package com.quicktvui.sdk.core.card;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;

//处理卡片回调
public abstract class ESCardCallBackAdapter implements ESCardCallBack {
    protected IEsAppLoadHandler iEsAppLoadHandler;
    protected String cardId;
    protected ESCardBean esCardBean;

    @Override
    public void onStartLoad(String cardId, IEsAppLoadHandler iEsAppLoadHandler) {
        this.cardId = cardId;
        this.iEsAppLoadHandler = iEsAppLoadHandler;
    }

    @Override
    public void onLoadSuccess(ESCardBean esCardBean) {
        this.esCardBean = esCardBean;
    }

    @Override
    public void onLoadFailed(ESCardBean esCardBean) {
    }

    /**
     * 向vue发送事件
     */
    @Override
    public void sendEvent2Vue(String eventName, EsMap esMap) {
        if (iEsAppLoadHandler != null) {
            iEsAppLoadHandler.sendEvent(eventName, esMap);
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroy() {
        //Activity销毁时释放引擎
        if (iEsAppLoadHandler != null) {
            iEsAppLoadHandler.onDestroy();
            iEsAppLoadHandler = null;
        }
    }
}
