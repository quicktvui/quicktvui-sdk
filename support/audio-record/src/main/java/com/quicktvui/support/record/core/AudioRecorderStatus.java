package com.quicktvui.support.record.core;

/**
 * 录音状态
 */
public class AudioRecorderStatus {

    private AudioRecorderType audioRecorderType;
    private String audioRecorderFile;

    private AudioRecorderStatusEnum status;

    public AudioRecorderStatus(AudioRecorderStatusEnum status) {
        this.status = status;
    }

    public AudioRecorderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AudioRecorderStatusEnum status) {
        this.status = status;
    }

    public AudioRecorderType getAudioRecorderType() {
        return audioRecorderType;
    }

    public void setAudioRecorderType(AudioRecorderType audioRecorderType) {
        this.audioRecorderType = audioRecorderType;
    }

    public String getAudioRecorderFile() {
        return audioRecorderFile;
    }

    public void setAudioRecorderFile(String audioRecorderFile) {
        this.audioRecorderFile = audioRecorderFile;
    }

    @Override
    public String toString() {
        return "AudioRecorderStatus{" +
                "audioRecorderType=" + audioRecorderType +
                ", audioRecoderFile='" + audioRecorderFile + '\'' +
                ", status=" + status +
                '}';
    }
}
