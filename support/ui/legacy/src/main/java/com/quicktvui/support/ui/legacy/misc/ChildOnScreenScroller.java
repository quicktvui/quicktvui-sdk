package com.quicktvui.support.ui.legacy.misc;

import android.graphics.Rect;
import android.view.View;

import android.support.annotation.NonNull;
import com.quicktvui.support.ui.v7.widget.RecyclerView;

public abstract class ChildOnScreenScroller {


    public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible){
        return false;
    }

    public static class Center extends ChildOnScreenScroller{
        final int orientation ;
        int scrollOffset;

        public Center(int orientation, int scrollOffset) {
            this.orientation = orientation;
            this.scrollOffset = scrollOffset;
        }


        public int getScrollOffset() {
            return scrollOffset;
        }

        public void setScrollOffset(int scrollOffset) {
            this.scrollOffset = scrollOffset;
        }

        public Center(int orientation) {
            this(orientation,0);
        }

        @Override
        public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {

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
                    exeScrollRecyclerView(parent,child,0,dy,immediate);
                    return dy != 0;
                }
            }else{



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

                    exeScrollRecyclerView(parent,child,dx,0,immediate);
                    return dx != 0;
                }

            }

            return false;
        }


        private void exeScrollRecyclerView(RecyclerView parent, View child, int sx, int sy, boolean immediate){
            if (immediate) {
                parent.scrollBy(sx, sy);
            } else {
                parent.smoothScrollBy(sx, sy);
            }
        }

    }

}
