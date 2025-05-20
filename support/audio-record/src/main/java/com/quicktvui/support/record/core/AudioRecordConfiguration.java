package com.quicktvui.support.record.core;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.io.File;
import java.util.concurrent.Executor;

import com.quicktvui.support.record.assist.QueueProcessingType;
import com.quicktvui.support.record.core.naming.FileNameGenerator;


/**
 *
 */
public final class AudioRecordConfiguration {

    private Context context;

    public final int audioSource;
    public final int sampleRateInHz;
    public final int channelConfig;
    public final int audioFormat;

    public final Executor taskExecutor;
    public final File audioCacheDir;
    public final FileNameGenerator audioFileNameGenerator;
    public final AudioRecorderType audioRecorderType;

    public AudioRecordConfiguration(Builder builder) {
        this.context = builder.context;
        this.audioSource = builder.audioSource;
        this.sampleRateInHz = builder.sampleRateInHz;
        this.channelConfig = builder.channelConfig;
        this.audioFormat = builder.audioFormat;
        this.taskExecutor = builder.taskExecutor;
        this.audioCacheDir = builder.audioCacheDir;
        this.audioFileNameGenerator = builder.audioFileNameGenerator;
        this.audioRecorderType = builder.audioRecorderType;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public String toString() {
        return "AudioRecordConfiguration{" +
                "context=" + context +
                ", audioSource=" + audioSource +
                ", sampleRateInHz=" + sampleRateInHz +
                ", channelConfig=" + channelConfig +
                ", audioFormat=" + audioFormat +
                ", taskExecutor=" + taskExecutor +
                ", audioCacheDir=" + audioCacheDir +
                ", audioFileNameGenerator=" + audioFileNameGenerator +
                ", audioRecorderType=" + audioRecorderType +
                '}';
    }

    public static class Builder {

        public static final int DEFAULT_THREAD_POOL_SIZE = 3;
        public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
        public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;

        private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        private int threadPriority = DEFAULT_THREAD_PRIORITY;

        private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;

        private FileNameGenerator audioFileNameGenerator;
        private AudioRecorderType audioRecorderType;

        private final Context context;

        private int audioSource = -1;
        private int sampleRateInHz = -1;
        private int channelConfig = -1;
        private int audioFormat = -1;

        private Executor taskExecutor = null;
        private File audioCacheDir = null;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setAudioSource(int audioSource) {
            this.audioSource = audioSource;
            return this;
        }

        public Builder setSampleRateInHz(int sampleRateInHz) {
            this.sampleRateInHz = sampleRateInHz;
            return this;
        }

        public Builder setChannelConfig(int channelConfig) {
            this.channelConfig = channelConfig;
            return this;
        }

        public Builder setAudioFormat(int audioFormat) {
            this.audioFormat = audioFormat;
            return this;
        }

        public Builder setAudioCacheDir(File audioCacheDir) {
            this.audioCacheDir = audioCacheDir;
            return this;
        }

        public Builder setAudioFileNameGenerator(FileNameGenerator audioFileNameGenerator) {
            this.audioFileNameGenerator = audioFileNameGenerator;
            return this;
        }

        public Builder setAudioRecorderType(AudioRecorderType audioRecorderType) {
            this.audioRecorderType = audioRecorderType;
            return this;
        }

        public Builder taskExecutor(Executor executor) {
            if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE
                    || threadPriority != DEFAULT_THREAD_PRIORITY
                    || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
            }
            this.taskExecutor = executor;
            return this;
        }

        public AudioRecordConfiguration build() {
            if (audioSource == -1) {
                audioSource = MediaRecorder.AudioSource.MIC;
            }
            if (sampleRateInHz == -1) {
                sampleRateInHz = 16000;
            }
            if (channelConfig == -1) {
                channelConfig = AudioFormat.CHANNEL_IN_MONO;
            }
            if (audioFormat == -1) {
                audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            }

            if (taskExecutor == null) {
                taskExecutor = DefaultConfigurationFactory
                        .createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            }

            if (audioCacheDir == null) {
                audioCacheDir = DefaultConfigurationFactory.createDiskCacheDir(context);
            }

            if (audioFileNameGenerator == null) {
                audioFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
            }

            if (audioRecorderType == null) {
                audioRecorderType = AudioRecorderType.AUDIO_RECORDER_TYPE_MP3;
            }

            return new AudioRecordConfiguration(this);
        }
    }
}
