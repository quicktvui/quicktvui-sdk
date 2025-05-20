package com.quicktvui.sdk.core.internal;

import android.content.Context;
import android.support.annotation.Nullable;

import com.tencent.mtt.hippy.HippyEngineContext;

import java.io.File;

import com.quicktvui.sdk.core.EsData;

/**
 * Create by sorosunrain on 2021/07/01 18:09
 */
public interface IEsViewer {

    Context getAppContext();
    EsContainerTaskV2 getTaskContainer();
    void toFinish();

    @Nullable
    EsData getEsData();
    @Nullable
    HippyEngineContext getEngineContext();

    void sendUIEvent(int viewId, String eventName, Object params);
    void sendNativeEvent(String eventName, Object params);

    @Nullable
    File getAppRuntimeDir();

}
