package com.quicktvui.sdk.core.pm;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

import java.util.Date;

import java.util.ArrayList;

public class EsPageView extends HippyViewGroup implements IEsPageView {
    final HippyMap params;
    View savedFocus;
    View dialogSavedFocus;
    public static final String TAG = "DebugPage";

    private EventHandler mEventHandler;
    private View frontDialogView;
    private boolean frontDialogHasFocus = true;

    public EsPageView(Context context, HippyMap params) {
        super(context);
        this.params = params;
        //这里作用类似focusDispatchView,用来使setNextFocusName生效
        setUseAdvancedFocusSearch(true);
        setAsRootView();
        logPerformance("create EsPageView");
//        setSkipRequestFocus(true);
    }

    @Override
    public int getPageId() {
        return getId();
    }

    @Override
    public void setEventHandler(EventHandler handler) {
        mEventHandler = handler;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        savedFocus = nul
        frontDialogView = null;
    }

    @Override
    public void notifyAfterShow() {
        Log.d(TAG, "notifyAfterShow this:" + getPageId());
        logPerformance("notifyAfterShow");
        releaseFocus();
        if(savedFocus != null){
            savedFocus.requestFocus();
            if(LogUtils.isDebug()) {
                Log.e(TAG, "notifyBeforeHide notifyAfterShow exe requestFocus:" + savedFocus);
            }
            savedFocus = null;
        }
        changePageHidden(false);
    }

    void blockFocus(){
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }

    void releaseFocus(){
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
    }

    public String getPageName() {
        return params != null ? params.getString("name") : null;
    }

    @Override
    public void notifyBeforeHide() {
        changePageHidden(true);
        savedFocus = findFocus();
        blockFocus();

        Log.i(TAG, "notifyBeforeHide this :" + getPageId());
    }

    /** 是否是Dialog模式,Dialog模式下不隐藏上一个界面 默认false **/
    public boolean isDialogMode() {
        return params != null && params.getBoolean("isDialogMode");
//        return true;
    }

    public static void logPerformance(String msg) {
        if (!L.DEBUG) return;
        long time = new Date().getTime();
        Log.i("DebugPerf","#####################");
        Log.i("DebugPerf",msg+" on "+time);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        logPerformance("onAttachedToWindow");
    }

    //region 按键事件

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(isPageHidden()) return false;
        if(mEventHandler != null) {
            mEventHandler.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    //endregion

    @Override
    public String toString() {
        return "EsPageView{" +
                "params='" + params + '\'' +
                ", id=" + getPageId() +
                ", super=" +super.toString() +
                '}';
    }


    @Override
    public void beforeDialogDivVisibleChange(HippyViewGroup hippyViewGroup,boolean show) {
        super.beforeDialogDivVisibleChange(hippyViewGroup,show);
        if (show) {
            dialogSavedFocus = findFocus();
//            Log.i(TAG, "beforeDialogDivVisibleChange dialogSavedFocus :" + dialogSavedFocus);
        }else{
            frontDialogHasFocus = hippyViewGroup != null && hippyViewGroup.hasFocus();
        }
    }

    @Override
    public void notifyDialogDivVisibleChange(final HippyViewGroup hippyViewGroup,boolean isShow) {
        super.notifyDialogDivVisibleChange(hippyViewGroup,isShow);
//        if (hippyViewGroup != frontDialogView) {
//            onFrontDialogChange(frontDialogView);
//        }
        if (hippyViewGroup == null) {
            return;
        }
//        Log.i(TAG,"notifyDialogDivVisibleChange isShow "+isShow+",frontDialogView:"+ ExtendUtil.debugViewLite(hippyViewGroup));
        if (hippyViewGroup == frontDialogView) {
            if (!isShow) {
                onFrontDialogHide(hippyViewGroup);
            }
        }else{
            if (isShow) {
                onFrontDialogShow(hippyViewGroup);
            }
        }
        frontDialogView = hippyViewGroup;
    }



    protected void onFrontDialogHide(View frontDialogView){
        if(dialogSavedFocus != null && frontDialogHasFocus){
            dialogSavedFocus.requestFocus();
//            Log.e(TAG, "onFrontDialogHide restore dialogSavedFocus requestFocus:" + dialogSavedFocus);
            dialogSavedFocus = null;
        }
        frontDialogHasFocus = true;
        this.frontDialogView = null;
    }




    protected void onFrontDialogShow(View frontDialogView){
        //记住上次焦点
        //savedFocus = findFocus();
    }


    @Override
    public void addFocusables(ArrayList<View> arrayList, int i, int i1) {
        if (frontDialogView != null && frontDialogView.hasFocus()) {
            if(LogUtils.isDebug()) {
                Log.i(TAG, "addFocusables add on frontDialogView hasFocus ");
            }
            frontDialogView.addFocusables(arrayList, i, i1);
        }else {
            super.addFocusables(arrayList, i, i1);
        }
    }
}
