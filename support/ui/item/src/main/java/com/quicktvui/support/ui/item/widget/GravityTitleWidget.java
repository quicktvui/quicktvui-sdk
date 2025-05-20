package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

import com.quicktvui.support.ui.item.utils.DimensUtil;
import com.quicktvui.support.ui.render.RenderNode;
import com.quicktvui.support.ui.render.TextNode;

public class GravityTitleWidget extends BuilderWidget<GravityTitleWidget.Builder> {


    TextNode mTitle;
    TextNode mSubTitle;

    RenderNode mTitleGroup;


    static boolean DEBUG_DRAW = false;

    public static class Builder extends BuilderWidget.Builder<GravityTitleWidget>{

        int titleColor = Color.WHITE;
        int subTitleColor = Color.parseColor("#b2ffffff");

        boolean centerInParent = false;
        boolean hasSubTitle = true;

        Rect mMargin = new Rect();

        public Builder setMargin(Rect margin) {
            mMargin = margin;
            return this;
        }

        TextNode.Gravity gravity = TextNode.Gravity.LEFT;

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder setSubTitleColor(int subTitleColor) {
            this.subTitleColor = subTitleColor;
            return this;
        }



//        public Builder setGravity(TextNode.Gravity gravity) {
//            this.gravity = gravity;
//            return this;
//        }

        public Builder setHasSubTitle(boolean hasSubTitle) {
            this.hasSubTitle = hasSubTitle;
            return this;
        }

        public Builder(Context context) {
            super(context);
        }

        @Override
        public GravityTitleWidget build() {
            return new GravityTitleWidget(this);
        }

    }


    GravityTitleWidget(Builder builder) {
        super(builder);


        setSize(MATCH_PARENT,MATCH_PARENT);


        initWithBuilder();
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);

        layout(width,height);

    }

    void layout(final int parentWidth,final int parentHeight){

        if(mBuilder.centerInParent){

            final int titleGroupHeight = mTitleGroup.height();

            final Rect margin = mBuilder.mMargin;

            final int y = (int) ((parentHeight - titleGroupHeight) * 0.5f) + margin.top;
            final int x = margin.left;

            mTitleGroup.setPosition(x,y);
            mTitleGroup.setWidth(parentWidth - margin.left - margin.right);
        }else{
            final Rect margin = mBuilder.mMargin;
            final int y = margin.top;
            final int x = margin.left;

            mTitleGroup.setPosition(x,y);
            mTitleGroup.setWidth(parentWidth - margin.left - margin.right);
        }


    }


    public GravityTitleWidget setTitle(String title){
        mTitle.setText(title);
        return this;
    }

    public GravityTitleWidget setSubTitle(String subTitle){
        if(hasSubTitle()) {
            mSubTitle.setText(subTitle);
        }
        return this;
    }


    void initWithBuilder(){

        mTitleGroup = new RenderNode();
        add(mTitleGroup);

//        mTitleGroup.setDebugDraw(true,Color.CYAN);

        mTitle = new TextNode();

        configTitle();

        final int titleHeight = DimensUtil.dp2Px(context,22);

        mTitle.setSize(MATCH_PARENT,titleHeight );
        if(hasSubTitle()) {
            final int subTitleHeight = DimensUtil.dp2Px(context,15);
            final int gap = DimensUtil.dp2Px(context,2f);
            mSubTitle = new TextNode();
            configSubTitle();
            mSubTitle.setSize(MATCH_PARENT,subTitleHeight);
            mSubTitle.setPosition(0,titleHeight + gap);

            mTitleGroup.setSize(MATCH_PARENT, subTitleHeight + titleHeight + gap );

        }else{
            mTitleGroup.setSize(MATCH_PARENT, titleHeight );
        }

    }

    void configTitle(){
        mTitle.setTextSize(DimensUtil.sp2px(context,22.7f));
        mTitle.setTextColor(mBuilder.titleColor);
        mTitle.setZOrder(-1);
        if(mBuilder.centerInParent){
            mTitle.setGravity(TextNode.Gravity.CENTER);
        }
        mTitle.setGravity(TextNode.Gravity.LEFT);
        mTitleGroup.add(mTitle);
    }

    void configSubTitle(){
        mSubTitle.setTextSize(DimensUtil.sp2px(context,14.7f));
        mSubTitle.setTextColor(mBuilder.subTitleColor);
        mSubTitle.setZOrder(999);
        if(mBuilder.centerInParent){
            mSubTitle.setGravity(TextNode.Gravity.CENTER);
        }
        mSubTitle.setGravity(TextNode.Gravity.LEFT);
        mTitleGroup.add(mSubTitle);

    }

    boolean hasSubTitle(){
        return mBuilder.hasSubTitle;
    }

    @Override
    public String getName() {
        return "GTitle";
    }


}
