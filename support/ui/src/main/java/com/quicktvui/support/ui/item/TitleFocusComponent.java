package com.quicktvui.support.ui.item;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;


@ESKitAutoRegister
public class TitleFocusComponent implements IEsComponent<TitleViewFocus> {

    @EsComponentAttribute
    public void itemTitleJson(TitleViewFocus view, EsMap json) {
        if (view != null) {
            view.setHippyMap(json);
        }
    }

    @Override
    public TitleViewFocus createView(Context context, EsMap params) {
        return new TitleViewFocus(context);
    }

    @Override
    public void dispatchFunction(TitleViewFocus view, String eventName, EsArray params, EsPromise promise) {
        if (ES_OP_GET_ES_INFO.equals(eventName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
    }

    @Override
    public void destroy(TitleViewFocus view) {

    }
}
