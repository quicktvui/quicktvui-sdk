package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.RenderNode;

@ESKitAutoRegister
@HippyController( name = ESTabsItemController.CLASS_NAME)
public class ESTabsItemController extends HippyViewController<ItemView> {
    public static final String CLASS_NAME = "TabsItem";

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        super.addView(parentView, view, index);
    }

    @Override
    public RenderNode createRenderNode(int id, HippyMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy) {
        return new TabsItemNode(id, props, className, hippyRootView, controllerManager, lazy);
    }

    @Override
    protected StyleNode createNode(boolean isVirtual) {
        return new TabsItemStyleNode();
    }

    @Override
    protected View createViewImpl(Context context) {
        return new ItemView(context);
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        return super.createViewImpl(context, iniProps);
    }
}
