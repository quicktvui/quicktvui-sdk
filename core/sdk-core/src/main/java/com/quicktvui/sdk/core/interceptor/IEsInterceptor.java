package com.quicktvui.sdk.core.interceptor;

import android.support.annotation.Keep;

import com.sunrain.toolkit.bolts.tasks.TaskCompletionSource;

import com.quicktvui.sdk.core.EsData;

/**
 *
 */
@Keep
public interface IEsInterceptor<T> {

    T intercept(EsData startData, Chain<T> chain);

    @Keep
    class Chain<T> {

        T data;
        private final TaskCompletionSource<Boolean> source;

        public Chain(T data, TaskCompletionSource<Boolean> source) {
            this.data = data;
            this.source = source;
        }

        public T getData() {
            return data;
        }

        public void toContinue(){
            source.setResult(true);
        }

        public void toBreak(){
            source.setResult(false);
        }
    }

}
