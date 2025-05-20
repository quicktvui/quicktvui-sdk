package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;

import com.quicktvui.support.ui.item.utils.DimensUtil;


public class ProgressWidget extends BuilderWidget<ProgressWidget.Builder> implements IProgressWidget{

    /**
     *  测量后的尺寸
     */
    private int mWidth;
    private int mHeight;

    /**
     * 进度值，0-100
     */
    private int mProgress=0;


    private  PaintFlagsDrawFilter pfd ;

    /**
     * 绘制进度的画笔
     */
    private Paint mProgressPaint;
    /**
     * 绘制进度底色的画笔
     */
    private Paint mProgressShadowPaint;
    private final float fitScale;
    private float progress;
    private final RectF progressRect;
    private final RectF progressShadowRect;
    /**
     * 进度条圆角半径
     */
    private float mRadius =0;
    /**
     * 获取焦点时是否显示
     */
    private boolean onFocusVisible =false;


    public static class Builder extends BuilderWidget.Builder<ProgressWidget>{

        /**
         * 进度颜色，默认淡蓝色
         */
        private int mProgressColor=Color.parseColor("#FF0CB8CB");
        /**
         * 进度底色颜色，默认深灰
         */
        public int mProgressShadowColor =Color.DKGRAY;
        /**
         * 进度条的宽度
         */
        private int progressStrockWidth =5;
        /**
         * 进度条距离底部的高度
         */
        private int margin =60;

        /**
         * 进度条圆角
         */
        private float radius = 0;
        /**
         *
         */
        private boolean visible=false;

        private Context context;

        public Builder(Context context) {
            super(context);
            this.context = context;
        }

        public ProgressWidget build(){
            return new ProgressWidget(this);
        }

        public Builder setProgressColor(int color) {
            this.mProgressColor = color;
            return this;
        }

        public Builder setProgressShadowColor(int color) {
            this.mProgressShadowColor = color;
            return this;
        }
        public Builder setProgressStrockWidth(int width) {
            this.progressStrockWidth = width;
            return this;
        }

        public Builder setMaginBottom(int margin) {
            this.margin  = margin;
            return this;
        }

        public Builder setProgressRadius(float radius) {
            this.radius = radius;
            return this;
        }
        public Builder setProgressVisible(boolean visible) {
            this.visible = visible;
            return this;
        }
    }
    public ProgressWidget(Builder builder) {
        super(builder);
        final Context context =builder.context;
        fitScale = DimensUtil.getFitScale(context);
        //进度的画笔
        mProgressPaint = new Paint();
        mProgressPaint.setColor(builder.mProgressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.FILL);

        //进度底色的画笔
        mProgressShadowPaint = new Paint();
        mProgressShadowPaint.setColor(builder.mProgressShadowColor);
        mProgressShadowPaint.setAntiAlias(true);
        mProgressShadowPaint.setDither(true);
        mProgressShadowPaint.setStyle(Paint.Style.FILL);

        mWidth = 0;
        mHeight = 0;
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

        progressRect = new RectF(0,0,0,0);
        progressShadowRect = new RectF(0,0,0,0);

    }
    @Override
    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        progress = mProgress*1.0f / 100 * 1.0f;
    }

    @Override
    public void setMarginBottom(int margin) {
        this.mBuilder.setMaginBottom(margin);
    }

    /**
     * 进度条高度
     * @param height
     */
    @Override
    public void setProgressBarHeight(int height) {
        this.mBuilder.setProgressStrockWidth(height);
    }

    /**
     * 进度条进度颜色
     * @param color
     */
    @Override
    public void setProgressColor(int color) {
        mBuilder.setProgressColor(color);
    }

    /**
     * 进度条底色
     * @param shadowColor
     */
    @Override
    public void setProgressShadowColor(int shadowColor) {
        mBuilder.setProgressShadowColor(shadowColor);
    }

    /**
     * 进度条圆角
     * @param radius
     */
    @Override
    public void setProgressCornerRadius(float radius) {
        this.mRadius = radius;
    }

    @Override
    public void setProgressVisible(boolean visible) {
        mBuilder.visible = visible;
    }


    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        this.onFocusVisible = gainFocus;
    }


    @Override
    public void onDraw(Canvas canvas) {
        mWidth = mParent.width();
        mHeight = mParent.height();
        canvas.setDrawFilter(pfd);

        /**
         * 绘制进度、进度底色
         */
        if (mBuilder.visible&& onFocusVisible){
            //底色
            progressShadowRect.top = mHeight-mBuilder.margin-mBuilder.progressStrockWidth;
            progressShadowRect.right = mWidth;
            progressShadowRect.bottom = mHeight-mBuilder.margin;

            canvas.drawRoundRect(progressShadowRect,mRadius,mRadius,mProgressShadowPaint);
            //进度
            progressRect.top = mHeight-mBuilder.margin-mBuilder.progressStrockWidth;
            progressRect.right = progress*mWidth;
            progressRect.bottom = mHeight-mBuilder.margin;
            canvas.drawRoundRect(progressRect,mRadius,mRadius,mProgressPaint);

        }
    }


    @Override
    public String getName() {
        return "Progress";
    }


}
