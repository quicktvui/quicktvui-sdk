package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.quicktvui.support.ui.item.utils.SharedDrawableManager;

import com.quicktvui.support.ui.item.R;

public class RoundShadowWidget extends BuilderWidget<RoundShadowWidget.Builder> implements IShadowWidget{


    ShadowImpl mShadowImpl;
    private int contentWidth =0;


    public static class Builder extends BuilderWidget.Builder<RoundShadowWidget>{

        int shadowSize = SHADOW_SIZE_XX;

        boolean isFocusActive =true;
        boolean isDefaultActive = true;

        public Builder setShadowSize(int shadowSize) {
            this.shadowSize = shadowSize;
            return this;
        }

        public Builder setFocusActive(boolean focusActive) {
            isFocusActive = focusActive;
            return this;
        }

        public Builder setDefaultActive(boolean defaultActive) {
            isDefaultActive = defaultActive;
            return this;
        }

        public Builder(Context context) {
            super(context);
        }


        public RoundShadowWidget build(){
            return new RoundShadowWidget(this);
        }
    }


    public static Builder builder(Context context){
        return new Builder(context);
    }


    public static final int SHADOW_SIZE_X = 0;
    public static final int SHADOW_SIZE_XX = 1;



    @Override
    protected void onCreate () {
        super.onCreate();
    }


    public RoundShadowWidget(Builder builder){
        super(builder);
        setSize(MATCH_PARENT,MATCH_PARENT);
        initShadow(builder.shadowSize,builder.isFocusActive,builder.isDefaultActive);
    }


    void initShadow(int shadowSize,boolean isFocusActive,boolean isDefaultActive){
        switch (shadowSize){
            case SHADOW_SIZE_X :
                mShadowImpl = new ShadowX(isDefaultActive,isFocusActive);
                break;
            default:
                mShadowImpl = new ShadowXX(isDefaultActive,isFocusActive);
                break;
        }
    }


    @Override
    public String getName() {
        return NAME;
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);


        final int contentWidth = width();
        final int contentHeight = height();

        if(mShadowImpl != null){
            mShadowImpl.setContentSize(contentWidth,contentWidth);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {

        contentWidth = mParent.width();

       if(mShadowImpl != null){
           mShadowImpl.draw(canvas);
       }
    }

    @Override
    public void setFocusShadowVisible(boolean focusShadowVisible) {
        if(mShadowImpl != null){
            mShadowImpl.setFocused(focusShadowVisible);
        }
    }

    @Override
    public void setDefaultShadowVisible(boolean defaultShadowVisible) {
        if (mShadowImpl!=null){
            mShadowImpl.setDefaultShadowActive(defaultShadowVisible);
        }
    }


    abstract class ShadowImpl {

        NinePatchShadowDrawable defaultDrawable;
        NinePatchShadowDrawable focusDrawable;

        boolean isDefaultActive = true;
        boolean isFocusActive = true;

        boolean isFocused = false;
        private boolean defaultShadowVisible = false;

        void setFocused(boolean b){
            this.isFocused = b;
        }

        ShadowImpl(boolean isDefaultActive,boolean isFocusActive) {
            this.isFocusActive = isFocusActive;
            this.isDefaultActive = isDefaultActive;
            init();
        }

        void init(){
            if(isDefaultActive) {
                defaultDrawable = new NinePatchShadowDrawable(defaultID(), defaultShadowRect());
            }
            if(isFocusActive) {
                focusDrawable = new NinePatchShadowDrawable(focusID(), focusShadowRect());
            }
        }


        void draw(Canvas canvas){

            if(isFocused){
                if( focusDrawable != null){
                    focusDrawable.draw(canvas);
                }
            }else{
                if(defaultDrawable != null && defaultShadowVisible){
                    defaultDrawable.draw(canvas);
                }
            }


        }

        void setContentSize(int width,int height){
            if(defaultDrawable != null){
                defaultDrawable.setContentSize(width,height);
            }
            if(focusDrawable != null){
                focusDrawable.setContentSize(width,height);
            }
        }

        abstract int defaultID();
        abstract int focusID();
        abstract Rect defaultShadowRect();
        abstract Rect focusShadowRect();

        public void setDefaultShadowActive(boolean defaultShadowVisible) {
            this.defaultShadowVisible = defaultShadowVisible;
        }
    }


    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        setFocusShadowVisible(gainFocus);
    }

    /**
     *
     */
    final class NinePatchShadowDrawable {

        Drawable mDrawable;

        final int drawableId;

        final Rect mShadowRect;


        NinePatchShadowDrawable(int drawableId, Rect shadowRect) {
            this.drawableId = drawableId;
            this.mShadowRect = shadowRect;
            init();
        }

        void init(){

            mDrawable = SharedDrawableManager.obtainDrawable(getContext(),drawableId);

        }

        void setContentSize(int width,int height){
            final int contentWidth = width;
            final int contentHeight = height;

//        final float density = getResources().getDisplayMetrics().scaledDensity;
            final float density = 1;
            final int  extraWidth = (int) ((mShadowRect.left + mShadowRect.right) * density);
            final int extraHeight = (int) ((mShadowRect.top + mShadowRect.bottom) * density);

            //让阴影比指定的view大小，要大出真正阴影的距离
            final int shadowWidth = contentWidth + extraWidth;
            final int shadowHeight = contentHeight + extraHeight;

            final int tx = (int) (extraWidth * 0.5f);
            final int ty = (int) (extraHeight * 0.5f);



            mDrawable.setBounds(10, -10, shadowWidth-10, shadowHeight+10);
            mDrawable.getBounds().offset(-tx,-ty);

            if(true){
                Log.d(TAG,"NinePatchShadowDrawable setContentSize is "+mDrawable.getBounds()+" contentWidth is "+contentWidth+" contentHeight is "+contentHeight+" mShadowRect is "+mShadowRect+" extraWidth is "+extraWidth );
            }
        }




        void draw(Canvas canvas){
            mDrawable.draw(canvas);
        }
    }


    final class ShadowXX extends ShadowImpl {


        ShadowXX(boolean isDefaultActive, boolean isFocusActive) {
            super(isDefaultActive, isFocusActive);
        }

        @Override
        int defaultID() {
            return R.drawable.ic_item_round_shadow_bg;
        }

        @Override
        int focusID() {
            return R.drawable.ic_item_round_shadow_bg;
        }

        @Override
        Rect defaultShadowRect() {
            return new Rect(40,40,40,40);
        }

        @Override
        Rect focusShadowRect() {
            return new Rect(40,40,40,40);
        }

    }


    final class ShadowX extends ShadowImpl {


        ShadowX(boolean isDefaultActive, boolean isFocusActive) {
            super(isDefaultActive, isFocusActive);
        }

        @Override
        int defaultID() {
            return R.drawable.ic_item_round_shadow_bg;
        }

        @Override
        int focusID() {
            return R.drawable.ic_item_round_shadow_bg;
        }

        @Override
        Rect defaultShadowRect() {
            return new Rect(22,36,22,33);
        }

        @Override
        Rect focusShadowRect() {
            return new Rect(22,36,22,33);
        }

    }


}
