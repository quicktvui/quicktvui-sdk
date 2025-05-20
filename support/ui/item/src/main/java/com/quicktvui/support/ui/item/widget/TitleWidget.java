package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;


import com.quicktvui.support.ui.item.BuildConfig;
import com.quicktvui.support.ui.render.TextNode;


public class TitleWidget extends BuilderWidget<TitleWidget.Builder> implements ITitleWidget {


    final int PADDING_HORIZONTAL = 10;

    static final int TEXT_SIZE  = 20;

    static int COLOR_FOCUS = Color.parseColor("#101010");

    TextNode mTitle;
    TextNode mSubTitle;

    private static boolean DEBUG = BuildConfig.DEBUG;

    public static class Builder extends BuilderWidget.Builder<TitleWidget>{

        public Builder(Context context) {
            super(context);
        }

        TitlesGenerator titlesGenerator;

        public Builder setTitlesGenerator(TitlesGenerator titlesGenerator) {
            this.titlesGenerator = titlesGenerator;
            return this;
        }

        public TitleWidget build(){
            return new TitleWidget(this);
        }
    }

    public interface TitlesGenerator{
        TextNode generateTitle(Context context);
        @Nullable TextNode generateSubTitle(Context context);
    }



    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        if (mBuilder!=null){
            changeBarStyle(gainFocus);
        }
        setMarquable(gainFocus);
    }

    private void changeBarStyle(boolean gainFocus) {
//        if (gainFocus){
//            setTitleColor(mBuilder.textColorFocus);
//        }else {
//            setTitleColor(mBuilder.textColorDefault);
//        }
//        invalidateSelf();
    }



    protected TitleWidget(Builder builder) {

        super(builder);

        setSize(MATCH_PARENT,  MATCH_PARENT);
        mTitle = builder.titlesGenerator.generateTitle(context);
        mSubTitle = builder.titlesGenerator.generateSubTitle(context);
        add(mTitle);
        if(mSubTitle != null){
            add(mSubTitle);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    //20190322 0041347: 【launcher】电视剧TAB下，新剧看不停，恩情无限封面图下方为白色，焦点停留时，影片名上方会出现一条黑色的线闪三四下后消失
    //为了解决这个问题，将白色背景加大一部分
    int extraHeight = 1;

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        if(DEBUG) {
            Log.d("TitleWidget", "onMeasure height is " + height);
        }


    }



    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }


    @Override
    public void setTitle(@Nullable String bar_text) {
        this.setText(bar_text);
    }



    @Override
    public void changeFocus(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        changeBarStyle(gainFocus);
    }

    @Override
    public void setVisible(boolean visible) {
        setVisible(visible,false);
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
        super.drawChildren(canvas);
    }


    public void setText(@Nullable final String text) {
        mTitle.setTag(text);
        if(updateTextRunnable != null) {
            removeCallbacks(updateTextRunnable);
            updateTextRunnable = null;
        }
        if(TextUtils.isEmpty(text)){
            //置空时，即刻执行
            mTitle.setText("");
            mTitle.setVisible(false,false);
        }else {
            mTitle.setVisible(true,false);
            updateTextRunnable = new SetTextRunnable(mTitle,text);
            postDelayed(updateTextRunnable, 100);
        }
        invalidateSelf();
    }

    final static class SetTextRunnable implements Runnable{
        final TextNode bTitleNode;
        final String text;

        SetTextRunnable(TextNode bTitleNode, String text) {
            this.bTitleNode = bTitleNode;
            this.text = text;
        }

        @Override
        public void run() {
            final Object tag = bTitleNode.getTag();
            if(tag != null && tag.equals(text)) {
                bTitleNode.setText(text);
            }else{
                Log.w("BottomTitle","setTextRunnable canceled tag is : "+tag+" bTitleNode is "+ bTitleNode);
            }
        }
    }

    Runnable updateTextRunnable;
    Runnable updateSubTextRunnable;

    public void cancelLoadText(){
        mTitle.setTag(null);
        if(updateTextRunnable != null){
            removeCallbacks(updateTextRunnable);
        }
        updateTextRunnable = null;
    }

    @Override
    public String getTitleText() {
        return mTitle.getText();
    }

    @Override
    public void setSubTitle(@Nullable String text) {
        if(mSubTitle != null) {
            mSubTitle.setTag(text);
            if (updateSubTextRunnable != null) {
                removeCallbacks(updateSubTextRunnable);
                updateSubTextRunnable = null;
            }
            if (TextUtils.isEmpty(text)) {
                //置空时，即刻执行
                mSubTitle.setText("");
            } else {
                updateSubTextRunnable = new SetTextRunnable(mSubTitle, text);
                postDelayed(updateSubTextRunnable, 100);
            }
            invalidateSelf();
        }
    }


    @Override
    @Nullable public String getSubTitleText() {
        if(mSubTitle != null){
            return mSubTitle.getText();
        }
        return null;
    }



    public void setTextSize(int unit , float textSize) {
        final float value  = TypedValue.applyDimension(unit,textSize,getContext().getResources().getDisplayMetrics());
        mTitle.setTextSize(value);
    }

    public void setTextColor(int textColor) {
        mTitle.setTextColor(textColor);
    }



    public void setMarquable(boolean isMarquable){
        mTitle.setMarqueAble(isMarquable);
        if(mSubTitle != null){
            mSubTitle.setMarqueAble(isMarquable);
        }
        invalidateSelf();
    }


}
