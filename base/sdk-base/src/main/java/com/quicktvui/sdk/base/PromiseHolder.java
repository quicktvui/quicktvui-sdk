package com.quicktvui.sdk.base;

import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by weipeng on 2021/12/28 11:32
 */
public class PromiseHolder {

    private final EsMap data = new EsMap();
    private EsPromise promise;

    public PromiseHolder() {
    }

    public PromiseHolder(EsPromise promise) {
        this.promise = promise;
    }

    public static PromiseHolder create(EsPromise promise){
        return new PromiseHolder(promise);
    }

    public static PromiseHolder create(){
        return new PromiseHolder();
    }

    public PromiseHolder put(EsMap v){
        data.pushAll(v);
        return this;
    }

    public PromiseHolder put(String k, Object v){
        try {
            data.pushObject(k,v);
        } catch (Exception e) {
           data.pushObject(k, e.getMessage());
        }
        return this;
    }

    public PromiseHolder singleData(Object v) {
        put("data", v);
        return this;
    }

    public PromiseHolder message(Object v){
        put("msg", v);
        return this;
    }

    public EsMap getData(){
        return data;
    }

    public void sendSuccess() {
        if(promise == null) return;
        promise.resolve(data);
    }

    public void sendFailed() {
        if(promise == null) return;
        promise.reject(data);
    }

}