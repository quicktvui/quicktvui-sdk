package com.quicktvui.sdk.core.internal;

import android.annotation.SuppressLint;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;

import java.io.File;
import java.util.List;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.fragment.EsPageFragment;
import com.quicktvui.sdk.core.pm.EsPageView;
import com.quicktvui.sdk.base.EsEmptyCallback;
import com.quicktvui.sdk.base.EsEventPacket;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.IEsRemoteEventCallback;

/**
 *
 */
public class EsAppLoadHandlerImplV2 implements IEsViewer, IEsAppLoadHandler, IEsRemoteEventCallback, IEsAppLoadCallback {

    private FragmentActivity mCacheActivity;
    private int mFragmentContainerLayoutId;
    private IEsAppLoadCallback mSource;
    private EsContainerTaskV2 mContainerTask;

    @SuppressLint("RestrictedApi")
    public void startLoad(FragmentActivity activity, int containerLayoutId, EsData data, IEsAppLoadCallback callback) {
        mCacheActivity = activity;
        mFragmentContainerLayoutId = containerLayoutId;
        callback.onStartLoad(this);
        mSource = callback;
        mContainerTask = new EsContainerTaskV2(activity, this);
        mContainerTask.startLoad(data);

        activity.getLifecycle().addObserver((GenericLifecycleObserver) (source, event) -> {
            switch (event) {
                case ON_START:
                    EsViewManager.get().onViewStart(this);
                    break;
                case ON_RESUME:
                    if (mContainerTask != null) {
                        mContainerTask.onResume();
                    }
                    EsViewManager.get().onViewResume(this);
                    break;
                case ON_PAUSE:
                    if (mContainerTask != null) {
                        mContainerTask.onPause();
                    }
                    EsViewManager.get().onViewPause(this);
                    break;
                case ON_STOP:
                    EsViewManager.get().onViewStop(this);
                    break;
                case ON_DESTROY:
                    if (mContainerTask != null) {
                        mContainerTask.destroy();
                    }
                    EsViewManager.get().onViewDestroy(this);
                    mSource = null;
                    mContainerTask = null;
                    mCacheActivity = null;
                    break;
            }
        });
    }

    public Context getContext() {
        return mCacheActivity;
    }

    //region IEsViewer

    @Override
    public Context getAppContext() {
        return mCacheActivity;
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
//        if (mContainerTask != null) {
//            mContainerTask.onResume();
//        }
    }

    @Override
    public void onPause() {
        EsViewManager.get().onViewPause(this);
//        if (mContainerTask != null) {
//            mContainerTask.onPause();
//        }
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
        mCacheActivity = null;
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

    //region 页面管理

    public void onPageOpen(EsPageView view) {
        if (L.DEBUG) L.logD("page_open " + view.getPageName());
        EsPageFragment last = getLastFragment();
        if (last != null) {
            last.saveFocus();
        }
        execTransaction(tr -> {
            if (last != null) {
                if (!view.isDialogMode() && !last.getPageView().isDialogMode()) {
                    tr.hide(last);
                }
            }
            tr.add(mFragmentContainerLayoutId, new EsPageFragment(view));
        });
    }

    public void onPageClose(EsPageView view) {
        if (L.DEBUG) L.logD("page_close " + view.getPageName());
        if (mCacheActivity != null) {
            List<Fragment> fragments = mCacheActivity.getSupportFragmentManager().getFragments();
            EsPageFragment removeFragment = null;
            for (int i = 0; i < fragments.size(); i++) {
                if(!(fragments.get(i) instanceof EsPageFragment)) continue;
                EsPageFragment fragment = (EsPageFragment) fragments.get(i);
                if (fragment.getPageId() == view.getPageId()) {
                    removeFragment = fragment;
                    break;
                }
            }
            if (removeFragment != null) {
                final Fragment f = removeFragment;
                execTransaction(tr -> tr.remove(f));
            }

            EsPageFragment last = getLastFragment();
            if (last != null) {
                last.resetFocus();
                if(last.isHidden()) {
                    execTransaction(tr -> {
                        tr.show(last);
                    });
                }
            }
        }
    }

    private EsPageFragment getLastFragment() {
        if (mCacheActivity != null) {
            List<Fragment> fragments = mCacheActivity.getSupportFragmentManager().getFragments();
            if(fragments.size() > 0) {
                // 倒序循环
                for (int i = fragments.size() - 1; i >= 0; i--) {
                    Fragment fragment = fragments.get(i);
                    if(fragment instanceof EsPageFragment){
                        return (EsPageFragment) fragment;
                    }
                }
            }
        }
        return null;
    }

    private void execTransaction(TransactionCallback callback) {
        if (mCacheActivity != null) {
            FragmentTransaction tr = mCacheActivity.getSupportFragmentManager().beginTransaction();
            callback.onTransaction(tr);
            tr.commitNow();
        }
    }

    private interface TransactionCallback {
        void onTransaction(FragmentTransaction tr);
    }

    //endregion
}
