package com.quicktvui.sdk.core.card;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;


/** 卡片 **/
@ESKitAutoRegister
public class ESCardViewComponent implements IEsComponent<WebFrameView> {
    private ESESCardCallBackIml esEsCardCallBackIml;

    @Override
    public WebFrameView createView(Context context, EsMap params) {
        this.esEsCardCallBackIml = new ESESCardCallBackIml();
        return new WebFrameView(context, params);
    }

    @EsComponentAttribute
    public void usePlaceHolder(WebFrameView view, boolean usePlaceHolder) {
        view.setUsePlaceHolder(usePlaceHolder);
    }

    @EsComponentAttribute
    public void contentPadding(WebFrameView view, EsArray esArray) {
        if (esArray != null && esArray.size() > 0) {
            view.setContentPadding(esArray.getInt(0), esArray.getInt(1), esArray.getInt(2), esArray.getInt(3));
        }
    }

    @EsComponentAttribute
    public void useDefaultUI(WebFrameView view, boolean useDefaultUI) {
        view.setUseDefaultUI(useDefaultUI);
    }

    @EsComponentAttribute
    public void cardId(WebFrameView view, String cardId) {
        view.load(cardId, true, esEsCardCallBackIml);
    }

    @EsComponentAttribute
    public void cardIdNoCache(WebFrameView view, String cardId) {
        view.load(cardId, false, esEsCardCallBackIml);
    }

    @EsComponentAttribute
    public void autoRecycle(WebFrameView view, boolean autoRecycle) {
        view.setAutoRecycle(autoRecycle);
    }

    @EsComponentAttribute
    public void useESEvent(WebFrameView view, boolean useESEvent) {
        view.setUseESEvent(useESEvent);
    }

    @EsComponentAttribute
    public void showPlaceHolder(WebFrameView view, boolean showPlaceHolder) {
        if (view != null) {
            view.showPlaceHolder(showPlaceHolder);
        }
    }

    @EsComponentAttribute
    public void placeHolderRect(WebFrameView view, EsArray esArray) {
        if (esArray != null && esArray.size() > 0) {
            int left = esArray.getInt(0);
            int top = esArray.getInt(1);
            int right = esArray.getInt(2);
            int bottom = esArray.getInt(3);
            view.setPlaceHolder(left, top, right, bottom);
        }
    }

    @EsComponentAttribute
    public void placeHolderRadius(WebFrameView view, int radius) {
        if (view != null) {
            view.setPlaceHolderRadius(radius);
        }
    }

    @EsComponentAttribute
    public void focusScale(WebFrameView view, float scale) {
        if (view != null) {
            view.setFocusScale(scale);
        }
    }

    @Override
    public void dispatchFunction(WebFrameView view, String eventName, EsArray params, EsPromise promise) {
        switch (eventName) {
            case "load":
                String cardId = params.getString(0);
                view.load(cardId, true, esEsCardCallBackIml);
                break;
            case "loadWithoutCache":
                String cardId2 = params.getString(0);
                view.load(cardId2, false, esEsCardCallBackIml);
                break;
            case "getLoadingStatus":
                EsMap esMap = new EsMap();
                esMap.pushInt("loadingStatus", view.getLoadingStatus());
                promise.resolve(esMap);
                break;
            case "sendEvent2Vue":
                String name = params.getString(0);
                EsMap eventMap = params.getMap(1);
                view.sendEvent2Vue(name, eventMap);
                break;
            case "autoRecycle":
                boolean autoRecycle = params.getBoolean(0);
                view.setAutoRecycle(autoRecycle);
                break;
            case "reload":
                view.reload();
                break;
            case "reset":
                view.reset();
                break;
            case "recycle":
                view.recycle();
                break;
            case "removeCache":
                view.removeCache();
                break;
            case "resizeCacheSize":
                view.resizeCacheSize(params.getInt(0));
                break;
            case "clearAllCache":
                view.clearAllCache();
                break;
        }
    }

    private class ESESCardCallBackIml extends ESCardCallBackImpl {

        public ESESCardCallBackIml() {

        }

        @Override
        public void onStartLoad(String cardId, IEsAppLoadHandler iEsAppLoadHandler) {
            super.onStartLoad(cardId, iEsAppLoadHandler);
        }

        @Override
        public void onLoadSuccess(ESCardBean esCardBean) {
            super.onLoadSuccess(esCardBean);
            EsMap esMap = new EsMap();
            esMap.pushInt("result", 1);
            esMap.pushString("cardId", esCardBean.getCardId());
            sendNativeCardEvent(esMap);
        }

        @Override
        public void onLoadFailed(ESCardBean esCardBean) {
            super.onLoadFailed(esCardBean);
            EsMap esMap = new EsMap();
            esMap.pushInt("result", 0);
            esMap.pushString("cardId", esCardBean.getCardId());
            sendNativeCardEvent(esMap);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }

    private void sendNativeCardEvent(EsMap esMap) {
        EsProxy.get().sendNativeEventTraceable(this, "onCardEvent", esMap);
    }

    @Override
    public void destroy(WebFrameView view) {
        view.destroy();
    }

}
