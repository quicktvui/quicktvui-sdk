package com.quicktvui.sdk.core.card;

import android.view.View;

import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;

public class ESCardBean {
    private String cardId = ""; //卡片id
    private View cacheView; //
    private IEsAppLoadHandler iEsAppLoadHandler;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public View getCacheView() {
        return cacheView;
    }

    public void setCacheView(View cacheView) {
        this.cacheView = cacheView;
    }

    public IEsAppLoadHandler getiEsAppLoadHandler() {
        return iEsAppLoadHandler;
    }

    public void setiEsAppLoadHandler(IEsAppLoadHandler iEsAppLoadHandler) {
        this.iEsAppLoadHandler = iEsAppLoadHandler;
    }
}
