package com.quicktvui.sdk.core.jsview.slot;

import android.content.Context;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;
import com.quicktvui.hippyext.pm.WindowNode;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;
import com.tencent.mtt.hippy.views.view.HippyViewGroupController;

/**
 * <br>
 *
 * <br>
 */

@ESKitAutoRegister
@HippyController(name = SlotViewController.CLASS_NAME)
public class SlotViewController extends HippyViewGroupController {

    public static final String CLASS_NAME = "SlotView";

    @Override
    protected View createViewImpl(Context context, HippyMap params) {
        return new SlotView(context, params);
    }

    @HippyControllerProps(
            name = "suspend",
            defaultType = "boolean",
            defaultBoolean = false
    )
    public void suspendPlaceHolderView(SlotView view, boolean suspend) {
        if (L.DEBUG) L.logD("want suspend: " + suspend);
        view.notifySuspendState(suspend);
    }

    @Override
    public void dispatchFunction(HippyViewGroup view, String eventName, HippyArray hippyArray, Promise promise) {
        switch (eventName) {
            case "suspend": // 调用时机太晚
//                doSuspend(view);
                break;
            case "unsuspend":
                doUnSuspend(view);
                break;
            default:
                super.dispatchFunction(view, eventName, hippyArray, promise);
                break;
        }
    }

    private void doSuspend(HippyViewGroup view) {
        L.logDF("dispatchFunction: suspend");
    }

    private void doUnSuspend(HippyViewGroup view) {
        if (L.DEBUG) L.logD("unsuspend");
        if (view instanceof SlotView) {
            ((SlotView) view).notifySuspendState(false);
        }

    }

    @Override
    public void onBatchComplete(HippyViewGroup view) {
        super.onBatchComplete(view);
        L.logIF("onBatchComplete view: " + view);
    }

    @Override
    public RenderNode createRenderNode(int i, HippyMap hippyMap, String s, HippyRootView hippyRootView, ControllerManager controllerManager, boolean b) {
        return new WindowNode(i, hippyMap, s, hippyRootView, controllerManager, b);
    }
}
