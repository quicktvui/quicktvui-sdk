package com.quicktvui.sdk.core.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.quicktvui.sdk.core.internal.EsViewManager;

public final class HomeKeyReceiver extends BroadcastReceiver {

        public void register(Context context) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            context.registerReceiver(this, filter);
        }

        public void unRegister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (onHomeKey(intent) == 1) {
                EsViewManager.get().finishAllApp();
//                if (isKillProcessOnClickHomeKey()) {
//                    L.logDF("exit after home key press");
//                    EsContext.get().postDelay(() -> Process.killProcess(Process.myPid()), 1000);
//                }
            }
        }

        public int onHomeKey(Intent intent) {

            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                String SYSTEM_DIALOG_REASON_KEY = "reason";
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
                    String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        // 短按home键
                        return 1;
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        // 长按home键
                        return 2;
                    }
                }
            }
            return 0;
        }
    }