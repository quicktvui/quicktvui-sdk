package com.quicktvui.sdk.base.image;

import android.content.Context;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by weipeng on 2022/03/01 16:14
 */
public interface IEsImageLoader {

    void loadImage(Context context, EsMap params, EsCallback<Object, Exception> callback);

    void destroy(Context context);

}
