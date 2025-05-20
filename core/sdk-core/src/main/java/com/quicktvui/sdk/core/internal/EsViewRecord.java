package com.quicktvui.sdk.core.internal;

import android.support.annotation.Nullable;

import com.quicktvui.sdk.core.EsData;
import com.tencent.mtt.hippy.HippyEngineContext;

import java.io.File;
import java.util.Objects;

/**
 * Create by weipeng on 2022/03/01 16:51
 * TODO 后由于业务需要，在IEsViewer新增了部分接口，此类后续删除
 */
public class EsViewRecord {

    private IEsViewer mViewer;

    public EsViewRecord(IEsViewer viewer) {
        this.mViewer = viewer;
    }

    public IEsViewer getViewer() {
        return mViewer;
    }

    @Nullable
    public HippyEngineContext getEngineContext() {
        return mViewer.getEngineContext();
    }

    @Nullable
    public File getAppRuntimeDir() {
        return mViewer.getAppRuntimeDir();
    }

    public void sendUIEvent(int viewId, String eventName, Object params) {
        mViewer.sendUIEvent(viewId, eventName, params);
    }

    public void sendNativeEvent(String eventName, Object params) {
        mViewer.sendNativeEvent(eventName, params);
    }

    public void finish() {
        mViewer.toFinish();
    }

    public EsData getEsData() {
        return mViewer.getEsData();
    }

    public boolean isSamePackage(String pkg){
        EsData data = mViewer.getEsData();
        return data != null && Objects.equals(data.getEsPackage(), pkg);
    }

    public boolean isHomePage() {
        EsData data = mViewer.getEsData();
        return data != null && data.isHomePage();
    }

}
