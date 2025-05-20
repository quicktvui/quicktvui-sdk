package com.quicktvui.support.core.nativeevent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public final class TimeChangeObserver extends BaseChangeObserver {
    //region 单例

    private static final class TimeChangeObserverHolder {
        private static final TimeChangeObserver INSTANCE = new TimeChangeObserver();
    }

    public static TimeChangeObserver get() {
        return TimeChangeObserverHolder.INSTANCE;
    }

    private TimeChangeObserver() {
    }

    //endregion

    @Override
    public void startObserver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);

        mContext.registerReceiver(this, filter);
    }

    @Override
    protected void triggerIfNeed() {
        notifyData(null);
    }

    @Override
    protected void onObserverChange(Context context, Intent intent) {
        notifyData(null);
    }

    @Override
    public void stopObserver() {
        if(mContext != null) mContext.unregisterReceiver(this);
    }
}