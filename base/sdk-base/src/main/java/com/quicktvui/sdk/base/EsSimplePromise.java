package com.quicktvui.sdk.base;

/**
 *
 */
public abstract class EsSimplePromise implements EsPromise {

    @Override
    public boolean isCallback() {
        return false;
    }

    @Override
    public String getCallId() {
        return null;
    }

    @Override
    public void setTransferType(int type) {

    }

    @Override
    public Object getProxy() {
        return null;
    }
}
