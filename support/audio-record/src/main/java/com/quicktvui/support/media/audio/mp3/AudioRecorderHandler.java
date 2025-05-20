package com.quicktvui.support.media.audio.mp3;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class AudioRecorderHandler extends Handler
        implements AudioRecord.OnRecordPositionUpdateListener {

    public static final int PROCESS_STOP = 1;
    private static final String TAG = AudioRecorderHandler.class.getSimpleName();
    private FileOutputStream outputStream;
    private byte[] decodeBuffer;
    private List<EncodeBuffer> decodeBuffers =
            Collections.synchronizedList(new LinkedList<>());

    public AudioRecorderHandler(File audioRecorderFile, int bufferSize, Looper looper) {
        super(looper);
        try {
            outputStream = new FileOutputStream(audioRecorderFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        decodeBuffer = new byte[(int) (7200 + (bufferSize * 2 * 1.25))];
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == PROCESS_STOP) {
            if (L.DEBUG) {
                L.logD("#---------DataEncodeThread---handleMessage--" +
                        "---->>>" + Thread.currentThread().getName());
            }
            try {
                for (; processData() > 0; ) ;
                removeCallbacksAndMessages(null);
                release();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            getLooper().quit();
        }
    }

    @Override
    public void onMarkerReached(AudioRecord recorder) {

    }

    @Override
    public void onPeriodicNotification(AudioRecord recorder) {
        if (L.DEBUG) {
            L.logD("#---------DataEncodeThread---onPeriodicNotification----" +
                    "-->>>" + Thread.currentThread().getName());
        }
        processData();
    }

    //从缓存区ChangeBuffers里获取待转换的PCM数据，转换为MP3数据,并写入文件
    private int processData() {
        if (decodeBuffers != null && decodeBuffers.size() > 0) {
            EncodeBuffer changeBuffer = decodeBuffers.remove(0);
            short[] buffer = changeBuffer.getData();
            int readSize = changeBuffer.getReadSize();

            if (L.DEBUG) {
                L.logD("#------processData----->>>" + changeBuffer);
            }

            if (readSize > 0) {
                int encodedSize = AndroidMP3Encoder.encode(buffer, buffer, readSize, decodeBuffer);
                if (encodedSize < 0) {
                    if (L.DEBUG) {
                        L.logD("#----mp3--encoded------->>>encodedSize:" + encodedSize);
                    }
                }
                try {
                    outputStream.write(decodeBuffer, 0, encodedSize);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (L.DEBUG) {
                        L.logD("#------write to file error------->>>");
                    }
                }
                return readSize;
            }
        }
        return 0;
    }

    private void release() {
        final int flushResult = AndroidMP3Encoder.flush(decodeBuffer);
        if (flushResult > 0) {
            try {
                outputStream.write(decodeBuffer, 0, flushResult);
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void addDecodeBuffer(short[] rawData, int readSize) {
        decodeBuffers.add(new EncodeBuffer(rawData, readSize));
    }
}
