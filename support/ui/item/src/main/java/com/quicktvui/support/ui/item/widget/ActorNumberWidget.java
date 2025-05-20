package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.quicktvui.support.ui.legacy.FConfig;
import com.quicktvui.support.ui.item.R;
import com.quicktvui.support.ui.item.utils.DimensUtil;
import com.quicktvui.support.ui.item.utils.SharedBitmapManager;
import com.quicktvui.support.ui.render.RenderNode;

import java.util.HashMap;
import java.util.Map;

public class ActorNumberWidget extends BuilderWidget<ActorNumberWidget.Builder> implements INumberIndexWidget {

        String text;
        TextPaint mPaint;

        int number;

        int mTop = 6;
        int mLeft = 6;

        int size = 50;//26.5dp

        Paint mBgPaint;

        RectF mBgRect;

        float textSize = 26.5f;

        final static int Radius = 4;

        int text_offset_x = 8;
        int text_offset_y = 20;

        boolean backGroundVisible = false;

        static Map<Integer,StaticLayout> layoutMapCache = new HashMap<>();


    //    static SharedDrawableManager.InternalDrawable numberBitmap;



        StaticLayout staticLayout;
    private final int baseLineY;
    /**
     * 设置序号比例缩放，如影视库中的item设置此值等比缩小一定比例。
     */
    private float scaleOffset =1.0f;
    private final Bitmap mIndexBgBitmap;

    public void setMagin(int left,int top){
            this.mLeft = left;
            this.mTop = top;
        }


        public static class Builder extends BuilderWidget.Builder<ActorNumberWidget>{

            public Builder(Context context) {
                super(context);
            }


            public ActorNumberWidget build(){
                return new ActorNumberWidget(this);
            }
        }

        protected ActorNumberWidget(Builder builder) {
            super(builder);
            mPaint = new TextPaint();
            mIndexBgBitmap = SharedBitmapManager.obtainBitmap(getContext(), R.drawable.ic_num_index_bg);
            mTop = DimensUtil.dp2Px(builder.context,mTop);
            mLeft = DimensUtil.dp2Px(builder.context,mLeft);
            size = (int) (DimensUtil.dp2Px(builder.context,26.7f));
            text_offset_x = DimensUtil.dp2Px(builder.context,text_offset_x);
            text_offset_y = DimensUtil.dp2Px(builder.context,text_offset_y);
            setBounds(mLeft,mTop,mLeft + size,mTop +size);
            mPaint.setColor(Color.WHITE);
            mPaint.setAntiAlias(true);
            mBgPaint = new Paint();
            mBgPaint.setAntiAlias(true);
            mBgPaint.setColor(builder.context.getResources().getColor(R.color.color_number_background));
            mBgRect = new RectF(0,0,size,size);
            textSize = builder.context.getResources().getDimension(R.dimen.index_number_text_size_common);
            setNumTextSize(0,textSize);

            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            //为基线到字体上边框的距离,即上图中的top
            float top = fontMetrics.top;
            //为基线到字体下边框的距离,即上图中的bottom         int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
            float bottom = fontMetrics.bottom;
            //基线中间点的y轴计算公式
            baseLineY = (int) (mBgRect.centerY() - top/2 - bottom/2);
        }

        public void setBgColor(int color){
            if(mBgPaint != null){
                mBgPaint.setColor(color);
                invalidateSelf();
            }
        }


        @Override
        public String getName() {
            return NAME;
        }


        StaticLayout obtainStaticLayout(int number){
            StaticLayout cached = layoutMapCache.get(number);
            if(cached == null){
                cached = new StaticLayout(number+"",mPaint,size, Layout.Alignment.ALIGN_CENTER,
                    0,0,true);
                layoutMapCache.put(number,cached);
            }
            return cached;
        }

        void setTextColor(int color){
            mPaint.setColor(color);
        }



