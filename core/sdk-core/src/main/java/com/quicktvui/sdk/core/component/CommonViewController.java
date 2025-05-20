package com.quicktvui.sdk.core.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.hippyext.IEsComponentTag;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.core.internal.EsPromiseProxy;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.sunrain.toolkit.utils.log.L;
import com.quicktvui.hippyext.views.fastlist.TemplateCodeParser;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.node.StyleNode;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.ControllerRegistry;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.ArgumentUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Create by weipeng on 2022/02/24 17:55
 * 提供覆写更多方法
 */
public class CommonViewController extends HippyViewController implements IEsComponentTag {

    private IEsComponent mComponent;
    private List<Method> mMethods;

    public CommonViewController(IEsComponent component) {
        this.mComponent = component;
    }

    @Override
    protected View createViewImpl(Context context) {
        return createViewImpl(context, null);
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        if (L.DEBUG) L.logI("--------------------------------------------------");
        if (L.DEBUG) L.logI("createViewImpl:" + mComponent.getClass().getSimpleName());
        if (L.DEBUG) L.logI("createViewImpl:" + (iniProps == null ? null : iniProps.toString()));
        EsViewManager.get().markObject2Engine(mComponent, ((HippyInstanceContext) context).getEngineContext());
        View view = mComponent.createView(context, MapperUtils.hpMap2EsMap(iniProps));
        findAllAttributeMethod();
        // tmp: 屏蔽首次舒适化的属性调用
        fillAttribute(view, iniProps);
        return view;
    }

    @Override
    public void setCustomProp(View view, String methodName, Object props) {
        if(mMethods == null) return;
        for (Method method : mMethods) {
            if (method.getName().equals(methodName)) {
                Type type = method.getGenericParameterTypes()[1];
                fillAttribute(method, type, view, MapperUtils.tryMapperHpData2EsData(props));
                return;
            }
        }
    }

    @Override
    public void dispatchFunction(View view, String functionName, HippyArray var) {
        this.dispatchFunction(view, functionName, var, null);
    }

    @Override
    public void dispatchFunction(View view, String functionName, HippyArray params, Promise promise) {
        if(L.DEBUG) {
            L.logD(mComponent.getClass().getSimpleName());
            L.logD("dispatchFunction view: " + view + ", functionName: " + functionName + ", params: " + params + ", promise: " + promise);
        }
        super.dispatchFunction(view, functionName, params, promise);
        mComponent.dispatchFunction(view, functionName,
                MapperUtils.hpArray2EsArray(params), promise == null ? null : new EsPromiseProxy(promise));
    }

    public IEsComponent getComponent() {
        return mComponent;
    }

    private void findAllAttributeMethod() {
        if (mMethods == null) {
            mMethods = new ArrayList<>();
            //Fixme weipeng 这里getDeclaredMethods方法会导致继承父类的属性无法被发现，这里暂时改成getMethods，但需要优化性能
            // ---> 排查发现是从mComponent反射方法，性能影响小，先不优化 add by weipeng
//            Method[] methods = mComponent.getClass().getDeclaredMethods();
            Method[] methods = mComponent.getClass().getMethods();
            for (Method method : methods) {
                EsComponentAttribute ann = method.getAnnotation(EsComponentAttribute.class);
                if (ann != null){
                    Type[] types = method.getGenericParameterTypes();
                    if(types != null && types.length == 2){
                        mMethods.add(method);
                    }
                }
            }
            Collections.sort(mMethods, (o1, o2) -> {
                EsComponentAttribute a1 = o1.getAnnotation(EsComponentAttribute.class);
                EsComponentAttribute a2 = o2.getAnnotation(EsComponentAttribute.class);
                assert a1 != null;
                assert a2 != null;
                return a1.index() - a2.index();
            });
        }
    }

