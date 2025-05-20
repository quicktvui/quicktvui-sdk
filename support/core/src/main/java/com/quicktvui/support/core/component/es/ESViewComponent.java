package com.quicktvui.support.core.component.es;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;

/**
 *
 */
@ESKitAutoRegister
public class ESViewComponent implements IEsComponent<ESView> {

    private static final String OP_SEND_UI_EVENT = "sendUIEvent";

    @Override
    public ESView createView(Context context, EsMap initParams) {
        return new ESView(context);
    }

    @Override
    public void dispatchFunction(ESView view, String eventName, EsArray params, EsPromise promise) {
        if (L.DEBUG) {
            L.logD("#---dispatchFunction------>>>" +
                    "eventName:" + eventName + "----->>>" +
                    "params:" + params.toString()
            );
        }
        switch (eventName) {
            case OP_SEND_UI_EVENT:
                String name = params.getString(0);
                EsMap value = params.getMap(1);
                view.sendUIEvent(name, value);
                break;
            default:
                break;
        }
    }

    @Override
    public void destroy(ESView tvProgressBarView) {

    }
}
