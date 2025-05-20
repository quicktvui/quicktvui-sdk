package com.quicktvui.support.player.ijk.utils;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class PlayerThreadTools {

    public static boolean useThread = false;
    public static boolean threadQuit = false;
    //    public static boolean useHandlerThread = true;
    private static volatile PlayerThreadTools instance;
    private Handler playerHandler;
    private final Handler uiHandler;
//    private final ExecutorService executorService;

    private PlayerThreadTools() {
        uiHandler = new Handler(Looper.getMainLooper());
        initThread();
    }

    private void initThread() {
        HandlerThread singleThread = new HandlerThread("PlayerSingleThread", Thread.MAX_PRIORITY);
        singleThread.start();
        playerHandler = new Handler(singleThread.getLooper());

//        executorService = Executors.newSingleThreadExecutor();
    }

    public static PlayerThreadTools getInstance() {
        if (instance == null) {
            synchronized (PlayerThreadTools.class) {
                if (instance == null) {
                    instance = new PlayerThreadTools();
                }
            }
        }
        return instance;
    }

    public void delegateMethod(Runnable task) {
        if (useThread) {
            runOnPlayerThread(task);
        } else {
            task.run();
        }
    }

    public void runOnPlayerThread(Runnable task) {
        runOnPlayerThread(task, 0);
    }

    public void runOnPlayerThread(Runnable task, int delayTime) {
        if (threadQuit) {
            initThread();
            threadQuit = false;
        }
        if (delayTime > 0) {
            playerHandler.postDelayed(task, delayTime);
        } else {
//            if (useHandlerThread)
            playerHandler.post(task);
//            else
//                executorService.submit(task);
        }
    }

    public void runOnUiThread(Runnable task) {
        runOnUiThread(task, 0);
    }

    public void runOnUiThread(Runnable task, int delayTime) {
        if (delayTime > 0) {
            uiHandler.postDelayed(task, delayTime);
        } else {
            uiHandler.post(task);
        }
    }

    public void init() {
        useThread = false;
    }

    public void quit() {
        Handler handler = playerHandler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            handler.getLooper().quitSafely();
        } else {
            handler.getLooper().quit();
        }
        playerHandler = null;
        threadQuit = true;
        useThread = false;
    }

}