    private void fillAttribute(View view, HippyMap props) {
        if(mMethods == null || mMethods.size() == 0) return;
        if (props == null) return;
        EsMap data = MapperUtils.hpMap2EsMap(props);
        for (Method method : mMethods) {
            Type type = method.getGenericParameterTypes()[1];
            fillAttribute(method, type, view, data.get(method.getName()));
        }
    }

    private void fillAttribute(Method method, Type type, View view, Object obj) {
        if (view == null) {
            L.logEF("view is NULL");
            return;
        }
        if (obj == null) return;
        if (TemplateCodeParser.isPendingPro(obj)) {
            return;
        }
        Object params;
        try {
            if (type == obj.getClass()) {
                params = obj;
            } else {
                params = ArgumentUtils.parseArgument(type, obj);
            }
        } catch (Exception e) {
            L.logEF(String.format("方法参数类型不对应: %s %s(%s)",
                    getComponent().getClass().getSimpleName(),
                    method.getName(),
                    type.getClass().getSimpleName()
            ));
            return;
        }
        try {
            method.invoke(getComponent(), view, params);
        } catch (Exception e) {
            L.logW("fill attribute", e);
        }
    }

    @Override
    public boolean invokePropMethodForPending(View view, String prop, Object data) {
        if (view == null) return false;
        for (Method method : mMethods) {
            if (method.getName().equals(prop)) {
                Type type = method.getGenericParameterTypes()[1];
                fillAttribute(method, type, view, MapperUtils.tryMapperHpData2EsData(data));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onViewDestroy(View view) {
        EsViewManager.get().unMarkObject2Engine(mComponent);
        if (mComponent != null) {
            mComponent.destroy(view);
//            mComponent = null;
        }
        super.onViewDestroy(view);
    }

    // ---------------------------------- NEW --------------------------------------//

    @Override
    protected void onCreateViewByCache(View view, String type, HippyMap props) {
        super.onCreateViewByCache(view, type, props);
    }

    @Override
    public void onAfterCreateView(View view, HippyMap initialProps) {
        super.onAfterCreateView(view, initialProps);
    }

    @Override
    public void onAfterUpdateProps(View v) {
        super.onAfterUpdateProps(v);
    }

    @Override
    protected void updateExtra(View view, Object object) {
        super.updateExtra(view, object);
    }

    @Override
    protected StyleNode createNode(boolean isVirtual, int rootId) {
        return super.createNode(isVirtual, rootId);
    }

    @Override
    protected StyleNode createNode(boolean isVirtual) {
        return super.createNode(isVirtual);
    }

    @Override
    public void updateLayout(int id, int x, int y, int width, int height, ControllerRegistry componentHolder) {
        super.updateLayout(id, x, y, width, height, componentHolder);
    }

    @Override
    protected boolean shouldInterceptLayout(View view, int x, int y, int width, int height) {
        return super.shouldInterceptLayout(view, x, y, width, height);
    }

    @Override
    protected boolean handleGestureBySelf() {
        return super.handleGestureBySelf();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
    }

    @Override
    protected void setGestureType(View view, String type, boolean flag) {
        super.setGestureType(view, type, flag);
    }

    @Override
    public RenderNode createRenderNode(int id, HippyMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy) {
        return super.createRenderNode(id, props, className, hippyRootView, controllerManager, lazy);
    }

    @Override
    public void onBatchComplete(View view) {
        super.onBatchComplete(view);
    }

    @Override
    protected void deleteChild(ViewGroup parentView, View childView) {
        super.deleteChild(parentView, childView);
    }

    @Override
    protected void deleteChild(ViewGroup parentView, View childView, int childIndex) {
        super.deleteChild(parentView, childView, childIndex);
    }

    @Override
    public void onBeforeViewDestroy(View view) {
        super.onBeforeViewDestroy(view);
    }

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        super.addView(parentView, view, index);
    }

    @Override
    protected void onManageChildComplete(View view) {
        super.onManageChildComplete(view);
    }

    @Override
    public View getChildAt(View viewGroup, int i) {
        return super.getChildAt(viewGroup, i);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mComponent = null;
    }
}
