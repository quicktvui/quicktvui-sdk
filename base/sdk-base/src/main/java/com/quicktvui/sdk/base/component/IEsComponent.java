package com.quicktvui.sdk.base.component;

import android.content.Context;
import android.view.View;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsTraceable;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by weipeng on 2022/02/24 17:48
 */
public interface IEsComponent<V extends View & IEsComponentView> extends IEsTraceable {

    V createView(Context context, EsMap params);

    void dispatchFunction(V view, String eventName, EsArray params, EsPromise promise);

    void destroy(V view);
}