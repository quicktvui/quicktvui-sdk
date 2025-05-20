package com.quicktvui.support.ui.legacy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.quicktvui.support.ui.legacy.R;
import com.quicktvui.support.ui.legacy.FConfig;
import com.quicktvui.support.ui.legacy.view.AttachInfo;
import com.quicktvui.support.ui.legacy.view.TVFocusScaleExcuter;
import com.quicktvui.support.ui.legacy.view.TVRootView;
import com.quicktvui.support.ui.legacy.view.TVViewUtil;
import com.quicktvui.support.ui.legacy.view.ITVView;
import com.quicktvui.support.ui.legacy.view.IFloatFocusManager;
import com.quicktvui.support.ui.legacy.view.InnerViewCode;

public class TVView extends View implements ITVView {

    private int mDuration = TVFocusScaleExcuter.DEFAULT_DURATION;

    private float mFocusScaleX = TVFocusScaleExcuter.DEFAULT_SCALE;
    private float mFocusScaleY = TVFocusScaleExcuter.DEFAULT_SCALE;

    public static boolean DEBUG = FConfig.DEBUG;


    private TVRootView mFRootView;

    private AttachInfo mAttachInfo = new AttachInfo();

    public TVView(Context context) {
        super(context);
    }

    public TVView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitializeFromAttributes(attrs);
    }

    public TVView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitializeFromAttributes(attrs);


    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public FImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        onInitializeFromAttributes(attrs);
//    }



    /**view初始化后调用
     * @param attrs
     */
    protected void onInitializeFromAttributes( AttributeSet attrs){
        if(attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TVImageView);

            setFocusScale(typedArray.getFloat(R.styleable.TVImageView_value_focus_scale, TVFocusScaleExcuter.DEFAULT_SCALE));

            setFocusScaleX(typedArray.getFloat(R.styleable.TVImageView_value_focus_scaleX,mFocusScaleX));
            setFocusScaleY(typedArray.getFloat(R.styleable.TVImageView_value_focus_scaleY,mFocusScaleY));

            setupFloatFocusMargin(typedArray);

            setFloatFocusFocusedAlpha(typedArray.getFloat(R.styleable.TVImageView_float_focus_focused_alpha,1));

            setFocusScaleDuration(typedArray.getInt(R.styleable.TVImageView_duration_focus_scale, TVFocusScaleExcuter.DEFAULT_DURATION));

            typedArray.recycle();
        }

    }

    void setupFloatFocusMargin(TypedArray typedArray){
        final Rect rect = mAttachInfo.mFloatFocusMarginRect;

//        int left = typedArray.getDimensionPixelSize(R.styleable.FImageView_float_focus_marginLeft,0);
//        int right = typedArray.getDimensionPixelSize(R.styleable.FImageView_float_focus_marginRight,0);
//        int top = typedArray.getDimensionPixelSize(R.styleable.FImageView_float_focus_marginTop,0);
//        int bottom = typedArray.getDimensionPixelSize(R.styleable.FImageView_float_focus_marginBottom,0);


        int left = ConvertUtil.convertPixel(typedArray, R.styleable.TVImageView_float_focus_marginLeft, 0);
        int right = ConvertUtil.convertPixel(typedArray, R.styleable.TVImageView_float_focus_marginRight, 0);
        int top = ConvertUtil.convertPixel(typedArray, R.styleable.TVImageView_float_focus_marginTop, 0);
        int bottom = ConvertUtil.convertPixel(typedArray, R.styleable.TVImageView_float_focus_marginBottom, 0);

        rect.set(left,top,right,bottom);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFRootView = TVRootView.findRootView(this);
    }



    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        onHandleFocusScale(gainFocus,direction,previouslyFocusedRect);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public View getView() {
        return this;
    }




    /**-------------------------------------------------------------------------------------**/

    @Override
    public void onHandleFocusScale(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if(isFocusable() && mFocusScaleX != 1 || mFocusScaleY != 1) {
            TVFocusScaleExcuter.handleOnFocusChange(this, gainFocus, mFocusScaleX, mFocusScaleY, mDuration);
        }
    }

    @Override
    public Rect getFloatFocusMarginRect() {
        return mAttachInfo.mFloatFocusMarginRect;
    }


    @Override
    public TVRootView getFRootView() {
        return mFRootView;
    }

    @Override
    public AttachInfo getAttachInfo() {
        return mAttachInfo;
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
    public String toString() {
        if(Companion.getDEBUG()) {
            if (getTag() != null) {
                return super.toString() +" view tag is "+getTag();
            }
        }
        return super.toString();
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
    public IFloatFocusManager getFloatFocusManager() {
        return getFRootView().getFloatFocusManager();
    }

    @Override
    public void setFloatFocusFocusedAlpha(float alpha) {
        mAttachInfo.setFloatFocusFocusedAlpha(alpha);
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
