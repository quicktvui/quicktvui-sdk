package com.quicktvui.sample.test_reg;

import android.content.Context;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.uimanager.HippyGroupController;

/**
 * <br>
 *
 * <br>
 */
@ESKitAutoRegister
@HippyController(
        name = "TestViewGroupController"
)
public class TestViewGroupController extends HippyGroupController {
    @Override
    protected View createViewImpl(Context context) {
        return null;
    }
}
