package com.quicktvui.sdk.core.jsview.slot;

import android.content.Context;
import android.support.annotation.NonNull;

import com.quicktvui.base.ui.FocusDispatchView;
import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;

import com.quicktvui.sdk.core.jsview.JsSlotViewManager;

/**
 * <br>
 *
 * <br>
 */
public final class SlotRootView extends FocusDispatchView implements HippyViewBase {

    public SlotRootView(@NonNull Context context) {
        super(context);
    }

    @Override
    public NativeGestureDispatcher getGestureDispatcher() {
        return null;
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher nativeGestureDispatcher) {
    }

    public void onJsViewAdd(SlotView view) {
        JsSlotViewManager.get().onViewCreated(view);
    }

    public void onJsViewRemove(SlotView view) {
        JsSlotViewManager.get().onViewDelete(view);
    }
}