    @Override
        public void onDraw(Canvas canvas) {
        int width = mParent.width();
        Log.e("parentwidth",width+"---");
        int a = (int) (35+0.5f*width);

        Log.e("parentwidth",a+"---"+a*Math.cos(45));
        mLeft = (int) (0.5f*width-a*Math.cos(45)-width*0.1f);
        mTop = mLeft;
        if( isVisible() ) {
                final int count = canvas.save();
                canvas.translate(mLeft,mTop);

                canvas.scale(scaleOffset,scaleOffset);
                if(backGroundVisible) {
                    canvas.drawBitmap(mIndexBgBitmap, null, mBgRect, mBgPaint);
                }
                if(text != null) {
                    canvas.drawText(text,mBgRect.width()*0.5f-mPaint.measureText(text)*0.5f, baseLineY, mPaint);
                }
                if(staticLayout != null) {
                    staticLayout.draw(canvas);
                }
                canvas.restoreToCount(count);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        @Override
        public void setNumText(String num_text) {
            this.text = num_text;
            this.backGroundVisible = !TextUtils.isEmpty(num_text);
            if(FConfig.DEBUG){
                Log.d("NumberDrawable","setNumText text is "+num_text+" this is "+this);
            }
            invalidateSelf();
        }

        @Override
        public void setNumber(int number) {
            this.number = number;

            if(FConfig.DEBUG){
                Log.d("NumberDrawable","setNumber number is "+number+" this is "+this);
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(number > 0) {
                    staticLayout = obtainStaticLayout(number);
                }else{
                    staticLayout = null;
                }
            }else {
                if(number > 0) {

                    this.text = number + "";
                }else{
                    this.text = "";
                }
            }

            this.backGroundVisible = number > 0;
            invalidateSelf();
        }

        @Override
        public void setNumTextColor(int num_text_color) {
            setTextColor(num_text_color);
            invalidateSelf();
        }

        @Override
        public void setNumTextSize(int unit, float size) {
            mPaint.setTextSize(size);
            invalidateSelf();
        }

        @Override
        public void setVisibility(int visible) {

           if(FConfig.DEBUG){
               Log.d("NumberDrawable","setVisibility visible is "+isVisible()+" this is "+this);
           }
           this.setVisible(visible == View.VISIBLE);
        }

        @Override
        public void setVisible(boolean isShow) {
            setVisible(isShow,false);
            invalidateSelf();
        }

        @Override
        public String toString() {
            if(FConfig.DEBUG){
                if(staticLayout != null){
                    return super.toString()+" static text is "+staticLayout.getText();
                }else{
                    return super.toString()+" text is "+text;
                }
            }else {
                return super.toString();
            }
        }

        @Override
        public boolean setVisible(boolean visible, boolean restart) {

            return super.setVisible(visible, restart);
        }


        public static class Disabled extends RenderNode implements INumberIndexWidget {

            @Override
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
            }

            @Override
            public void setNumText(String num_text) {

            }

            @Override
            public void setNumber(int number) {

            }

            @Override
            public void setNumTextColor(int num_text_color) {

            }

            @Override
            public void setNumTextSize(int unit, float size) {

            }

            @Override
            public void setVisibility(int visible) {

            }

            @Override
            public void setVisible(boolean isShow) {

            }

            @Override
            public void setNumberWidgetScaleOffset(float numberScaleOffset) {

            }

            @Override
            public void setWidgetScale(float scale) {

            }

            @Override
            public void onFocusChange(boolean gainFocus) {

            }

            @Override
            public RenderNode getRenderNode() {
                return this;
            }

            @Override
            public void onViewDetachedFromWindow(View view) {

            }

            @Override
            public void onViewAttachedToWindow(View view) {

            }

        }
    /**
     * 设置序号比例缩放，如影视库中的item设置此值等比缩小一定比例。
     */
     public void setNumberWidgetScaleOffset(float scaleOffset){
         if (scaleOffset<=0){
             this.scaleOffset= 1.0f;
         }else {
             this.scaleOffset= scaleOffset;
         }
     }

    /**
     * 圆形item需要用此调整角标的位置
     * @param x
     * @param y
     */
     public void translateIndexPosition(int x ,int y){
         this.mLeft = x;
         this.mTop = y;
     }
}
