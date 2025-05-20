package com.quicktvui.sdk.core.card;

import android.support.v4.util.LruCache;

//卡片缓存
public class ESCardCache {
    private final LruCache<String, ESCardBean> cache = new LruCache<>(5);

    public ESCardCache() {
    }

    public ESCardBean get(String cacheKey) {
        return cache.get(cacheKey);
    }

    public void put(String cacheKey, ESCardBean esCardBean) {
        if (esCardBean != null) {
            cache.put(cacheKey, esCardBean);
        }
    }

    public ESCardBean remove(String cacheKey) {
        if (containsKey(cacheKey)) {
            return cache.remove(cacheKey);
        }
        return null;
    }

    public void clear() {
        cache.evictAll();
    }

    public void resize(int size) {
        cache.resize(size);
    }

    public boolean containsKey(String cacheKey) {
        return cache.snapshot().containsKey(cacheKey);
    }
}
