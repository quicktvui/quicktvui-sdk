package com.quicktvui.sdk.core.pm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.hippyext.pm.PageRootNode;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;
import com.tencent.mtt.hippy.views.view.HippyViewGroupController;

@ESKitAutoRegister
@HippyController(
        name = EsPageController.CLASS_NAME
)
public class EsPageController extends HippyViewGroupController {
    public static final String CLASS_NAME = "ESPageRootView";
    private HippyInstanceContext instanceContext;

    public EsPageController() {
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        this.instanceContext = (HippyInstanceContext) context;
        return new EsPageView(context, iniProps);
    }

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        super.addView(parentView, view, index);
    }

    @Override
    public void onViewDestroy(HippyViewGroup hippyViewGroup) {
        super.onViewDestroy(hippyViewGroup);
    }

    @Override
    public RenderNode createRenderNode(int id, HippyMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy) {
        return new PageRootNode(id, props, className, hippyRootView, controllerManager, lazy);
    }
}
