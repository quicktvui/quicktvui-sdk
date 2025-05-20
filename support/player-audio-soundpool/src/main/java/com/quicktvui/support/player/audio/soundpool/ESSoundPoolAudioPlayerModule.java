package com.quicktvui.support.player.audio.soundpool;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;



/**
 *
 */
@ESKitAutoRegister
public class ESSoundPoolAudioPlayerModule implements IEsModule, IEsInfo,
        SoundPoolManager.SoundPoolListener {

    public enum Events {
        EVENT_ON_LOAD_ERROR("onESSoundPoolLoadError"),
        EVENT_ON_LOAD_COMPLETE("onESSoundPoolLoadComplete");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private SoundPoolManager soundPoolManager;

    @Override
    public void init(Context context) {
        soundPoolManager = SoundPoolManager.getInstance();
        soundPoolManager.registerSoundPoolListener(this);
    }

    @Override
    public void onSoundLoadComplete(int sampleId, int status) {
        EsMap esMap = new EsMap();
        esMap.pushInt("sampleId", sampleId);
        esMap.pushInt("status", status);
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_LOAD_COMPLETE.toString(), esMap);
    }

    @Override
    public void onSoundLoadError(String url) {
        EsMap esMap = new EsMap();
        esMap.pushString("url", url);
        EsProxy.get().sendNativeEventTraceable(this,
                Events.EVENT_ON_LOAD_ERROR.toString(), esMap);
    }

    public void initSoundPool(int usage, int contentType,
                              int maxStreams, int streamType, boolean enableCache) {
        SoundPoolManager.getInstance().initSoundPool(usage, contentType, maxStreams, streamType, enableCache);
    }

    public void load(String url, EsPromise promise) {
        if (L.DEBUG) {
            L.logD("------1-----load--------->>>>>" +
                    "url" + url + "----"
            );
        }
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.load(url, promise);
    }

    public void unload(int soundID) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.unload(soundID);
    }

    public void play(int soundID, float leftVolume, float rightVolume,
                     int priority, int loop, float rate, EsPromise promise) {
        if (soundPoolManager == null) {
            return;
        }
        int streamID = soundPoolManager.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        promise.resolve(streamID);
    }

    public void pause(int streamID) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.pause(streamID);
    }

    public void autoPause() {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.autoPause();
    }

    public void resume(int streamID) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.resume(streamID);
    }

    public void autoResume() {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.autoResume();
    }

    public void stop(int streamID) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.stop(streamID);
    }

    public void setVolume(int streamID, float leftVolume, float rightVolume) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.setVolume(streamID, leftVolume, rightVolume);
    }

    public void setRate(int streamID, float rate) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.setRate(streamID, rate);
    }

    public void setPriority(int streamID, int priority) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.setPriority(streamID, priority);
    }

    public void setLoop(int streamID, int loop) {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.setLoop(streamID, loop);
    }

    /**
     *
     */
    public void release() {
        if (soundPoolManager == null) {
            return;
        }
        soundPoolManager.release();
    }

    @Override
    public void destroy() {
        if (soundPoolManager != null) {
            soundPoolManager.unregisterSoundPoolListener(this);
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
