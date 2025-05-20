package com.quicktvui.support.media.audio.mp3;

import com.quicktvui.support.record.core.AudioRecordConfiguration;
import com.quicktvui.support.record.core.AudioRecorderListener;
import com.quicktvui.support.record.core.AudioRecorderStatus;
import com.quicktvui.support.record.core.AudioRecorderStatusEnum;
import com.quicktvui.support.record.core.BaseAudioRecorderManager;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public class AudioRecorderMp3Manager extends BaseAudioRecorderManager
        implements AudioRecorderListener {

    private static final String TAG = "AudioRecorderMp3Manager";
    private static AudioRecorderMp3Manager instance;
    private AudioRecorderMp3 audioRecorder;

    private AudioRecorderMp3Manager() {
        audioRecorder = new AudioRecorderMp3();
        audioRecorder.setAudioRecorderListener(this);
    }

    public static AudioRecorderMp3Manager getInstance() {
        synchronized (AudioRecorderMp3Manager.class) {
            if (instance == null) {
                instance = new AudioRecorderMp3Manager();
            }
        }
        return instance;
    }

    @Override
    public void init(AudioRecordConfiguration configuration) {
        super.init(configuration);
        if (L.DEBUG) {
            L.logD("#---------init---------->>>");
        }
    }


    @Override
    public void startRecord() {
        if (audioRecorder == null) {
            if (L.DEBUG) {
                L.logD("#---------startRecorder---audioRecorder is null------->>>");
            }
            return;
        }

        if (audioRecorder.isRecording()) {
            if (L.DEBUG) {
                L.logD("#---------startRecorder-----正在录音返回----->>>");
            }
            return;
        }
        try {
            String key = System.currentTimeMillis() + "";
            String name = getFileNameGenerator().generate(key, getAudioRecorderType());
            String absolutePath = getConfiguration().audioCacheDir.getAbsolutePath();

            try {
                if (getConfiguration() != null) {
                    File cacheDir = getConfiguration().audioCacheDir;
                    if (cacheDir != null && !cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (L.DEBUG) {
                L.logD("#---------startRecorder---------->>>" + absolutePath + name);
            }

            File recorderFile = new File(absolutePath, name);
            //
            audioRecorder.startRecording(recorderFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRecording() {
        return audioRecorder != null && audioRecorder.isRecording();
    }

    @Override
    public void stopRecord() {
        try {
            if (L.DEBUG) {
                L.logD("#---------stopRecorder---------->>>");
            }
            audioRecorder.stopRecording();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelRecord() {
        try {
            if (L.DEBUG) {
                L.logD("#---------cancelRecorder---------->>>");
            }
            audioRecorder.cancelRecorder();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        if (L.DEBUG) {
            L.logD("#---------release---------->>>");
        }
    }

    @Override
    public void onAudioRecorderVolumeChanged(int volume) {
        notifyAudioRecorderVolumeChanged(volume);
    }

    @Override
    public void onAudioRecorderStatusChanged(AudioRecorderStatus status) {
        if (status.getStatus() == AudioRecorderStatusEnum.AUDIO_RECORDER_STATUS_SUCCESS) {
            if (L.DEBUG) {
                L.logD(this + "#-----------onAudioRecorderStatusChanged---->>>" + status);
            }
        }
        notifyAudioRecorderStatusChanged(status);
    }
}
