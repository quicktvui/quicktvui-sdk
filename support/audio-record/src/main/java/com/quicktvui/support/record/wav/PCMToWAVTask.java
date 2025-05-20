package com.quicktvui.support.record.wav;

import android.content.Context;

import com.sunrain.toolkit.utils.log.L;


/**
 *
 */
public class PCMToWAVTask implements Runnable {

    private static final String TAG = "PCMToWAVTask";

    private Context context;
    private String fileName;
    private AudioRecordListener recordListener;
    private AudioRecordStreamListener recordStreamListener;

    public PCMToWAVTask(Context context,
                        String fileName,
                        AudioRecordListener recordListener,
                        AudioRecordStreamListener recordStreamListener) {
        this.context = context;
        this.fileName = fileName;
        this.recordListener = recordListener;
        this.recordStreamListener = recordStreamListener;
    }

    @Override
    public void run() {
        try {
            String destinationFileName = FileUtil.getWavFileAbsolutePath(context, fileName);
            if (L.DEBUG) {
                L.logD("#----------makePCMFileToWAVFile------>>>>>" + destinationFileName);
            }

            if (PcmToWav.makePCMFileToWAVFile(
                    FileUtil.getPcmFileAbsolutePath(context, fileName),
                    destinationFileName, true)) {
                if (L.DEBUG) {
                    L.logD("#---------makePCMFileToWAVFile------success------->>>>>");
                }
                recordListener.onAudioRecordPcmToWavSuccess(destinationFileName);
            }
            //
            else {
                if (recordListener != null) {
                    recordListener.onAudioRecordPcmToWavError(destinationFileName);
                }
                if (L.DEBUG) {
                    L.logD("#-------error---makePCMFileToWAVFile fail--->>>>>");
                }
            }
            fileName = null;
        } catch (Throwable e) {
            e.printStackTrace();
            if (recordListener != null) {
                recordListener.onAudioRecordPcmToWavError("");
            }
        }
    }
}
