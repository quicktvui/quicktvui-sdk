package com.quicktvui.support.ui.largelist;

import android.graphics.Rect;
import android.view.View;

import android.support.annotation.NonNull;
import com.quicktvui.support.ui.v7.widget.RecyclerView;

import com.quicktvui.support.ui.legacy.misc.ChildOnScreenScroller;

public class AdvanceCenterScroller extends ChildOnScreenScroller {


    final int orientation;
    int scrollOffset;
    int scrollType = SCROLL_TYPE_CENTER;
    public static final int SCROLL_TYPE_CENTER = 0;
    public static final int SCROLL_TYPE_PAGE = 1;
    public static final int SCROLL_TYPE_NONE = -1;
    public static final String TAG = "AdvanceCenterScroller";

    public AdvanceCenterScroller(int orientation, int scrollOffset) {
        this.orientation = orientation;
        this.scrollOffset = scrollOffset;
    }


    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }


    public AdvanceCenterScroller(int orientation) {
        this(orientation, 0);
    }

    public void setScrollType(int scrollType) {
        this.scrollType = scrollType;
    }

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {

        if (scrollType == SCROLL_TYPE_CENTER) {
            if (orientation == RecyclerView.VERTICAL) {
                final int parentTop = parent.getPaddingTop();
                final int childTop = child.getTop() + rect.top - child.getScrollY();

                final int parentCenter = (int) (parentTop + parent.getHeight() * 0.5f);
                View focused = null;

                if (focused == null) {
                    focused = child.findFocus();
                    focused = focused != null ? focused : child;
                }
                if (focused != null) {
                    final int childCenter = (int) (childTop + focused.getHeight() * 0.5f);

                    final int dy = childCenter - parentCenter + scrollOffset;
                    exeScrollRecyclerView(parent, child, 0, dy, immediate);
                    return dy != 0;
                }
            } else {

                final int parentLeft = parent.getPaddingLeft();
                final int childLeft = child.getLeft() + rect.left - child.getScrollX();

                final int parentCenter = (int) (parentLeft + parent.getWidth() * 0.5f);
                View focused = null;

                if (focused == null) {
                    focused = child.findFocus();
                    focused = focused != null ? focused : child;
                }

                if (focused != null) {
                    final int childCenter = (int) (childLeft + focused.getWidth() * 0.5f);

                    final int dx = childCenter - parentCenter + scrollOffset;

                    exeScrollRecyclerView(parent, child, dx, 0, immediate);
                    return dx != 0;
                }

            }
        } else {

            if (orientation == RecyclerView.HORIZONTAL) {
//                final int parentLeft = parent.getPaddingLeft();
//                final int parentWidth = parent.getWidth();
//                final int parentRight = parentLeft + parentWidth;
//
//                final int childLeft = child.getLeft() + rect.left - child.getScrollX();
//                final int childRight = childLeft + child.getWidth();
//
//
//
//                int dx = 0;
//                if(childLeft >= parentRight){//向右翻页
//                    dx = parentWidth;
//
//                }else if(childRight <= parentLeft){//向前翻页
//                   dx = parentWidth * -1;
//
//                }else{
//                    //不滚动
//
//                }
//                Log.d(TAG,"requestChildRectangleOnScreen parentLeft :"+parentLeft+",parentWidth:"+parentWidth+",childLeft:"+childLeft+",childRight:"+childRight+",parentRight:"+parentRight+",dx:"+dx);
//                exeScrollRecyclerView(parent,child,dx,0,immediate);
//                return dx != 0;
                return true;
            }

        }
        return true;
    }


    private void exeScrollRecyclerView(RecyclerView parent, View child, int sx, int sy, boolean immediate) {
        if (immediate) {
            parent.scrollBy(sx, sy);
        } else {
            parent.smoothScrollBy(sx, sy);
        }
    }

}
