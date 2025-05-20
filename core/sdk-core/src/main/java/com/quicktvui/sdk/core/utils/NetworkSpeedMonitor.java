package com.quicktvui.sdk.core.utils;

import android.annotation.SuppressLint;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;

public class NetworkSpeedMonitor {
    private long previousTotalRxBytes = 0;
    private long previousTimeStamp = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int interval = 1000; // 每隔1秒获取一次网速

    public void startMonitoring(final NetworkSpeedListener listener) {
        previousTotalRxBytes = getTotalRxBytes();
        previousTimeStamp = System.currentTimeMillis();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long currentTotalRxBytes = getTotalRxBytes();
                long currentTimeStamp = System.currentTimeMillis();

                long byteDelta = currentTotalRxBytes - previousTotalRxBytes;
                long timeDelta = currentTimeStamp - previousTimeStamp;

                if (timeDelta > 0) {
                    long speed = (byteDelta * 1000) / timeDelta; // 计算速度：字节每秒
                    listener.onNetworkSpeedChanged(speed);
                }

                previousTotalRxBytes = currentTotalRxBytes;
                previousTimeStamp = currentTimeStamp;

                handler.postDelayed(this, interval);
            }
        }, interval);
    }

    public void stopMonitoring() {
        handler.removeCallbacksAndMessages(null);
    }

    private long getTotalRxBytes() {
        return TrafficStats.getTotalRxBytes();
    }

    public interface NetworkSpeedListener {
        void onNetworkSpeedChanged(long speed); // speed 单位为字节每秒
    }

    /**
     * 转换字节每秒 (B/s) 为适当的单位 (KB/s 或 MB/s)
     */
    @SuppressLint("DefaultLocale")
    public String convertSpeed(long speedInBytes) {
        double speedInKbps = speedInBytes / 1024.0;
        double speedInMbps = speedInKbps / 1024.0;

        if (speedInMbps >= 1) {
            return String.format("%.1fMB/s", speedInMbps);
        } else {
            return String.format("%.0fKB/s", speedInKbps);
        }
    }
}

