package com.quicktvui.support.ui.viewpager.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.quicktvui.base.ui.FocusDispatchView;
import com.quicktvui.hippyext.AutoFocusManager;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.Utils;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.util.ArrayList;


@SuppressLint("ViewConstructor")
class WaterfallListView extends FastListView {

    private String firstFocusTargetID = null;
    private int bindPosition = -1;
    RecyclerViewPager viewPager;

    int[] touchDown = new int[2];

    ViewDragHelper mDragHelper;
    boolean autoSearchFocusEnable = false;
    View lastSelect = null;
    public WaterfallListView(Context context, HippyMap hippyMap) {
        super(context, hippyMap);
    }

    public void setAutoSearchFocusEnable(boolean autoSearchFocusEnable) {
        this.autoSearchFocusEnable = autoSearchFocusEnable;
    }

    @Override
    public void onRequestAutofocus(View child, View target, int type) {
        if(bindPosition != getCurrentSelectPage()){
            Log.e(AutoFocusManager.TAG,"autofocus return on waterfallList return bindPosition != currentPage , bindPosition:"+bindPosition+",getCurrentSelectPage:"+getCurrentSelectPage());
            return;
        }
        super.onRequestAutofocus(child, target, type);
    }

    public void setBindPosition(int bindPosition) {
        this.bindPosition = bindPosition;
       // Log.v(AutoFocusManager.TAG,"setBindPosition bindPosition:"+bindPosition+",this:"+ ExtendUtil.debugView(this));
    }


    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if(autoSearchFocusEnable) {
            lastSelect = focused;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.lastSelect = null;
    }

    //    @Override
//    public View focusSearch(View focused, int direction) {
//        final View v =  super.focusSearch(focused, direction);
//        Log.i(RecyclerViewPager.FOCUS_TAG,"-----focusSearch focused:"+focused+",direction this:"+this);
//        Log.i(RecyclerViewPager.FOCUS_TAG,"focusSearch return "+v);
//        return v;
//    }

//    @Override
//    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
//        boolean b =  super.requestFocus(direction, previouslyFocusedRect);
//        Log.i(RecyclerViewPager.FOCUS_TAG,"-----requestFocus direction:"+direction+",this:"+this);
//        return b;
//    }

    @Override
    protected View onInterceptFocusSearchFailed(View focused, int direction) {
//        Log.v(RecyclerViewPager.FOCUS_TAG,"---------onInterceptFocusSearchFailed direction:"+direction+",focused:"+focused);
        if (viewPager != null) {
            return viewPager.onContentViewFocusSearchFailed(this,focused,direction);
        }
        return null;
    }

//    @Override
//    public View focusSearch(int direction) {
//        View v =  super.focusSearch(direction);
//        Log.i(RecyclerViewPager.FOCUS_TAG,"focusSearch focusSearch:"+direction+",result:"+v);
//        return v;
//    }

//    @Override
//    public void setDescendantFocusability(int focusability) {
//        super.setDescendantFocusability(focusability);
//        if(LogUtils.isDebug()) {
//            if(focusability == FOCUS_BLOCK_DESCENDANTS) {
//                Log.e(FocusDispatchView.TAG, "+++setDescendantFocusability block this:" + this);
//            }else{
//                Log.i(FocusDispatchView.TAG, "setDescendantFocusability release this:" + this);
//            }
//        }
//    }


    @Override
    public void notifyBringToFront(boolean front) {
        super.notifyBringToFront(front);
        this.lastSelect = null;
    }

    @Override
    public void setPendingData(Object data, RenderNode templateNode, boolean changeVisibility, boolean useDiff) {
        super.setPendingData(data, templateNode, changeVisibility, useDiff);
        this.lastSelect = null;
    }


