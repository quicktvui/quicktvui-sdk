package com.quicktvui.support.ui.rvsliding.slidingview;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

/**
 * 根节点view
 */
public class SlidingTopView extends HippyViewGroup {

    public static final String TAG = "SlidingTopViewLog";

    private int viewHeight = 0;
    private int scrollBottomHeight = 0;
    private int scrollTopHeight = 0;
    private int duration = 0;
    private boolean enableSliding = false;

    public boolean isEnableSliding() {
        return enableSliding;
    }

    public void setEnableSliding(boolean enableSliding) {
        this.enableSliding = enableSliding;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getScrollBottomHeight() {
        return scrollBottomHeight;
    }

    public void setScrollBottomHeight(int scrollBottomHeight) {
        this.scrollBottomHeight = scrollBottomHeight;
    }

    public int getScrollTopHeight() {
        return scrollTopHeight;
    }

    public void setScrollTopHeight(int scrollTopHeight) {
        this.scrollTopHeight = scrollTopHeight;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public SlidingTopView(Context context) {
        super(context);
    }

    @Override
    public void addView(View child, int index) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "addView child:" + child + ",index:" + index);
        }
        super.addView(child, index);
    }

    @Override
    protected HippyEngineContext getHippyContext() {
        return super.getHippyContext();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //执行系统默认
        return super.dispatchKeyEvent(event);
    }
}
