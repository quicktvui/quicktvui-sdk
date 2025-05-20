package com.quicktvui.support.ui.largelist;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.hippyext.RenderUtil;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.util.ArrayList;

import com.quicktvui.support.ui.v7.widget.LinearSmoothScroller;
import com.quicktvui.support.ui.v7.widget.RecyclerView;
import com.quicktvui.support.ui.legacy.misc.ItemDecorations;
import com.quicktvui.support.ui.legacy.widget.SingleLineRecyclerView;

public class EasyListView extends SingleLineRecyclerView {

    private int pendingFocusPosition = -1;
    private int pendingScrollPosition = -1;
    private AdvanceCenterScroller mScroller;
    //    private int childSize = 0;
    private static final String TAG = "EasyListViewLog";
    protected int selectPos = -1;
    protected int scrollType = AdvanceCenterScroller.SCROLL_TYPE_CENTER;
    private boolean block = false;

    public EasyListView(@NonNull Context context, boolean vertical) {
        super(context, vertical ? SingleLineRecyclerView.VERTICAL : SingleLineRecyclerView.HORIZONTAL);
    }

    public EasyListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
//        Log.i(TAG,"EasyListView addFocusables :direction : "+direction);
        super.addFocusables(views, direction, focusableMode);
    }

    public void initLayoutManager() {
        this.onFinishInflate();
    }

    public void blockFocus() {
        final View root = getRootView();
        if (root instanceof ViewGroup) {
            this.block = true;
            ((ViewGroup) root).setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        }
    }

    public void releaseFocus() {
        if (block) {
            final View root = getRootView();
            if (root instanceof ViewGroup) {
                ((ViewGroup) root).setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
            }
            this.block = false;
        }
    }

    @Override
    protected SingleLineLayoutManager onCreateLayoutManager(int orientation) {
        mScroller = new AdvanceCenterScroller(orientation);
        setChildOnScreenScroller(mScroller);
        return new EasyLinearLayoutManager(this,
                orientation);
    }

    public void setChildSize(int childSize) {
        if (childSize <= 0) {
            throw new IllegalArgumentException("childSize cant be null, please make sure you call initParams with a valid itemWidth and itemHeight");
        } else {
//            Log.w(TAG,"setChildSize:"+childSize);
        }
//        this.childSize = childSize;
    }

    public void setScrollType(int type) {
//        Log.w(TAG,"setScrollType:"+type);
        this.scrollType = type;
        if (scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
            addItemDecoration(new ItemDecorations.ListEndBlank(2000, orientation));
        }
        if (mScroller != null) {
            mScroller.setScrollType(type);
        }
    }

    public int getScrollType() {
        return scrollType;
    }

    public EasyLinearLayoutManager getEasyLayoutManager() {
        return (EasyLinearLayoutManager) getLayoutManager();
    }

    public void setPendingFocusPosition(int pos) {
        this.pendingFocusPosition = pos;
        setScrollPosition(pos);
    }

    public void markPendingFocusPosition(int pos) {
        this.pendingFocusPosition = pos;
//        setScrollPosition(pos);
    }

    protected boolean requestSelectChildPosition(){
        if(hasFocus()){
            return false;
        }
        if(getSelectChildPosition() > -1 && getEasyLayoutManager() != null){
            final int pos = getSelectChildPosition();
            final View v = getEasyLayoutManager().findViewByPosition(pos);
            if (v != null) {
                return v.requestFocus();
            }
        }
        return false;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if(LogUtils.isDebug()) {
            Log.i(TAG, "requestFocus direction :" + direction);
        }
        if(requestSelectChildPosition()){
            Log.i(TAG,"requestFocus by requestSelectChildPosition");
            return true;
        }else {
            return super.requestFocus(direction, previouslyFocusedRect);
        }
    }

    void setPendingScrollPosition(int pos) {
        this.pendingScrollPosition = pos;
    }

    public void setFocusPosition(int pos) {
//        Log.e(TAG,"setFocusPosition pos:"+pos+",getVisible:"+getVisibility()+",size:"+getWidth());
//        scrollToPosition(pos);
        setScrollPosition(pos);
        if (!requestChildFocusReal(pos)) {
//            Log.d(TAG,"setFocusPosition setPendingFocusPosition:"+pos+",id:"+this+",pos:"+pos);
            markPendingFocusPosition(pos);
        } else {
//            Log.d(TAG,"setFocusPosition findFocus requestFocus directly"+",id:"+this+",pos:"+pos);
        }
    }

    public void setScrollPosition(int pos) {
//        Log.d(TAG,"setScrollPosition :"+pos+",id:"+getId());
        if (getWidth() > 0 && getHeight() > 0) {
            if (scrollType == AdvanceCenterScroller.SCROLL_TYPE_CENTER) {
                setPendingScrollPosition(pos);
            }
            scrollToPositionWithScrollType(pos);
            if (getParent() != null)
                RenderUtil.requestNodeLayout((View) getParent());
        } else {
            setPendingScrollPosition(pos);
            if (getLayoutManager() != null) {
                getLayoutManager().scrollToPosition(pos);
            }
//            Log.d(TAG,"setPendingScrollPosition waiting for parent size:"+pos+",id:"+getId());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && getLayoutManager() != null && pendingScrollPosition > -1) {
//            Log.d(TAG,"onSizeChanged pendingScrollPosition : "+pendingScrollPosition+",start scrollToPositionWithScrollType!");
            scrollToPositionWithScrollType(pendingScrollPosition);
        }
    }

    public void scrollToPositionWithScrollType(int pos) {
        //居中scrollToPositionWithScrollType
        if (getLayoutManager() != null) {
            if (getScrollType() == AdvanceCenterScroller.SCROLL_TYPE_CENTER) {
                int offset = 0;
                final View v = getEasyLayoutManager().findViewByPosition(pos);
                int childSize = 0;
                if (v != null) {
                    childSize = v.getWidth();
                }
                if (childSize > 0) {
                    if (orientation == HORIZONTAL) {
                        offset = (int) ((getWidth() - childSize) * 0.5f);

                    } else {
                        offset = (int) ((getHeight() - childSize) * 0.5f);
                    }
                }
//                Log.d(TAG,"scrollToPositionWithScrollType pos "+pos+",offset:"+offset+",childSize:"+childSize+",getWidth:"+getWidth());
                getEasyLayoutManager().scrollToPositionWithOffset(pos, offset);
            } else {
                getEasyLayoutManager().scrollToPositionWithOffset(pos, 0);
            }

        } else {

        }
    }

    public boolean requestChildFocus(int pos) {
        setFocusPosition(pos);
        return false;
    }

    public boolean requestChildFocusDerectly(int pos) {
        requestChildFocusReal(pos);
        return false;
    }

    private boolean requestChildFocusReal(int pos) {
        if (getVisibility() == View.VISIBLE && getLayoutManager() != null) {
            final View v = getEasyLayoutManager().findViewByPosition(pos);
            if (v != null && v.getVisibility() == VISIBLE && v.getWidth() > 0 && v.getHeight() > 0) {
//                Log.d(TAG,"requestChildFocusReal :"+pos+",id:"+this+",width:"+getWidth()+",height:"+getHeight());
                return v.requestFocus();
            }
        }
        return false;
    }


    @Override
    public void setSelectChildPosition(int position) {
        super.setSelectChildPosition(position);
        //Log.d(TAG,"------setSelectChildPosition position:"+position);
        this.selectPos = position;
        if (getLayoutManager() != null) {
            final View view = getEasyLayoutManager().findViewByPosition(position);
            for (int i = 0; i < getEasyLayoutManager().getChildCount(); i++) {
                final View child = getEasyLayoutManager().getChildAt(i);
                if (child instanceof PendingItemView) {
                    ((PendingItemView) child).setSingleSelect(child == view);
                }
            }
        }
    }

    public void clearFocusMemory() {
//        Log.d(TAG,"clearFocusMemory:this"+this);
        this.defaultSectionPosition = -1;
        this.mSelectedChild = null;
    }

    public void setFocusMemoryPosition(int position) {
        this.defaultSectionPosition = position;
    }

    public View onInterceptFocusSearch(@NonNull View focused, int direction) {
        return null;
    }

    public void destroy() {
        clearFocusMemory();
        setLayoutManager(null);
    }

    private static final class StickyTopicScroller extends LinearSmoothScroller {
        public StickyTopicScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            //原本的返回值
//            return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
            //修改，返回item置顶的偏移量
            return boxStart - viewStart;
        }

    }

    public static class EasyLinearLayoutManager extends SingleLineLayoutManager {

        EasyListView listView;

        public EasyLinearLayoutManager(EasyListView singleLineRecyclerView, int orientation) {
            super(singleLineRecyclerView, orientation);
            this.listView = singleLineRecyclerView;
        }

        public void smoothScrollToPositionTop(RecyclerView recyclerView, int position) {
//            LinearSmoothScroller linearSmoothScroller =
//                    new StickyTopicScroller(recyclerView.getContext());
//            linearSmoothScroller.setTargetPosition(position);
//            startSmoothScroll(linearSmoothScroller);
            // 关闭滚动动画
            scrollToPositionWithOffset(position, 0);
        }

        @Override
        public void layoutDecoratedWithMargins(@NonNull View child, int left, int top, int right, int bottom) {
            super.layoutDecoratedWithMargins(child, left, top, right, bottom);
            listView.onLayoutItem(child, getPosition(child));
            final int pos = getPosition(child);
            final boolean select = pos == listView.selectPos;
            if (child instanceof PendingItemView) {
                ((PendingItemView) child).setSingleSelect(select);
            }
            if (select) {
//                Log.d(TAG, "layoutDecoratedWithMargins setSelect true，pos:"+pos);
            }
        }


        @Override
        public void onLayoutCompleted(State state) {
            super.onLayoutCompleted(state);
//            Log.e(TAG,"onLayoutChildren call!!!!! ");
            if (listView != null) {
                listView.onLayoutCompleted(state);
            }
        }

        @Override
        public void onLayoutChildren(Recycler recycler, State state) {
            super.onLayoutChildren(recycler, state);
//            Log.e(TAG,"onLayoutChildren call!!!!! listView.pendingScrollPosition :"+listView.pendingScrollPosition);
            if (listView != null) {
                listView.onLayoutChildren(state);
            }
            for (int i = 0; i < getItemCount(); i++) {
                final View child = getChildAt(i);
                if (child != null) {
                    final int pos = getPosition(child);
                    if (listView.pendingFocusPosition > -1 && pos == listView.pendingFocusPosition) {
                        listView.pendingFocusPosition = -1;
                        //Log.e(TAG, "layoutDecoratedWithMargins find pendingFocusPosition do Focus:" + pos + ",this:" + listView);
                        scrollToChildForAdjust(pos, child);
                        listView.releaseFocus();
                        child.requestFocus();
                    } else if (listView.pendingScrollPosition > -1 && pos == listView.pendingScrollPosition) {
                        listView.pendingScrollPosition = -1;
                        scrollToChildForAdjust(pos, child);
                    }
                }
            }


        }

        void scrollToChildForAdjust(int pos, View v) {
            if (v != null && listView.scrollType == AdvanceCenterScroller.SCROLL_TYPE_CENTER) {
                int offset = 0;
                int childSize = v.getWidth();
                if (childSize > 0) {
                    if (listView.orientation == HORIZONTAL) {
                        offset = (int) ((getWidth() - childSize) * 0.5f);

                    } else {
                        offset = (int) ((getHeight() - childSize) * 0.5f);
                    }
                    scrollToPositionWithOffset(pos, offset);
                    RenderUtil.requestNodeLayout((View) listView.getParent());
//                    Log.d(TAG,"scrollToChildForAdjust offset:"+offset);
                } else {
//                    Log.e(TAG,"scrollToChildForAdjust fail childSize < 0");
                }
            }

        }

        @Override
        public void onItemsRemoved(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
            super.onItemsRemoved(recyclerView, positionStart, itemCount);
        }

        @Override
        public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state) {
            return super.onFocusSearchFailed(focused, focusDirection, recycler, state);
        }

        @Nullable
        @Override
        public View onInterceptFocusSearch(@NonNull View focused, int direction) {
            View v = listView.onInterceptFocusSearch(focused, direction);
            if (v == null) {
                v = super.onInterceptFocusSearch(focused, direction);
            }

            return v;
        }
    }

    protected void onLayoutCompleted(State state) {

    }

    protected void onLayoutChildren(State state) {

    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
    }

    protected void onLayoutItem(View child, int pos) {

    }


}
