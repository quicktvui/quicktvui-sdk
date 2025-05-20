package com.quicktvui.sdk.base.model;

import android.support.annotation.Nullable;

/**
 *
 */
public final class LocalCacheInfo {

    private long mTotalSize;

    @Nullable
    private CacheInfo mRpkInfo;         // 快应用信息
    @Nullable
    private CacheInfo mCardInfo;         // 快应用信息
    @Nullable
    private CacheInfo mPlugin;          // 插件信息
    @Nullable
    private CacheInfo mSoInfo;          // 动态so信息

    @Nullable
    private CacheInfo mRuntimeInfo;     // runtime信息

    public long getTotalSize() {
        return mTotalSize;
    }

    @Nullable
    public CacheInfo getRpkCacheInfo() {
        return mRpkInfo;
    }

    public void setRpkCacheInfo(@Nullable CacheInfo info) {
        this.mRpkInfo = info;
        plusTotalSize(info);
    }

    @Nullable
    public CacheInfo getCardInfo() {
        return mCardInfo;
    }

    public LocalCacheInfo setCardInfo(@Nullable CacheInfo info) {
        this.mCardInfo = info;
        return this;
    }

    @Nullable
    public CacheInfo getPluginCacheInfo() {
        return mPlugin;
    }

    public void setPluginCacheInfo(@Nullable CacheInfo info) {
        this.mPlugin = info;
        plusTotalSize(info);
    }

    @Nullable
    public CacheInfo getSoCacheInfo() {
        return mSoInfo;
    }

    public void setSoCacheInfo(@Nullable CacheInfo info) {
        this.mSoInfo = info;
        plusTotalSize(info);
    }

    @Nullable
    public CacheInfo getRuntimeInfo() {
        return mRuntimeInfo;
    }

    public void setRuntimeInfo(@Nullable CacheInfo info) {
        this.mRuntimeInfo = info;
        plusTotalSize(info);
    }

    private void plusTotalSize(@Nullable CacheInfo info){
        if(info == null) return;
        mTotalSize += info.mSize;
    }

    public static final class CacheInfo {
        public long mSize;
        public String mPath;
    }

}
