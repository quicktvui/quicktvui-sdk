package com.quicktvui.support.ui.item;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import android.support.annotation.RequiresApi;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.uimanager.RenderNode;

import com.quicktvui.support.ui.item.host.FrameLayoutHostView;


public class HippyBaseFrameLayout extends FrameLayoutHostView
        implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "HippyBaseView";

    public HippyBaseFrameLayout(Context context) {
        this(context, null);
    }

    public HippyBaseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HippyBaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

    }

    @Override
    public void onGlobalLayout() {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    /**
     * requestPlayerViewLayout
     */
    public void requestPlayerViewLayout() {
        if (getContext() instanceof HippyInstanceContext) {
            try {
                final HippyEngineContext context = ((HippyInstanceContext) getContext()).getEngineContext();
                if (context != null) {
                    context.getDomManager().addNulUITask(() -> {
                        RenderNode node = context.getRenderManager().getRenderNode(getId());
                        if (node != null) {
                            node.updateLayout(node.getX(), node.getY(), node.getWidth(), node.getHeight());
                            node.updateViewRecursive();
                        }
                    });
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}