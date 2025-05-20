package com.quicktvui.sdk.base;

public interface EsPromise {
    void resolve(Object value);

    void reject(Object error);

    boolean isCallback();

    String getCallId();

    void setTransferType(int type);

    Object getProxy();
}