package com.quicktvui.support.swiper;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;


@ESKitAutoRegister
public class ESSwiperSlideViewComponent implements IEsComponent<SwiperView> {

    @Override
    public SwiperView createView(Context context, EsMap params) {
        return new SwiperView(context);
    }

    @Override
    public void dispatchFunction(SwiperView view, String eventName, EsArray params, EsPromise promise) {
        if ("doAnimation".equals(eventName)) {
            view.doTransform(
                    Float.parseFloat(params.getString(0)),
                    Float.parseFloat(params.getString(1)),
                    Float.parseFloat(params.getString(2)),
                    Float.parseFloat(params.getString(3)),
                    Float.parseFloat(params.getString(4)),
                    Float.parseFloat(params.getString(5)),
                    params.getLong(6)
            );
        }
        //
        else if (ES_OP_GET_ES_INFO.equals(eventName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
    }

    @Override
    public void destroy(SwiperView view) {

    }
}
