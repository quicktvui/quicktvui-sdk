package com.quicktvui.sdk.core.jsview;

import android.os.Bundle;

import com.extscreen.runtime.api.ability.slotview.RecyclerViewEventHandler;

import org.json.JSONObject;

import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * <br>
 *
 * <br>
 */
public class RecyclerViewHandlerImpl implements RecyclerViewEventHandler {

    private final JsSlotView mSlotView;

    public RecyclerViewHandlerImpl(JsSlotView slotView) {
        this.mSlotView = slotView;
    }

    @Override
    public void onBindViewHolder(int position, Object data) {
        sendEvent(Constants.GLOBAL_EVENT.EVT_ON_SLOT_BIND, position, data);
    }

    @Override
    public void onViewAttachedToWindow(int position, Object data) {
        sendEvent(Constants.GLOBAL_EVENT.EVT_ON_SLOT_ATTACH, position, data);
    }

    @Override
    public void onViewDetachedFromWindow(int position, Object data) {
        sendEvent(Constants.GLOBAL_EVENT.EVT_ON_SLOT_DETACH, position, data);
    }

    @Override
    public void onViewRecycled(int position, Object data) {
        sendEvent(Constants.GLOBAL_EVENT.EVT_ON_SLOT_RECYCLE, position, data);
    }

    private void sendEvent(String eventName, int position, Object data) {
        if (data instanceof Bundle) {
            EsMap map = new EsMap();
            map.pushBundle((Bundle) data);
            data = map;
        } else if (data instanceof JSONObject) {
            EsMap map = new EsMap();
            map.pushJSONObject((JSONObject) data);
            data = map;
        }
        EsMap map = new EsMap();
        map.pushInt("position", position);
        map.pushObject("data", data);
        mSlotView.sendEvent(eventName, map);
    }
}
