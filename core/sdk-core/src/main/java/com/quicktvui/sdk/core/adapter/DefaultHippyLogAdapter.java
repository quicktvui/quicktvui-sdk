package com.quicktvui.sdk.core.adapter;

import com.quicktvui.sdk.core.ext.log.ILogCallback;
import com.quicktvui.sdk.core.internal.EsContext;
import com.tencent.mtt.hippy.adapter.DefaultLogAdapter;

import java.util.Set;

/**
 * <br>
 *
 * <br>
 */
public class DefaultHippyLogAdapter extends DefaultLogAdapter {

    @Override
    public void log(String tag, String msg) {
        super.log(tag, msg);

        Set<ILogCallback> logCallbacks = EsContext.get().getLogCallbacks();
        if (logCallbacks == null) return;

        for (ILogCallback logCallback : logCallbacks) {
            logCallback.log(tag, msg);
        }
    }
}
