package com.quicktvui.support.record.core;

/**
 *
 */
public interface IAudioRecorder {

    void init(AudioRecordConfiguration configuration);

    void startRecord();

    void stopRecord();

    boolean isRecording();

    void cancelRecord();

    void release();

    void registerAudioRecorderListener(AudioRecorderListener listener);

    void unregisterAudioRecorderListener(AudioRecorderListener listener);
}
