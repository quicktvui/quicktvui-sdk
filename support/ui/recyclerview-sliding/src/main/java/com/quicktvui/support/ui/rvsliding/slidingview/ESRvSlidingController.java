package com.quicktvui.support.ui.rvsliding.slidingview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.HippyGroupController;
import com.tencent.mtt.hippy.uimanager.RenderNode;

/***
 * slidingView组件，包含一个topview及一个listContent
 */
@ESKitAutoRegister
@HippyController(name = ESRvSlidingController.CLASS_NAME)
public class ESRvSlidingController extends HippyGroupController<RvSlidingView> {
    public static final String CLASS_NAME = "RvSlidingView";
    public static final String TAG = "RvSlidingViewLog";

    public ESRvSlidingController() {
    }

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        super.addView(parentView, view, index);
    }

    @Override
    public RenderNode createRenderNode(int id, HippyMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy) {
        //将业务逻辑集中在TabsNode中处理
        return new RvSlidingNode(id, props, className, hippyRootView, controllerManager, lazy);
    }

    @Override
    protected StyleNode createNode(boolean isVirtual) {
        return super.createNode(isVirtual);
    }

    protected View createViewImpl(Context context) {
        //这里创建RvSlidingView 根view
        return new RvSlidingView(context);
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        return super.createViewImpl(context, iniProps);
    }
}
