package com.quicktvui.sdk.core.component;

import android.view.View;
import android.view.ViewGroup;

import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.ControllerRegistry;
import com.tencent.mtt.hippy.uimanager.RenderNode;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;

/**
 * Create by weipeng on 2022/08/25 21:02
 */
public interface IOverrideMethods<V extends View & IEsComponentView> {

    void onCreateViewByCache(View view, String type, EsMap props);

    void onAfterCreateView(View view, EsMap initialProps);

    void onAfterUpdateProps(V v);

    void updateExtra(View view, Object object);

    StyleNode createNode(boolean isVirtual, int rootId);

    StyleNode createNode(boolean isVirtual);

    void updateLayout(int id, int x, int y, int width, int height, ControllerRegistry componentHolder);

    boolean shouldInterceptLayout(View view, int x, int y, int width, int height);

    boolean handleGestureBySelf() ;

    void onFocusChange(View v, boolean hasFocus);

    void setGestureType(V view, String type, boolean flag);

    RenderNode createRenderNode(int id, EsMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy);

    void onBatchComplete(V view) ;

    void deleteChild(ViewGroup parentView, View childView) ;

    void deleteChild(ViewGroup parentView, View childView, int childIndex);

    void onBeforeViewDestroy(V view);

    void addView(ViewGroup parentView, View view, int index);

    void onManageChildComplete(V view);

    int getChildCount(V viewGroup);

    View getChildAt(V viewGroup, int i);

}
