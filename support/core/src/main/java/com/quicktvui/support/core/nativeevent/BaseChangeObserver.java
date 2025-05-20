package com.quicktvui.support.core.nativeevent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.observer.BaseObservable;
import com.sunrain.toolkit.utils.observer.BaseObserver;

public abstract class BaseChangeObserver extends BroadcastReceiver {

        protected Context mContext;
        private BaseObservable mObservable;

        public <T> void observer(Context context, BaseObserver<T> observer) {
            if (context == null) return;
            mContext = context;
            boolean isFirst = mObservable == null;
            if(isFirst) mObservable = new BaseObservable();
            mObservable.addObserver(observer);
            if(isFirst) startObserver();
            triggerIfNeed();
        }

        public <T> void notifyData(T data) {
            if(L.DEBUG) L.logD("observer changed " + data);
            if (mObservable != null) {mObservable.notifyObservers(data);}
        }

        public void release() {
            stopObserver();
            if (mObservable != null) {
                mObservable.deleteObservers();
            }
            mObservable = null;
            mContext = null;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            onObserverChange(context, intent);
        }

        // 注册监听
        protected abstract void startObserver();

        // 非sticky broadcast可在这触发一次
        protected abstract void triggerIfNeed();

        protected abstract void onObserverChange(Context context, Intent intent);

        protected abstract void stopObserver();
    }