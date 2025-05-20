package com.quicktvui.sdk.core.assets;

public final class AssetData {
    public String id;
    public float version;
    public String versionStr;
    public String url;
    public String md5;
    public boolean autoRemove = true;

    // fix > 2.5 so会重复下载问题
    public String abi;

    public AssetData(String id, float version, String url) {
        this.id = id;
        this.version = version;
        this.url = url;
    }

    public AssetData md5(String md5) {
        this.md5 = md5;
        return this;
    }

    public AssetData versionStr(String ver) {
        this.versionStr = ver;
        return this;
    }

    public AssetData autoRemove(boolean remove) {
        this.autoRemove = remove;
        return this;
    }

    public AssetData abi(String abi) {
        this.abi = abi;
        return this;
    }
}