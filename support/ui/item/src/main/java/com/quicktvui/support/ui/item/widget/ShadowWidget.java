package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.util.Log;


import com.quicktvui.support.ui.item.Config;
import com.quicktvui.support.ui.item.utils.DimensUtil;

import com.quicktvui.support.ui.item.R;

public class ShadowWidget extends BuilderWidget<ShadowWidget.Builder> implements IShadowWidget{


    ShadowImpl mShadowImpl;

    public static final String TAG = "ShadowWidget";

    public static class Builder extends BuilderWidget.Builder<ShadowWidget>{

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


        public ShadowWidget build(){
            return new ShadowWidget(this);
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


    public ShadowWidget(Builder builder){
        super(builder);
        setSize(MATCH_PARENT,MATCH_PARENT);
        initShadow(builder.shadowSize,builder.isFocusActive,builder.isDefaultActive);
    }



    void initShadow(int shadowSize,boolean isFocusActive,boolean isDefaultActive){
        mShadowImpl = new ShadowV2(getBuilder().context,isDefaultActive,isFocusActive);
    }

    public void setShadowImpl(ShadowImpl mShadowImpl) {
        this.mShadowImpl = mShadowImpl;
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
            mShadowImpl.setContentSize(contentWidth,contentHeight);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {

       if(mShadowImpl != null){
           mShadowImpl.draw(canvas);
       }
    }

    @Override
    public void setFocusShadowVisible(boolean focusShadowVisible) {
        if(mShadowImpl != null){
            mShadowImpl.setFocusShadowActive(focusShadowVisible);
        }
    }

    @Override
    public void setDefaultShadowVisible(boolean defaultShadowVisible) {
        if (mShadowImpl!=null){
            mShadowImpl.setDefaultShadowActive(defaultShadowVisible);
        }
    }


    public static abstract class ShadowImpl {

        NinePatchShadowDrawable defaultDrawable;
        NinePatchShadowDrawable focusDrawable;

        boolean isDefaultActive = true;
        boolean isFocusActive = true;

        boolean isFocused = false;
        final Context context;

        void setFocused(boolean b){
            this.isFocused = b;
        }

        public ShadowImpl(Context context,boolean isDefaultActive,boolean isFocusActive) {
            this.isFocusActive = isFocusActive;
            this.isDefaultActive = isDefaultActive;
            this.context = context;
            init();
        }

        void init(){
            if(isDefaultActive) {
                defaultDrawable = new NinePatchShadowDrawable(context,defaultID(), defaultShadowRect());
            }
            if(isFocusActive) {
                focusDrawable = new NinePatchShadowDrawable(context,focusID(), focusShadowRect());
            }
        }


        void draw(Canvas canvas){

            if(isFocused){
                if( focusDrawable != null&&isFocusActive){
                    focusDrawable.draw(canvas);
                }
            }else{
                if(defaultDrawable != null && isDefaultActive){
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

        public abstract int defaultID();
        public abstract int focusID();
        public abstract Rect defaultShadowRect();
        public abstract Rect focusShadowRect();

        void setDefaultShadowActive(boolean defaultShadowVisible) {
            this.isDefaultActive = defaultShadowVisible;
        }

        void setFocusShadowActive(boolean focusShadowActive){
            this.isFocusActive = focusShadowActive;
        }
    }


    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        if (mShadowImpl!=null){
            mShadowImpl.setFocused(gainFocus);
        }
    }


    public static Drawable obtainShadowDrawable(Context context, int drawableID) {
        //final Context applicationContext = context.getApplicationContext();
        Drawable d = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            d = context.getResources().getDrawableForDensity(drawableID,240,null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                d = context.getResources().getDrawableForDensity(drawableID,240);
            }else{
                d = context.getResources().getDrawable(drawableID);
            }
        }
        return d;
    }

    /**
     *
     */
    public static final class NinePatchShadowDrawable {

        NinePatchDrawable mDrawable;

        final int drawableId;

        final Rect mShadowRect;

        boolean valid = true;

        final Context context;

        public NinePatchShadowDrawable(Context context,int drawableId, int shadowLeft, int shadowRight, int shadowTop, int shadowBottom) {
            this.drawableId = drawableId;
            mShadowRect = new Rect();

            this.mShadowRect.left = shadowLeft;
            this.mShadowRect.right = shadowRight;
            this.mShadowRect.top = shadowTop;
            this.mShadowRect.bottom = shadowBottom;
            this.context = context;
            init();
        }

        final Rect padding = new Rect();

        void init(){

            final int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
            Log.i(TAG,"init dpi:"+densityDpi);
            mDrawable = (NinePatchDrawable) obtainShadowDrawable(context,drawableId);

            mDrawable.getPadding(padding);
            float targetDensity  = 1f;
//            if(Math.abs(mShadowRect.bottom - padding.bottom) > 10 ){
//                //这里为了做兼容
//                if(padding.bottom != 0) {
//                    targetDensity =  240 / (float)densityDpi;
//                    Log.e(TAG,"Shadow error :"+padding.left+" after targetDensity："+targetDensity+" padding.bottom:"+padding.bottom+" targetDensity:"+targetDensity * 160);
//                }
//            }
            mDrawable.setTargetDensity((int) (targetDensity * 240));

            mDrawable.getPadding(padding);

            Log.i(TAG,"after padding:"+padding);
        }

        final int scaledValue(int size){
            return size;
        }

        void setContentSize(int width,int height){
            final int contentWidth = width;
            final int contentHeight = height;

//        final float density = getResources().getDisplayMetrics().scaledDensity;
            final float density = 1;
//            final int  extraWidth = (int) ((mShadowRect.left + mShadowRect.right) * density);
//            final int extraHeight = (int) ((mShadowRect.top + mShadowRect.bottom) * density);

          //  final Rect padding = new Rect();
            //mDrawable.getPadding(padding);

//            float scale = getScaleValue(context,padding);
//
//            final int paddingLeft = (int) (padding.left * scale);
//            final int paddingRight = (int) (padding.right * scale);
//            final int paddingTop = (int) (padding.top * scale);
//            final int paddingBottom = (int) (padding.bottom * scale);
//
//            Log.i(TAG,"NinePatchShadowDrawable paddingLeft:"+paddingLeft+",paddingRight:"+paddingRight+",paddingTop:"+paddingTop+" ,paddingBottom:"+paddingBottom);
//
//            final int  extraWidth = paddingLeft * + paddingRight;
//            final int extraHeight = paddingTop + paddingBottom;
//
//            final int tx = paddingLeft * -1;
//            final int ty = paddingTop * -1;

            final int paddingLeft = scaledValue(mShadowRect.left);
            final int paddingTop =  scaledValue(mShadowRect.top);


            final int extraWidth = paddingLeft + scaledValue(mShadowRect.right);
            final int extraHeight = paddingTop + scaledValue(mShadowRect.bottom);

            final int tx = paddingLeft * -1;
            final int ty = paddingTop * -1;


            //让阴影比指定的view大小，要大出真正阴影的距离
            final int shadowWidth = contentWidth + extraWidth;
            final int shadowHeight = contentHeight + extraHeight;
            mDrawable.setBounds(0, 0, shadowWidth, shadowHeight);
            mDrawable.getBounds().offset(tx,ty);

            if(Config.DEBUG){
                Log.d(TAG,"NinePatchShadowDrawable setContentSize is "+mDrawable.getBounds()+" contentWidth is "+contentWidth+" contentHeight is "+contentHeight+" mShadowRect is "+mShadowRect+" extraWidth is "+extraWidth +" desity:"+context.getResources().getDisplayMetrics().density);
            }
        }

        public NinePatchShadowDrawable(Context context,int drawableId, Rect shadowRect) {
            this.drawableId = drawableId;
            this.mShadowRect = shadowRect;
            this.context = context;
            init();
        }


        void draw(Canvas canvas){
            if(valid) {
                mDrawable.draw(canvas);
            }
        }
    }


    final class ShadowV2 extends ShadowImpl {


        ShadowV2(Context context,boolean isDefaultActive, boolean isFocusActive) {
            super(context,isDefaultActive, isFocusActive);
        }

        @Override
        public int defaultID() {
            return R.drawable.shadow_focus_v2;
        }

        @Override
        public int focusID() {
            return R.drawable.shadow_focus_v2;
        }

        @Override
        public Rect defaultShadowRect() {
            return new Rect(107,107,107,105);
        }

        @Override
        public Rect focusShadowRect() {
            return new Rect(107,107,107,105);
        }

    }

    public int  dp2px(int size) {
        return DimensUtil.dp2Px(context,size);
    }







}
