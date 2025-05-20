package com.quicktvui.support.ui.rvsliding.slidingview;

import android.util.Log;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.tencent.mtt.hippy.utils.LogUtils;

import com.quicktvui.support.ui.rvsliding.utils.RvSlidingUtils;

/**
 * fastlist滑动事件
 */
public class FastListPageChangeListener extends RecyclerView.OnScrollListener {
    private SlidingTopView topView;
    private boolean isShowTop = false;
    private final int suspensionTop;
    private final int scrollBottomHeight;
    private final int scrollTopHeight;
    private final int duration;
    private boolean enableSliding;

    public FastListPageChangeListener(SlidingTopView topView) {
        this.topView = topView;
        suspensionTop = topView.getViewHeight() > 0 ? topView.getViewHeight() : 322;
        scrollBottomHeight = topView.getScrollBottomHeight() > 0 ? topView.getScrollBottomHeight() : 50;
        scrollTopHeight = topView.getScrollTopHeight() > 0 ? topView.getScrollTopHeight() : 50;
        duration = topView.getDuration() > 0 ? topView.getDuration() : 50;
        enableSliding = topView.isEnableSliding();
        if (LogUtils.isDebug()) {
            Log.d("FastListPageListener", "suspensionTop:" + suspensionTop);
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        assert recyclerView instanceof TVListView;
        final TVListView tl = (TVListView) recyclerView;
        int scrollPosition = tl.getOffsetY();
        if (enableSliding){
            if (LogUtils.isDebug()) {
                Log.d("FastListPageListener", "onScrolled dx:" + dx + ",dy:" + dy + ",offsetY:" + scrollPosition);
            }
            if (dy > 0 && scrollPosition > scrollBottomHeight) {//向下滑动
                if (!isShowTop) {
                    RvSlidingUtils.moveToBottom(topView, suspensionTop,duration);
                    isShowTop = true;
                }
            } else if (dy < 0 && scrollPosition <= scrollTopHeight) { //向上滑动
                if (isShowTop) {
                    RvSlidingUtils.moveToTop(topView, suspensionTop,duration);
                    isShowTop = false;
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }
}
