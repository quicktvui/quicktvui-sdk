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

import com.quicktvui.support.ui.item.utils.DimensUtil;

public class RoundFocusBorderWidget extends BuilderWidget<RoundFocusBorderWidget.Builder> implements IFocusBorderWidget{

    Paint mPaint = new Paint();

    Paint mPaintBg = new Paint();

    int strokeWidth = 3;

    int blackStroke = 2;

    float roundCorner ;

    float blackRoundCorner ;

    RectF drawRect = new RectF();
    RectF blackRect = new RectF();

    float frameInterval = 0.5f;
    private boolean borderVisible=true;

    public static class Builder extends BuilderWidget.Builder<RoundFocusBorderWidget>{

        public Builder(Context context) {
            super(context);
        }


        public RoundFocusBorderWidget build(){
            return new RoundFocusBorderWidget(this);
        }
    }

    protected RoundFocusBorderWidget(Builder builder) {
        super(builder);


        setSize(MATCH_PARENT,MATCH_PARENT);

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaintBg.setAntiAlias(true);
        mPaintBg.setColor(Color.BLACK);
        mPaintBg.setStyle(Paint.Style.STROKE);
        mPaintBg.setStrokeWidth(strokeWidth);

        roundCorner  = DimensUtil.dp2Px(builder.context,6.5f);
        blackRoundCorner = roundCorner - 2;

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

        blackRect.set(bounds.left,bounds.top,bounds.right,bounds.right);
        blackRect.inset(1,1);

        drawRect.set(blackRect);
        drawRect.inset(1 - strokeWidth,1 - strokeWidth);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(isVisible()&&borderVisible) {
            canvas.drawRoundRect(blackRect,520,520,mPaintBg);
            canvas.drawRoundRect(drawRect, 520, 520, mPaint);
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
