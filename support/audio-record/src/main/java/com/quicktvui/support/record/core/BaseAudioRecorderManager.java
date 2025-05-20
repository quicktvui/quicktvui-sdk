package com.quicktvui.support.record.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.quicktvui.support.record.core.naming.FileNameGenerator;
import com.quicktvui.support.record.utils.Preconditions;


/**
 *
 */
public abstract class BaseAudioRecorderManager implements IAudioRecorder {

    private static final String TAG = BaseAudioRecorderManager.class.getSimpleName();
    protected List<AudioRecorderListener> listenerList =
            Collections.synchronizedList(new ArrayList<>());
    private AudioRecordConfiguration configuration;
    private FileNameGenerator fileNameGenerator;
    private AudioRecorderType audioRecorderType;

    @Override
    public void init(AudioRecordConfiguration configuration) {
        if (this.configuration != null) {
            return;
        }
        this.configuration = configuration;
        this.fileNameGenerator = configuration.audioFileNameGenerator;
        this.audioRecorderType = configuration.audioRecorderType;
    }

    public AudioRecordConfiguration getConfiguration() {
        return configuration;
    }

    public FileNameGenerator getFileNameGenerator() {
        return fileNameGenerator;
    }

    public AudioRecorderType getAudioRecorderType() {
        return audioRecorderType;
    }

    @Override
    public void release() {
        if (listenerList != null) {
            listenerList.clear();
        }
    }

    @Override
    public void registerAudioRecorderListener(AudioRecorderListener listener) {
        Preconditions.checkNotNull(listener);
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    @Override
    public void unregisterAudioRecorderListener(AudioRecorderListener listener) {
        Preconditions.checkNotNull(listener);
        listenerList.remove(listener);
    }

    protected void notifyAudioRecorderStatusChanged(
            AudioRecorderStatus recorderStatus) {
        if (listenerList != null && listenerList.size() > 0) {
            for (AudioRecorderListener listener : listenerList) {
                try {
                    listener.onAudioRecorderStatusChanged(recorderStatus);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void notifyAudioRecorderVolumeChanged(int volume) {
        if (listenerList != null && listenerList.size() > 0) {
            for (AudioRecorderListener listener : listenerList) {
                try {
                    listener.onAudioRecorderVolumeChanged(volume);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
