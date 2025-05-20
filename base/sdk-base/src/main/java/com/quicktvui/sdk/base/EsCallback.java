package com.quicktvui.sdk.base;

public interface EsCallback<S, E> {
    void onSuccess(S s);
    void onFailed(E e);
}