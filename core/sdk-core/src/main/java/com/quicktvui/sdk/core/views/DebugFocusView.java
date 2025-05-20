package com.quicktvui.sdk.core.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

public class DebugFocusView extends FrameLayout {
    Paint paint;
    Rect rect;

    int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE,
            Color.WHITE};
    int colorIndex = 0;

    long lastUpdateTime = 0;

    public DebugFocusView(@NonNull Context context) {
        super(context);
        setFocusable(false);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setColor(Color.RED);
        float[] intervals = {10, 5}; // 虚线长度为10像素，间隙为5像素
        paint.setPathEffect(new DashPathEffect(intervals, 0));
        rect = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.set(0, 0, w, h);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawRect(rect, paint);
    }
    public void nextTick(){
        if(System.currentTimeMillis() - lastUpdateTime > 500){
            colorIndex++;
            if (colorIndex >= colors.length) {
                colorIndex = 0;
            }
            paint.setColor(colors[colorIndex]);
            invalidate();
            lastUpdateTime =  System.currentTimeMillis();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }

}
