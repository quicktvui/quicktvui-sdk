/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.graphics.Bitmap;
import android.os.Build;

import com.quicktvui.support.canvas.image.CanvasImageLoader;


public abstract class CanvasImageLoadRenderAction extends CanvasRenderAction implements CanvasImageLoader.RecoverImageCallback {

    private int mPageId = Constants.INVALID_PAGE_ID;
    private int mCanvasId = -1;
    private boolean mLoadingBitmap = false;

    public CanvasImageLoadRenderAction(String action, String parameter) {
        super(action, parameter);
    }

    public void markLoading(int pageId, int canvasId) {
        mPageId = pageId;
        mCanvasId = canvasId;

        synchronized (this) {
            mLoadingBitmap = true;
        }
    }

    public boolean isLoading() {
        return mLoadingBitmap;
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        synchronized (this) {
            mLoadingBitmap = false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CanvasManager.getInstance().triggerRender(mPageId, mCanvasId);
        }
    }

    @Override
    public void onFailure() {
        synchronized (this) {
            mLoadingBitmap = false;
        }
    }
}
