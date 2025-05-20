package com.quicktvui.sdk.core.utils;

import android.support.annotation.NonNull;

/**
 *
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler(Thread thread) {
//        SecurityManager sm = System.getSecurityManager();
//        if (sm != null) {
//            sm.checkPermission(
//                    new RuntimePermission("setDefaultUncaughtExceptionHandler")
//            );
//        }
//
//        SecurityManager security = System.getSecurityManager();
//        if (security != null) {
//            security.checkAccess(thread);
//        }

//        Thread.setDefaultUncaughtExceptionHandler();
//        mDefaultHandler = thread.getUncaughtExceptionHandler();
//        thread.setUncaughtExceptionHandler(this);
    }

    public static void startWatch(Thread thread) {
//        new CrashHandler(thread);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        }
    }
}
