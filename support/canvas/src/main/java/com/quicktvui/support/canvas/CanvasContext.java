/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;

import android.support.annotation.RequiresApi;

import com.quicktvui.support.canvas.image.CanvasImageHelper;
import com.quicktvui.support.canvas.utils.DisplayUtil;
import com.sunrain.toolkit.utils.Utils;


public class CanvasContext {

    protected int mDesignWidth;
    private int mPageId;
    private int mCanvasElementId;
    private int mCanvasWidth;
    private int mCanvasHeight;

    public CanvasContext(int pageId, int canvasId, int designWidth) {
        mPageId = pageId;
        mCanvasElementId = canvasId;
        mDesignWidth = designWidth;
    }

    public String type() {
        return "";
    }

    public boolean is2d() {
        return false;
    }

    public boolean isWebGL() {
        return false;
    }

    public int getPageId() {
        return mPageId;
    }

    public int getCanvasElementId() {
        return mCanvasElementId;
    }

    protected int getDesignWidth() {
        return mDesignWidth;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    /*public ESCanvasController getCanvas() {
        return CanvasManager.getInstance().getCanvas(mPageId, mCanvasElementId);
    }*/

    public void updateSize(int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int getCanvasWidth() {
        if (mCanvasWidth > 0) {
            return mCanvasWidth;
        }

       /* ESCanvasController canvas = CanvasManager.getInstance().getCanvas(mPageId, mCanvasElementId);
        if (canvas == null) {
            return 0;
        }
        return canvas.getCanvasWidth();*/
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int getCanvasHeight() {
        if (mCanvasHeight > 0) {
            return mCanvasHeight;
        }

        /*ESCanvasController canvas = CanvasManager.getInstance().getCanvas(mPageId, mCanvasElementId);
        if (canvas == null) {
            return 0;
        }
        return canvas.getCanvasHeight();*/
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public RectF getDesignDisplayRegion() {
        RectF rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;

        if (mCanvasWidth > 0 && mCanvasHeight > 0) {
            rectF.right = getDesignSize(mCanvasWidth);
            rectF.bottom = getDesignSize(mCanvasHeight);
        } else {
            /*ESCanvasController canvas = CanvasManager.getInstance().getCanvas(mPageId, mCanvasElementId);
            if (canvas == null) {
                return null;
            }

            int width = canvas.getCanvasWidth();
            int height = canvas.getCanvasHeight();
            if (width > 0 && height > 0) {
                rectF.right = getDesignSize(width);
                rectF.bottom = getDesignSize(height);
            } else {
                CanvasViewContainer hostView = (CanvasViewContainer) canvas.getHostView();
                if (hostView == null) {
                    return null;
                }

                int measuredWidth = hostView.getMeasuredWidth();
                int measuredHeight = hostView.getMeasuredHeight();

                if (measuredWidth <= 0 || measuredHeight <= 0) {
                    return null;
                }

                rectF.right = getDesignSize(measuredWidth);
                rectF.bottom = getDesignSize(measuredHeight);
            }*/
        }

        return rectF;
    }

    public void destroy() {
    }

    protected float getRealSize(float size) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return DisplayUtil.getRealPxByWidth(size, mDesignWidth);
        }
        // 9.0以上size为浮点数有误差，向上取整解决
        return (float) Math.ceil(DisplayUtil.getRealPxByWidth(size, mDesignWidth));
    }

    protected int getRealSize(int size) {
        return (int) DisplayUtil.getRealPxByWidth(size, mDesignWidth);
    }

    protected float getDesignSize(float size) {
        return DisplayUtil.getDesignPxByWidth(size, mDesignWidth);
    }

    protected int getDesignSize(int size) {
        return (int) DisplayUtil.getDesignPxByWidth(size, mDesignWidth);
    }

    protected int getScreenWidth() {
        return DisplayUtil.getScreenWidth(Utils.getApp());
    }

    protected int getScreenHeight() {
        return DisplayUtil.getScreenHeight(Utils.getApp());
    }

    public Bitmap createBitmap(Bitmap src) {
        return CanvasImageHelper.getInstance().createBitmap(src);
    }

    public Bitmap createBitmap(int width, int height) {
        return CanvasImageHelper.getInstance().createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        return CanvasImageHelper.getInstance().createBitmap(width, height, config);
    }

    public Bitmap createBitmap(
            Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) {
        return CanvasImageHelper.getInstance().createBitmap(source, x, y, width, height, m, filter);
    }
}
