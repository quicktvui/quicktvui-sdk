package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.extscreen.support.viewpager2.widget.ViewPager2;
import com.quicktvui.hippyext.RenderUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class ViewPager2ScrollHelper {
    private final ViewPager2 vp;
    private final float speedUp = 3f;
    public static final String TAG = TabsView.TAG;
    TabsParam tabsParam;

    public ViewPager2ScrollHelper(ViewPager2 vp) {
        this.vp = vp;
        init();
    }

    View findPageViewByPosition(int position){
        if (recyclerView != null) {
            RecyclerView.ViewHolder vh =   recyclerView.findViewHolderForAdapterPosition(position);
            if (vh != null) {
                return vh.itemView;
            }
        }
        return null;
    }

    public void setTabsParam(TabsParam tabsParam) {
        this.tabsParam = tabsParam;
    }

    private RecyclerView recyclerView;
    private Object mAccessibilityProvider;
    private Object mScrollEventAdapter;
    private Method onSetNewCurrentItemMethod;
    private Method getRelativeScrollPositionMethod;
    private Method notifyProgrammaticScrollMethod;
    private Field mCurrentItemField;

    public void reLayout(){
        recyclerView.requestLayout();
        RenderUtil.reLayoutView(vp);
        RenderUtil.reLayoutView(recyclerView);
    }

    void init() {
        try{
            Field mRecyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            mRecyclerViewField.setAccessible(true);
            recyclerView = (RecyclerView) mRecyclerViewField.get(vp);
            if (recyclerView != null) {
                recyclerView.setFocusable(false);
            }
//            recyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//            vp.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            Field currentItem = ViewPager2.class.getDeclaredField("mCurrentItem");
            currentItem.setAccessible(true);
            mCurrentItemField = currentItem;
//            recyclerView.layoutManager
            Field mAccessibilityProviderField =
                    ViewPager2.class.getDeclaredField("mAccessibilityProvider");
            mAccessibilityProviderField.setAccessible(true);
            mAccessibilityProvider = mAccessibilityProviderField.get(vp);
            onSetNewCurrentItemMethod =
                    mAccessibilityProvider.getClass().getDeclaredMethod("onSetNewCurrentItem");
            onSetNewCurrentItemMethod.setAccessible(true);

            Field mScrollEventAdapterField =
                    ViewPager2.class.getDeclaredField("mScrollEventAdapter");
            mScrollEventAdapterField.setAccessible(true);
            mScrollEventAdapter = mScrollEventAdapterField.get(vp);
            getRelativeScrollPositionMethod =
                    mScrollEventAdapter.getClass().getDeclaredMethod("getRelativeScrollPosition");
            getRelativeScrollPositionMethod.setAccessible(true);

            notifyProgrammaticScrollMethod = mScrollEventAdapter.getClass().getDeclaredMethod(
                    "notifyProgrammaticScroll",
                    int.class,
                    boolean.class
        );
            notifyProgrammaticScrollMethod.setAccessible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void  setCurrentItem(int item){
        this.setCurrentItem(item,false,3);
    }

    public void postTaskDelay(Runnable runnable,int delay){
        if (vp != null) {
            vp.postDelayed(runnable,delay);
        }
    }

    /**
     * 模拟手写Viewpage2的setCurrentItemInternal(int item, boolean smoothScroll)方法
     * 其中smoothScroll为true
     * 主要目的是通过手动实现vp的翻页方法达到控制RecycleView执行滚动的SmoothScroller对象
     */
    public void  setCurrentItem(int item,boolean anim,float speedUp) {
        Log.i(TAG, "setCurrentItem item:" + item + ",anim:" + anim + ",speedUp:" + speedUp);
        if(!anim){
//            try {
//                mCurrentItemField.setInt(vp,item);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
            if (recyclerView != null) {
                recyclerView.stopScroll();
            }
            vp.setCurrentItem(item,false);
//            recyclerView.scrollToPosition(item);
            reLayout();
        }else{
            RecyclerView.Adapter adapter = vp.getAdapter();
            if (adapter == null || adapter.getItemCount() <= 0) {
                return;
            }
            item = Math.max(0,item);
            item = Math.min(item,adapter.getItemCount() - 1);
            if (item == vp.getCurrentItem() && vp.getScrollState() == ViewPager2.SCROLL_STATE_IDLE) {
                Log.e(TAG,"setCurrentItem return item == vp.getCurrentItem() item:"+item);
                return;
            }
            if (item == vp.getCurrentItem()) {
                Log.e(TAG,"setCurrentItem return item == vp.getCurrentItem() 1 item:"+item);
                return;
            }
            try {
//                Toast.makeText(vp.getContext(), "切换tab:"+item,Toast.LENGTH_SHORT).show();
//                vp.setCurrentItem(item,false);
                //这里先停止滚动一下，否则频繁切换会导致切换失败
                if(recyclerView != null) {
                    recyclerView.stopScroll();
                    mCurrentItemField.setInt(vp, item);
                    onSetNewCurrentItemMethod.invoke(mAccessibilityProvider);
                    notifyProgrammaticScrollMethod.invoke(mScrollEventAdapter, item, true);
                    smoothScrollToPosition(item, vp.getContext(), recyclerView.getLayoutManager(), speedUp);
                }
//                reLayout();
            }catch (Exception e){
                Log.e(TAG,"set Current error on "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 模拟手写RecyclerView的smoothScrollToPosition方法 替换了startSmoothScroll的参数达到了改变速度的目的
     */
    private void  smoothScrollToPosition(
            int item,
            Context context,
            RecyclerView.LayoutManager layoutManager,
            float speedUp
    ) {
        assert layoutManager != null;
        final LinearSmoothScroller linearSmoothScroller = getSlowLinearSmoothScroller(context,speedUp);
        replaceDecelerateInterpolator(linearSmoothScroller);
        linearSmoothScroller.setTargetPosition(item);
        layoutManager.startSmoothScroll(linearSmoothScroller);
    }

    /**
     * 通过复写LinearSmoothScroller的方法来实现速度改变
     */
    private LinearSmoothScroller getSlowLinearSmoothScroller(Context context,float speed){


        return new LinearSmoothScroller(vp.getContext()){
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                if(tabsParam != null && tabsParam.speedPerPixel > 0){
                    return (tabsParam.speedPerPixel / displayMetrics.densityDpi) / speed;
                }else {
                    float superResult = super.calculateSpeedPerPixel(displayMetrics);
                    return superResult / speed;
                }
            }
        };
    }

    /**
     * 修改SmoothScroller的默认差值器，将其改为减速差值器
     */
    private void replaceDecelerateInterpolator(RecyclerView.SmoothScroller linearSmoothScroller) {
        try {
            Field mDecelerateInterpolatorField =
                    LinearSmoothScroller.class.getDeclaredField("mDecelerateInterpolator");
            mDecelerateInterpolatorField.setAccessible(true);
            mDecelerateInterpolatorField.set(linearSmoothScroller, new DecelerateInterpolator());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}