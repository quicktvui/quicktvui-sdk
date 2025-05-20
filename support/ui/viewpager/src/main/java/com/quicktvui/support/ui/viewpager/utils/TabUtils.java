package com.quicktvui.support.ui.viewpager.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import android.support.v7.widget.RecyclerView;

import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.hippy.uimanager.InternalExtendViewUtil;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TabUtils {

    public static void blockRootFocus(View view) {
        InternalExtendViewUtil.blockRootFocus(view);
    }

    public static void unBlockRootFocus(View view) {
        InternalExtendViewUtil.unBlockRootFocus(view);
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

    public static View findNodeViewByID(View view, String id) {
//        if (TextUtils.isEmpty(id)) {
//            return null;
//        }
//        if(view.getTag() instanceof FastAdapter.ItemTag){
//            if(id.equals(((FastAdapter.ItemTag) view.getTag()).getId())){
//                return view;
//            }
//        }
//        if(view instanceof ViewGroup){
//            for(int i =0 ; i < ((ViewGroup) view).getChildCount(); i ++){
//                View v = findNodeViewByID(((ViewGroup) view).getChildAt(i),id);
//                if (v != null) {
//                    return v;
//                }
//            }
//        }
//        return null;
        return ExtendUtil.findViewBySID(id, view);
    }

    public static long launchTime = 0;
    static long lastTime = 0;
    public static void logPerformance(String msg) {
        if(LogUtils.isDebug()) {
            SimpleDateFormat format  = new SimpleDateFormat("HH:mm:ss.SSS");
            long time = new Date().getTime();
            long delta = time - lastTime;
            if (delta > 1000) {
                Log.i("DebugTabsCache", "-------------------------");
            }
            lastTime = time;
            Log.i("DebugTabsCache", msg + " on " + format.format(new Date()) + ",delta:" + delta);
        }
    }

    /**
     * 给tabView发送事件
     */
    public static void sendTabsEvent(View tabsView, String eventName, HippyMap map) {
        HippyMap params = new HippyMap();
        params.pushString("eventName", eventName);
        params.pushMap("params", map);
        if (tabsView != null) {
            HippyViewEvent event = new HippyViewEvent(TabEnum.TAB_EVENT.getName());
            event.send(tabsView, params);
        }
    }

    public static void moveToTop(View view, View tabsView, boolean useSuspensionBg, int index) {
        if (LogUtils.isDebug()) {
            Log.v("TabUtilsTag", "moveToTop-----view:" + view.getId() + "-----tabsView:" + tabsView.getId() + "-----index:" + index);
        }
        TranslateAnimation moveToTopAni = new TranslateAnimation(0f, 0f, 0f, -index);
        moveToTopAni.setDuration(20);
        moveToTopAni.setFillAfter(true);
        moveToTopAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                sendTabsEvent(tabsView, TabEnum.SUSPENSION_TOP_START.getName(), new HippyMap());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int[] colors = new int[]{
                        Color.parseColor("#FF000000"),
                        Color.parseColor("#CC000000"),
                        Color.parseColor("#00000000")
                };
                if (useSuspensionBg && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                    drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                    drawable.setColors(colors);
                    view.setBackground(drawable);
                }
                sendTabsEvent(tabsView, TabEnum.SUSPENSION_TOP_END.getName(), new HippyMap());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(moveToTopAni);
    }

    public static void moveToBottom(View view, View tabsView, boolean useSuspensionBg, int index){
        moveToBottom(view,tabsView,useSuspensionBg,index,20);
    }

    public static void moveToBottom(View view, View tabsView, boolean useSuspensionBg, int index,int duration) {
        if (LogUtils.isDebug()) {
            Log.v("TabUtilsTag", "moveToBottom-----view:" + view.getId() + "-----tabsView:" + tabsView.getId() + "-----index:" + index);
        }
        TranslateAnimation moveToBottomAni = new TranslateAnimation(0f, 0f, -index, 0f);
        moveToBottomAni.setDuration(duration);
        moveToBottomAni.setFillAfter(true);
        moveToBottomAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                sendTabsEvent(tabsView, TabEnum.SUSPENSION_BOTTOM_START.getName(), new HippyMap());
                if (useSuspensionBg) {
                    view.setBackground(null);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sendTabsEvent(tabsView, TabEnum.SUSPENSION_BOTTOM_END.getName(), new HippyMap());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(moveToBottomAni);
    }

}
