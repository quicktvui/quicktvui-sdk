package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;
import android.util.TypedValue;

import com.quicktvui.support.ui.item.utils.DimensUtil;
import com.quicktvui.support.ui.item.utils.SharedDrawableManager;

import com.quicktvui.support.ui.item.R;
import com.quicktvui.support.ui.render.DrawableNode;
import com.quicktvui.support.ui.render.TextNode;


@Deprecated
public class ActorBottomTitleWidget extends BuilderWidget<ActorBottomTitleWidget.Builder> implements ITitleWidget {


    TextNode mTextRender;

    DrawableNode mDrawableNode;

    final int PADDING_HORIZONTAL = 10;

    static final int TEXT_SIZE  = 20;


    public static class Builder extends BuilderWidget.Builder<ActorBottomTitleWidget>{



        public Builder(Context context) {
            super(context);
        }


        public ActorBottomTitleWidget build(){
            return new ActorBottomTitleWidget(this);
        }
    }


    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        setMarquable(gainFocus);
    }


    protected ActorBottomTitleWidget(Builder builder) {

        super(builder);

        setSize(MATCH_PARENT,  DimensUtil.dp2Px(builder.context,40));

        mTextRender = new TextNode();
        mTextRender.setSize(MATCH_PARENT,MATCH_PARENT);
        mTextRender.setPaddingLeft(PADDING_HORIZONTAL);
        mTextRender.setPaddingRight(PADDING_HORIZONTAL);

        mTextRender.setGravity(TextNode.Gravity.CENTER);

        mTextRender.setTextSize(DimensUtil.sp2px(builder.context,TEXT_SIZE));

        add(mTextRender);

        final Drawable defaultBGDrawable = SharedDrawableManager.obtainDrawable(builder.context, R.drawable.item_bar_unfocus_back);
        mDrawableNode = new DrawableNode(defaultBGDrawable);
        mDrawableNode.setSize(MATCH_PARENT,MATCH_PARENT);
        mDrawableNode.setZOrder(-1);
        add(mDrawableNode);


    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        if(DEBUG) {
            Log.d("TitleWidget", "onMeasure height is " + height);
        }

        if(mParent != null && getY() <= 0){
            layoutSelf(mParent.height());
        }
    }


    void layoutSelf(final int parentHeight){
        if(DEBUG) {
            Log.d("TitleWidget", "layoutSelf parentHeight is " + parentHeight + " this height is " + height());
        }
        //将自己放在底部
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setPosition(0,parentHeight - height());
                invalidateSelf();
            }
        },16);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }



    @Override
    public void setTitle(String bar_text) {
        this.setText(bar_text);
    }



    @Override
    public void changeFocus(boolean gainFocus, int direction, Rect previouslyFocusedRect) {

    }

    @Override
    public void setVisible(boolean visible) {
        setVisible(visible,false);
    }

    @Override
    public void cancelLoadText() {
        if(updateTextRunnable != null){
            removeCallbacks(updateTextRunnable);
            updateTextRunnable = null;
        }
    }



    @Override
    public String getTitleText() {
        return null;
    }

    @Override
    public void setSubTitle(@Nullable String bar_text) {

    }

    @Nullable
    @Override
    public String getSubTitleText() {
        return null;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void drawChildren(Canvas canvas) {
        if(getY() > 0) {
            super.drawChildren(canvas);
        }
    }

    public void setText(final String text) {
        mTextRender.setText("");
        if(updateTextRunnable != null) {
            removeCallbacks(updateTextRunnable);
        }
        updateTextRunnable = new Runnable() {
            @Override
            public void run() {
                mTextRender.setText(text);
            }
        };

        postDelayed(updateTextRunnable,600);
    }

    Runnable updateTextRunnable;


    public void setBackGroundResource(int resource) {
        setBackgroundResourceInner(resource);
    }

    public void setTextSize(int unit , float textSize) {
        final float value  = TypedValue.applyDimension(unit,textSize,getContext().getResources().getDisplayMetrics());
        mTextRender.setTextSize(value);
    }

    public void setTextColor(int textColor) {
        mTextRender.setTextColor(textColor);
    }


    void setBackgroundResourceInner(int resource){
        final Drawable drawable = SharedDrawableManager.obtainDrawable(getContext(),resource);
        mDrawableNode.setDrawable(drawable);
    }

    public void setMarquable(boolean isMarquable){
        mTextRender.setMarqueAble(isMarquable);
    }

    public void setTextSize(float size){
        mTextRender.setTextSize(size);
    }

    public void setGravity(TextNode.Gravity gravity){
        mTextRender.setGravity(gravity);
    }

    public void setPaddingLeft(float paddingLeft){
        mTextRender.setPaddingLeft((int) paddingLeft);
    }

    public void setPaddingRight(float paddingRight){
        mTextRender.setPaddingRight((int) paddingRight);
    }

}
