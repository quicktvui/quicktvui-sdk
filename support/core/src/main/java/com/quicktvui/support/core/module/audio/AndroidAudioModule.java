package com.quicktvui.support.core.module.audio;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

/**
 * 音量调节
 */
@ESKitAutoRegister
public class AndroidAudioModule implements IEsModule, IEsInfo {

    private AudioManager audioManager;

    @Override
    public void init(Context context) {
        try {
            audioManager = (AudioManager) context.getApplicationContext()
                    .getSystemService(Service.AUDIO_SERVICE);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------
    public void requestAudioFocus(int streamType, int durationHint, EsPromise promise) {
        try {
            audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    promise.resolve(focusChange);
                }
            }, streamType, durationHint);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void abandonAudioFocus(EsPromise promise) {
        try {
            audioManager.abandonAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    promise.resolve(focusChange);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    //------------------------通话音量------------------------
    public void getStreamVoiceCallMaxVolume(EsPromise promise) {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        promise.resolve(maxVolume);
    }

    public void getStreamVoiceCallVolume(EsPromise promise) {
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        promise.resolve(volume);
    }


    //------------------------系统音量------------------------
    public void getStreamSystemMaxVolume(EsPromise promise) {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        promise.resolve(maxVolume);
    }

    public void getStreamSystemVolume(EsPromise promise) {
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        promise.resolve(volume);
    }


    //------------------------铃声音量------------------------
    public void getStreamRingMaxVolume(EsPromise promise) {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        promise.resolve(maxVolume);
    }

    public void getStreamRingVolume(EsPromise promise) {
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        promise.resolve(volume);
    }

    //------------------------音乐音量------------------------
    public void getStreamMusicMaxVolume(EsPromise promise) {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        promise.resolve(maxVolume);
    }

    public void getStreamMusicVolume(EsPromise promise) {
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        promise.resolve(volume);
    }

    //------------------------提示音音量------------------------
    public void getStreamAlarmMaxVolume(EsPromise promise) {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        promise.resolve(maxVolume);
    }

    public void getStreamAlarmVolume(EsPromise promise) {
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        promise.resolve(volume);
    }
    //------------------------音量------------------------

    public void getStreamMaxVolume(int streamType, EsPromise promise) {
        int maxVolume = audioManager.getStreamMaxVolume(streamType);
        promise.resolve(maxVolume);
    }

    public void getStreamVolume(int streamType, EsPromise promise) {
        int volume = audioManager.getStreamVolume(streamType);
        promise.resolve(volume);
    }

    //------------------------调整音量------------------------
    public void adjustStreamVolume(EsArray esArray) {
        int streamType = esArray.getInt(0);
        int adjust = esArray.getInt(1);
        int flags = esArray.getInt(2);
        if (L.DEBUG) {
            L.logD("#---------adjustStreamVolume---------->>>"
                    + "----->>>streamType:" + streamType
                    + "----->>>adjust:" + adjust
                    + "----->>>flags:" + flags
            );
        }
        audioManager.adjustStreamVolume(streamType, adjust, flags);
    }

    public void setStreamVolume(EsArray esArray) {
        int streamType = esArray.getInt(0);
        int index = esArray.getInt(1);
        int flags = esArray.getInt(2);

        if (L.DEBUG) {
            L.logD("#---------setStreamVolume---------->>>"
                    + "----->>>streamType:" + streamType
                    + "----->>>index:" + index
                    + "----->>>flags:" + flags
            );
        }
        audioManager.setStreamVolume(streamType, index, flags);
    }

    public void setStreamMute(EsArray esArray) {
        int streamType = esArray.getInt(0);
        boolean state = esArray.getBoolean(1);
        if (L.DEBUG) {
            L.logD("#---------setStreamMute---------->>>"
                    + "----->>>streamType:" + streamType
                    + "----->>>state:" + state
            );
        }
        audioManager.setStreamMute(streamType, state);
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
