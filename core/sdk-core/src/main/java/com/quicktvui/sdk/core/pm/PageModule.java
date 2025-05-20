package com.quicktvui.sdk.core.pm;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.annotation.HippyMethod;
import com.tencent.mtt.hippy.annotation.HippyNativeModule;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;

@ESKitAutoRegister
@HippyNativeModule(name = "PageModule")
public class PageModule extends HippyNativeModuleBase {

    public PageModule(HippyEngineContext context) {
        super(context);
    }

    @HippyMethod(name = "push")
    public void push(String path) {
//        if (L.DEBUG) L.logD("FRGMT push: " + path);
//        if (!EsFragmentRouter.get().isStart()) {
//            Context context = EsContext.get().getContext();
//            Intent intent = new Intent(context, BrowserFragmentActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("path", path);
//            context.startActivity(intent);
//            return;
//        }
//
//        EsFragmentRouter.get().navigate(path);
    }

//    @HippyMethod(name = "replace")
//    public void replace(String path) {
//
//    }

}
