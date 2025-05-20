package com.quicktvui.sdk.core;

import com.tencent.mtt.hippy.HippyAPIProvider;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.Provider;
import com.tencent.mtt.hippy.modules.javascriptmodules.HippyJavaScriptModule;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

import java.util.List;
import java.util.Map;

/**
 * Create by weipeng on 2022/05/06 15:56
 * Describe TODO 将来看看能不能从这里入手来注册自定义组件
 */
public class EsAPIProvider implements HippyAPIProvider {
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
