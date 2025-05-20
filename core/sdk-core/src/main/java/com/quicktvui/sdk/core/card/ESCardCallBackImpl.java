package com.quicktvui.sdk.core.card;

import android.util.Log;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;

public class ESCardCallBackImpl extends ESCardCallBackAdapter {
    private final static String TAG = "ESCardView";

    public ESCardCallBackImpl() {
    }

    @Override
    public void onStartLoad(String cardId, IEsAppLoadHandler iEsAppLoadHandler) {
        super.onStartLoad(cardId, iEsAppLoadHandler);
    }

    @Override
    public void onLoadSuccess(ESCardBean esCardBean) {
        super.onLoadSuccess(esCardBean);
        Log.v(TAG, "cardId:" + esCardBean.getCardId() + " 加载成功!");
    }

    @Override
    public void onLoadFailed(ESCardBean esCardBean) {
        super.onLoadFailed(esCardBean);
        Log.v(TAG, "cardId:" + esCardBean.getCardId() + " 加载失败!");
    }

    @Override
    public void sendEvent2Vue(String eventName, EsMap esMap) {
        super.sendEvent2Vue(eventName, esMap);
        Log.v(TAG, "sendEvent2Vue: eventName:" + eventName + " cardId:" + (esCardBean != null ? esCardBean.getCardId() : ""));
    }

    @Override
    public void onCreate() {
        //TODO:把生命周期发给vue层
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

}
