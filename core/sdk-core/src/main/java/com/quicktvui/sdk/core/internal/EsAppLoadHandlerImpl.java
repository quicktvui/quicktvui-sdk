package com.quicktvui.sdk.core.internal;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;

import java.io.File;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.pm.EsPageView;
import com.quicktvui.sdk.base.EsEmptyCallback;
import com.quicktvui.sdk.base.EsEventPacket;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.IEsRemoteEventCallback;

/**
 *
 */
public class EsAppLoadHandlerImpl implements IEsViewer, IEsAppLoadHandler, IEsRemoteEventCallback, IEsAppLoadCallback{

    private Context mCacheContext;
    private int mFragmentContainerLayoutId;
    private IEsAppLoadCallback mSource;
    private EsContainerTaskV2 mContainerTask;

    public void startLoad(Context context, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        mCacheContext = context;
        mFragmentContainerLayoutId = containerLayoutId;
        callback.onStartLoad(this);
        mSource = callback;
        mContainerTask = new EsContainerTaskV2(context, this);
        mContainerTask.startLoad(data);
    }

    public Context getContext() {
        return mCacheContext;
    }

    //region IEsViewer

    @Override
    public Context getAppContext() {
        return mCacheContext;
    }

    @Override
    public EsContainerTaskV2 getTaskContainer() {
        return mContainerTask;
    }

    @Override
    public void toFinish() {
        if (mContainerTask != null) {
            mContainerTask.getEsLoadCallback().requestFinish();
        }
    }

    @Nullable
    @Override
    public EsData getEsData() {
        return mContainerTask == null ? null : mContainerTask.getEsData();
    }

    @Nullable
    @Override
    public HippyEngineContext getEngineContext() {
        return mContainerTask == null ? null : mContainerTask.getEngineContext();
    }

    @Override
    public void sendUIEvent(int viewId, String eventName, Object params) {
        if (mContainerTask != null) {
            mContainerTask.sendUIEvent(viewId, eventName, params);
        }
    }

    @Override
    public void sendNativeEvent(String eventName, Object params) {
        if (mContainerTask != null) {
            mContainerTask.sendNativeEvent(eventName, params);
        }
    }

    @Nullable
    @Override
    public File getAppRuntimeDir() {
        return mContainerTask == null ? null : mContainerTask.getAppRuntimeDir();
    }

    //endregion

    //region IEsAppHandler

    @Override
    public void onStart() {
        EsViewManager.get().onViewStart(this);
    }

    @Override
    public void onResume() {
        EsViewManager.get().onViewResume(this);
        if (mContainerTask != null) {
            mContainerTask.onResume();
        }
    }

    @Override
    public void onPause() {
        EsViewManager.get().onViewPause(this);
        if (mContainerTask != null) {
            mContainerTask.onPause();
        }
    }

    @Override
    public void onStop() {
        EsViewManager.get().onViewStop(this);
    }

    @Override
    public void onDestroy() {
        EsViewManager.get().onViewDestroy(this);
        if (mContainerTask != null) {
            mContainerTask.destroy();
        }
        mSource = null;
        mContainerTask = null;
        mCacheContext = null;
    }

    @Override
    public void dispatchKeyEvent(KeyEvent event) {
        if (mContainerTask != null) {
            mContainerTask.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onBackPressed(EsEmptyCallback callback) {
        if (mContainerTask != null) {
            return mContainerTask.onBackPressed(callback);
        }
        return false;
    }

    @Override
    public void sendEvent(String eventName, Object params) {
        if (mContainerTask != null) {
            mContainerTask.sendNativeEvent(eventName, params);
        }
    }

    //endregion

    //region IEsNativeEventCallback

    @Override
    public void onReceiveEvent(String eventName, String eventData) {
        if (mContainerTask != null) {
//            mContainerTask.getEsLoadCallback().onEsAppEvent();
        }
    }

    //endregion

    //region IEsAppLoadCallback

    @Override
    public void onStartLoad(IEsAppLoadHandler handler) {
        if (mSource != null) {
            mSource.onStartLoad(handler);
        }
    }

    @Override
    public void onViewLoaded(HippyRootView view) {
        if (mSource != null) {
            mSource.onViewLoaded(view);
        }
    }

    @Override
    public void addSubPage(EsPageView view) {
        if (mSource != null) {
            mSource.addSubPage(view);
        }
    }

    @Override
    public void removeSubPage(EsPageView view) {
        if (mSource != null) {
            mSource.removeSubPage(view);
        }
    }

    @Override
    public void onLoadError(EsException e) {
        if (mSource != null) {
            mSource.onLoadError(e);
        }
    }

    @Override
    public void onEsAppEvent(EsEventPacket packet) {
        if (mSource != null) {
            mSource.onEsAppEvent(packet);
        }
    }

    @Override
    public void requestFinish() {
        if (mSource != null) {
            mSource.requestFinish();
        }
    }

    //endregion
}
