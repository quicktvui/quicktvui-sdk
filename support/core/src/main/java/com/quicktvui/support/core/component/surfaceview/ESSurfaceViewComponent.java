package com.quicktvui.support.core.component.surfaceview;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;

/**
 * 空SurfaceView
 */
@ESKitAutoRegister
public class ESSurfaceViewComponent implements IEsComponent<ESSurfaceView> {

    @Override
    public ESSurfaceView createView(Context context, EsMap initParams) {
        return new ESSurfaceView(context);
    }

    @Override
    public void dispatchFunction(ESSurfaceView view, String eventName, EsArray params, EsPromise promise) {

    }

    @Override
    public void destroy(ESSurfaceView tvProgressBarView) {

    }
}
