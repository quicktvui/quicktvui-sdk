package com.quicktvui.support.core.component.es;

import android.content.Context;
import android.widget.FrameLayout;

import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;

/**
 *
 */
public class ESView extends FrameLayout implements IEsComponentView {

    public ESView(Context context) {
        super(context);
    }

    public void sendUIEvent(String eventName, EsMap event) {
        if (L.DEBUG) {
            L.logD("#---sendUIEvent------>>>" +
                    "eventName:" + eventName + "----->>>" +
                    "event:" + event.toString()
            );
        }
        EsProxy.get().sendUIEvent(getId(), eventName, event);
    }
}
