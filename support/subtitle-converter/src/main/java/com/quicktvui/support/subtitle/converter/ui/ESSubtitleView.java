package com.quicktvui.support.subtitle.converter.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.subtitle.converter.R;


public class ESSubtitleView extends LinearLayout implements IEsComponentView {
    private Context context;
    private View subTitleView;
    private TextView background_textView, foreground_textView;
//    private StrokeTextView content;

    public ESSubtitleView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
//        L.logIF("getContext:" + context);
//        L.logIF("getContext:" + EsProxy.get().getContext());
//        L.logIF("getContext:" + App.getInstance());
        subTitleView = View.inflate(EsProxy.get().getContext(), R.layout.es_subtitle_view, null);
        background_textView = subTitleView.findViewById(R.id.background_textView);
        foreground_textView = subTitleView.findViewById(R.id.foreground_textView);
        TextPaint textPaint = background_textView.getPaint();
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(2);
//        content = subTitleView.findViewById(R.id.subtitle_content);
//        content.setTextColor(Color.WHITE);
//        content.setTextSize(22);
        addView(subTitleView);
    }

    public void setText(String text) {
        if (background_textView != null) {
            background_textView.setText(text);
            foreground_textView.setText(text);
        }
    }

    public void setTextSize(float size) {
        if (background_textView != null) {
            background_textView.setTextSize(size);
            foreground_textView.setTextSize(size);
        }
    }

    public void setTextSize(int unit, float size) {
        if (background_textView != null) {
            background_textView.setTextSize(unit, size);
            foreground_textView.setTextSize(unit, size);
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        if (background_textView != null) {
            background_textView.setPadding(left, top, right, bottom);
            foreground_textView.setPadding(left, top, right, bottom);
        }
    }

    public void setEllipsize(TextUtils.TruncateAt where) {
        if (background_textView != null) {
            background_textView.setEllipsize(where);
            foreground_textView.setEllipsize(where);
        }
    }

    public void setSingleLine() {
        if (background_textView != null) {
            background_textView.setSingleLine();
            foreground_textView.setSingleLine();
        }
    }

    public void setMarqueeRepeatLimit(int marqueeLimit) {
        if (background_textView != null) {
            background_textView.setMarqueeRepeatLimit(marqueeLimit);
            foreground_textView.setMarqueeRepeatLimit(marqueeLimit);
        }
    }

    public void setLines(int lines) {
        if (background_textView != null) {
            background_textView.setLines(lines);
            foreground_textView.setLines(lines);
        }
    }

    public void setMaxLines(int maxLines) {
        if (background_textView != null) {
            background_textView.setMaxLines(maxLines);
            foreground_textView.setMaxLines(maxLines);
        }
    }

    public void setTextColor(ColorStateList colorStateList) {
        if (foreground_textView != null) {
            foreground_textView.setTextColor(colorStateList);
        }
    }

    public void setTypeStyle(String typeStyle) {
        if (background_textView != null) {
            setTypeStyle(background_textView, typeStyle);
            setTypeStyle(foreground_textView, typeStyle);
        }
    }

    public void setTypeStyle(TextView textView, String type) {
        switch (type) {
            case "bold":
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                break;
            case "italic":
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            default:
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (subTitleView != null) {
            subTitleView.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            subTitleView.layout(0, 0, w, h);
        }
        if (background_textView != null) {
            background_textView.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            background_textView.layout(0, 0, w, h);
        }
        if (foreground_textView != null) {
            foreground_textView.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            foreground_textView.layout(0, 0, w, h);
        }
    }

}
