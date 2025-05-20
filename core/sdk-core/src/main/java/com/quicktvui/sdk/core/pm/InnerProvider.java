package com.quicktvui.sdk.core.pm;

import com.quicktvui.sdk.core.EsAPIProvider;
import com.quicktvui.sdk.core.jsview.slot.SlotRootViewController;
import com.quicktvui.sdk.core.jsview.slot.SlotViewController;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.Provider;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发版时的内置组件，可以放在这里
 */
public class InnerProvider extends EsAPIProvider {
    @Override
    public List<Class<? extends HippyViewController>> getControllers() {
        List<Class<? extends HippyViewController>> components = new ArrayList<>();
        components.add(EsPageController.class);
        components.add(EsRouterController.class);

        components.add(SlotRootViewController.class);
        components.add(SlotViewController.class);
        return components;
    }

    @Override
    public Map<Class<? extends HippyNativeModuleBase>, Provider<? extends HippyNativeModuleBase>> getNativeModules(HippyEngineContext context) {
        Map<Class<? extends HippyNativeModuleBase>, Provider<? extends HippyNativeModuleBase>> modules = new HashMap<>();
        modules.put(PageModule.class, (Provider<PageModule>) () -> new PageModule(context));
        return modules;
    }
}
