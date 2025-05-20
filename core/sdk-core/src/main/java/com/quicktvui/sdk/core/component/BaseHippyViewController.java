package com.quicktvui.sdk.core.component;

import android.content.Context;
import android.view.View;

import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

import com.quicktvui.sdk.core.internal.EsViewManager;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 * Created by WeiPeng on 2023-10-30 16:09
 */
public abstract class BaseHippyViewController<T extends View & HippyViewBase> extends HippyViewController<T> {

    protected void markObject2Engine(Context context) {
        EsViewManager.get().markObject2Engine(this, ((HippyInstanceContext) context).getEngineContext());
    }

    @Override
    public void onViewDestroy(T t) {
        super.onViewDestroy(t);
        EsViewManager.get().unMarkObject2Engine(this);
    }
}
