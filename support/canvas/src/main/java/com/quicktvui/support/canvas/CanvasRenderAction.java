/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.support.annotation.NonNull;

import com.quicktvui.support.canvas.canvas2d.CanvasContextRendering2D;

public abstract class CanvasRenderAction extends Action {

    private final String mParameter;

    public CanvasRenderAction(String action, String parameter) {
        super(action);
        mParameter = parameter;
    }

    @Override
    public int hashCode() {
        return (getAction() + mParameter).hashCode();
    }

    public boolean canClear(@NonNull CanvasContextRendering2D context) {
        return false;
    }

    public boolean useCompositeCanvas() {
        return false;
    }

    public boolean supportHardware(@NonNull CanvasContextRendering2D context) {
        return true;
    }

    public abstract void render(@NonNull CanvasContextRendering2D context);
}
