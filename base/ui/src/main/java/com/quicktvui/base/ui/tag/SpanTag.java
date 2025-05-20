package com.quicktvui.base.ui.tag;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import org.xml.sax.Attributes;

public class SpanTag extends HtmlTag {

    public SpanTag(Context context) {
        super(context);
    }

    private static String getTextColorPattern(String style) {
        String cssName = "text-color";
        String cssVal = getHtmlCssValue(style, cssName);
        if (TextUtils.isEmpty(cssVal)) {
            cssName = "color";
            cssVal = getHtmlCssValue(style, cssName);
        }
        return cssVal;
    }

    @Nullable
    private static String getHtmlCssValue(String style, String cssName) {
        if (TextUtils.isEmpty(style)) return null;
        final String[] keyValueSet = style.toLowerCase().split(";");
        if (keyValueSet == null) return null;
        for (int i = 0; i < keyValueSet.length; i++) {
            final String match = keyValueSet[i].replaceAll(" ", "").toLowerCase();
            if (match.indexOf(cssName) == 0) {
                final String[] parts = match.split(":");
                if (parts == null || parts.length != 2) continue;
                return parts[1];
            }
        }
        return null;
    }

    private static String getBackgroundColorPattern(String style) {
        String cssName = "background-color";
        String cssVal = getHtmlCssValue(style, cssName);

        if (TextUtils.isEmpty(cssVal)) {
            cssName = "bakground";
            cssVal = getHtmlCssValue(style, cssName);
        }

        return cssVal;
    }

    private static String getTextFontSizePattern(String style) {
        String cssName = "font-size";
        String cssVal = getHtmlCssValue(style, cssName);
        if (TextUtils.isEmpty(cssVal)) {
            cssName = "text-size";
            cssVal = getHtmlCssValue(style, cssName);
        }
        return cssVal;
    }

    private static String getTextDecorationPattern(String style) {
        String cssName = "text-decoration";
        String cssVal = getHtmlCssValue(style, cssName);
        return cssVal;
    }

    private static String getTextFontPattern(String style) {
        String cssName = "font-weight";
        String cssVal = getHtmlCssValue(style, cssName);
        return cssVal;
    }


    @Override
    public void startHandleTag(Editable text, Attributes attributes) {
        String style = attributes.getValue("", "style");
        if (TextUtils.isEmpty(style)) return;


        String textColorPattern = getTextColorPattern(style);
        if (!TextUtils.isEmpty(textColorPattern)) {
            int c = getHtmlColor(textColorPattern);
            c = c | 0xFF000000;
            start(text, new ForegroundColorSpan(c));

        }

        startMarkTextFont(text, style);

        String backgroundColorPattern = getBackgroundColorPattern(style);
        if (!TextUtils.isEmpty(backgroundColorPattern)) {
            int c = getHtmlColor(backgroundColorPattern);
            c = c | 0xFF000000;
            //注意，第二个参数可以为任意Object类型，这里起到标记的作用
            start(text, new Background(c));
        }

    }

    private void startMarkTextFont(Editable text, String style) {

        String fontSize = getTextFontSizePattern(style);
        String textDecoration = getTextDecorationPattern(style);
        String fontWidget = getTextFontPattern(style);

        int textSize = -1;
        if (!TextUtils.isEmpty(fontSize)) {
            textSize = getHtmlSize(fontSize);
        }
        int textDecorationVal = -1;
        if (!TextUtils.isEmpty(textDecoration)) {
            if (textDecoration.equals("underline")) {
                textDecorationVal = TextFontSpan.TextDecoration_UNDERLINE;
            } else if (textDecoration.equals("line-through")) {
                textDecorationVal = TextFontSpan.TextDecoration_LINE_THROUGH;
            } else if (textDecoration.equals("overline")) {
                textDecorationVal = TextFontSpan.TextDecoration_OVERLINE;//暂不支持
            } else if (textDecoration.equals("none")) {
                textDecorationVal = TextFontSpan.TextDecoration_NONE;
            }
        }
        int fontWeidgtVal = -1;
        if (!TextUtils.isEmpty(fontWidget)) {
            if (textDecoration.equals("normal")) {
                fontWeidgtVal = TextFontSpan.FontWidget_NORMAL;
            } else if (textDecoration.equals("bold")) {
                fontWeidgtVal = TextFontSpan.FontWidget_BOLD;
            }
        }

        start(text, new Font(textSize, textDecorationVal, fontWeidgtVal));
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
