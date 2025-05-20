package com.quicktvui.sdk.core.jsview.slot;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

/**
 * <br>
 *
 * <br>
 */
@ESKitAutoRegister
@HippyController(name = SlotRootViewController.CLASS_NAME)
public class SlotRootViewController extends HippyViewController<SlotRootView> {

    public static final String CLASS_NAME = "SlotRootView";

    @Override
    protected View createViewImpl(Context context) {
        Log.i("DebugWindowNode","SlotRootViewController createViewImpl");
        return new SlotRootView(context);
    }

    @Override
    protected void addView(ViewGroup viewGroup, View view, int index) {
//        super.addView(viewGroup, view, i);
        try {
            SlotRootView rootView = (SlotRootView) viewGroup;
            SlotView jsView = (SlotView) view;
            rootView.onJsViewAdd(jsView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void deleteChild(ViewGroup viewGroup, View view) {
//        super.deleteChild(viewGroup, view);
        if (view instanceof SlotView) {
            SlotRootView rootView = (SlotRootView) viewGroup;
            rootView.onJsViewRemove((SlotView) view);
        }
    }

}
