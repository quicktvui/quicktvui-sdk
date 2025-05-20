package com.quicktvui.sdk.core.utils;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 * Created by WeiPeng on 2023-12-12 13:21
 */
public interface DefaultLifecycleObserver extends LifecycleObserver {

    default void onCreate(@NonNull LifecycleOwner owner) {
    }

    default void onStart(@NonNull LifecycleOwner owner) {
    }

    default void onResume(@NonNull LifecycleOwner owner) {
    }

    default void onPause(@NonNull LifecycleOwner owner) {
    }

    default void onStop(@NonNull LifecycleOwner owner) {
    }

    default void onDestroy(@NonNull LifecycleOwner owner) {
    }


}
