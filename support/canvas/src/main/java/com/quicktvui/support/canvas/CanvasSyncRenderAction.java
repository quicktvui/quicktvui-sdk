/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.support.annotation.NonNull;

import com.quicktvui.support.canvas.canvas2d.CanvasContextRendering2D;

import java.util.Map;

public abstract class CanvasSyncRenderAction extends Action {

    public CanvasSyncRenderAction(String action) {
        super(action);
    }

    public abstract void render(
            @NonNull CanvasContextRendering2D context, @NonNull Map<String, Object> result)
            throws Exception;
}
