package com.quicktvui.support.ui.item;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;


@ESKitAutoRegister
public class RippleViewComponent implements IEsComponent<RippleView> {
    public static final String CLASS_NAME = "RippleView";

    protected static final String ITEM_COLOR = "color";
    protected static final String ITEM_SHOW_RIPPLE_VIEW = "isShowRipple";
    protected static final String ITEM_HIDE_RIPPLE_VIEW = "hideRipple";
    protected static final String ITEM_INIT_RIPPLE_STATE = "rippleVisible";


    @EsComponentAttribute
    public void rippleColor(RippleView view, String color) {
        if (view != null) {
            view.init(color, "isSetColor");
        }
    }
    @EsComponentAttribute
    public void color(RippleView view, String color) {
        if (view != null) {
            view.init(color, "isSetColor");
        }
    }

    @EsComponentAttribute
    public void isShowRipple(RippleView view, Boolean isShow) {
        if (view != null) {
            view.setShowRipple(isShow);
        }
    }

    @EsComponentAttribute
    public void hideRipple(RippleView view, Boolean isHide) {
        if (view != null) {
            view.setHideRipple(isHide);
        }
    }

    @EsComponentAttribute
    public void rippleVisible(RippleView view, String state) {
        if (view != null) {
            switch (state) {
                case "VISIBLE":
                case "visible":
                    view.setVisibility(View.VISIBLE);
                    break;
                case "INVISIBLE":
                case "invisible":
                    view.setVisibility(View.INVISIBLE);
                    break;
                case "GONE":
                case "gone":
                    view.setVisibility(View.GONE);
                    break;
            }

        }
    }

    @Override
    public RippleView createView(Context context, EsMap params) {
        return new RippleView(context);
    }

    @Override
    public void dispatchFunction(RippleView view, String eventName, EsArray params, EsPromise promise) {
        if (ES_OP_GET_ES_INFO.equals(eventName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
    }

    @Override
    public void destroy(RippleView view) {

    }
}
