package com.quicktvui.support.gif;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.quicktvui.sdk.base.component.IEsComponentView;


public class GifView extends ImageView implements IEsComponentView {

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
