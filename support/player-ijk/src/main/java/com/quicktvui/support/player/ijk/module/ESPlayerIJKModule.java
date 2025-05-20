package com.quicktvui.support.player.ijk.module;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.player.ijk.player.IjkLibManager;

import com.quicktvui.support.player.ijk.setting.Settings;
import com.quicktvui.support.player.ijk.utils.DisplayAndDecodeTools;
import com.quicktvui.support.player.ijk.utils.PlayerThreadTools;
import com.quicktvui.support.player.ijk.utils.TimedHelper;

@ESKitAutoRegister
public class ESPlayerIJKModule implements IEsModule, IEsInfo {

    private Context context;
    private String esPackageName;

    @Override
    public void init(Context context) {
        this.context = context.getApplicationContext();
        PlayerThreadTools.getInstance().init();
        esPackageName = EsProxy.get().getEsPackageName(this);
        Settings.addRpkCountMap(esPackageName);
    }

    /**
     * 设置ijkplayer的参数策略
     * @param type
     */
    @Deprecated
    public void setOptionCategory(int type) {
        Settings.setOptionCategory(esPackageName, type);
    }

    public void setBitmapSubSizeType(int type) {
        TimedHelper.setBitmapSubSizeType(esPackageName, type);
    }

    public void getDisplayLevel() {
        DisplayAndDecodeTools.getDisplayLevel(context);
    }

    public void getDecodeLevel() {
        DisplayAndDecodeTools.getDecodeLevel();
    }

    public void getDecodeLevel2() {
        DisplayAndDecodeTools.getDecodeLevel2();
    }

    public void usePlayerThread(boolean useThread) {
        PlayerThreadTools.useThread = useThread;
    }


    public void setIjkDynamically(boolean isDynamically) {
        IjkLibManager.getInstance().setIjkDynamically(isDynamically);
    }

    public void setIjkSoTagType(int tagType) {
        IjkLibManager.getInstance().setIjkSoTagType(tagType);
    }

    public void setIjkSoTag(String ijkSoTag) {
        IjkLibManager.getInstance().setIjkSoTag(ijkSoTag);
    }

    @Override
    public void destroy() {
        PlayerThreadTools.getInstance().quit();
        Settings.removeRpkCountMap(esPackageName);
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        /*try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, BuildConfig.ES_KIT_BUILD_TAG_COUNT);
            map.pushString(IEsInfo.ES_PROP_INFO_PACKAGE_NAME, BuildConfig.LIBRARY_PACKAGE_NAME);
            map.pushString(IEsInfo.ES_PROP_INFO_CHANNEL, BuildConfig.ES_KIT_BUILD_TAG_CHANNEL);
            map.pushString(IEsInfo.ES_PROP_INFO_BRANCH, BuildConfig.ES_KIT_BUILD_TAG);
            map.pushString(IEsInfo.ES_PROP_INFO_COMMIT_ID, BuildConfig.ES_KIT_BUILD_TAG_ID);
            map.pushString(IEsInfo.ES_PROP_INFO_RELEASE_TIME, BuildConfig.ES_KIT_BUILD_TAG_TIME);
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        promise.resolve(map);
    }

}
