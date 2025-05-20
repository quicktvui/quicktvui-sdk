package com.quicktvui.support.record.core;

/**
 *
 */
public interface AudioRecorderListener {

    void onAudioRecorderStatusChanged(AudioRecorderStatus status);

    void onAudioRecorderVolumeChanged(int volume);
}
