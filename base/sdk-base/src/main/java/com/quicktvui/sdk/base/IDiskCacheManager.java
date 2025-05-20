package com.quicktvui.sdk.base;

import com.quicktvui.sdk.base.model.LocalCacheInfo;

/**
 *  快应用缓存管理
 */
public interface IDiskCacheManager {

    /** 获取快应用缓存信息 **/
    void getEsCacheInfo(EsSingleCallback<LocalCacheInfo> callback);

    /** 清除所有 **/
    void clearAllCache(LocalCacheInfo info);

    /** 清除缓存 **/
    void clearCache(LocalCacheInfo.CacheInfo... info);

}
