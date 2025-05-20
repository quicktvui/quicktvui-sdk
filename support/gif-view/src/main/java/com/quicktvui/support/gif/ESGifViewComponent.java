package com.quicktvui.support.gif;

import android.content.Context;
import com.quicktvui.rastermill.FrameSequenceDrawable;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;

import com.quicktvui.giflibrary.GifImageLoader;

@ESKitAutoRegister
public class ESGifViewComponent implements IEsComponent<GifView> {

    private Context context;

    @Override
    public GifView createView(Context context, EsMap params) {
        this.context = context;
        return new GifView(context);
    }


    @EsComponentAttribute
    public void url(GifView gifView, String url) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            GifImageLoader.display(context, url, gifView, FrameSequenceDrawable.LOOP_INF);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatchFunction(GifView view, String eventName, EsArray params, EsPromise promise) {

    }

    @Override
    public void destroy(GifView view) {

    }
}
