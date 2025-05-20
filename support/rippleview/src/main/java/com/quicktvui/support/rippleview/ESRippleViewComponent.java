package com.quicktvui.support.rippleview;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;

@ESKitAutoRegister
public class ESRippleViewComponent implements IEsComponent<RippleView> {
    @Override
    public RippleView createView(Context context, EsMap params) {
        return new RippleView(context);
    }

    @EsComponentAttribute
    public void initParams(RippleView rippleView, EsMap initPrams) {
        final String color = initPrams.getString("color");
        final String path = initPrams.getString("path");
        rippleView.init(color, path);
    }

    @Override
    public void dispatchFunction(RippleView view, String functionName, EsArray params, EsPromise promise) {
        if (ES_OP_GET_ES_INFO.equals(functionName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
    }

    @Override
    public void destroy(RippleView view) {

    }
}
