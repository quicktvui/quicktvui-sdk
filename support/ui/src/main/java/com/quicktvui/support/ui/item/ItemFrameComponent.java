package com.quicktvui.support.ui.item;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.tencent.mtt.hippy.views.view.HippyViewGroupController;


@ESKitAutoRegister
public class ItemFrameComponent extends HippyViewGroupController implements IEsComponent<ItemFrame> {

    public static final String CLASS_NAME = "ItemFrame";

    @Override
    public ItemFrame createView(Context context, EsMap params) {
        return new ItemFrame(context);
    }

    @EsComponentAttribute
    public void hideShadow(ItemFrame view, boolean enable) {
        if (view != null) {
            view.setHideShadow(enable);
        }
    }
    @EsComponentAttribute
    public void itemShowShimmer(ItemFrame view, boolean enable) {
        if (view != null) {
            view.setEnableShimmer(enable);
        }
    }
    @EsComponentAttribute
    public void shimmerSize(ItemFrame view, EsArray array) {
        if (view != null) {
            view.setShimmerSize(array);
        }
    }


    @Override
    public void dispatchFunction(ItemFrame view, String eventName, EsArray params, EsPromise promise) {
        if (ES_OP_GET_ES_INFO.equals(eventName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
    }

    @Override
    public void destroy(ItemFrame view) {

    }
}
