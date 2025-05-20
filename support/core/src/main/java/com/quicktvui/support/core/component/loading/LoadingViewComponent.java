package com.quicktvui.support.core.component.loading;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.graphics.Color;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;

/**
 * Create by weipeng on 2022/03/03 20:55
 */
@ESKitAutoRegister
public class LoadingViewComponent implements IEsComponent<LoadingView> {

    @Override
    public LoadingView createView(Context context, EsMap params) {
        return new LoadingView(context);
    }

    @EsComponentAttribute
    public void color(LoadingView view, String color) {
        final int c = Color.parseColor(color);
        final LoadingDrawable drawable = (LoadingDrawable) view.getDrawable();
        drawable.setColor(c);
        view.invalidate();
    }

    @Override
    public void dispatchFunction(LoadingView loadingView, String eventName, EsArray esArray, EsPromise promise) {
        switch (eventName) {
            //getVersion
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                try {
                    map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
                    map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                promise.resolve(map);
                break;
        }
    }

    @Override
    public void destroy(LoadingView loadingView) {

    }

}
