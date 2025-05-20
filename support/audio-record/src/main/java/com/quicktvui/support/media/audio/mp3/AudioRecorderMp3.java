package com.quicktvui.support.media.audio.mp3;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.HandlerThread;
import android.os.Message;

import com.quicktvui.support.record.core.AudioRecorderListener;
import com.quicktvui.support.record.core.AudioRecorderStatus;
import com.quicktvui.support.record.core.AudioRecorderStatusEnum;
import com.quicktvui.support.record.core.AudioRecorderType;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.quicktvui.support.record.pcm.PCMFormat;


/**
 *
 */
public class AudioRecorderMp3 {

    static {
        System.loadLibrary("lamemp3");
    }

    private static final String TAG = "Mp3AudioRecorder";
    //默认采样率
    private static final int DEFAULT_SAMPLING_RATE = 44100;
    //转换周期，录音每满160帧，进行一次转换
    private static final int FRAME_COUNT = 160;
    //输出MP3的码率
    private static final int BIT_RATE = 32;
    //根据资料假定的最大值。 实测时有时超过此值。
    private static final int MAX_VOLUME = 2000;

    private AudioRecord audioRecord = null;
    private int bufferSize;
    private File audioRecorderFile;
    private int mVolume;
    private short[] pcmBuffer;
    private int samplingRate;
    private int channelConfig;
    private PCMFormat audioFormat;
    private AtomicBoolean isRecording = new AtomicBoolean(false);

    private AtomicBoolean isRecordThreadRunning = new AtomicBoolean(false);

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    private HandlerThread decodeHandlerThread;
    private AudioRecorderHandler audioRecorderHandler;

    //
    private AudioRecorderListener audioRecorderListener;


    /**
     * Default constructor. Setup recorder with default sampling rate 1 channel,
     * 16 bits pcm
     */
    public AudioRecorderMp3() {
        this(DEFAULT_SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, PCMFormat.PCM_16BIT);
    }

    public AudioRecorderMp3(int samplingRate, int channelConfig, PCMFormat audioFormat) {
        this.samplingRate = samplingRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
    }

