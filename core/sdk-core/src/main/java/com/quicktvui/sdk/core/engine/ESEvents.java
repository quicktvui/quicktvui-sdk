package com.quicktvui.sdk.core.engine;

import com.sunrain.toolkit.utils.log.L;

import java.util.HashMap;
import java.util.Map;

import com.quicktvui.sdk.base.EsNativeEventCallback;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 * Created by WeiPeng on 2023-11-03 16:12
 */
public class ESEvents {

    //region Java层接收Vue事件

    private static final Map<Integer, EsNativeEventCallback> sNativeEvents = new HashMap<>();

    public static synchronized void on(int engineId, EsNativeEventCallback listener) {
        if (listener == null) {
            L.logEF("on event error, listener is null");
            return;
        }
        sNativeEvents.put(engineId, listener);
    }

    public static synchronized EsNativeEventCallback getNativeEvent(int engineId) {
        return sNativeEvents.get(engineId);
    }

    //endregion

    /** 解绑引擎事件 **/
    static synchronized void off(int engineId) {
        sNativeEvents.remove(engineId);
    }

}
