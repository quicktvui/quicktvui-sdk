package com.quicktvui.support.ui.rvsliding.slidingview;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;

/**
 * 根节点view
 */
public class RvSlidingView extends FrameLayout implements HippyViewBase {

    private RvSlidingNode rvSlidingNode;
    private Context mContext;

    public static final String TAG = "RvSlidingViewLog";

    public RvSlidingView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setSlidingNode(RvSlidingNode rvSlidingNode) {
        this.rvSlidingNode = rvSlidingNode;
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (rvSlidingNode != null) {
            rvSlidingNode.destroy();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (rvSlidingNode != null && rvSlidingNode.dispatchKeyEvent(event)) {
            //被拦截了，直接返回true
            return true;
        }
        //执行系统默认
        return super.dispatchKeyEvent(event);
    }

    @Override
    public NativeGestureDispatcher getGestureDispatcher() {
        return null;
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher nativeGestureDispatcher) {

    }
}
