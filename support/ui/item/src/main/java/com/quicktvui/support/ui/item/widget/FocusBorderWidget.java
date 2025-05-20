package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import com.quicktvui.support.ui.item.ItemCenter;

import com.quicktvui.support.ui.item.R;


public class FocusBorderWidget extends BuilderWidget<FocusBorderWidget.Builder> implements IFocusBorderWidget{

    Paint mPaint = new Paint();

    Paint mPaintBg = new Paint();

    float roundCorner ;

    float blackRoundCorner ;

    RectF drawRect = new RectF();
    RectF blackRect = new RectF();

    private boolean borderVisible=true;

    public static class Builder extends BuilderWidget.Builder<FocusBorderWidget>{

        float roundCorner = context.getResources().getDimension(R.dimen.frame_border_corner);
        int strokeWidth = 3;
        int inset = 1;
        int stokeColor = ItemCenter.defaultFocusBorderColor;


        public Builder(Context context) {
            super(context);
        }

        public Builder setRoundCorner(float roundCorner) {
            this.roundCorner = roundCorner;
            return this;
        }

        public Builder setInset(int inset) {
            this.inset = inset;
            return this;
        }

        public void setStokeColor(int stokeColor) {
            this.stokeColor = stokeColor;
        }

        public FocusBorderWidget build(){

            return new FocusBorderWidget(this);
        }
    }

    protected FocusBorderWidget(Builder builder) {
        super(builder);


        setSize(MATCH_PARENT,MATCH_PARENT);

        mPaint.setAntiAlias(true);
        mPaint.setColor(builder.stokeColor);
        mPaint.setStrokeWidth(builder.strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaintBg.setAntiAlias(true);
        mPaintBg.setColor(Color.BLACK);
        mPaintBg.setStyle(Paint.Style.STROKE);
        mPaintBg.setStrokeWidth(builder.strokeWidth);

        roundCorner  = builder.roundCorner;
        blackRoundCorner = Math.max(0,roundCorner - 2);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left , top  ,  right , bottom );
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        setVisible(gainFocus, false);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        blackRect.set(bounds.left,bounds.top,bounds.right,bounds.bottom);
        blackRect.inset(mBuilder.inset,mBuilder.inset);

        drawRect.set(blackRect);
        drawRect.inset(1 - getBuilder().strokeWidth,1 - getBuilder().strokeWidth);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(isVisible()&&borderVisible) {
            canvas.drawRoundRect(blackRect,blackRoundCorner,blackRoundCorner,mPaintBg);
            canvas.drawRoundRect(drawRect, roundCorner, roundCorner, mPaint);
        }
    }


    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }


    @Override
    public void setBorderColor(int borderColor) {
        mPaint.setColor(borderColor);
    }

    @Override
    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
    }
}
