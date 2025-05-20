package com.quicktvui.sdk.core.module;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;

import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 * Create by weipeng on 2022/02/28 18:45
 */
public class CommonModule extends HippyNativeModuleBase {

    private final CommonModuleInfo mModuleInfo;

    public CommonModule(HippyEngineContext context, IEsModule module) {
        super(context);
        EsViewManager.get().markObject2Engine(module, context);
        mModuleInfo = new CommonModuleInfo(module);
        module.init(context.getGlobalConfigs().getContext());
    }

    public CommonModuleInfo getModuleInfo() {
        return mModuleInfo;
    }

}
