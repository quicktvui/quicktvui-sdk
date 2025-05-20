package com.quicktvui.support.record.wav;

public interface AudioRecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);
}
