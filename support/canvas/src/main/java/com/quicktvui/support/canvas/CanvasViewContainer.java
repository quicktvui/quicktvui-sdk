/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.component.IEsComponentView;


public class CanvasViewContainer extends FrameLayout implements IEsComponentView {

    private View mCanvasView;

    public CanvasViewContainer(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    /*public void setCanvasView(View view) {
        mCanvasView = view;
        removeAllViews();
        if (mCanvasView != null) {
            addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }*/

    /*public void setActions(String actions) {
        CanvasActionHandler.getInstance().processAsyncActions(1, 1, actions);
    }*/

    public void setBackgroundColor(int color) {
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child.getWidth() <= 0 || child.getHeight() <= 0) {
            child.requestLayout();
        }
        return super.drawChild(canvas, child, drawingTime);
    }
}
