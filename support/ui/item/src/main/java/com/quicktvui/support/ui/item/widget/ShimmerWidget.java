package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.quicktvui.support.ui.item.R;
import com.quicktvui.support.ui.item.view.shimmer.FocusShimmer;
import com.quicktvui.support.ui.render.DrawableNode;

public class ShimmerWidget extends BuilderWidget<ShimmerWidget.Builder> implements IWidget {

    public final static String NAME = "SHIMMER";
    DrawableNode mShimmerNode;
    FocusShimmer mFocusShimmer;

    View targetHostView;

    public ShimmerWidget(Builder builder) {
        super(builder);
        setSize(MATCH_PARENT, MATCH_PARENT);
        initShimmerConfig(builder);
        initShimmerNode();
    }

    @Override
    public void onDrawBackGround(Canvas canvas) {
        super.onDrawBackGround(canvas);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public String getName() {
        return NAME;
    }

    //初始化闪屏drawable配置
    void initShimmerConfig(Builder builder) {
        mFocusShimmer = new FocusShimmer.Builder().asColor()
                .shimmerDuration(builder.shimmerDuration)
                .setOption(FocusShimmer.OptionsFactory.get(builder.roundCorner))
                .shimmerColor(builder.shimmerColor)
                .setOffset(builder.offsetX,builder.offsetY)
                .setSize(builder.width,builder.height)
                .build(builder.hostView);
    }

    //初始化闪屏drawableNode
    void initShimmerNode() {
        if (mShimmerNode == null) {
            mShimmerNode = new DrawableNode((Drawable) mFocusShimmer);
            mShimmerNode.setSize(MATCH_PARENT, MATCH_PARENT);
            mShimmerNode.setZOrder(-1);
        }
        add(mShimmerNode);
    }


    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        if (mFocusShimmer != null) {
            Log.d("shimmer_abs","onFocus ==="+gainFocus+"||||this===="+this.toString());
            if(targetHostView == null && mBuilder.targetHostViewID > 0){
                targetHostView = mBuilder.hostView.findViewById(mBuilder.targetHostViewID);
                mFocusShimmer.setParentView(targetHostView);
            }
            if (gainFocus == true){
//                Log.d("shimmer_abs","onFocus ==="+this.toString());
                mShimmerNode.setVisible(true,false);
                mFocusShimmer.onFocus(null);
            }else {
                mFocusShimmer.setVisible(false);
                mShimmerNode.setVisible(false,false);
            }
        }
    }

    public static class Builder extends BuilderWidget.Builder<ShimmerWidget> {
        private View hostView;
        private int roundCorner;
        //设置流光中心区域的颜色
        private int shimmerColor = 0x50ffffff;
        //流光动画时间
        private long shimmerDuration = 1200;
        //流光动画x的偏移量
        private float offsetX = 0f;
        //流光动画y偏移量
        private float offsetY = 0f;
        //流光动画的大小
        private int width = -1;
        private int height = -1;

        private int targetHostViewID  = -1;
        public Builder(Context context, View hostView) {
            super(context);
            this.hostView = hostView;
            this.roundCorner = context.getResources().getDimensionPixelSize(R.dimen.item_bar_corner);
        }

        /**
         * 设置流光效果的偏移，从左上角算起
         * @param offsetX 流光效果的x轴偏移量
         * @param offsetY 流光效果的x轴偏移量
         * @return
         */
        public Builder setOffset(float offsetX,float offsetY){
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            return this;
        }

        public Builder setTargetHostViewID(int targetHostViewID) {
            this.targetHostViewID = targetHostViewID;
            return this;
        }

        /**
         * 设置流光效果的区域，如果设置小于等于0则认为直接根据父布局的大小来加载
         * @return
         */
        public Builder setSize(int width,int height){
            this.width = width;
            this.height = height;
            return this;
        }
        public Builder setShimmerColor(int shimmerColor){
            this.shimmerColor = shimmerColor;
            return this;
        }
        public Builder setDuration(long duration){
            this.shimmerDuration = duration;
            return this;
        }

        @Override
        public Class getWidgetClass() {
            return ShimmerWidget.class;
        }

        public Builder setRoundConner(int roundConner) {
            this.roundCorner = roundConner;
            return this;
        }

        @Override
        public String getName() {
            return NAME;
        }

        public ShimmerWidget build() {
            return new ShimmerWidget(this);
        }


    }


}