    @Override
    public void addFocusables(ArrayList<View> arrayList, int direction, int focusableMode) {
        boolean takeover = false;
//        if(!viewPager.isPreferSaveMemory()){
//            Log.i(RecyclerViewPager.FOCUS_TAG, "super addFocusables on !viewPager.isPreferSaveMemory()");
//            super.addFocusables(arrayList, direction, focusableMode);
//            return;
//        }
        if(isPostTaskPaused() || isPageHidden() || getVisibility() != View.VISIBLE){
            if(LogUtils.isDebug()) {
                Log.e("WaterfallListView", "addFocusables return isPostTaskPaused:" + isPostTaskPaused()
                        + ",isPageHidden:" + isPageHidden() + ",view getVisibility : " + getVisibility() + "this:" + this);
            }
            return;
        }else{
            if(LogUtils.isDebug()) {
                Log.i("WaterfallListView", "addFocusables ok isPostTaskPaused:" + isPostTaskPaused() + ",this:" + this);
            }
        }
        if (getDescendantFocusability() == FOCUS_BLOCK_DESCENDANTS) {
            if(LogUtils.isDebug()) {
                Log.i(FocusDispatchView.TAG, "addFocusables on block return" + isPostTaskPaused() + ",this:" + this);
            }
            return;
        }
        if(!hasFocus() && !TextUtils.isEmpty(firstFocusTargetID)){
            View targetView = Utils.findItemViewByID(this,firstFocusTargetID);
            View placeholderContainer = Utils.getPlaceholderContainer(targetView);
            if (placeholderContainer != null && placeholderContainer.isFocusable()) {
                targetView = placeholderContainer;
            }
            if(LogUtils.isDebug()) {
                Log.e("WaterfallListView", "addFocusables findTargetView:"+targetView+",firstFocusTargetID:"+firstFocusTargetID);
            }
            if(getCurrentSelectPage() != this.bindPosition){
                if(LogUtils.isDebug()) {
                    Log.e("WaterfallListView", "addFocusables return on invalid pos" + this.bindPosition + ",currentPage:" + getCurrentSelectPage());
                }
                return;
            }
            if(targetView != null && targetView.isFocusable() && targetView.getVisibility() == View.VISIBLE && targetView.getAlpha() > 0){
                //arrayList.clear();
                //arrayList.add(targetView);
                takeover = true;
                if(LogUtils.isDebug()) {
                    Log.w("WaterfallListView", "addFocusables use firstFocusTargetID: " + firstFocusTargetID + ",targetView:" + targetView);
                }
                targetView.addFocusables(arrayList,direction,focusableMode);
            }
        }
        if (!takeover) {
            if(autoSearchFocusEnable && viewPager.getTabsParam() != null && !hasFocus()){
                int scroll = getOffsetY();
                int checkOffset = (int) (getOrientation() == VERTICAL ? viewPager.getTabsParam().checkOffset * getHeight() :
                                        viewPager.getTabsParam().checkOffset * getWidth());
                if (lastSelect != null && lastSelect.getVisibility() == View.VISIBLE) {
                    if(LogUtils.isDebug()) {
                        Log.i("WaterfallListView", "add lastSelect" + lastSelect);
                    }
                    lastSelect.addFocusables(arrayList,direction,focusableMode);
                    return;
                }
                if (getChildCount() > 0  && scroll < checkOffset) {
                    final View firstView = findViewByPosition(0);
                    if (firstView != null && firstView.getVisibility() == View.VISIBLE) {
                        if(LogUtils.isDebug()) {
                            Log.i("WaterfallListView", "add FirstView" + firstView);
                        }
                        firstView.addFocusables(arrayList,direction,focusableMode);
                        return;
                    }
                }

            }

            super.addFocusables(arrayList, direction, focusableMode);
        }
    }

    void setFirstFocusTarget(String id){
        this.firstFocusTargetID = id;
    }

    public int getBindPosition() {
        return bindPosition;
    }

    int getCurrentSelectPage(){
     return this.viewPager == null ? -1 : this.viewPager.getCurrentItem();
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher dispatcher) {
        super.setGestureDispatcher(dispatcher);
    }

    @Override
    public String toString() {
        return "WaterfallListView{" +
                "firstFocusTargetID='" + firstFocusTargetID + '\'' +
                ", bindPosition=" + bindPosition +
                ", isPostTaskPaused=" + isPostTaskPaused() +
                '}'+",super:"+super.toString();
    }
}
