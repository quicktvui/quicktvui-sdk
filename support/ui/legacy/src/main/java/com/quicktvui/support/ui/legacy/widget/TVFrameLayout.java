package com.quicktvui.support.ui.legacy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.quicktvui.support.ui.legacy.FConfig;
import com.quicktvui.support.ui.legacy.view.AttachInfo;
import com.quicktvui.support.ui.legacy.view.ITVFocusGroup;
import com.quicktvui.support.ui.legacy.view.ITVView;
import com.quicktvui.support.ui.legacy.view.ITVViewGroup;
import com.quicktvui.support.ui.legacy.view.InnerViewCode;
import com.quicktvui.support.ui.legacy.view.InnerViewGroupCode;
import com.quicktvui.support.ui.legacy.view.TVFocusScaleExcuter;
import com.quicktvui.support.ui.legacy.view.TVRootView;
import com.quicktvui.support.ui.legacy.view.TVViewUtil;

import com.quicktvui.support.ui.legacy.R;

import com.quicktvui.support.ui.legacy.view.IFloatFocusManager;

import java.util.ArrayList;


public class TVFrameLayout extends FrameLayout implements ITVViewGroup, ITVFocusGroup {

    private final String TAG = "ScaleGroup";

    private int mDuration = TVFocusScaleExcuter.DEFAULT_DURATION;
    private float mFocusScaleX = TVFocusScaleExcuter.DEFAULT_SCALE;
    private float mFocusScaleY = TVFocusScaleExcuter.DEFAULT_SCALE;

    private AttachInfo mAttachInfo = new AttachInfo();

    private TVRootView mFRootView;

    public static boolean DEBUG = FConfig.DEBUG;

    public TVFrameLayout(Context context) {
        super(context);
    }

