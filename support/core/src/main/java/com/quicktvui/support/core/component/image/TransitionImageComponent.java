package com.quicktvui.support.core.component.image;


import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.graphics.Color;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;

/**
 * Create by weipeng on 2022/04/01 15:55
 */
@ESKitAutoRegister
public class TransitionImageComponent implements IEsComponent<TransitionImageView> {

    public static final String OP_SET_NEXT_COLOR = "setNextColor";
    public static final String OP_SET_NEXT_IMG = "setNextImage";
    public static final String OP_SET_TRANS_TIME = "setTransitionTime";

    @Override
    public TransitionImageView createView(Context context, EsMap params) {
        return new TransitionImageView(this, context);
    }

    @EsComponentAttribute
    public void transitionTime(TransitionImageView view, int duration) {
        view.setTransitionDuration(duration);
    }

    @EsComponentAttribute
    public void roundedCorner(TransitionImageView view, int roundedCorner) {
        view.setRoundedCorner(roundedCorner);
    }

    @Override
    public void dispatchFunction(TransitionImageView view, String eventName, EsArray params, EsPromise promise) {

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

            case OP_SET_NEXT_IMG:
                if (params.size() > 0) {
                    String url = params.getString(0);
                    view.showNextImage(url);
                }
                break;
            case OP_SET_NEXT_COLOR:
                if (params.size() > 0) {
                    Object color = params.get(0);
                    if (color instanceof String) {
                        try {
                            view.showNextColor(Color.parseColor((String) color));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (color instanceof Number) {
                        view.showNextColor((Integer) color);
                    }
                }
                break;
            case OP_SET_TRANS_TIME:
                if (params.size() > 0) {
                    view.setTransitionDuration(params.getInt(0));
                }
                break;
            default:
                PromiseHolder.create(promise).message("不支持的方法 " + eventName).sendFailed();
                break;
        }
    }

    @Override
    public void destroy(TransitionImageView view) {

    }
}
