package com.quicktvui.support.ui.render;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class DrawableNode extends RenderNode{

    Drawable mDrawable;

    public DrawableNode(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if(mDrawable != null) {
            mDrawable.setBounds(bounds);
            invalidateSelf();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(mDrawable != null)
            mDrawable.draw(canvas);
    }


    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        if(mDrawable != null && !getBounds().isEmpty()){
            mDrawable.setBounds(getBounds());
        }
        invalidateSelf();
    }
}
