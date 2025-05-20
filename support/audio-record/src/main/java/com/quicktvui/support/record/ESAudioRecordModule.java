package com.quicktvui.support.record;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.support.record.core.AudioRecordConfiguration;
import com.quicktvui.support.record.core.AudioRecorderListener;
import com.quicktvui.support.record.core.AudioRecorderStatus;
import com.quicktvui.support.record.core.IAudioRecorder;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.media.audio.mp3.AudioRecorderMp3Manager;

@ESKitAutoRegister
public class ESAudioRecordModule implements IEsModule, IEsInfo, AudioRecorderListener {

    public static final String EVENT_PROP_AUDIO_RECORDER_TYPE = "audioRecorderType";
    public static final String EVENT_PROP_AUDIO_RECORDER_STATE = "audioRecorderStatus";
    public static final String EVENT_PROP_AUDIO_RECORDER_FILE_PATH = "audioRecorderFile";
    public static final String EVENT_PROP_AUDIO_RECORDER_VOLUME = "audioRecorderVolume";

    public enum Events {
        EVENT_ON_AUDIO_RECORDER_STATUS_CHANGED("onAudioRecorderStatusChanged"),//
        EVENT_ON_AUDIO_RECORDER_VOLUME_CHANGED("onAudioRecorderVolumeChanged");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private IAudioRecorder audioRecorder;
    private Context context;

    @Override
    public void init(Context context) {
        this.context = context;
    }

    public void initDefaultAudioRecorder() {
        try {
            if (audioRecorder != null) {
                if (L.DEBUG) {
                    L.logD(this + "#-----initDefaultAudioRecorder---已经初始化过->>>");
                }
                return;
            }

            String rootPath = EsProxy.get().getEsAppPath(this);
            File audioCacheDir = new File(rootPath + File.separator + "audio");
            try {
                if (!audioCacheDir.exists()) {
                    audioCacheDir.mkdirs();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            AudioRecordConfiguration audioRecordConfiguration = new AudioRecordConfiguration.Builder(context).setAudioCacheDir(audioCacheDir).build();
            this.audioRecorder = AudioRecorderMp3Manager.getInstance();
            this.audioRecorder.init(audioRecordConfiguration);
            this.audioRecorder.registerAudioRecorderListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void initAudioRecorder(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, String cachePath) {
        try {
            if (audioRecorder != null) {
                if (L.DEBUG) {
                    L.logD(this + "#-----initAudioRecorder---已经初始化过->>>");
                }
                return;
            }

            String rootPath = EsProxy.get().getEsAppPath(this);
            if (TextUtils.isEmpty(cachePath)) {
                cachePath = "audio";
            }
            File audioCacheDir = new File(rootPath + cachePath);
            try {
                if (!audioCacheDir.exists()) {
                    audioCacheDir.mkdirs();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            AudioRecordConfiguration audioRecordConfiguration = new AudioRecordConfiguration.Builder(context).setAudioSource(audioSource).setChannelConfig(channelConfig).setAudioFormat(audioFormat).setSampleRateInHz(sampleRateInHz).setAudioCacheDir(audioCacheDir).build();
            this.audioRecorder = AudioRecorderMp3Manager.getInstance();
            this.audioRecorder.init(audioRecordConfiguration);
            this.audioRecorder.registerAudioRecorderListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void startRecord() {
        try {
            if (audioRecorder == null) {
                return;
            }
            if (L.DEBUG) {
                L.logD(this + "#-----startRecorder---->>>");
            }
            audioRecorder.startRecord();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        try {
            if (audioRecorder == null) {
                return;
            }
            if (L.DEBUG) {
                L.logD(this + "#-----stopRecorder---->>>");
            }
            audioRecorder.stopRecord();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void isRecording(EsPromise promise) {
        try {
            if (audioRecorder == null) {
                promise.resolve(false);
                return;
            }
            if (L.DEBUG) {
                L.logD(this + "#-----isRecording---->>>");
            }
            promise.resolve(audioRecorder.isRecording());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void cancelRecord() {
        try {
            if (audioRecorder == null) {
                return;
            }
            if (L.DEBUG) {
                L.logD(this + "#-----cancelRecorder---->>>");
            }
            audioRecorder.cancelRecord();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            if (L.DEBUG) {
                L.logD(this + "#-----release---->>>");
            }
            if (audioRecorder != null) {
                audioRecorder.unregisterAudioRecorderListener(this);
                audioRecorder.release();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        release();
    }

    @Override
    public void onAudioRecorderStatusChanged(AudioRecorderStatus status) {
        EsMap esMap = new EsMap();
        esMap.pushInt(EVENT_PROP_AUDIO_RECORDER_TYPE, status.getAudioRecorderType().getType());
        esMap.pushInt(EVENT_PROP_AUDIO_RECORDER_STATE, status.getStatus().ordinal());
        esMap.pushString(EVENT_PROP_AUDIO_RECORDER_FILE_PATH, status.getAudioRecorderFile());
        if (L.DEBUG) {
            L.logD(this + "#-----onAudioRecorderStatusChanged---->>>" + esMap);
        }
        EsProxy.get().sendNativeEventTraceable(
                this,
                Events.EVENT_ON_AUDIO_RECORDER_STATUS_CHANGED.toString(),
                esMap);
    }

    @Override
    public void onAudioRecorderVolumeChanged(int volume) {
        EsMap esMap = new EsMap();
        esMap.pushInt(EVENT_PROP_AUDIO_RECORDER_VOLUME, volume);
        if (L.DEBUG) {
            L.logD(this + "#-----onAudioRecorderVolumeChanged---->>>" + esMap);
        }
        EsProxy.get().sendNativeEventTraceable(
                this,
                Events.EVENT_ON_AUDIO_RECORDER_VOLUME_CHANGED.toString(),
                esMap);
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
