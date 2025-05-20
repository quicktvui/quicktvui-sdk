package com.quicktvui.support.ui.rvsliding.utils;

import android.view.View;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.node.NodeProps;
import com.tencent.mtt.hippy.uimanager.CustomControllerHelper;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.RenderNode;

/**
 */
public class RenderNodeUtils {
    /**
     * 获取当前view的子节点
     *
     * @param view
     * @return
     */
    public static RenderNode getRenderNode(View view) {
        return getHippyContext(view).getRenderManager().getRenderNode(view.getId());
    }

    public static HippyEngineContext getHippyContext(View view) {
        return ((HippyInstanceContext) view.getContext()).getEngineContext();
    }

    public static <T> T findViewController(HippyEngineContext context, RenderNode node) {
        if (node != null) {
            final HippyViewController t = CustomControllerHelper.getViewController(context.getRenderManager().getControllerManager(),
                    node);
            if (t != null) {
                return (T) t;
            }
        }
        return null;
    }

    public static <T> T findViewController(View view, RenderNode node) {
        final HippyEngineContext context = ((HippyInstanceContext) view.getContext()).getEngineContext();
        if (node != null) {
            final HippyViewController t = CustomControllerHelper.getViewController(context.getRenderManager().getControllerManager(),
                    node);
            if (t != null) {
                return (T) t;
            }
        }
        return null;
    }

    public static View findViewById(HippyEngineContext context, int id) {
        return context.getRenderManager().getControllerManager().findView(id);
    }


    public static void doDiffProps(HippyViewController vc, HippyMap props, View view) {
        for (String prop : props.keySet()) {
            if (prop == null) {
//                Log.e(TAG, "doDiffProps prop is null  en:" + en + ",position:" + position + ",vc:" + vc);
                continue;
            }
            if (prop.equals(NodeProps.STYLE) && props.get(prop) instanceof HippyMap) {
                doDiffProps(vc, props.getMap(prop), view);
            } else {
                invokeProp(vc, props, prop, view);
            }
        }
    }

    static void invokeProp(HippyViewController vc, HippyMap props, String prop, View view) {
        if (view == null) {
            return;
        }
        final Object dataFromValue = props.get(prop);
        CustomControllerHelper.invokePropMethodForPending(vc, view, prop, dataFromValue);
    }
}
