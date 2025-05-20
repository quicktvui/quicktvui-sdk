package com.quicktvui.support.player.manager.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.BuildConfig;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;


public class ESBaseFrameLayout extends FrameLayout implements IEsComponentView,
        ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "EsBaseFrameLayout";

    public ESBaseFrameLayout(Context context) {
        this(context, null);
    }

    public ESBaseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ESBaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

    }

    @Override
    public void onGlobalLayout() {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "---------onGlobalLayout------------>>>>>>");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "---------onAttachedToWindow------------>>>>>>");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "---------onDetachedFromWindow------------>>>>>>");
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "---------onLayout------------>>>>>>");
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    /**
     * requestPlayerViewLayout
     */
    public void requestPlayerViewLayout() {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "---------requestPlayerViewLayout---xxxx221--------->>>>>>");
        }
        EsProxy.get().updateLayout(this);
    }
}