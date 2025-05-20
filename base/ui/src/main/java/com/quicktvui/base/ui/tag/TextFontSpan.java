package com.quicktvui.base.ui.tag;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;

public class TextFontSpan extends AbsoluteSizeSpan {

    public static final int FontWidget_NORMAL = 400;
    public static final int FontWidget_BOLD = 750;

    public static final int TextDecoration_NONE = 0;
    public static final int TextDecoration_UNDERLINE = 1;
    public static final int TextDecoration_LINE_THROUGH = 2;
    public static final int TextDecoration_OVERLINE = 3;

    private int fontWidget = -1;
    private int textDecoration = -1;

    private int mSize = -1;

    public TextFontSpan(int size, int textDecoration, int fontWidget) {
        this(size, false);
        this.mSize = size;
        this.fontWidget = fontWidget;
        this.textDecoration = textDecoration;
        //这里我们以px作为单位，方便统一调用
    }

    /**
     * 保持构造方法无法被外部调用
     *
     * @param size
     * @param dip
     */
    protected TextFontSpan(int size, boolean dip) {
        super(size, dip);
    }

    public TextFontSpan(Parcel src) {
        super(src);
        fontWidget = src.readInt();
        textDecoration = src.readInt();
        mSize = src.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(fontWidget);
        dest.writeInt(textDecoration);
        dest.writeInt(mSize);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (this.mSize >= 0) {
            super.updateDrawState(ds);
        }

        if (fontWidget == FontWidget_BOLD) {
            ds.setFakeBoldText(true);
        } else if (fontWidget == FontWidget_NORMAL) {
            ds.setFakeBoldText(false);
        }
        if (textDecoration == TextDecoration_NONE) {
            ds.setStrikeThruText(false);
            ds.setUnderlineText(false);
        } else if (textDecoration == TextDecoration_LINE_THROUGH) {
            ds.setStrikeThruText(true);
            ds.setUnderlineText(false);
        } else if (textDecoration == TextDecoration_UNDERLINE) {
            ds.setStrikeThruText(false);
            ds.setUnderlineText(true);
        }

    }

    @Override
    public void updateMeasureState(TextPaint ds) {
        if (this.mSize >= 0) {
            super.updateMeasureState(ds);
        }

        if (fontWidget == FontWidget_BOLD) {
            ds.setFakeBoldText(true);
        } else if (fontWidget == FontWidget_NORMAL) {
            ds.setFakeBoldText(false);
        }

        if (textDecoration == TextDecoration_NONE) {
            ds.setStrikeThruText(false);
            ds.setUnderlineText(false);
        } else if (textDecoration == TextDecoration_LINE_THROUGH) {
            ds.setStrikeThruText(true);
            ds.setUnderlineText(false);
        } else if (textDecoration == TextDecoration_UNDERLINE) {
            ds.setStrikeThruText(false);
            ds.setUnderlineText(true);
        }
    }
}
