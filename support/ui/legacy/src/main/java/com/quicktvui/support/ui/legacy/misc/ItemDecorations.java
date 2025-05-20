package com.quicktvui.support.ui.legacy.misc;

import android.graphics.Rect;
import android.view.View;

import android.support.annotation.NonNull;
import com.quicktvui.support.ui.v7.widget.RecyclerView;

public class ItemDecorations {

    public static class SimpleBetweenItem extends RecyclerView.ItemDecoration {

        final int itemSpace;
        final boolean horizontal;

        public SimpleBetweenItem(int itemSpace,boolean horizontal) {
            this.itemSpace = itemSpace;
            this.horizontal = horizontal;
        }

        public SimpleBetweenItem(int itemSpace) {
            this.itemSpace = itemSpace;
            this.horizontal = true;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if(horizontal) {
                outRect.right = itemSpace;
            }else{
                outRect.bottom = itemSpace;
            }
        }
    }


    public static class ListEndBlank extends RecyclerView.ItemDecoration{
        final int blank;
        final int orientation;


        public ListEndBlank(int blank, int orientation) {
            this.blank = blank;
            this.orientation = orientation;
        }

        public ListEndBlank(int orientation) {
            this(100,orientation);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position;
            try{
                position = parent.getChildAdapterPosition(view);
            }catch (Throwable t){
                position = -1;
            }
            if(position > 0) {
                if (orientation == RecyclerView.HORIZONTAL) {
                    if(position >= state.getItemCount() - 1){
                        outRect.right = blank;
                    }
                } else {
                    if(position >= state.getItemCount() - 1){
                        outRect.bottom = blank;
                    }
                }
            }
        }
    }


}
