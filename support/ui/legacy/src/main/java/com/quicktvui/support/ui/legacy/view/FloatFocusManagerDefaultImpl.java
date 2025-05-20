package com.quicktvui.support.ui.legacy.view;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.quicktvui.support.ui.legacy.FConfig;

class FloatFocusManagerDefaultImpl  implements IFloatFocusManager{
    IFloatFocus mFloatFocus;

    static boolean DEBUG = FConfig.DEBUG;

    final static String TAG = "IFloatFocusManager";

    boolean mFrozen = false;

    boolean mVisible = true;

    View mCancelMoveOneShot;

    ITVView mFocused;

    FloatFocusType mFocusType = FloatFocusType.Default;

    final TVRootView mRootView;

    final Handler mHandler;

    ReLocatedTask mReLocatedTask;

    FloatFocusManagerDefaultImpl(TVRootView rootView) {
        mRootView = rootView;
        mHandler  = new Handler();
    }



    @Override
    public View getFloatFocusView() {
        return mFloatFocus.getView();
    }

    @Override
    public void setFloatFocus(IFloatFocus floatFocus) {
        this.mFloatFocus = floatFocus;
        if(mFloatFocus != null)
            mFloatFocus.setVisible(false);
        mFocusType = FloatFocusType.Custom;
    }

    /**
     * 锁定/解决锁定 浮动焦点
     *
     * @param b
     */
    @Override
    public void setFrozen(boolean b) {
        if(DEBUG){
            Log.v(IFloatFocus.TAG,TAG+ " setFrozen b  is "+b);
        }
        mFrozen = b;
        if(b){
            if(mFloatFocus != null)
            mFloatFocus.frozen();
        }
    }

    /***
     * 根据目前选中的焦点，定位其位置
     */
    @Override
    public void reLocateFocused(int duration) {
        removePedingTask();
        if(DEBUG){
            Log.v(IFloatFocus.TAG,TAG+ " reLocateFocused mFocused  is "+mFocused);
        }
        if(mFocused != null){
            AttachInfo ai =  mFocused.getAttachInfo();
            ai.mCancelMove = false;
            locateView(mFocused, duration);
        }
    }

    /***
     * 将焦点框定位到指定fview
     *
     * @param view 指定fview
     */
    @Override
    public void locateView(ITVView view, int duration) {
        removePedingTask();
        locateViewInternal(null,view,duration);
    }

    /***
     * 设置可见状态
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        this.mVisible = visible;
        if(mFloatFocus != null)
             mFloatFocus.setVisible(visible);
    }

    @Override
    public void show(int duration) {
        if(mFloatFocus != null)
        mFloatFocus.show(duration);
    }

    @Override
    public void dismiss(int duration) {
        if(mFloatFocus != null)
        mFloatFocus.dismiss(duration);
    }

    @Override
    public void offsetFLoatFocus(int dx, int dy) {
        if(mFloatFocus != null)
        mFloatFocus.offset(dx,dy);
    }

    /***
     * 一次性的将浮动焦点冻结（根据focused判断一次）
     *
     * @param focused
     */
    @Override
    public void pauseMoveOneShot(ITVView focused) {
            AttachInfo ai =  focused.getAttachInfo();
            ai.mCancelMove = true;
    }

    @Override
    public void dismissAndReappear(int interval) {
        if(DEBUG){
            Log.v(IFloatFocus.TAG,TAG + " dismissAndReappear interval is "+interval);
        }
        dismiss(0);
        setFrozen(true);
        removePedingTask();
        pendingRelocated(interval);
    }

    @Override
    public void cancelDismissAndReappear() {
        removePedingTask();
    }

    void removePedingTask(){
        if(mReLocatedTask != null){
            mHandler.removeCallbacks(mReLocatedTask);
            mReLocatedTask = null;
        }
    }

    void pendingRelocated(int interval){

        mReLocatedTask = new ReLocatedTask();
        mHandler.postDelayed(mReLocatedTask,interval);

    }



