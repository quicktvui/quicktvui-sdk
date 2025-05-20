/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.bridge;

import com.quicktvui.support.canvas.annotation.Extension;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Callback interface for hybrid invocation with {@link Extension.Mode#CALLBACK}.
 */
public class Callback {

    protected String mJsCallback;
    protected Extension.Mode mMode;
    protected AtomicBoolean mIsCalled = new AtomicBoolean(false);

    /**
     * Invoke callback with specified response.
     *
     * @param response invocation response.
     */
    public void callback(Response response) {
        if (mMode == Extension.Mode.CALLBACK
                || mIsCalled.compareAndSet(false, true)
                || response.getCode() == Response.CODE_CUSTOM_CALLBACK) {
            //doCallback(response);
        }
    }

 /*   protected void doCallback(Response response) {
        mExtensionManager.callback(response, mJsCallback);
    }*/
}
