package com.quicktvui.base.ui.tag;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import org.xml.sax.Attributes;

/**
 * Create by sorosunrain on 2021/07/19 15:22
 */
public class FontTag extends HtmlTag{

    public FontTag(Context context) {
        super(context);
    }

    @Override
    public void startHandleTag(Editable text, Attributes attributes) {
        String textColor = attributes.getValue("", "color");
        String textSize = attributes.getValue("", "size");

        if(!TextUtils.isEmpty(textColor)){
            int color = getHtmlColor(textColor) | 0xFF000000;
            start(text, new ForegroundColorSpan(color));
        }

        if(!TextUtils.isEmpty(textSize)){
            int size = getHtmlSize(textSize);
            start(text, new Font(size));
        }
    }

    @Override
    public void endHandleTag(Editable text) {
        Background b = getLast(text, Background.class); //读取出最后标记类型
        if (b != null) {
            end(text, Background.class, new BackgroundColorSpan(b.color)); //设置为Android可以解析的24种ParcelableSpan基本分类，当然也可以自己定义，但需要集成原有的分类
        }

        final ForegroundColorSpan fc = getLast(text, ForegroundColorSpan.class);
        if (fc != null) {
            end(text, ForegroundColorSpan.class, new ForegroundColorSpan(fc.getForegroundColor()));
        }

        Font f = getLast(text, Font.class);
        if (f != null) {
            end(text, Font.class, new TextFontSpan(f.textSize, f.textDecordation, f.fontWeidght)); //使用自定义的
        }
    }
}
