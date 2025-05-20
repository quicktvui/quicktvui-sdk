package com.quicktvui.support.ui;

import android.view.View;

public class ViewUtil {
    public static void reLayoutView(View view,int x,int y,int width,int height){
        view.measure(
                View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY)
        );
        view.layout(x,y,x+width,y+height);
    }

    public static void reLayoutView(View view){
        reLayoutView(view,view.getLeft(),view.getRight(),view.getWidth(),view.getHeight());
    }
}
