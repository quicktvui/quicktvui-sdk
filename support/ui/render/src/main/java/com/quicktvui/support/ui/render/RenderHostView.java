package com.quicktvui.support.ui.render;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class RenderHostView extends View implements RenderHost{

    RootNode mRootNode;

    public RenderHostView(Context context) {
        super(context);
    }

    public RenderHostView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RenderHostView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mRootNode != null){
            mRootNode.setSize(getMeasuredWidth(),getMeasuredHeight());
        }
    }

    @Override
    public void setRootNode(RenderNode node) {
        if(node instanceof RootNode){
            this.setRootNode((RootNode) node);
        }else{
           Log.e("RenderHostView"," setRootNode error： node must be a instance of RootNode");
        }
    }

    public void setRootNode(RootNode node){
        final RenderNode old = mRootNode;
        if(old != null){
            old.destroy();
        }
        this.mRootNode = node;
        if(getWidth() > 0 && getHeight() > 0){
            mRootNode.setSize(getWidth(),getHeight());
        }
        requestLayout();
    }

    @Override
    public RootNode getRootNode() {
        if(this.mRootNode == null){
            this.mRootNode = new RootNode(this);
        }
        return mRootNode;
    }

    @Override
    public View getHostView() {
        return this;
    }


    @Override
    public <T> T as() {
        return (T) this;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mRootNode != null){
            mRootNode.draw(canvas);
        }
    }
}
