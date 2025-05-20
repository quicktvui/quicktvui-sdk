package com.quicktvui.support.ui.legacy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.quicktvui.support.ui.legacy.view.ITVView;
import com.quicktvui.support.ui.legacy.view.TVViewUtil;

import com.quicktvui.support.ui.v7.widget.LinearLayoutManager;
import com.quicktvui.support.ui.v7.widget.RecyclerView;

import java.util.ArrayList;

import com.quicktvui.support.ui.legacy.R;
import com.quicktvui.support.ui.legacy.misc.ChildOnScreenScroller;
import com.quicktvui.support.ui.legacy.misc.ItemDecorations;

public class SingleLineRecyclerView extends TVRecyclerView {

    protected SingleLineLayoutManager singleLineLayoutManager;

    protected int orientation = HORIZONTAL;

    FocusEventListener mFocusListener;


    ChildOnScreenScroller mCustomChildOnScreenScroller;

    OnClickListener innerClickListener;

    protected int defaultSectionPosition = -1;

    protected int activatedPosition = -1;

    protected boolean disableFocusIntercept = false;

    public final static String TAG = "SingleLineRecyclerView";



    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if(onItemClickListener != null) {
            this.innerClickListener = new InnerClickListener(onItemClickListener, this);
        }else{
            this.innerClickListener = null;
        }
    }

    private boolean enableFocusMemory = true;

    public interface OnLayoutManagerCallback{
        void onLayoutCompleted(State state);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(DEBUG) {
            Log.d("SingleLineRecyclerView", "onWindowVisibilityChanged onWindowFocusChanged:"+hasWindowFocus);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(DEBUG) {
            Log.d("SingleLineRecyclerView", "onWindowVisibilityChanged visibility:"+visibility);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(DEBUG) {
            Log.d("SingleLineRecyclerView", "onAttachedToWindow");
        }
    }

    public View getSelectedChild(){
        return mSelectedChild;
    }


    public void setActivatedPosition(int activatedPosition) {
//        final int last = activatedPosition;
        this.activatedPosition = activatedPosition;

        if(getChildCount() > 0){


            for(int i = 0; i < getChildCount(); i ++){
                final View child = singleLineLayoutManager.getChildAt(i);
                if(child != null){
                    callItemStateChangeIfNeed(child,child.isFocused() ? StateListPresenter.STATE_FOCUSED : StateListPresenter.STATE_NORMAL);
                }
            }
            if(activatedPosition > -1) {
                final View newOne = singleLineLayoutManager.findViewByPosition(activatedPosition);
                if(newOne != null){
                    callItemStateChangeIfNeed(newOne,StateListPresenter.STATE_ACTIVATED);
                }
            }
        }
    }

    final class InnerClickListener implements OnClickListener {
        final OnItemClickListener onItemClickListener;
        final RecyclerView parent;

        InnerClickListener(OnItemClickListener onItemClickListener, RecyclerView parent) {
            this.onItemClickListener = onItemClickListener;
            this.parent = parent;
        }

        @Override
        public void onClick(View v) {
            final int position = parent.getChildAdapterPosition(v);
            if(DEBUG) {
                Log.d("SingleLineRecyclerView", "InnerClickListener onClick position :" + position + " view : " + v);
            }
            onItemClickListener.onItemClick(v,position,parent);
        }
    }


    OnLayoutManagerCallback onLayoutManagerCallback;


    public void setOnLayoutManagerCallback(OnLayoutManagerCallback onLayoutManagerCallback) {
        this.onLayoutManagerCallback = onLayoutManagerCallback;
    }


    public void setChildOnScreenScroller(@Nullable ChildOnScreenScroller scroller) {
        this.mCustomChildOnScreenScroller = scroller;
    }

    private int mScrollOffset = 0;

    public SingleLineRecyclerView(Context context, int orientation) {
        super(context);
        this.orientation = orientation;
    }

    public SingleLineRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeFromAttributes(attrs);
    }


    public SingleLineRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeFromAttributes(attrs);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        defaultSectionPosition = -1;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        defaultSectionPosition = mFocusChildPosition;
    }


    public void disableFocusIntercept(boolean disable) {
        this.disableFocusIntercept = disable;
    }

    public void applyChildOnScreenScroller(){
        //TODO
    }

    public int getSelectChildPosition() {
        return defaultSectionPosition;
    }

    @Override
    protected void callItemStateChangeIfNeed(View child, int state) {
//        if(activatedPosition > -1) {
//            final int position = getChildAdapterPosition(child);
//            if (position == activatedPosition) {
//                super.callItemStateChangeIfNeed(child, StateListPresenter.STATE_ACTIVATED);
//                return;
//            }
//        }


        super.callItemStateChangeIfNeed(child, state);
    }


    /**
     * 设置选中的子view,注意此方法只在view已经显示出来以后调用才有效。
     * @param position
     */
    public void setSelectChildPosition(int position){
        if(getChildCount() > 0){

            final View child = singleLineLayoutManager.findViewByPosition(position);
            if(child != null && child != mFocusedView && child != mSelectedChild){
                if(mSelectedChild != null){
                    callItemStateChangeIfNeed(mSelectedChild,StateListPresenter.STATE_NORMAL);
                }
                mSelectedChild = child;
                callItemStateChangeIfNeed(child,StateListPresenter.STATE_SELECTED);
            }
        }
        this.defaultSectionPosition = position;
    }


    public void initializeFromAttributes(AttributeSet attrs){
        if(attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.SingleLineRecyclerView);
            this.orientation = typedArray.getInt(android.support.v7.recyclerview.R.styleable.RecyclerView_android_orientation,orientation);
            Log.d("SingleLineRecyclerView","onInitializeFromAttributes orientation : "+orientation);
            this.mScrollOffset = (int) typedArray.getDimension(R.styleable.SingleLineRecyclerView_focus_scroll_offset,0);
            this.enableFocusMemory = typedArray.getBoolean(R.styleable.SingleLineRecyclerView_enable_focus_memory,true);
            this.disableFocusIntercept =  typedArray.getBoolean(R.styleable.SingleLineRecyclerView_disable_focus_intercept,false);
            typedArray.recycle();
        }
    }




    public void setScrollOffset(int mScrollOffset) {
        this.mScrollOffset = mScrollOffset;
        if(singleLineLayoutManager != null){
            singleLineLayoutManager.childOnScreenScroller.setScrollOffset(mScrollOffset);
        }
    }




    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initLayoutManager();
    }

    private void initLayoutManager() {
        if(singleLineLayoutManager == null) {
            Log.d("SingleLineRecyclerView","initLayoutManager orientation : "+orientation);
            singleLineLayoutManager = onCreateLayoutManager(orientation);
            setLayoutManager(singleLineLayoutManager);
            onAddDefaultItemDecoration();
            singleLineLayoutManager.setFocusEventListener(mFocusListener);
        }
    }

    protected SingleLineLayoutManager onCreateLayoutManager(int orientation){
        return new SingleLineLayoutManager( this,orientation);
    }

    protected boolean isSelectedChildValid(View child){
        boolean b = child != null;
        if(b) {
            b &= child.getVisibility() == View.VISIBLE;
            if (DEBUG) {
                Log.d(TAG, "isSelectedChildValid Visibility():" + b);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                b &= child.isAttachedToWindow();
                if (DEBUG) {
                    Log.d(TAG, "isSelectedChildValid isAttachedToWindow:" + b);
                }
            }
            b &= TVViewUtil.isViewDescendantOf(child, this);
            if (DEBUG) {
                Log.d(TAG, "isSelectedChildValid isViewDescendantOf:" + b);
            }
            final int visibility = child.getWindowVisibility();
            if (DEBUG) {
                Log.d(TAG, "isSelectedChildValid child visibility:" + visibility);
            }
        }

        return b;
    }

    protected void onAddDefaultItemDecoration(){
        addItemDecoration(new ItemDecorations.ListEndBlank(orientation));
    }

    public SingleLineLayoutManager getSingleLineLayoutManager() {
        return singleLineLayoutManager;
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if(DEBUG) {
            Log.d("SingleLineRecyclerView", "addFocusables on defaultSectionPosition：" + defaultSectionPosition+",enableFocusMemory:"+enableFocusMemory);
        }
        if(!hasFocus() && enableFocusMemory) {

            View target  = null;
            if ( defaultSectionPosition > -1) {
                target = singleLineLayoutManager.findViewByPosition(defaultSectionPosition);
//                if (target == null) {
//                    Presenter.ViewHolder vh = findChildViewHolder(defaultSectionPosition);
//                    target = vh != null ? vh.view : null;
//                }

            }
//            Log.e("SingleLineRecyclerView", "findViewByPosition target:"+target+",childCount:"+getChildCount());
            if (!isSelectedChildValid(target)) {
                Log.e("SingleLineRecyclerView", "set target null on !isSelectedChildValid");
                target = null;
            }
            if (target != null) {
                //没有View被选中
                target.addFocusables(views, direction, focusableMode);
                if(DEBUG) {
                    Log.d("SingleLineRecyclerView", "addFocusables on defaultSectionPosition：" + defaultSectionPosition);
                }
                return;
            }else{
                if(DEBUG) {
                    Log.e("SingleLineRecyclerView", "addFocusables on  error on target is null defaultSectionPosition ：" + defaultSectionPosition+",childCount:"+getChildCount());
                }
            }

            if(!isSelectedChildValid(mSelectedChild)){
                mSelectedChild = null;
            }
            final int childCount = getChildCount();
            if (childCount > 0) {
                if (mSelectedChild == null) {
                    //没有View被选中
                    final View v = getChildAt(0);
                    if (v != null && v.getVisibility() == View.VISIBLE) {
                        if(DEBUG) {
                            Log.d("SingleLineRecyclerView", "没有过焦点的列表，焦点给第一个view：" + v);
                        }
                        v.addFocusables(views, direction, focusableMode);
                    }else{
                        super.addFocusables(views, direction, focusableMode);
                    }
                } else {
                    if(DEBUG) {
                        Log.d("SingleLineRecyclerView", "有过焦点的列表，焦点给曾经有过焦点的View：mLastFocusedChild：" + mFocusedView);
                    }
                    mSelectedChild.addFocusables(views, direction, focusableMode);
                }
            }
        }else{
            Log.d("SingleLineRecyclerView", "addFocusables on super");
            super.addFocusables(views, direction, focusableMode);
        }
    }



    @Override
    protected void onDetachedFromWindow() {
        if(DEBUG) {
            Log.d("SingleLineRecyclerView", "onDetachedFromWindow");
        }
        super.onDetachedFromWindow();
    }

    public void setFocusEventListener(FocusEventListener mFocusListener) {
        this.mFocusListener = mFocusListener;
        if(singleLineLayoutManager != null){
            singleLineLayoutManager.setFocusEventListener(mFocusListener);
        }


    }






    @Override
    public View focusSearch(View focused, int direction) {
        View v =  super.focusSearch(focused, direction);
        return v;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public static class FocusEventListener{

        protected View onFocusSearchFailedAtEnd(@NonNull View focused, int direction){
            return focused;
        }

        protected View onInterceptFocusSearch(@NonNull View focused, int direction){
            return null;
        }

        public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state){
            return null;
        }

    }

    protected View onFocusSearchFailedAtEnd(@NonNull View focused, int direction){
        if(!disableFocusIntercept) {
            if (mShakeEndEnable){
                shakeRecycleView();
            }
            return focused;
        }else{
            return null;
        }
    }




    @Override
    protected ITVView.TVOrientation getShakeRecycleViewOrientation(ShakeEndCallback layoutManager) {
        return orientation == VERTICAL ? ITVView.TVOrientation.VERTICAL : ITVView.TVOrientation.HORIZONTAL;
    }

    public static class SingleLineLayoutManager extends LinearLayoutManager {

        private final int orientation;

        final ChildOnScreenScroller.Center childOnScreenScroller;
        private FocusEventListener mFocusListener;
        private final SingleLineRecyclerView mRecyclerView;

        @Override
        public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
            final ChildOnScreenScroller scroller = mRecyclerView.mCustomChildOnScreenScroller != null ? mRecyclerView.mCustomChildOnScreenScroller : childOnScreenScroller;
            if(scroller.requestChildRectangleOnScreen(parent,child,rect,immediate,focusedChildVisible)){
                return true;
            }
            return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
        }


        public SingleLineLayoutManager(SingleLineRecyclerView mRecyclerView, int orientation) {
            super(mRecyclerView.getContext(), orientation, false);
            this.mRecyclerView = mRecyclerView;
            childOnScreenScroller = new ChildOnScreenScroller.Center(orientation);
            childOnScreenScroller.setScrollOffset(mRecyclerView.mScrollOffset);
            this.orientation  = orientation;
        }

        public void setFocusEventListener(FocusEventListener mFocusListener) {
            this.mFocusListener = mFocusListener;
        }


        @Override
        public void onAdapterChanged(@Nullable Adapter oldAdapter, @Nullable Adapter newAdapter) {
            super.onAdapterChanged(oldAdapter, newAdapter);
            mRecyclerView.mSelectedChild = null;
            mRecyclerView.defaultSectionPosition = -1;
        }

        @Override
        public void layoutDecoratedWithMargins(@NonNull View child, int left, int top, int right, int bottom) {

            int position = getPosition(child);

//            if(mRecyclerView.activatedPosition >= 0 && mRecyclerView.activatedPosition == position){
//                mRecyclerView.callItemStateChangeIfNeed(child,StateListPresenter.STATE_ACTIVATED);
//            }
            if(mRecyclerView.innerClickListener != null){
                child.setOnClickListener(mRecyclerView.innerClickListener);
            }
            super.layoutDecoratedWithMargins(child, left, top, right, bottom);
        }



        @Override
        public void onAttachedToWindow(RecyclerView view) {
            super.onAttachedToWindow(view);
        }

        @Override
        public void onLayoutCompleted(State state) {
            super.onLayoutCompleted(state);
            if(mRecyclerView.onLayoutManagerCallback != null){
                mRecyclerView.onLayoutManagerCallback.onLayoutCompleted(state);
            }
        }

        @Override
        public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull State state, @NonNull View child, @Nullable View focused) {
            return super.onRequestChildFocus(parent, state, child, focused);
        }


        @Nullable
        @Override
        public View onInterceptFocusSearch(@NonNull View focused, int direction) {
            View result = null;
            if(mFocusListener != null){
                result = mFocusListener.onInterceptFocusSearch(focused,direction);
            }

            if(result == null) {
                int vector = 0;

                boolean vertical = orientation == RecyclerView.VERTICAL;
                if (vertical) {
                    if (direction == FOCUS_UP) {
                        vector = -1;
                    } else if (direction == FOCUS_DOWN) {
                        vector = 1;
                    }
                } else {
                    if (direction == FOCUS_LEFT) {
                        vector = -1;
                    } else if (direction == FOCUS_RIGHT) {
                        vector = 1;
                    }
                }

                final int focusPosition = mRecyclerView.mFocusChildPosition;

                if (result == null && vector != 0) {
                    int targetPosition = focusPosition + vector;

                    final int itemCount = getItemCount();
                    if (targetPosition > itemCount - 1) {
                        //最后一个
                        if(mFocusListener != null){
                            result =   mFocusListener.onFocusSearchFailedAtEnd(focused,direction);
                        }
                        if(result == null) {
                            result = mRecyclerView.onFocusSearchFailedAtEnd(focused, direction);
                        }
                    }else if(targetPosition >= 0){
                        //目标在此之内，焦点应该拦截在内部
                        final View child = findViewByPosition(targetPosition);

                        if(child != null ){
                            if(child.isFocusable()) {
                                result = child;
                            }
                        }else if(result == null) {
                            result = focused;
                        }
                    }

                }
            }


            return result;
        }

        @Override
        public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state) {
            if(mFocusListener != null){
                final View v = mFocusListener.onFocusSearchFailed(focused,focusDirection,recycler,state);
                if(v != null){
                    return v;
                }
            }
            if(DEBUG) {
                Log.e("SingleList", "onFocusSearchFailed return super)");
            }
            return super.onFocusSearchFailed(focused, focusDirection, recycler, state);
        }

    }

}
