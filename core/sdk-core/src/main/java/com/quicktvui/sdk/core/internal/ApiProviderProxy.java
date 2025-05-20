package com.quicktvui.sdk.core.internal;

import com.tencent.mtt.hippy.HippyAPIProvider;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.Provider;
import com.tencent.mtt.hippy.modules.javascriptmodules.HippyJavaScriptModule;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

import java.util.List;
import java.util.Map;

/**
 * Create by weipeng on 2022/03/03 14:43
 */
public class ApiProviderProxy implements HippyAPIProvider {

    @Override
    public Map<Class<? extends HippyNativeModuleBase>, Provider<? extends HippyNativeModuleBase>> getNativeModules(HippyEngineContext context) {
        return null;
    }

    @Override
    public List<Class<? extends HippyJavaScriptModule>> getJavaScriptModules() {
        return null;
    }

    @Override
    public List<Class<? extends HippyViewController>> getControllers() {
        return null;
    }
}
