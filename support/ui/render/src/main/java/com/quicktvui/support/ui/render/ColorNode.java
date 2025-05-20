package com.quicktvui.support.ui.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


public class ColorNode extends RenderNode {

    int color;

    float roundRadiusX;
    float roundRadiusY;

    RectF mTempRectF;


    public ColorNode(int color) {
        this.color = color;
        mDefaultPaint.setColor(color);
        mDefaultPaint.setStyle(Paint.Style.FILL);
    }


    public ColorNode() {
        this(Color.GRAY);
    }

    public void setRoundRadiusX(float roundRadiusX) {
        this.roundRadiusX = roundRadiusX;
    }

    public void setRoundRadiusY(float roundRadiusY) {
        this.roundRadiusY = roundRadiusY;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(roundRadiusX > 0 || roundRadiusY > 0){
            if(mTempRectF == null){
                mTempRectF = new RectF(getDrawBounds());
            }else{
                mTempRectF.set(getDrawBounds());
            }
            canvas.drawRoundRect(mTempRectF, roundRadiusX,roundRadiusY,mDefaultPaint);
        }else {
            canvas.drawRect(getDrawBounds(), mDefaultPaint);
        }

    }


    /**
     * 设置绘制颜色
     * @param color
     */
    public void setColor(int color){
        mDefaultPaint.setColor(color);
        invalidateSelf();
    }

    public void setStyle(Paint.Style style){
        mDefaultPaint.setStyle(style);
    }



}
