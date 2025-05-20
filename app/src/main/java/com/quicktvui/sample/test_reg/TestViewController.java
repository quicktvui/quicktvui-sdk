package com.quicktvui.sample.test_reg;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

/**
 * <br>
 *
 * <br>
 */
@ESKitAutoRegister
@HippyController(
        name = "TestViewController"
)
public class TestViewController extends HippyViewController {
    @Override
    protected View createViewImpl(Context context) {
        return new TextView(context);
    }
}
