package com.quicktvui.sdk.core;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Create by weipeng on 2022/03/14 19:43
 */
public class EsAppContext extends ContextWrapper {
    public EsAppContext(Context base) {
        super(base);
    }
}
