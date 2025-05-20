package com.quicktvui.support.core.module.develop;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 *
 */
@ESKitAutoRegister
public class AndroidDevelopModule implements IEsModule, IEsInfo {

    private AndroidDevelopManager developManager;

    @Override
    public void init(Context context) {
        this.developManager = AndroidDevelopManager.getInstance();
    }

    public void getDevelop(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        EsMap esMap = new EsMap();
        esMap.pushString("appId", develop.getAppId());
        esMap.pushString("appKey", develop.getAppKey());
        esMap.pushString("packageName", develop.getPackageName());
        esMap.pushString("versionName", develop.getVersionName());
        esMap.pushInt("versionCode", develop.getVersionCode());
        esMap.pushString("channel", develop.getChannel());
        esMap.pushString("releaseTime", develop.getReleaseTime());
        promise.resolve(esMap);
    }

    public void getAppId(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        if (promise != null && develop != null) {
            promise.resolve(develop.getAppId());
        }
    }

    public void getAppKey(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        if (promise != null && develop != null) {
            promise.resolve(develop.getAppKey());
        }
    }

    public void getPackageName(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        if (promise != null && develop != null) {
            promise.resolve(develop.getPackageName());
        }
    }

    public void getVersionName(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        if (promise != null && develop != null) {
            promise.resolve(develop.getVersionName());
        }
    }

    public void getVersionCode(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        if (promise != null && develop != null) {
            promise.resolve(develop.getVersionCode());
        }
    }

    public void getChannel(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        if (promise != null && develop != null) {
            promise.resolve(develop.getChannel());
        }
    }

    public void getReleaseTime(EsPromise promise) {
        Develop develop = developManager.getDevelop();
        if (promise != null && develop != null) {
            promise.resolve(develop.getReleaseTime());
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
            map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }

    @Override
    public void destroy() {

    }
}