    public TVFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitializeFromAttributes(attrs);
    }

    public TVFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitializeFromAttributes(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TVFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onInitializeFromAttributes(attrs);
    }




    /**view初始化后调用
     * @param attrs
     */
    protected void onInitializeFromAttributes( AttributeSet attrs){
        if(attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TVFrameLayout);

            setFocusScale(typedArray.getFloat(R.styleable.TVFrameLayout_value_focus_scale, TVFocusScaleExcuter.DEFAULT_SCALE));

            setFocusScaleX(typedArray.getFloat(R.styleable.TVFrameLayout_value_focus_scaleX,mFocusScaleX));
            setFocusScaleY(typedArray.getFloat(R.styleable.TVFrameLayout_value_focus_scaleY,mFocusScaleY));

            setupFloatFocusMargin(typedArray);

            setFloatFocusFocusedAlpha(typedArray.getFloat(R.styleable.TVFrameLayout_float_focus_focused_alpha,1));

            setFocusScaleDuration(typedArray.getInt(R.styleable.TVFrameLayout_duration_focus_scale, TVFocusScaleExcuter.DEFAULT_DURATION));

            typedArray.recycle();
        }


    }


    void setupFloatFocusMargin(TypedArray typedArray){

        final Rect rect = mAttachInfo.mFloatFocusMarginRect;

//        int left = typedArray.getDimensionPixelSize(R.styleable.FFrameLayout_float_focus_marginLeft,0);
//        int right = typedArray.getDimensionPixelSize(R.styleable.FFrameLayout_float_focus_marginRight,0);
//        int top = typedArray.getDimensionPixelSize(R.styleable.FFrameLayout_float_focus_marginTop,0);
//        int bottom = typedArray.getDimensionPixelSize(R.styleable.FFrameLayout_float_focus_marginBottom,0);

        int left = ConvertUtil.convertPixel(typedArray, R.styleable.TVFrameLayout_float_focus_marginLeft, 0);
        int right = ConvertUtil.convertPixel(typedArray, R.styleable.TVFrameLayout_float_focus_marginRight, 0);
        int top = ConvertUtil.convertPixel(typedArray, R.styleable.TVFrameLayout_float_focus_marginTop, 0);
        int bottom = ConvertUtil.convertPixel(typedArray, R.styleable.TVFrameLayout_float_focus_marginBottom, 0);

        rect.set(left,top,right,bottom);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFRootView = TVRootView.findRootView(this);
    }


    @Override
    public void requestChildFocus(View child, View focused) {
        if(child instanceof ITVView && focused instanceof ITVView)
            requestChildMoveFloatFocus((ITVView)child,(ITVView) focused);
        super.requestChildFocus(child, focused);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        onHandleFocusScale(gainFocus, direction, previouslyFocusedRect);
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    /**-------------------------------------------------------------------------------------**/

    @Override
    public void onHandleFocusScale(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if(isFocusable() && mFocusScaleX != 1 || mFocusScaleY != 1)
            TVFocusScaleExcuter.handleOnFocusChange(this, gainFocus, mFocusScaleX,mFocusScaleY, mDuration);
    }

    @Override
    public Rect getFloatFocusMarginRect() {
        return mAttachInfo.mFloatFocusMarginRect;
    }

    @Override
    public ITVView getFloatFocusFocusableView() {
        return this;
    }

    /**{@link ITVView#setFocusScale(float)}
     * @param scale 缩放倍数
     */
    @Override
    public void setFocusScale(float scale){
        this.mFocusScaleX = scale;
        this.mFocusScaleY = scale;
    }

    /**
     * 设置View获得焦点的放大倍数
     *
     */
    @Override
    public void setFocusScaleX(float scale) {
        this.mFocusScaleX = scale;
    }

    /**
     * 设置View获得焦点的放大倍数
     *
     */
    @Override
    public void setFocusScaleY(float scale) {
        this.mFocusScaleY = scale;
    }


    /**{@link ITVView#setFocusScaleDuration(int)}
     * @param duration 缩放动画时长 单位：毫秒
     */
    @Override
    public void setFocusScaleDuration(int duration){
        this.mDuration = duration;
    }


    @Override
    public float getFocusScaleX() {
        return mFocusScaleX;
    }

    @Override
    public float getFocusScaleY() {
        return mFocusScaleY;
    }

    /**-------------------------------------------------------------------------------------**/

    @Override
    public View getView() {
        return this;
    }

    @Override
    public String toString() {
        if(DEBUG) {
            if (getTag() != null) {
                return super.toString() +" view tag is "+getTag();
            }
        }
        return super.toString();
    }

    /**
     * 请求移动浮动焦点到对应view
     *
     * @param child
     * @param focused
     */
    @Override
    public void requestChildMoveFloatFocus(ITVView child, ITVView focused) {
        InnerViewGroupCode.handleChildMoveFloatFocusForLayout(this,child,focused);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        //只有当view本身是一个focusable的view时，才需要移动焦点
        if( isFocusable()){
            InnerViewCode.handleFloatFocusMove(this);
        }
        boolean  handle = super.requestFocus(direction, previouslyFocusedRect);

        return handle;
    }

    @Override
    public TVRootView getFRootView() {
        return mFRootView;
    }

    @Override
    public AttachInfo getAttachInfo() {
        return mAttachInfo;
    }

    @Override
    public IFloatFocusManager getFloatFocusManager() {
        return getFRootView().getFloatFocusManager();
    }

    @Override
    public void setFloatFocusFocusedAlpha(float alpha) {
        mAttachInfo.setFloatFocusFocusedAlpha(alpha);
    }




    public int nextSpecifiedFocusUpIndex = -1;
    public int nextSpecifiedFocusDownIndex = -1;
    public int nextSpecifiedFocusLeftIndex = -1;
    public int nextSpecifiedFocusRightIndex = -1;

    public int nextSpecifiedFocusUpId = -1;
    public int nextSpecifiedFocusDownId = -1;
    public int nextSpecifiedFocusLeftId = -1;
    public int nextSpecifiedFocusRightId = -1;

    @Override
    public void setNextSpecifiedFocusIndex(int index) {
        this.nextSpecifiedFocusDownIndex = index;
        this.nextSpecifiedFocusLeftIndex = index;
        this.nextSpecifiedFocusRightIndex = index;
        this.nextSpecifiedFocusUpIndex = index;
    }

    @Override
    public void setNextSpecifiedFocusIndex(int direction, int index){
        switch (direction){
            case FOCUS_DOWN:
                this.nextSpecifiedFocusDownIndex = index;
                break;
            case FOCUS_UP :
                this.nextSpecifiedFocusUpIndex = index;
                break;
            case FOCUS_LEFT :
                this.nextSpecifiedFocusLeftIndex = index;
                break;

            case FOCUS_RIGHT :
                this.nextSpecifiedFocusRightIndex = index;
                break;
        }
    }


    private final ArrayList<View> tempFocusList = new ArrayList();

    @Override
    public View getNextSpecifiedFocus(View focused, int direction) {
        int specifiedIndex = -1;
        int nextSpecifiedId = -1;
        switch (direction){
            case FOCUS_DOWN:
                specifiedIndex = this.nextSpecifiedFocusDownIndex;
                nextSpecifiedId = this.nextSpecifiedFocusDownId;
                break;
            case FOCUS_UP :
                specifiedIndex = this.nextSpecifiedFocusUpIndex;
                nextSpecifiedId = this.nextSpecifiedFocusUpId;
                break;
            case FOCUS_LEFT :
                specifiedIndex =  this.nextSpecifiedFocusLeftIndex ;
                nextSpecifiedId = this.nextSpecifiedFocusLeftId;
                break;

            case FOCUS_RIGHT :
                specifiedIndex = this.nextSpecifiedFocusRightIndex;
                nextSpecifiedId = this.nextSpecifiedFocusRightId;
                break;
        }
        if(specifiedIndex >= 0 && nextSpecifiedId < 0){
            try {
                if (specifiedIndex >= 0 && specifiedIndex < getChildCount()) {
                    View target = getChildAt(specifiedIndex);

                    target.addFocusables(tempFocusList,direction);
                    if(tempFocusList.size() > 0){
                        return tempFocusList.get(0);
                    }
                }
            }finally {
                tempFocusList.clear();
            }
        }

        if(nextSpecifiedId > 0){
            return findViewById(nextSpecifiedId);
        }

        return null;
    }


    @Override
    public int getNextSpecifiedFocusUpId() {
        return nextSpecifiedFocusUpId;
    }

    @Override
    public void setNextSpecifiedFocusUpId(int i) {
        this.nextSpecifiedFocusUpId = i;
    }

    @Override
    public int getNextSpecifiedFocusDownId() {
        return nextSpecifiedFocusDownId;
    }

    @Override
    public void setNextSpecifiedFocusDownId(int i) {
        this.nextSpecifiedFocusDownId = i;
    }

    @Override
    public int getNextSpecifiedFocusLeftId() {
        return nextSpecifiedFocusLeftId;
    }

    @Override
    public void setNextSpecifiedFocusLeftId(int i) {
        this.nextSpecifiedFocusLeftId = i;
    }

    @Override
    public int getNextSpecifiedFocusRightId() {
        return this.nextSpecifiedFocusRightId;
    }

    @Override
    public void setNextSpecifiedFocusRightId(int i) {
        this.nextSpecifiedFocusRightId = i;
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if(DEBUG) {
            TVViewUtil.debugPerformance(this,"onMeasure width is "+getWidth()+" height is "+getHeight());
        }
        super.onMeasure(widthSpec, heightSpec);


    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(DEBUG){
            TVViewUtil.debugPerformance(this,"onLayout");
        }
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    public void requestLayout() {
        if(DEBUG){
            TVViewUtil.debugPerformance(this,"requestLayout");
        }
        super.requestLayout();

    }


}
