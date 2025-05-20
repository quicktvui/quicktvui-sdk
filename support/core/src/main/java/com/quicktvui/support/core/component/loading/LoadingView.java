package com.quicktvui.support.core.component.loading;

import android.content.Context;
import android.widget.ImageView;

import com.quicktvui.sdk.base.component.IEsComponentView;

public final class LoadingView extends ImageView implements IEsComponentView {
    public LoadingView(Context context) {
        super(context);
        setImageDrawable(new LoadingDrawable(this));
    }
}