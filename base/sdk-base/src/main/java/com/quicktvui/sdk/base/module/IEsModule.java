package com.quicktvui.sdk.base.module;

import android.content.Context;

import com.quicktvui.sdk.base.IEsTraceable;

/**
 * Create by weipeng on 2022/02/28 18:27
 */
public interface IEsModule extends IEsTraceable {

    void init(Context context);

    void destroy();
}
