package com.quicktvui.support.record.wav;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;

import com.quicktvui.support.record.core.AudioRecordConfiguration;
import com.quicktvui.support.record.utils.Preconditions;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;


/**
 *
 */
public class AudioRecorder {

    public static final String TAG = "AudioRecorder";
    //音频输入-麦克风
    private static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;
    //录音对象
    private AudioRecord audioRecord;
    //录音状态
    private Status status = Status.STATUS_NO_READY;
    //文件名
    private String fileName;

    private AudioRecordConfiguration configuration;

    private AudioRecordListener recordListener;
    private AudioRecordStreamListener recordStreamListener;

    private Context context;

    private Executor taskExecutor;

    public AudioRecorder() {
        if (L.DEBUG) {
            L.logD("#-------AudioRecorder----构造方法-->>>>>");
        }
    }

    @SuppressLint("MissingPermission")
    public void createAudio(Context context,
                            int audioSource,
                            int sampleRateInHz,
                            int channelConfig,
                            int audioFormat) {
        if (L.DEBUG) {
            L.logD("#-------createAudio------>>>>>\n"
                    + " audioSource:" + audioSource
                    + " sampleRateInHz:" + sampleRateInHz
                    + " channelConfig:" + channelConfig
                    + " audioFormat:" + audioFormat
            );
        }
        this.context = context;
        AUDIO_SAMPLE_RATE = sampleRateInHz;
        AUDIO_CHANNEL = channelConfig;
        AUDIO_ENCODING = audioFormat;
        AUDIO_INPUT = audioSource;

        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        status = Status.STATUS_READY;
    }

    @SuppressLint("MissingPermission")
    public void createAudio(AudioRecordConfiguration configuration) {

        if (L.DEBUG) {
            L.logD("#-------createAudio---START--->>>>>" + configuration);
        }
        this.configuration = Preconditions.checkNotNull(configuration);
        this.taskExecutor = this.configuration.taskExecutor;
        this.context = this.configuration.getContext();
        AUDIO_SAMPLE_RATE = this.configuration.sampleRateInHz;
        AUDIO_CHANNEL = this.configuration.channelConfig;
        AUDIO_ENCODING = this.configuration.audioFormat;
        AUDIO_INPUT = this.configuration.audioSource;

        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        status = Status.STATUS_READY;

        if (L.DEBUG) {
            L.logD("#-------createAudio---END--->>>>>" + audioRecord.getState());
        }
    }


    public void setRecordListener(AudioRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void setRecordStreamListener(AudioRecordStreamListener recordStreamListener) {
        this.recordStreamListener = recordStreamListener;
    }

    /**
     * 开始录音
     */
    public void startRecord(String fileName) {
        if (status == Status.STATUS_NO_READY || TextUtils.isEmpty(fileName)) {
            if (L.DEBUG) {
                L.logD("#-------startRecord--录音尚未初始化,请检查是否禁止了录音权限---->>>>>" + audioRecord.getState());
            }
        }
        if (status == Status.STATUS_START) {
            if (L.DEBUG) {
                L.logD("#-------startRecord--正在录音---->>>>>" + audioRecord.getState());
            }
            return;
        }
        if (L.DEBUG) {
            L.logD("#-------startRecord------>>>>>" + audioRecord.getState());
        }
        this.fileName = fileName;
        audioRecord.startRecording();

        //
        RecordTask recordTask = new RecordTask(
                context,
                audioRecord,
                fileName,
                bufferSizeInBytes,
                recordListener,
                recordStreamListener
        );
        this.taskExecutor.execute(recordTask);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                writeDataTOFile();
//            }
//        }).start();
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        if (L.DEBUG) {
            L.logD("#-------pauseRecord------>>>>>");
        }
        if (status != Status.STATUS_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (L.DEBUG) {
            L.logD("#-------stopRecord------>>>>>");
        }
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            if (L.DEBUG) {
                L.logD("#-------stopRecord----录音尚未开始-->>>>>");
            }
            return;
        } else {
            if (status == Status.STATUS_START) {
                audioRecord.stop();
                status = Status.STATUS_STOP;
                //
                PCMToWAVTask recordTask = new PCMToWAVTask(
                        context,
                        fileName,
                        recordListener,
                        recordStreamListener
                );
                this.taskExecutor.execute(recordTask);
//
//                try {
//                    makePCMFileToWAVFile(recordListener);
//                } catch (IllegalStateException e) {
//                    throw new IllegalStateException(e.getMessage());
//                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (L.DEBUG) {
            L.logD("#-------release------>>>>>");
        }
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = Status.STATUS_NO_READY;
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        fileName = null;
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = Status.STATUS_NO_READY;
    }


    /**
     * 将音频信息写入文件
     *
     * @param
     */
    private void writeDataTOFile() {
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
        status = Status.STATUS_START;
        while (status == Status.STATUS_START) {
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

    /**
     * 将单个pcm文件转化为wav文件
     */
    private void makePCMFileToWAVFile(AudioRecordListener recordListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String destinationFileName = FileUtil.getWavFileAbsolutePath(context, fileName);
                    L.logD("#----------makePCMFileToWAVFile------>>>>>" + destinationFileName);
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
        }).start();
    }

    /**
     * 获取录音对象的状态
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }
}