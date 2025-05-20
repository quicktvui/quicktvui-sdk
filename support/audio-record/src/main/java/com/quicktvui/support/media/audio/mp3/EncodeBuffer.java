package com.quicktvui.support.media.audio.mp3;

public class EncodeBuffer {

    private short[] rawData;
    private int readSize;

    public EncodeBuffer(short[] rawData, int readSize) {
        this.rawData = rawData.clone();
        this.readSize = readSize;
    }

    public short[] getData() {
        return rawData;
    }

    public int getReadSize() {
        return readSize;
    }

    @Override
    public String toString() {
        return "EncodeBuffer{" +
                ", readSize=" + readSize +
                '}';
    }
}
