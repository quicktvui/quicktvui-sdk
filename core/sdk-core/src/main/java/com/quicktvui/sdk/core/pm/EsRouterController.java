package com.quicktvui.sdk.core.pm;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

@ESKitAutoRegister
@HippyController(
        name = EsRouterController.CLASS_NAME
)
public class EsRouterController extends HippyViewController<EsRouterView> {
    public static final String CLASS_NAME = "ESPageRouterView";
    public static final String TAG = "ESPageRouterLog";

    public EsRouterController() {
    }

    @Override
    protected View createViewImpl(Context context) {
        return null;
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        return new EsRouterView(context);
    }

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        //super.addView(parentView, view, index);
        try {
            EsRouterView router = (EsRouterView) parentView;
            EsPageView pv = (EsPageView) view;
            if (L.DEBUG) L.logW("addView " + pv);
            router.open(pv);
        } catch (Exception e) {
            L.logE("add view parent: " + parentView + ", view: " + view + ", index: " + index, e);
        }
    }

    @Override
    protected void deleteChild(ViewGroup parentView, View childView, int childIndex) {
        Log.i(TAG, "PageRouter deleteChild childView:" + childView + ",childIndex:" + childIndex);
        //super.deleteChild(parentView, childView, childIndex);
        if(childView instanceof EsPageView){
            if (L.DEBUG) L.logW("deleteView " + childView);
            EsRouterView router = (EsRouterView) parentView;
            try {
                router.close(((EsPageView) childView).getPageId());
            } catch (Exception e) {
                L.logW("delete view parent: " + parentView + ", childView: " + childView + ", childIndex: " + childIndex, e);
            }
        }
    }

}
