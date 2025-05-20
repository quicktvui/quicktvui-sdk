package com.quicktvui.support.ui.rvsliding.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.quicktvui.base.ui.FocusDispatchView;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.supportui.views.recyclerview.RecyclerView;

public class RvSlidingUtils {

    public static void blockRootFocus(View view) {
        FocusDispatchView rootView = FocusDispatchView.findRootView(view);
        if (rootView != null) {
            rootView.blockFocus();
        }
    }

    public static void unBlockRootFocus(View view) {
        FocusDispatchView rootView = FocusDispatchView.findRootView(view);
        if (rootView != null) {
            rootView.unBlockFocus();
        }
    }

    public static int getVectorByDirection(int direction, int orientation) {
        int vector = 0;
        boolean vertical = orientation == RecyclerView.VERTICAL;
        if (vertical) {
            if (direction == View.FOCUS_UP) {
                vector = -1;
            } else if (direction == View.FOCUS_DOWN) {
                vector = 1;
            }
        } else {
            if (direction == View.FOCUS_LEFT) {
                vector = -1;
            } else if (direction == View.FOCUS_RIGHT) {
                vector = 1;
            }
        }
        return vector;
    }

    public static void blockFocus(View v) {
        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        }
    }

    public static void unBlockFocus(View v) {
        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        }
    }

    /**
     * 给tabView发送事件
     */
    public static void sendTopEvent(View topView, String eventName, HippyMap map) {
        HippyMap params = new HippyMap();
        params.pushString("eventName", eventName);
        params.pushMap("params", map);
        if (topView != null) {
            HippyViewEvent event = new HippyViewEvent(SlidingEnum.TOP_VIEW_EVENT.getName());
            event.send(topView, params);
        }
    }

    public static void moveToTop(View topView, int index, int duration) {
        TranslateAnimation moveToTopAni = new TranslateAnimation(0f, 0f, 0f, -index);
        moveToTopAni.setDuration(duration);
        moveToTopAni.setFillAfter(false);
        moveToTopAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                sendTopEvent(topView, SlidingEnum.TOP_VIEW_TOP_START.getName(), new HippyMap());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topView.setVisibility(View.GONE);
                sendTopEvent(topView, SlidingEnum.TOP_VIEW_TOP_END.getName(), new HippyMap());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topView.startAnimation(moveToTopAni);
    }

    public static void moveToBottom(View topView, int index, int duration) {
        TranslateAnimation moveToBottomAni = new TranslateAnimation(0f, 0f, -index, 0f);
        moveToBottomAni.setDuration(duration);
        moveToBottomAni.setFillAfter(false);
        moveToBottomAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                sendTopEvent(topView, SlidingEnum.TOP_VIEW_BOTTOM_START.getName(), new HippyMap());
                topView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sendTopEvent(topView, SlidingEnum.TOP_VIEW_BOTTOM_END.getName(), new HippyMap());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topView.startAnimation(moveToBottomAni);
    }
}