    public void startRecording(File audioRecorderFile) throws IOException {
        if (isRecordThreadRunning.get()) {
            if (L.DEBUG) {
                L.logD("#---------startRecording----录音线程还在执行---->>>");
            }
            return;
        }

        if (!isRecording.compareAndSet(false, true)) {
            if (L.DEBUG) {
                L.logD("#---------startRecording----compareAndSet失败---->>>");
            }
            return;
        }

        this.audioRecorderFile = audioRecorderFile;

        if (audioRecord == null) {
            initAudioRecorder();
        }
        audioRecord.startRecording();

        if (L.DEBUG) {
            L.logD("#-------startRecording---------->>>" + audioRecorderFile.getAbsolutePath());
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (L.DEBUG) {
                    L.logD("#-------录音线程开始------start---->>>");
                }
                if (!isRecordThreadRunning.compareAndSet(false, true)) {
                    return;
                }
                //----------------------------------------------------------
                while (isRecording.get()) {
                    try {
                        if (audioRecord != null) {
                            int readSize = audioRecord.read(pcmBuffer, 0, bufferSize);
                            if (readSize > 0) {
                                //待转换的PCM数据放到转换线程中
                                audioRecorderHandler.addDecodeBuffer(pcmBuffer, readSize);
                                calculateRealVolume(pcmBuffer, readSize);
                            }
                            //
                            else {
                                if (L.DEBUG) {
                                    L.logD("#-------startRecording------data is null---->>>");
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                //----------------------------------------------------------
                // 录音完毕，释放AudioRecord的资源
                try {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                    // 录音完毕，通知转换线程停止，并等待直到其转换完毕
                    Message message = Message.obtain();
                    message.what = AudioRecorderHandler.PROCESS_STOP;
                    audioRecorderHandler.sendMessage(message);

                    decodeHandlerThread.join();
                    if (audioRecorderListener != null) {
                        AudioRecorderStatus status = new AudioRecorderStatus(
                                AudioRecorderStatusEnum.AUDIO_RECORDER_STATUS_SUCCESS);
                        status.setAudioRecorderFile(audioRecorderFile.getPath());
                        status.setAudioRecorderType(AudioRecorderType.AUDIO_RECORDER_TYPE_MP3);
                        //
                        audioRecorderListener.onAudioRecorderStatusChanged(status);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //停止
                isRecordThreadRunning.set(false);
                if (L.DEBUG) {
                    L.logD("#--------录音线程结束------end---->>>");
                }
            }
        };
        executor.execute(runnable);
    }

    public void stopRecording() {
        if (!isRecording.compareAndSet(true, false)) {
            if (L.DEBUG) {
                L.logD("#---------stopRecording-----compareAndSet失败---->>>");
            }
            return;
        }

        AudioRecorderStatus status = new AudioRecorderStatus(
                AudioRecorderStatusEnum.AUDIO_RECORDER_STATUS_STOP);
        if (audioRecorderFile != null) {
            status.setAudioRecorderFile(audioRecorderFile.getPath());
        }
        status.setAudioRecorderType(AudioRecorderType.AUDIO_RECORDER_TYPE_MP3);
        //
        if (audioRecorderListener != null) {
            audioRecorderListener.onAudioRecorderStatusChanged(status);
        }
        if (L.DEBUG) {
            L.logD("#---------stopRecording--------->>>");
        }
    }

    public void cancelRecorder() {
        isRecording.set(false);

        AudioRecorderStatus status = new AudioRecorderStatus(
                AudioRecorderStatusEnum.AUDIO_RECORDER_STATUS_CANCEL);
        status.setAudioRecorderFile(audioRecorderFile.getPath());
        status.setAudioRecorderType(AudioRecorderType.AUDIO_RECORDER_TYPE_MP3);
        //
        if (audioRecorderListener != null) {
            audioRecorderListener.onAudioRecorderStatusChanged(status);
        }

        if (L.DEBUG) {
            L.logD("#---------cancelRecorder--------->>>");
        }
    }

    /**
     *
     */
    public boolean isRecording() {
        return isRecording.get() && isRecordThreadRunning.get();
    }

    //计算音量大小
    private void calculateRealVolume(short[] buffer, int readSize) {
        double sum = 0;
        for (int i = 0; i < readSize; i++) {
            sum += buffer[i] * buffer[i];
        }
        if (readSize > 0) {
            double amplitude = sum / readSize;
            mVolume = (int) Math.sqrt(amplitude);
        }

        //
        if (audioRecorderListener != null) {
            audioRecorderListener.onAudioRecorderVolumeChanged(mVolume);
        }
    }


    @SuppressLint("MissingPermission")
    private void initAudioRecorder() {
        if (L.DEBUG) {
            L.logD("#---------initAudioRecorder---START------>>>");
        }
        int bytesPerFrame = audioFormat.getBytesPerFrame();
        /* Get number of samples. Calculate the mPCMBuffer size (round up to the factor of given frame size) */
        int frameSize = AudioRecord.getMinBufferSize(samplingRate, channelConfig, audioFormat.getAudioFormat()) / bytesPerFrame;
        if (frameSize % FRAME_COUNT != 0) {
            frameSize = frameSize + (FRAME_COUNT - frameSize % FRAME_COUNT);
        }
        bufferSize = frameSize * bytesPerFrame;
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                samplingRate,
                channelConfig,
                audioFormat.getAudioFormat(),
                bufferSize
        );

        pcmBuffer = new short[bufferSize];
        AndroidMP3Encoder.init(samplingRate, 1, samplingRate, BIT_RATE);
        decodeHandlerThread = new HandlerThread("decode-thread");
        decodeHandlerThread.start();
        audioRecorderHandler =
                new AudioRecorderHandler(audioRecorderFile, bufferSize, decodeHandlerThread.getLooper());
        audioRecord.setRecordPositionUpdateListener(audioRecorderHandler, audioRecorderHandler);
        audioRecord.setPositionNotificationPeriod(FRAME_COUNT);
    }

    public void setAudioRecorderListener(AudioRecorderListener audioRecorderListener) {
        this.audioRecorderListener = audioRecorderListener;
    }
}
