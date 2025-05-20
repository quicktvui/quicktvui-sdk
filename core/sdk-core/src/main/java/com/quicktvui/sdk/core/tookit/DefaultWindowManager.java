package com.quicktvui.sdk.core.tookit;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.quicktvui.sdk.core.ui.wm.AbstractWindowManager;

/**
 * <br>
 *
 * <br>
 */
final class DefaultWindowManager extends AbstractWindowManager implements IESToolkit{

    public DefaultWindowManager(Context ctx) {
        super(ctx);
    }

    @Override
    protected void configWindowManagerLayoutParams(WindowManager.LayoutParams params) {
        noFocus(params);
    }

    @Override
    public IESToolkit focusable() {
        hasFocus(getWindowParams());
        return this;
    }

    @Override
    public void show(View view) {
        removeView();
        addView(view);
    }

    @Override
    public boolean isToolKitShow() {
        return isShown();
    }

    @Override
    public void hide() {
        removeView();
    }
}
