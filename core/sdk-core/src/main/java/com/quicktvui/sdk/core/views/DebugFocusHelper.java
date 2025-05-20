package com.quicktvui.sdk.core.views;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.sunrain.toolkit.utils.ToastUtils;

import com.quicktvui.sdk.core.ext.log.ILogCallback;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.tookit.ESToolkitManager;
import com.quicktvui.sdk.core.tookit.ToolkitUseCase;

public class DebugFocusHelper {
    ESToolkitManager.Callback mDebugFocusCallback;
    DebugFocusRoot debugFocus;
    ViewTreeObserver.OnGlobalFocusChangeListener mOnGlobalFocusChangeListener;
    final Activity activity;
    ILogCallback logCallback;

    public DebugFocusHelper(Activity activity) {
        this.activity = activity;
    }

    public void startListen(){
        final ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        mDebugFocusCallback =  (ESToolkitManager.Callback<Boolean>) enable -> {
            if(enable) {
                debugFocus = new DebugFocusRoot(activity);
                activity.addContentView(debugFocus,
                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                debugFocus.onAttachToParent(rootView.getWidth(),rootView.getHeight());
                View focused = rootView.findFocus();
                debugFocus.notifyFocusedViewChanged(rootView,null,focused);

                mOnGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
                    @Override
                    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                        if(debugFocus == null) return;
                        debugFocus.notifyFocusedViewChanged(rootView,null,newFocus);
                    }
                };

                rootView.getViewTreeObserver().addOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);

                logCallback = new ILogCallback() {
                    @Override
                    public void log(String tag, String msg) {
                        if(debugFocus == null) return;
                        debugFocus.printLog(tag, msg);
                    }
                };
                EsContext.get().addLogCallback(logCallback);
            }else{
                releaseDebugFocusView(true);
            }
        };
        ESToolkitManager.get().listen(ToolkitUseCase.DEBUG_FOCUS,mDebugFocusCallback);
    }

    public void stopListen(){
        if (mDebugFocusCallback != null) {
            ESToolkitManager.get().unListen(mDebugFocusCallback);
            mDebugFocusCallback = null;
        }
        if (mOnGlobalFocusChangeListener != null) {
            activity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);
            mOnGlobalFocusChangeListener = null;
        }
        if (logCallback != null) {
            EsContext.get().removeLogCallback(logCallback);
            logCallback = null;
        }
        releaseDebugFocusView(false);
    }

    private void releaseDebugFocusView(boolean showToast) {
        if (debugFocus != null) {
            debugFocus.setVisibility(View.GONE);
            debugFocus.cancelUpdateInfo();
            if (showToast) {
                ToastUtils.showLong("焦点调试关闭，退出页面后生效");
            }
            debugFocus = null;
        }
    }

}
