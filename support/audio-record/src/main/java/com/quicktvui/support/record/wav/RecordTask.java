package com.quicktvui.support.record.wav;

import android.content.Context;
import android.media.AudioRecord;

import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public final class RecordTask implements Runnable {

    private static final String TAG = "RecordTask";

    private AudioRecordListener recordListener;
    private AudioRecordStreamListener recordStreamListener;
    private int bufferSizeInBytes;

    private AudioRecorder.Status status = AudioRecorder.Status.STATUS_NO_READY;

    private AudioRecord audioRecord;
    private String fileName;
    private Context context;

    public RecordTask(Context context,
                      AudioRecord audioRecord,
                      String fileName,
                      int bufferSizeInBytes,
                      AudioRecordListener recordListener,
                      AudioRecordStreamListener recordStreamListener) {
        this.context = context;
        this.fileName = fileName;
        this.audioRecord = audioRecord;
        this.recordListener = recordListener;
        this.recordStreamListener = recordStreamListener;
        this.bufferSizeInBytes = bufferSizeInBytes;
    }

    @Override
    public void run() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];

        FileOutputStream fos = null;
        int readsize = 0;
        try {
            String currentFileName = fileName;
            File file = new File(FileUtil.getPcmFileAbsolutePath(this.context, currentFileName));
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (IllegalStateException e) {
            if (L.DEBUG) {
                L.logD("#-------error------>>>>>" + e.getMessage());
            }
            throw new IllegalStateException(e.getMessage());
        } catch (FileNotFoundException e) {
            if (L.DEBUG) {
                L.logD("#-------error------>>>>>" + e.getMessage());
            }

        }
        //将录音状态设置成正在录音状态
        status = AudioRecorder.Status.STATUS_START;
        while (status == AudioRecorder.Status.STATUS_START) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata);
                    if (L.DEBUG) {
                        L.logD("#-------录音中---->>>>>" + audiodata.length);
                    }
                    if (recordStreamListener != null) {
                        recordStreamListener.recordOfByte(audiodata, 0, audiodata.length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (L.DEBUG) {
                        L.logD("#-------error--录音错误---->>>>>" + e.getMessage());
                    }
                }
            }
        }
        try {
            if (fos != null) {
                fos.close();// 关闭写入流
            }
        } catch (IOException e) {
            if (L.DEBUG) {
                L.logD("#-------error------>>>>>" + e.getMessage());
            }
        }
        if (this.recordListener != null) {
            this.recordListener.onAudioRecordSuccess(this.fileName);
        }
    }
}
