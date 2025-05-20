package com.quicktvui.support.core.module.power;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

/**
 * PowerManager
 */
public class AndroidPowerManager {

    private static final String TAG = "PowerManager";

    private static AndroidPowerManager instance;

    private Context context;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;


    private AndroidPowerManager() {
    }

    public static AndroidPowerManager getInstance() {
        synchronized (AndroidPowerManager.class) {
            if (instance == null) {
                instance = new AndroidPowerManager();
            }
        }
        return instance;
    }

    @SuppressLint("InvalidWakeLockTag")
    public void init(Context context) {
        this.context = context;
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
    }

    public void wakeLockAcquire() {
        this.wakeLock.acquire();
    }

    public void wakeLockRelease() {
        this.wakeLock.release();
    }
}
