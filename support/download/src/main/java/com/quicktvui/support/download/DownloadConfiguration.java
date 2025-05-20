package com.quicktvui.support.download;

import android.content.Context;

import java.io.File;
import java.util.concurrent.Executor;

/**
 *
 */
public final class DownloadConfiguration {

    public final Executor taskExecutor;
    public final File downloadCacheDir;
    public final long interpolator;

    private Context context;

    public DownloadConfiguration(Builder builder) {
        this.context = builder.context;
        this.taskExecutor = builder.taskExecutor;
        this.downloadCacheDir = builder.downloadCacheDir;
        this.interpolator = builder.interpolator;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public String toString() {
        return "DownloadConfiguration{" +
                "taskExecutor=" + taskExecutor +
                ", downloadCacheDir=" + downloadCacheDir +
                ", interpolator=" + interpolator +
                ", context=" + context +
                '}';
    }

    public static class Builder {

        private final Context context;

        private Executor taskExecutor = null;
        private File downloadCacheDir = null;
        private long interpolator = 1000;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDownloadCacheDir(File downloadCacheDir) {
            this.downloadCacheDir = downloadCacheDir;
            return this;
        }

        public Builder setInterpolator(long interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public DownloadConfiguration build() {
            if (taskExecutor == null) {
                taskExecutor = DefaultConfigurationFactory.createExecutor();
            }

            if (downloadCacheDir == null) {
                downloadCacheDir = DefaultConfigurationFactory.createDiskCacheDir(context);
            }

            return new DownloadConfiguration(this);
        }
    }
}
