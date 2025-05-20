package com.quicktvui.support.asyncplayer;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;


/**
 *
 */
@ESKitAutoRegister
public class ESAsyncPlayerModule implements IEsModule, IEsInfo {

    private AsyncPlayer asyncPlayer;
    private Context context;

    @Override
    public void init(Context context) {
        this.context = context;
        this.asyncPlayer = new AsyncPlayer("ESAsyncPlayer");
    }

    public void play(String url, int usage, int contentType, boolean looping, int stream) {

        if (L.DEBUG) {
            L.logD("---------play--------->>>>>" +
                    "url" + url + "----" +
                    "usage" + usage + "----" +
                    "contentType" + contentType + "----" +
                    "looping" + looping + "----" +
                    "stream" + stream + "----"
            );
        }
        Uri uri = Uri.parse(url);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(usage)
                            .setContentType(contentType)
                            .build();
            asyncPlayer.play(this.context, uri, looping, audioAttributes);
        }
        //
        else {
            asyncPlayer.play(this.context, uri, looping, stream);
        }
    }


    public void stop() {
        if (L.DEBUG) {
            L.logD("-----1----stop--------->>>>>");
        }
        if (asyncPlayer != null) {
            if (L.DEBUG) {
                L.logD("-----2----stop--------->>>>>");
            }
            asyncPlayer.stop();
        }
    }

    @Override
    public void destroy() {
        try {
            stop();
            asyncPlayer = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
