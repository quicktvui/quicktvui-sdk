package com.quicktvui.support.subtitle.converter.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class StrokeTextView extends TextView {
    private TextView backGroundText = null;//用于描边的TextView
    private int strokeColor;//
    private int strokeWidth;//

    public StrokeTextView(Context context) {
        this(context, null);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        backGroundText = new TextView(context, attrs, defStyle);
        strokeColor = Color.BLACK;
        strokeWidth = 2;
    }

    public void init() {
        TextPaint tp1 = backGroundText.getPaint();
        //设置描边宽度
        tp1.setStrokeWidth(strokeWidth);
        //背景描边并填充全部
        tp1.setStyle(Paint.Style.FILL_AND_STROKE);
        //设置描边颜色
        backGroundText.setTextColor(strokeColor);
        //将背景的文字对齐方式做同步
        backGroundText.setGravity(getGravity());
    }


    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        if (backGroundText != null) {
            backGroundText.setTextSize(size);
        }
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        if (backGroundText != null) {
            backGroundText.setTextSize(unit, size);
        }
    }

    public void setStrokeColor(String color) {
        if (!TextUtils.isEmpty(color) && backGroundText != null) {
            this.strokeColor = Color.parseColor(color);
            invalidate();
        }
    }

    public void setStrokeWidth(int width) {
        if (backGroundText != null) {
            this.strokeWidth = width;
            setWidth(getWidth() + width);
            backGroundText.setWidth(getWidth());
//            setLetterSpacing(0.1f);
//            backGroundText.setLetterSpacing(0.1f);
//            requestLayout();
//            invalidate();
        }
    }


    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        //同步布局参数
        backGroundText.setLayoutParams(params);
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = backGroundText.getText();
        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            backGroundText.setText(getText());
            this.postInvalidate();
        }
        backGroundText.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        backGroundText.layout(left, top, right, bottom);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //其他地方，backGroundText和super的先后顺序影响不会很大，但是此处必须要先绘制backGroundText，
        init();
        backGroundText.draw(canvas);
        super.onDraw(canvas);
    }

}
