package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.views.view.HippyViewGroupController;

import java.util.Date;

import com.quicktvui.support.ui.viewpager.utils.TabUtils;


/***
 * tabs组件，包含一个tab及一个listContent
 */
@ESKitAutoRegister
@HippyController(
        name = ESTabsController.CLASS_NAME
)
public class ESTabsController extends HippyViewGroupController {
    public static final String CLASS_NAME = "TabsView";
    public static final String TAG = "TabsViewCLLog";

    public ESTabsController() {
    }

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        super.addView(parentView, view, index);
    }

    /**
     * singlePageMode:单页面模式 tv_list代替viewpager
     */
    @Override
    public RenderNode createRenderNode(int id, HippyMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy) {
        TabUtils.logPerformance("createRenderNode");
        //将业务逻辑集中在TabsNode中处理
        if (props.containsKey("singlePageMode") && props.getBoolean("singlePageMode")) {
            return new SingleTabsNode(id, props, className, hippyRootView, controllerManager, lazy);
        } else {
            return new TabsNode(id, props, className, hippyRootView, controllerManager, lazy);
        }
    }

    //zhaopeng add
    @HippyControllerProps(name = "autoFocusID", defaultType = HippyControllerProps.STRING)
    public void setAutoFocusID(View view, String id) {
        Log.i(AutoFocusUtils.TAG,"|-----> controller call setAutoFocusID:"+id+",view :"+view);
        if(view instanceof TabsView){
            ((TabsView) view).setAutoFocusID(id);
        }
    }
    //zhaopeng add
    @HippyControllerProps(name = "autoFocus", defaultType = HippyControllerProps.STRING)
    public void setAutoFocus(View view, String id) {
        Log.i(AutoFocusUtils.TAG,"|-----> controller call setAutoFocusID:"+id+",view :"+view);
        if(view instanceof TabsView){
            ((TabsView) view).setAutoFocusID(id);
        }
    }
//    //zhaopeng add
//    @Override
//    public void setFocusSID(View view, String id) {
//        Log.i(AutoFocusUtils.TAG,"|-----> controller call setFocusSID:"+id+",view :"+view);
//        if(view instanceof TabsView){
//            ((TabsView) view).setAutoFocusID(id);
//        }
//    }

    @Override
    protected StyleNode createNode(boolean isVirtual) {
        //这里将dom node接管，用来存储数据
        TabUtils.logPerformance("createStyleNode");
        return new TabsStyleNode();
    }

    protected View createViewImpl(Context context) {
        //这里创建TabsView
//        TabUtils.logPerformance("createViewImpl");
        return new TabsView(context);
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        TabUtils.launchTime = new Date().getTime();
        TabUtils.logPerformance("createViewImpl with iniProps");
        return super.createViewImpl(context, iniProps);
    }

}
