package com.quicktvui.support.ui.item.host;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.support.ui.legacy.widget.TVLinearLayout;
import com.quicktvui.support.ui.item.BuildConfig;
import com.quicktvui.support.ui.item.widget.BuilderWidget;
import com.quicktvui.support.ui.render.DrawOrderBridge;
import com.quicktvui.support.ui.render.RenderNode;
import com.quicktvui.support.ui.render.RootNode;

import java.util.HashMap;
import java.util.Map;

public class LinearLayoutHostView extends TVLinearLayout implements ItemHostView , DrawOrderBridge.BridgeView {

    protected int mPreferWidth;

    protected int mPreferHeight;
    DrawOrderBridge mDrawOrderBridge;

    final static boolean DEBUG = BuildConfig.DEBUG;

    public LinearLayoutHostView(Context context) {
        super(context);
        init();
    }

    public LinearLayoutHostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinearLayoutHostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LinearLayoutHostView(Context context, int width, int height) {
        super(context);
        this.mPreferWidth = width;
        this.mPreferHeight = height;
        init();
    }


    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if(mPreferWidth > 0 && mPreferHeight > 0) {
//            final int width = mPreferWidth;
//            final int height = mPreferHeight;
////            changeSizeInternal(width, height);
//            int w = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
//            int h = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
//            super.onMeasure(w,h);
//            getRootNode().setSize(width, height);
//        }else{
//            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//            getRootNode().setSize(getMeasuredWidth(), getMeasuredHeight());
//        }
//    }

    private void init(){
        mDrawOrderBridge = new DrawOrderBridge(this);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getRootNode().setSize(w, h);
//        Log.d("DrawOrderHostView","onSizeChanged h:"+h);
        if(onHostViewSizeChangeListener != null){
            onHostViewSizeChangeListener.onSizeChanged(this,w,h);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        HostViewUtil.INSTANCE.notifyWidgetViewDetached(getRootNode(),this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        HostViewUtil.INSTANCE.notifyWidgetViewAttached(getRootNode(),this);
    }


    @Override
    public void callFocusChange(boolean gainFocus) {
        if(getRootNode() != null) {
            callFocusChange(getRootNode(), gainFocus);
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
        if(getRootNode() != null) {
            callFocusChange(getRootNode(), gainFocus);
        }
    }

    void callFocusChange(RenderNode node,boolean gainFocus){
        for(RenderNode child : node.children()){
            if(child instanceof BuilderWidget){
                ((BuilderWidget) child).onFocusChange(gainFocus);
            }
        }
    }


    @Override
    public ItemHostView addWidget(RenderNode widget) {
        if(widget instanceof BuilderWidget){
            final BuilderWidget i = (BuilderWidget) widget;
            childrenMap().put(i.getName(),i);
        }
        if(DEBUG) {
            Log.v("DrawOrderHostView","addWidget :"+widget.getName());
        }
        getRootNode().add(widget);
        invalidate();
        return this;
    }

    @Deprecated
    @Override
    public ItemHostView addWidgetToBack(RenderNode widget) {

        if(DEBUG) {
            throw new RuntimeException("此方法已过时，请不要调用 widget is :"+widget);
        }
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
//        changeSizeInternal(width,height);
        if(mPreferWidth > 0 || mPreferHeight > 0 && getWidth() != width || getHeight() != height) {
            final ViewGroup.LayoutParams lp = getLayoutParams();
            if(lp != null ){
                lp.width = width;
                lp.height = height;
                requestLayout();
            }
        }
    }


    public BuilderWidget findWidget(String name){
        BuilderWidget result = childrenMap().get(name);
        return result;
    }

    Map<String,BuilderWidget> mChildrenWidgets;

    Map<String,BuilderWidget> childrenMap(){
        if(mChildrenWidgets == null){
            mChildrenWidgets = new HashMap<>();
        }
        return mChildrenWidgets;
    }



    @Override
    public <T> T as() {
        return (T) this;
    }

    @Override
    public <T> T findIWidget(String name) {
        return (T) findWidget(name);
    }



    //通过以下方法来达到控制绘制顺序的目的
    //-------------------------------------开始-------------------

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

        if(DEBUG) {
            Log.d("DrawOrderHostView","onViewAdded");
        }
        mDrawOrderBridge.requestSortZOrder();
    }

    @Override
    public void setRootNode(RenderNode node) {
        if(DEBUG) {
            Log.w("DrawOrderHostView","setRootNode node:"+node);
        }
    }

    @Override
    public RootNode getRootNode() {
        return mDrawOrderBridge.getRootNode();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(mDrawOrderBridge.handleDispatchDraw(canvas)){
            if(DEBUG) {
                Log.v("DrawOrderHostView","draw  mDrawOrderBridge draw");
            }
            return;
        }else{
            if(DEBUG) {
                Log.v("DrawOrderHostView","draw  super draw");
            }
            super.dispatchDraw(canvas);
        }
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if(mDrawOrderBridge.handleIfNeedOnDrawChild(canvas,child,drawingTime)){
            return true;
        }else{
            return super.drawChild(canvas,child,drawingTime);
        }
    }


    @Override
    public void superDrawChild(Canvas canvas, View child) {
        super.drawChild(canvas,child,0);
    }

    @Override
    public void superDraw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void superDispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    //-------------------------------------结束-------------------

    private OnHostViewSizeChangeListener onHostViewSizeChangeListener;

    @Override
    public void setOnHostViewSizeChangeListener(OnHostViewSizeChangeListener listener) {
        this.onHostViewSizeChangeListener = listener;
    }
}
