package com.quicktvui.support.ui.item.widget;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;

import com.quicktvui.support.ui.item.R;
import com.quicktvui.support.ui.render.DrawableNode;
import com.quicktvui.support.ui.render.TextNode;

import org.jetbrains.annotations.Nullable;

public class TipWidget extends BuilderWidget<TipWidget.Builder> implements ITipWidget {

    private DrawableNode bgDrawableNode;
    private TextNode textNode;
    private int tipHeight = (int) dp2px(23.3f);

    private String textColor = "#ffffff";
    private float textSize = sp2px(14.0f);
    private int paddingHorizontal = (int) dp2px(6f);


    private int tipWidth = 0;

    public TipWidget(Builder builder) {
        super(builder);

        setSize(0,tipHeight);

    }

    @Override
    public String getName() {
        return NAME;
    }

    private void setupBgDrawableNodeIfNeed() {

        if (bgDrawableNode == null) {
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.text_border);
            bgDrawableNode = new DrawableNode(drawable);
            bgDrawableNode.setSize(MATCH_PARENT, MATCH_PARENT);
            bgDrawableNode.setZOrder(-1);
            add(bgDrawableNode);
        }


    }

    private void setupTextNodeIfNeed(String tip) {

        if (textNode == null) {
            textNode = new TextNode();
            textNode.setPosition(paddingHorizontal, 0).setSize(0, MATCH_PARENT).setZOrder(100);
            textNode.setGravity(TextNode.Gravity.LEFT);
            textNode.setTextSize(textSize);
            textNode.setText(tip);
            textNode.setTextColor(Color.parseColor(textColor));
            textNode.setZOrder(100);
            add(textNode);
        }
    }

    public static class Builder extends BuilderWidget.Builder<TipWidget> {

        float marginTop = dp2px(209.3f); //设计图textView距离顶部的距离 px

        public Builder(Context context) {
            super(context);
        }

        @Deprecated
        public Builder setMarginTop(float marginTop) {
            this.marginTop = marginTop;
            return this;
        }


        @Deprecated
        public Builder setMarginLeft(float marginLeft) {
            return this;
        }

        public TipWidget build() {
            return new TipWidget(this);
        }
    }

    Runnable updateRunnable = null;

    @Override
    public void setTip(@Nullable  final String tip) {
        if (TextUtils.isEmpty(tip)) {
            setVisible(false,false);
            if(textNode != null) {
                textNode.setText("");
            }
            return;
        }
        setupTextNodeIfNeed(tip);
        setupBgDrawableNodeIfNeed();

        setVisible(false,false);
        if(updateRunnable != null){
            removeCallbacks(updateRunnable);
        }
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                tipWidth = (int) measureWidth(tip,textSize);
                TipWidget.this.setWidth(tipWidth + 2 * paddingHorizontal);
                textNode.setWidth(tipWidth);
                textNode.setText(tip);
                setVisible(true,false);
            }
        };
        postDelayed(updateRunnable, 500);
    }

    @Override
    public void draw(Canvas canvas) {
        if(getY() > 0) {
            super.draw(canvas);
        }
    }

    static float dp2px(Float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    float sp2px(Float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    float measureWidth(String text, Float textSize) {
        if(TextUtils.isEmpty(text)){
            return 0;
        }
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(text);
    }


}
