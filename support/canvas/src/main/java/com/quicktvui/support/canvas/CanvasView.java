/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.view.View;

import android.support.annotation.NonNull;


public interface CanvasView  {

    void drawCanvas();

    @NonNull
    View get();
}
