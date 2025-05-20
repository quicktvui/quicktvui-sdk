package com.quicktvui.support.ui.item.host;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.quicktvui.support.ui.legacy.widget.TVView;
import com.quicktvui.support.ui.item.widget.BuilderWidget;
import com.quicktvui.support.ui.render.RenderNode;

public class SimpleHostView extends TVView implements ItemHostView {


    protected int mPreferWidth;

    protected int mPreferHeight;


    public SimpleHostView(Context context, int width, int height) {
        this(context,null);
        this.mPreferWidth = width;
        this.mPreferHeight = height;
    }

    public SimpleHostView(Context context) {
        this(context,null);
    }

    public SimpleHostView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleHostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mPreferWidth > 0 && mPreferHeight > 0) {
            final int width = mPreferWidth;
            final int height = mPreferHeight;
            changeSizeInternal(width, height);
        }else{
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }


    void changeSizeInternal(int width,int height){
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getFrontRoot().setSize(w, h);
        getBackRoot().setSize(w,h);

        if(onHostViewSizeChangeListener != null){
            onHostViewSizeChangeListener.onSizeChanged(this,w,h);
        }
    }


    FocusChangeListener focusChangeListener;


    @Override
    public void setFocusChangeListener(FocusChangeListener focusChangeListener) {
        this.focusChangeListener = focusChangeListener;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(focusChangeListener != null){
            focusChangeListener.onFocusChanged(this,gainFocus,direction,previouslyFocusedRect);
        }
        if(mBackLayerRoot != null) {
            callFocusChange(mBackLayerRoot, gainFocus);
        }
        if(mFrontLayerRoot != null) {
            callFocusChange(mFrontLayerRoot, gainFocus);
        }
    }

    void callFocusChange(RenderNode node,boolean gainFocus){
        for(RenderNode child : node.children()){
            if(child instanceof BuilderWidget){
                ((BuilderWidget) child).onFocusChange(gainFocus);
            }
        }
    }


    HostRootNode mFrontLayerRoot;
    HostRootNode mBackLayerRoot;


    protected synchronized HostRootNode getFrontRoot(){
        if(mFrontLayerRoot == null){
            mFrontLayerRoot = new HostRootNode(this);
        }
        return mFrontLayerRoot;
    }

    protected synchronized HostRootNode getBackRoot(){
        if(mBackLayerRoot == null){
            mBackLayerRoot = new HostRootNode(this);
            ViewCompat.setBackground(this,mBackLayerRoot);
        }
        return mBackLayerRoot;
    }


    @Override
    public ItemHostView addWidget(RenderNode widget) {
        getFrontRoot().add(widget);
        invalidate();
        return this;
    }

    @Override
    public ItemHostView addWidgetToBack(RenderNode widget) {
        getBackRoot().add(widget);
        invalidate();
        return this;
    }

    @Override
    public View getHostView() {
        return this;
    }

    @Override
    public void changeSize(int width, int height) {
        this.mPreferWidth = width;
        this.mPreferHeight = height;
        changeSizeInternal(width,height);
    }

    @Override
    public <T> T as() {
        return (T) this;
    }

    @Override
    public <T> T findIWidget(String name) {
        return (T) findWidget(name);
    }



    @Override
    public void callFocusChange(boolean gainFocus) {
        if(mBackLayerRoot != null) {
            callFocusChange(mBackLayerRoot, gainFocus);
        }
        if(mFrontLayerRoot != null) {
            callFocusChange(mFrontLayerRoot, gainFocus);
        }
    }

    private OnHostViewSizeChangeListener onHostViewSizeChangeListener;

    @Override
    public void setOnHostViewSizeChangeListener(OnHostViewSizeChangeListener listener) {
        this.onHostViewSizeChangeListener = listener;
    }


    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mFrontLayerRoot != null){
            mFrontLayerRoot.draw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        HostViewUtil.INSTANCE.notifyWidgetViewDetached(getFrontRoot(),this);
        HostViewUtil.INSTANCE.notifyWidgetViewDetached(getBackRoot(),this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        HostViewUtil.INSTANCE.notifyWidgetViewAttached(getFrontRoot(),this);
        HostViewUtil.INSTANCE.notifyWidgetViewAttached(getBackRoot(),this);
    }

    public BuilderWidget findWidget(String name){
        BuilderWidget result = getBackRoot().findWidget(name);
        if(result != null){
            return result;
        }
        result = getFrontRoot().findWidget(name);
        return result;
    }


}
