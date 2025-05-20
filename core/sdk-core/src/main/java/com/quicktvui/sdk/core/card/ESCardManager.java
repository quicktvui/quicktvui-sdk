package com.quicktvui.sdk.core.card;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;

import com.quicktvui.sdk.core.utils.DefaultLifecycleObserver;

import java.util.concurrent.ConcurrentHashMap;

public class ESCardManager implements DefaultLifecycleObserver {
    private final static String TAG = "ESCardManager";
    private ConcurrentHashMap<Integer, ESCardCache> cacheMap = new ConcurrentHashMap();
    private static volatile ESCardManager INSTANCE;

    public static ESCardManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ESCardManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ESCardManager();
                }
            }
        }
        return INSTANCE;
    }

    public ESCardManager() {
    }

    //获取当前activity对应的缓存池，如果没有则创建
    public ESCardCache getCardCache(Context context) {
        if (cacheMap == null) {
            cacheMap = new ConcurrentHashMap<>();
        }
        if (cacheMap.containsKey(context.hashCode())) {
            return cacheMap.get(context.hashCode());
        }
        ESCardCache cardCache = new ESCardCache();
        cacheMap.put(context.hashCode(), cardCache);
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
        }
        return cardCache;
    }

    //获取缓存
    public ESCardBean getCache(Context context, String cardId) {
        if (cacheMap != null && cacheMap.containsKey(context.hashCode()) && cacheMap.get(context.hashCode()) != null) {
            return cacheMap.get(context.hashCode()).get(cardId);
        }
        return null;
    }

    //放入缓存池
    public void putCache(Context context, String cacheKey, ESCardBean esCardBean) {
        if (cacheMap != null && cacheMap.get(context.hashCode()) != null && cacheKey != null && esCardBean != null) {
            cacheMap.get(context.hashCode()).put(cacheKey, esCardBean);
        }
    }

    //删除缓存
    public void removeCache(Context context, String cacheKey) {
        if (cacheMap != null && cacheMap.get(context.hashCode()) != null) {
            if (cacheMap.get(context.hashCode()).get(cacheKey) != null && cacheMap.get(context.hashCode()).get(cacheKey).getiEsAppLoadHandler() != null) {
                cacheMap.get(context.hashCode()).get(cacheKey).getiEsAppLoadHandler().onDestroy();
            }
            cacheMap.get(context.hashCode()).remove(cacheKey);
        }
    }

    public void resize(Context context, int newSize) {
        if (cacheMap != null && cacheMap.get(context.hashCode()) != null) {
            cacheMap.get(context.hashCode()).resize(newSize);
        }
    }

    public boolean containsKey(Context context, String cacheKey) {
        if (cacheMap != null && cacheMap.get(context.hashCode()) != null) {
            return cacheMap.get(context.hashCode()).containsKey(cacheKey);
        }
        return false;
    }

    public void clearAllCache(Context context) {
        if (cacheMap != null && cacheMap.get(context.hashCode()) != null) {
            cacheMap.get(context.hashCode()).clear();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        if (owner instanceof LifecycleOwner && cacheMap.containsKey(owner.hashCode())) {
            this.cacheMap.remove(owner.hashCode());
        }
    }

    public void onDestroy() {
        this.cacheMap = null;
    }

}
