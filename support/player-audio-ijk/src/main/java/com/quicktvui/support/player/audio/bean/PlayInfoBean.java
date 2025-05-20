package com.quicktvui.support.player.audio.bean;

import com.quicktvui.sdk.base.args.EsMap;


public class PlayInfoBean {
    private boolean hadPlay = false;
    private String mUrl = "";
    private EsMap mExtraInfo = null;

    public PlayInfoBean() {}

    public PlayInfoBean(boolean hadPlay, String url, EsMap extraInfo) {
        this.hadPlay = hadPlay;
        mUrl = url;
        mExtraInfo = extraInfo;
    }

    public boolean isHadPlay() {
        return hadPlay;
    }

    public void setHadPlay(boolean hadPlay) {
        this.hadPlay = hadPlay;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public EsMap getExtraInfo() {
        return mExtraInfo;
    }

    public void setExtraInfo(EsMap mExtraInfo) {
        this.mExtraInfo = mExtraInfo;
    }

    public void reset() {
        hadPlay = false;
        mUrl = "";
        mExtraInfo = null;
    }
}
