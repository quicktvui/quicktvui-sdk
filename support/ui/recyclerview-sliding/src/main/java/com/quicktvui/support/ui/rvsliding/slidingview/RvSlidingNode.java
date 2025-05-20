
package com.quicktvui.support.ui.rvsliding.slidingview;


import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.LogUtils;

import com.quicktvui.support.ui.rvsliding.utils.RenderNodeUtils;
import com.quicktvui.support.ui.rvsliding.utils.SlidingEnum;


/**
 * tv-list 联动模式
 */
public class RvSlidingNode extends RenderNode {
    private RvSlidingView rvSlidingView;//自身rvSlidingView
    private SlidingTopView slidingTopView; //导航TopView
    private FastListView fastListView; //内容列表
    public static final String TAG = "RvSlidingViewLog";
    private FastListPageChangeListener fastListPageChangeListener;//内容fastlist的滑动监听

    private final static int RESUME_TASK_DELAY = 600;

    public RvSlidingNode(int mId, HippyMap mPropsToUpdate, String className, HippyRootView rootView, ControllerManager componentManager, boolean isLazyLoad) {
        super(mId, mPropsToUpdate, className, rootView, componentManager, isLazyLoad);
    }

    @Override
    public View createView() {
        return super.createView();
    }

    @Override
    public View createViewRecursive() {
        return super.createViewRecursive();
    }

    @Override
    public void manageChildrenComplete() {
        super.manageChildrenComplete();
        if (LogUtils.isDebug()) {
            Log.i(TAG, "RvSlidingNode manageChildrenComplete childCount :" + getChildCount());
        }

        rvSlidingView = (RvSlidingView) RenderNodeUtils.findViewById(getHippyContext(), getId());
        rvSlidingView.setSlidingNode(this);
        if (this.slidingTopView == null) {
            this.slidingTopView = findTopView();
        }

        if (this.fastListView == null) {
            this.fastListView = findContentView();
        }

        if (fastListView != null && slidingTopView != null) {
            fastListPageChangeListener = new FastListPageChangeListener(slidingTopView);
            fastListView.setOnScrollListener(fastListPageChangeListener);
        }
    }

    @Override
    public void dispatchUIFunction(String functionName, HippyArray var, Promise promise) {
        super.dispatchUIFunction(functionName, var, promise);
        switch (functionName) {

        }
    }

    private SlidingTopView findTopView() {
        SlidingTopView viewGroup = null;
        //通过node获取tabListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof SlidingTopView) {
                viewGroup = (SlidingTopView) nodeView;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "TabsNode findViewGroup index:" + i + ",view :" + viewGroup);
                }
            }
        }
        return viewGroup;
    }

    /**
     * 前端对应content tv_list的name：tabs_content_list
     *
     * @return
     */
    private FastListView findContentView() {
        FastListView v = null;
        //通过node获取fastListView
        for (int i = 0; i < getChildCount(); i++) {
            RenderNode node = getChildAt(i);
            final View nodeView = RenderNodeUtils.findViewById(getHippyContext(), node.getId());
            if (nodeView instanceof FastListView && (node.getProps().getString("name").equals(SlidingEnum.SLIDING_CONTENT_LIST.getName()) || node.getProps().getString("name").equals(SlidingEnum.SERIES_WATERFALL.getName()))) {
                v = (FastListView) nodeView;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "RvSlidingNode findContentView index i:" + i + ",view :" + v);
                }
                break;
            } else if (nodeView instanceof ViewGroup && node.getChildCount() > 1) {
                for (int j = 0; j < node.getChildCount(); j++) {
                    RenderNode nodeChild = node.getChildAt(j);
                    final View nodeChildView = RenderNodeUtils.findViewById(getHippyContext(), nodeChild.getId());
                    if (nodeChildView instanceof FastListView && (nodeChild.getProps().getString("name").equals(SlidingEnum.SLIDING_CONTENT_LIST.getName()) || nodeChild.getProps().getString("name").equals(SlidingEnum.SERIES_WATERFALL.getName()))) {
                        v = (FastListView) nodeChildView;
                        if (LogUtils.isDebug()) {
                            Log.i(TAG, "RvSlidingNode findContentView index i:" + i + "j:" + j + ",view :" + v);
                        }
                        break;
                    }
                }
            }
        }
        return v;
    }

    /**
     * 处理焦点回退
     *
     * @param event
     * @return
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }


    protected HippyEngineContext getHippyContext() {
        return RenderNodeUtils.getHippyContext(mRootView);
    }

    protected void destroy() {

    }
}
