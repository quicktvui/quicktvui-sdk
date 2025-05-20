package com.quicktvui.sdk.core.module;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.annotation.HippyNativeModule;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.modules.PromiseImpl;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 * Create by weipeng on 2022/02/28 18:53
 */
public class CommonModuleInfo extends HippyNativeModuleInfo {

    private IEsModule module;

    public CommonModuleInfo(IEsModule module) {
        super();
        this.module = module;
        mName = module.getClass().getSimpleName();
    }

    @Override
    public void initialize() {
        if(mInit) return;

        Class cls = module.getClass();

        mMethods = new HashMap<>();

        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if(!Modifier.isPublic(method.getModifiers())) continue;
            String methodName = method.getName();
            if(mMethods.containsKey(methodName)) continue;
            mMethods.put(methodName, new CommonNativeMethod(method));
        }

        mInit = true;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public HippyNativeModule.Thread getThread() {
        return HippyNativeModule.Thread.MAIN;
    }

    @Override
    public void destroy() {
        EsViewManager.get().unMarkObject2Engine(module);
        module.destroy();
        module = null;
        super.destroy();
    }

    private final class CommonNativeMethod extends HippyNativeMethod{
        public CommonNativeMethod(Method method) {
            super(method);
        }

        @Override
//        public void invoke(HippyEngineContext context, Object receiver, HippyArray args, PromiseImpl promise) throws Exception{
        public void invoke(HippyEngineContext context, Object receiver, HippyArray args, PromiseImpl promise) {
            // HOOK 替换成自己的Module
            super.invoke(context, module, args, promise);
        }

        @Override
        protected Object[] prepareArguments(HippyEngineContext context, Type[] paramClss, HippyArray args, PromiseImpl promise) {
            // Hook

            // 1、将自定义的EsMap/EsArray换为HippyMap/HippyArray
            paramClss = MapperUtils.tryMapperEsClass2HpClass(paramClss);

            // 2、获取参数映射
            Object[] objects = super.prepareArguments(context, paramClss, args, promise);

            // 3、替换回自己的Map/Array
            objects = MapperUtils.tryMapperHpData2EsData(objects);

            return objects;
        }
    }
}
