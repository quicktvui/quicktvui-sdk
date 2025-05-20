package com.quicktvui.support.record.wav;

public interface AudioRecordListener {
    public void onAudioRecordSuccess(String fileName);

    public void onAudioRecordError();

    public void onAudioRecordPcmToWavSuccess(String fileName);

    public void onAudioRecordPcmToWavError(String fileName);
}
