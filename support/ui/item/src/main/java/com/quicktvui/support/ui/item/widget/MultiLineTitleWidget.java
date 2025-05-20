package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.quicktvui.support.ui.item.Config;
import com.quicktvui.support.ui.item.utils.DimensUtil;

import org.jetbrains.annotations.Nullable;

import com.quicktvui.support.ui.item.R;
import com.quicktvui.support.ui.item.presenter.StandardItemPlugin;

import com.quicktvui.support.ui.render.ColorNode;
import com.quicktvui.support.ui.render.OnSizeChangedListener;
import com.quicktvui.support.ui.render.RenderNode;
import com.quicktvui.support.ui.render.TextNode;

public class MultiLineTitleWidget extends BuilderWidget<MultiLineTitleWidget.Builder> implements StandardItemPlugin.MultiLineTitle, OnSizeChangedListener {

    // FIXME: 2019-12-15 zhaopeng 使用俩个textNode共同显示浪费内存，需要后期优化
    private TextNode focusStateTextNode;
    private TextNode normalStateTextNode;

    private ColorNode textBG;


    public static final String TAG = "MultiLineTitle";

    boolean isFocused = false;

    public MultiLineTitleWidget(Builder builder) {
        super(builder);

        create();
    }

    void create(){
        DimensUtil.init(mBuilder.context.getApplicationContext());
        setSize(MATCH_PARENT,MATCH_PARENT);

        focusStateTextNode = new TextNode();
        textBG = new ColorNode(mBuilder.focusBGColor);
        textBG.setRoundRadiusX(mBuilder.focusBGRounderCorner);
        textBG.setRoundRadiusY(mBuilder.focusBGRounderCorner);
        textBG.setSize(MATCH_PARENT,MATCH_PARENT);

        final int paddingHorizontal = DimensUtil.dp2Px(4);

        focusStateTextNode.setSize(MATCH_PARENT, -2);
        focusStateTextNode.setTextColor(mBuilder.focusTextColor);
        focusStateTextNode.setTextSize(DimensUtil.sp2px(context,mBuilder.textSizeSP));
        focusStateTextNode.setOnSizeChangedListener(this);
        focusStateTextNode.setMaxLines(mBuilder.maxLines);
        focusStateTextNode.setPaddingLR(paddingHorizontal);
        focusStateTextNode.setZOrder(2);
        focusStateTextNode.setGravity(TextNode.Gravity.CENTER);


        normalStateTextNode = new TextNode();

        normalStateTextNode.setSize(MATCH_PARENT,DimensUtil.dp2Px(20));

        normalStateTextNode.setTextColor(mBuilder.normalTextColor);
        normalStateTextNode.setPaddingLR(paddingHorizontal);
        normalStateTextNode.setTextSize(DimensUtil.sp2px(context,mBuilder.textSizeSP));
        normalStateTextNode.setGravity(TextNode.Gravity.CENTER);
//        focusStateTextNode.setOnSizeChangedListener(this);
//        focusStateTextNode.setMaxLines(mBuilder.maxLines);
        focusStateTextNode.setZOrder(2);

        //focusStateTextNode.setBackGround(textBG);
        callFocusChange(false);

        focusStateTextNode.setVisible(false,false);

        add(textBG);
        add(normalStateTextNode);
        add(focusStateTextNode);

    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        fixTextPosition();
    }

    void fixTextPosition(){
        final RenderNode p = mParent;
        if(p != null) {
            final int textMargin = mBuilder.textMarginHorizontal;
            final int textMarginBottom = mBuilder.textMarginParentBottom;
            focusStateTextNode.setWidth(p.width() - textMargin * 2);
//            Log.d(TAG, "fixTextPosition isFocused:" + isFocused);
            if (isFocused) {
                focusStateTextNode.setPosition(textMargin, (int) (p.height() - focusStateTextNode.height() * 0.5f));
                fixTextBG();
            } else {
                normalStateTextNode.setPosition(0, p.height() + textMarginBottom);
                invalidateSelf();
            }
        }
    }

    void fixTextBG(){
        if(textBG != null) {
            int textY = focusStateTextNode.getY();
            int marginV = DimensUtil.dp2Px(8);
            textBG.setSize(focusStateTextNode.width(), focusStateTextNode.height() + marginV * 2);
            textBG.setPosition(mBuilder.textMarginHorizontal, textY - marginV);
            invalidateSelf();
        }
    }

    @Override
    public String getName() {
        return StandardItemPlugin.MultiLineTitle.NAME;
    }

    @Override
    public void onSizeChanged(RenderNode node, int width, int height) {
        //focusStateTextNode 文本大小变化的监听
        if(Config.DEBUG){
           Log.d(TAG,"on focusStateTextNode SizeChanged width : "+width+",height: "+height+" is Focused:"+isFocused+" text:"+normalStateTextNode.getText());
        }
        if(width > 0 && height > 0) {
            fixTextPosition();
        }
    }

    public static class Builder extends BuilderWidget.Builder<MultiLineTitleWidget> {


        public int focusTextColor = Color.WHITE;
        public int normalTextColor ;
        public float textSizeSP;
        public int maxLines = 3;
        public int focusBGColor ;
        public int focusBGRounderCorner = 4;
        public int textMarginHorizontal = 5;
        public int textMarginParentBottom;


        public Builder(Context context) {
            super(context);
            normalTextColor = context.getResources().getColor(R.color.color_multi_line_text_normal);
            focusBGColor = context.getResources().getColor(R.color.color_nulti_line_bg_focus);
            textMarginParentBottom = DimensUtil.dp2Px(context,10);
            textSizeSP = 16f;
        }

        @Override
        public Class getWidgetClass() {
            return MultiLineTitleWidget.class;
        }

        @Override
        public MultiLineTitleWidget build() {
            return new MultiLineTitleWidget(this);
        }
    }
    @Override
    public void callFocusChange(boolean focus) {
        if(Config.DEBUG) {
            Log.d(TAG, "callFocusChangexx focus:" + focus);
        }
        isFocused = focus;
        fixTextPosition();
        focusStateTextNode.setVisible(focus,false);
        normalStateTextNode.setVisible(!focus,false);
        if(focusStateTextNode != null) {
            if (focus) {
//                focusStateTextNode.setTextColor(mBuilder.focusTextColor);
                textBG.setVisible(!TextUtils.isEmpty(focusStateTextNode.getText()),false);
//                fixTextPosition();
            } else {
//                focusStateTextNode.setMaxLines(1);
//                focusStateTextNode.setTextColor(mBuilder.normalTextColor);
                textBG.setVisible(false,false);

            }
        }

    }

    @Override
    public void setText(@Nullable String title) {
        if(focusStateTextNode != null){
            normalStateTextNode.setText(title);
            focusStateTextNode.setText(title);
        }
    }

    @Override
    public void enableMultiLine(boolean enable) {

    }
}
