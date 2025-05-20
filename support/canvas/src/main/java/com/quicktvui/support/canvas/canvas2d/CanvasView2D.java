/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.canvas2d;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import android.support.annotation.NonNull;

import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.support.canvas.CanvasContext;
import com.quicktvui.support.canvas.CanvasManager;
import com.quicktvui.support.canvas.CanvasRenderAction;
import com.quicktvui.support.canvas.CanvasView;
import com.quicktvui.support.canvas.Constants;
import com.sunrain.toolkit.utils.ThreadUtils;
import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.util.ArrayList;


public class CanvasView2D extends View implements CanvasView, HippyViewBase, IEsComponentView {

    private static final String TAG = "CanvasView2D";

    public CanvasView2D(Context context) {
        super(context);
        init();
    }

    public CanvasView2D(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView2D(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (LogUtils.isDebug()) {
            Log.d(Constants.TAG, "onDraw--->: ");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (getWidth() <= 0 || getHeight() <= 0) {
            Log.e(Constants.TAG, "canvas view size is zero!");
            //在CanvasViewContainer中addView后，并没有触发CanvasView2D测量，这里在大小为0时重新触发
            ViewParent parent = getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup parentView = (ViewGroup) parent;
                if ((parentView.getWidth() - parentView.getPaddingLeft() - parentView.getPaddingRight()) > 0
                        && (parentView.getHeight() - parentView.getPaddingTop() - parentView.getPaddingBottom()) > 0) {
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        requestLayout();
                    }
                }
            }
            return;
        }
        int pageId = CanvasManager.getInstance().getPageId(this);
        int ref = CanvasManager.getInstance().getRef(this);
        //获取渲染控制动作
        ArrayList<CanvasRenderAction> renderActions = null;
        if (LogUtils.isDebug()) {
            Log.d(Constants.TAG, "prepare renderActions pageId is: " + pageId + "ref is: " + ref);
        }
        renderActions = CanvasManager.getInstance().getRenderActions(pageId, ref);
        if (renderActions == null || renderActions.isEmpty()) {
            CanvasManager.getInstance().addRenderActions(pageId, ref, CanvasManager.getInstance().getAction(pageId));
            Log.e(Constants.TAG, "renderActions is empty return--->" + "pageId: " + pageId + "ref: " + ref);
            return;
        }
        CanvasContext context = CanvasManager.getInstance().getContext(pageId, ref);
        if (context == null || !context.is2d()) {
            Log.e(Constants.TAG, "CanvasContext is null,return :" + ref);
            return;
        }

        boolean supportHardware = true;
        for (CanvasRenderAction renderAction : renderActions) {
            if (!renderAction.supportHardware((CanvasContextRendering2D) context)) {
                supportHardware = false;
                break;
            }
        }

        if (supportHardware) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (LogUtils.isDebug()) {
            Log.d(Constants.TAG, "render执行--->: " + renderActions);
        }
        ((CanvasContextRendering2D) context).render(this, canvas, renderActions);
    }

    @Override
    public void drawCanvas() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
                if (LogUtils.isDebug()) {
                    Log.d(Constants.TAG, "invalidate--->: ");
                }
            }
        });
    }

    @NonNull
    @Override
    public View get() {
        return this;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CanvasManager.getInstance().removeCanvas(this);
            if (LogUtils.isDebug()) {
                Log.d(Constants.TAG, "onDetachedFromWindow--->removeCanvas: ");
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CanvasManager.getInstance().addCanvas(this);
            if (LogUtils.isDebug()) {
                Log.d(Constants.TAG, "onAttachedToWindow--->addCanvas: ");
            }
        }*/
    }

    @Override
    public NativeGestureDispatcher getGestureDispatcher() {
        return null;
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher nativeGestureDispatcher) {

    }
}
