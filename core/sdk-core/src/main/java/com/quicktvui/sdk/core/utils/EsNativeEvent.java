package com.quicktvui.sdk.core.utils;

import com.quicktvui.sdk.base.EsEventPacket;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by weipeng on 2022/04/09 17:48
 */
public class EsNativeEvent implements EsEventPacket {

    private final String mEventName;
    private final EsMap mData;
    private final EsPromise mPromise;

    public EsNativeEvent(String name, EsMap data, EsPromise promise) {
        mEventName = name;
        mData = data;
        mPromise = promise;
    }

    public static void process(String name, EsMap data, EsPromise promise) {
        new EsNativeEvent(name, data, promise).handleEvent();
    }

    public void handleEvent() {
//        EsNativeEventListener listener = EsContext.get().getNativeEventListener();
//        if(listener == null){
//            if (L.DEBUG) L.logD("未注册事件监听");
//            listener = event -> event.postValue(new EsMap());
//        }
//        try {
//            listener.onEvent(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public String getEventName() {
        return mEventName;
    }

    @Override
    public EsMap getEventData() {
        return mData;
    }

    @Override
    public void postValue(EsMap data) {
        PromiseHolder.create(mPromise).put(data).sendSuccess();
    }

    @Override
    public String toString() {
        return "EsNativeEvent{" +
                "mEventName='" + mEventName + '\'' +
                ", mData=" + mData +
                ", mPromise=" + mPromise +
                '}';
    }
}
