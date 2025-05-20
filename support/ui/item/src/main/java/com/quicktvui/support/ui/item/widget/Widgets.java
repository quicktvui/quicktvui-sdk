package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Color;

import com.quicktvui.support.ui.item.utils.DimensUtil;

import com.quicktvui.support.ui.render.ColorNode;
import com.quicktvui.support.ui.render.Layout;
import com.quicktvui.support.ui.render.RenderNode;
import com.quicktvui.support.ui.render.TextNode;

public class Widgets {

    static final int DEFAULT_TITLE_HEIGHT = 40;
    static final int DEFAULT_TITLE_TEXT_SIZE = 20;
    static final int DEFAULT_TITLE_PADDING_LEFT= 10;
    static final int DEFAULT_SUBTITLE_HEIGHT = 30;
    static final int DEFAULT_SUBTITLE_TEXT_SIZE = 20;
    static final int DEFAULT_TITLE_BG_COLOR = 0xB2000000;

    public static TextNode defaultTitle(Context context){

        DimensUtil.init(context.getApplicationContext());

        RenderNode bg = new ColorNode(DEFAULT_TITLE_BG_COLOR);

        TextNode title = (TextNode) new TextNode().setSize(RenderNode.MATCH_PARENT,DimensUtil.dp2Px(DEFAULT_TITLE_HEIGHT));
        title.setTextSize(DimensUtil.dp2Px(DEFAULT_TITLE_TEXT_SIZE));
        title.setGravity(TextNode.Gravity.LEFT);
        title.setTextColor(Color.WHITE);
        title.setMarqueAble(true);
        title.setBackGround(bg);
        title.setPaddingLR(DimensUtil.dp2Px(DEFAULT_TITLE_PADDING_LEFT));

        title.setLayout(new Layout.Relative().alignParentBottom());

        return title;
    }

    public static TextNode defaultSubTitle(Context context){

        DimensUtil.init(context.getApplicationContext());

        TextNode subTitle = (TextNode) new TextNode().setSize(RenderNode.MATCH_PARENT,DimensUtil.dp2Px(DEFAULT_SUBTITLE_HEIGHT));
        subTitle.setMarqueAble(true);
        subTitle.setTextSize(DimensUtil.dp2Px(DEFAULT_SUBTITLE_TEXT_SIZE));
        subTitle.setTextColor(0x80FFFFFF);
        subTitle.setPaddingLR(DimensUtil.dp2Px(DimensUtil.dp2Px(DEFAULT_TITLE_PADDING_LEFT)));
        subTitle.setGravity(TextNode.Gravity.LEFT);
        subTitle.setLayout(new Layout.Relative().alignParentBottom().translateY(DimensUtil.dp2Px(DEFAULT_SUBTITLE_HEIGHT + 16)));
        subTitle.setZOrder(1);

        return subTitle;
    }


}
