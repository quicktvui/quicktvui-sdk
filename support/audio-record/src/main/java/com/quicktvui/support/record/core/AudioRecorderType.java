package com.quicktvui.support.record.core;

/**
 * 格式
 */
public enum AudioRecorderType {

    AUDIO_RECORDER_TYPE_MP3(0, ".mp3"),
    AUDIO_RECORDER_TYPE_WAV(1, ".wav"),
    AUDIO_RECORDER_TYPE_PCM(2, ".pcm");

    private int type;
    private String fileNameSuffix;

    AudioRecorderType(int type, String fileNameSuffix) {
        this.type = type;
        this.fileNameSuffix = fileNameSuffix;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileNameSuffix() {
        return fileNameSuffix;
    }

    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
    }
}
