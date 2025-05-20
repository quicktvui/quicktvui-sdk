package com.quicktvui.support.canvas;

import android.os.Build;
import android.view.View;

import com.quicktvui.support.canvas.canvas2d.CanvasView2D;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.RenderNode;

public class CanvasNode extends RenderNode {
    public CanvasNode(int mId, HippyMap mPropsToUpdate, String className, HippyRootView rootView, ControllerManager componentManager, boolean isLazyLoad) {
        super(mId, mPropsToUpdate, className, rootView, componentManager, isLazyLoad);
    }

    @Override
    public View createView() {
        CanvasView2D view = (CanvasView2D) super.createView();
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                CanvasManager.getInstance().addCanvas(view);
                CanvasManager.getInstance().setCurrentId(view, getId(), getId());
            }
        }
        return view;
    }
}
