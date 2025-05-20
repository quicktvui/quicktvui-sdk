package com.quicktvui.support.ui.largelist;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.quicktvui.support.ui.legacy.view.TVViewUtil;

public class FocusWatcher {
    private static final String TAG = "FocusWatcher";
    ViewTreeObserver.OnGlobalFocusChangeListener mOnGlobalFocusChangeListener;

    final FocusWatch watcher;

    public FocusWatcher(FocusWatch watcher) {
        this.watcher = watcher;
    }

    public void stopListenGlobalFocusChange() {
        if (mOnGlobalFocusChangeListener != null) {
            watcher.getTarget().getViewTreeObserver().removeOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);
        }
    }

    public void listenGlobalFocusChangeIfNeed() {
        stopListenGlobalFocusChange();
        final ViewGroup target = watcher.getTarget();
        mOnGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                Log.d(TAG, "onGlobalFocusChanged hasFocus : " + target.hasFocus() + " this :" + this);
                if (target.hasFocus()) {
                    if (oldFocus == null) {
                        //首次获得焦点
                        watcher.notifyRecyclerViewFocusChanged(true, false, null, newFocus);
                    } else {
                        //焦点在内部，但上一个view不属于内部
                        final boolean isOldFocusDescendantOf = TVViewUtil.isViewDescendantOf(oldFocus, target);
                        if (!isOldFocusDescendantOf) {
                            watcher.notifyRecyclerViewFocusChanged(true, false, oldFocus, newFocus);
                        }
                    }
                } else {
                    final boolean isNewFocusDescendantOf = TVViewUtil.isViewDescendantOf(newFocus, target);
                    Log.d(TAG, "onGlobalFocusChanged  hasFocus : " + target.hasFocus() + " isNewFocusDescendantOf : " + isNewFocusDescendantOf);
                    if (!isNewFocusDescendantOf) {
                        //焦点丢失
                        final boolean isOldFocusDescendantOf = TVViewUtil.isViewDescendantOf(oldFocus, target);

                        if (isOldFocusDescendantOf) {
                            watcher.notifyRecyclerViewFocusChanged(false, true, oldFocus, newFocus);
                        }
                    }
                }
            }
        };
        watcher.getTarget().getViewTreeObserver().addOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);
    }

    public interface FocusWatch {
        void notifyRecyclerViewFocusChanged(boolean hasFocus, boolean isOldFocusDescendantOf, View oldFocus, View focused);

        ViewGroup getTarget();
    }
}