    final class ReLocatedTask implements Runnable{

        @Override
        public void run() {
            if(DEBUG){
                Log.v(IFloatFocus.TAG,TAG + " ReLocatedTask run is ");
            }
            setFrozen(false);
            reLocateFocused(0);
            show(IFloatFocus.DURATION);
        }
    }




//    /***
//     * 一次性的将浮动焦点冻结（根据focused判断一次）
//     *
//     * @param focused
//     */
//    @Override
//    public void resumeMoveOneShot(ITVView focused) {
//        AttachInfo ai =  focused.getAttachInfo();
//        ai.mCancelMove = false;
//    }


    void requestChildMoveFloatFocus(ITVView child, ITVView focused) {
        if(mFocused == null && mFloatFocus != null){
            mFloatFocus.dismiss(0);
            mFloatFocus.setVisible(true);
            mFloatFocus.show(500);
        }
        if(focused != mFocused) {
            locateViewInternal(child, focused,-1);
        }
        mFocused = focused;
    }

    void locateViewInternal(ITVView child, ITVView focused, int duration){
        if(DEBUG){
            Log.v(IFloatFocus.TAG,TAG+ " locateViewInternal mEnableFloatFocus  is "+mEnableFloatFocus);
        }
        if(mEnableFloatFocus && mFloatFocus != null){

            final ITVView fFocused =  focused;
            if(mFrozen) {
                if(DEBUG){
                    Log.w(IFloatFocus.TAG,TAG+ "requestChildMoveFloatFocus floatFocus frozen!!!!! focused is "+focused);
                }
                return;
            }
            AttachInfo ai = fFocused.getAttachInfo();
            mFloatFocus.transformTo(fFocused,ai.mFloatFocusOffset,ai.mFloatFocusAlpha,duration);
//            final TVMoveReuqest mv = getFloatFocusMoveRectForFView(fFocused,attachInfo.mFloatFocusOffset);
//            mv.alpha = attachInfo.mFloatFocusAlpha;
//            if(mv.valid)
//                mFloatFocus.move(mv,duration);
        }
    }



//    final float scale = v.getFocusScaleRect() + 1;
//    final float scaledWidth = next.getWidth() * scale;
//    final float scaledHeight = next.getHeight() * scale;
//    zoom.setSize(scaledWidth + FLOAT_FOCUS_LINE_WIDTH * 0 + padding * 2, scaledHeight + FLOAT_FOCUS_LINE_WIDTH * 0+ padding * 2);
//    //                android.util.Log.v("", "next width is " + next.getWidth() + " height is " + next.getHeight());
//    // android.util.Log.v("", "focusImage width is " + mFocusImage.getWidth() + " height is " + mFocusImage.getHeight());
//    RelativeMovetoAction move = new RelativeMovetoAction(next);
//
//    float targetX = next.getRealityX() -(scaledWidth - next.getWidth()) * 0.5f - FLOAT_FOCUS_LINE_WIDTH * 0 + padding;
//    float targetY = next.getRealityY() -(scaledHeight - next.getHeight()) * 0.5f - FLOAT_FOCUS_LINE_WIDTH  * 0+ padding;

    IFloatFocus setupDefaultFloatFocus() {
        if(mEnableFloatFocus && mFocusType == FloatFocusType.Default) {
            final TVRootView rootView = mRootView;
            if(mFloatFocus != null){
                mFloatFocus.remove(rootView);
            }

            TVFocusFrame v = new TVFocusFrame(rootView.getContext());
            v.setVisible(false);
            v.addToContainer(mRootView);
            if (v != null) {
                mFloatFocus =  v;
            }
            if (DEBUG) {
                Log.v(TAG, "onFinishInflate floatfocus is " + mFloatFocus);
            }
            return v;
        }
        return null;
    }


    void bringToFront(){
        if(mFloatFocus != null){
            mFloatFocus.bringToFront();
        }
    }
}
