package com.quicktvui.base.ui.tag;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;

public abstract class HtmlTag {

    private Context context;

    public HtmlTag(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    private static final Map<String, Integer> sColorNameMap;

    static {
        sColorNameMap = new HashMap<>();
        sColorNameMap.put("black", Color.BLACK);
        sColorNameMap.put("darkgray", Color.DKGRAY);
        sColorNameMap.put("gray", Color.GRAY);
        sColorNameMap.put("lightgray", Color.LTGRAY);
        sColorNameMap.put("white", Color.WHITE);
        sColorNameMap.put("red", Color.RED);
        sColorNameMap.put("green", Color.GREEN);
        sColorNameMap.put("blue", Color.BLUE);
        sColorNameMap.put("yellow", Color.YELLOW);
        sColorNameMap.put("cyan", Color.CYAN);
        sColorNameMap.put("magenta", Color.MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", Color.DKGRAY);
        sColorNameMap.put("grey", Color.GRAY);
        sColorNameMap.put("lightgrey", Color.LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", 0xFF800080);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);
        sColorNameMap.put("white", Color.WHITE);
        sColorNameMap.put("transparent", Color.TRANSPARENT);

    }

    @ColorInt
    public static int getHtmlColor(String colorString) {

        if (sColorNameMap.containsKey(colorString.toLowerCase())) {
            Integer colorInt = sColorNameMap.get(colorString);
            if (colorInt != null) return colorInt;
        }

        return parseHtmlColor(colorString.toLowerCase());
    }

    @ColorInt
    public static int parseHtmlColor(String colorString) {

        if (colorString.charAt(0) == '#') {
            if (colorString.length() == 4) {
                StringBuilder sb = new StringBuilder("#");
                for (int i = 1; i < colorString.length(); i++) {
                    char c = colorString.charAt(i);
                    sb.append(c).append(c);
                }
                colorString = sb.toString();
            }
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() == 9) {

                int alpha = Integer.parseInt(colorString.substring(1, 3), 16);
                int red = Integer.parseInt(colorString.substring(3, 5), 16);
                int green = Integer.parseInt(colorString.substring(5, 7), 16);
                int blue = Integer.parseInt(colorString.substring(7, 8), 16);
                color = Color.argb(alpha, red, green, blue);
            } else {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        } else if (colorString.startsWith("rgb(") || colorString.startsWith("rgba(") && colorString.endsWith(")")) {
            colorString = colorString.substring(colorString.indexOf("("), colorString.indexOf(")"));
            colorString = colorString.replaceAll(" ", "");
            String[] colorArray = colorString.split(",");
            if (colorArray.length == 3) {
                return Color.argb(255, Integer.parseInt(colorArray[0]), Integer.parseInt(colorArray[1]), Integer.parseInt(colorArray[2]));
            } else if (colorArray.length == 4) {
                return Color.argb(Integer.parseInt(colorArray[3]), Integer.parseInt(colorArray[0]), Integer.parseInt(colorArray[1]), Integer.parseInt(colorArray[2]));
            }

        }
        throw new IllegalArgumentException("Unknown color");
    }

    public int getHtmlSize(String fontSize) {
        fontSize = fontSize.toLowerCase();
        if (fontSize.endsWith("px")) {
            return (int) Double.parseDouble(fontSize.substring(0, fontSize.indexOf("px")));
        } else if (fontSize.endsWith("sp")) {
            float sp = (float) Double.parseDouble(fontSize.substring(0, fontSize.indexOf("sp")));
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
        } else if (TextUtils.isDigitsOnly(fontSize)) {  //如果不带单位，默认按照sp处理
            float sp = (float) Double.parseDouble(fontSize);
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
        }
        return -1;
    }

    public static <T> T getLast(Spanned text, Class<T> kind) {

        T[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            return objs[objs.length - 1];
        }
    }

    public static void start(Editable text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);  //添加标记在最后一位，注意开始位置和结束位置
    }

    @SuppressWarnings("unchecked")
    public static void end(Editable text, Class kind, Object repl) {
        Object obj = getLast(text, kind); //读取kind类型
        if (obj != null) {
            setSpanFromMark(text, obj, repl);
        }
    }

    private static void setSpanFromMark(Spannable text, Object mark, Object... spans) {
        int where = text.getSpanStart(mark);
        text.removeSpan(mark);
        //移除原有标记，因为原有标记不是默认的24种ParcelableSpan子类，因此无法渲染文本
        int len = text.length();
        if (where != len) {
            for (Object span : spans) {
                text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //注意：开始位置和结束位置，因为SpannableStringBuilder的append添加字符方法导致len已经大于where了
            }
        }
    }

    public abstract void startHandleTag(Editable text, Attributes attributes);  //开始解析

    public abstract void endHandleTag(Editable text);  //结束解析


    public static class Font {  //定义标记
        int textSize;
        int textDecordation;
        int fontWeidght;

        public Font(int textSize) {
            this.textSize = textSize;
        }

        public Font(int textSize, int textDecordation, int fontWeidght) {
            this.textSize = textSize;
            this.textDecordation = textDecordation;
            this.fontWeidght = fontWeidght;
        }
    }

    public static class Background { //定义标记
        int color;

        public Background(int color) {
            this.color = color;
        }
    }
}
