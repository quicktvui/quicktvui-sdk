package com.quicktvui.sdk.core.jsview.slot;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.observer.BaseObservable;
import com.sunrain.toolkit.utils.observer.BaseObserver;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;
import com.tencent.mtt.hippy.views.view.CardRootView;

import com.quicktvui.sdk.core.jsview.chutil.GenericMotionUtil;
import com.quicktvui.sdk.core.jsview.chutil.MouseUtil;

/**
 * <br>
 *
 * <br>
 */
public final class SlotView extends CardRootView implements HippyViewBase {

    private final MutableLiveData<Boolean> _suspendObservable = new MutableLiveData<>();
    private final LiveData<Boolean> mSuspendObservable = _suspendObservable;

    // 使用liveData存在插件support/androidx问题，用传统实现
    private BaseObservable mObservable;
    private boolean mSuspend;

    public SlotView(Context context, HippyMap params) {
        super(context);

        setClipChildren(false);
        setClipToPadding(false);

//        notifySuspendState(false);
        mObservable = new BaseObservable();
        mObservable.notifyObservers(false);
    }

    @Override
    public NativeGestureDispatcher getGestureDispatcher() {
        return null;
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher nativeGestureDispatcher) {

    }

    void notifySuspendState(boolean suspend) {
        L.logDF("notify " + suspend);
        _suspendObservable.postValue(suspend);


        mSuspend = suspend;
        if (mObservable != null) {
            mObservable.notifyObservers(suspend);
        }
    }

    public void observe(LifecycleOwner owner, Observer<Boolean> observer){
        mSuspendObservable.observe(owner, observer);
    }

    public void observe(BaseObserver<Boolean> observer) {
        if (mObservable != null) {
            mObservable.addObserver(observer);
            observer.onUpdate(mObservable, mSuspend);
        }
    }

    public void release() {
        if (mObservable != null) {
            mObservable.deleteObservers();
            mObservable = null;
        }
    }

    public void setMouseEnable(){
        MouseUtil.setViewMouseStatus(this, true);
        GenericMotionUtil.setOnGenericMotionListener(this);
        MouseUtil.setViewDefaultGenericMotion(this);
    }
}
