/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

public abstract class Action {
    private String mAction;

    public Action(String action) {
        mAction = action;
    }

    public String getAction() {
        return mAction;
    }
}
