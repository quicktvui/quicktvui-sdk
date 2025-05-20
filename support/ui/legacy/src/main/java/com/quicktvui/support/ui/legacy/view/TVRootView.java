package com.quicktvui.support.ui.legacy.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.Nullable;

import com.quicktvui.support.ui.legacy.FConfig;

import com.quicktvui.support.ui.legacy.R;


public class TVRootView extends InnerFocusDispatchRoot {

    FloatFocusManagerDefaultImpl mFloatFocusManager;

//    IFloatFocus mFloatFocus;

    static public boolean DEBUG = FConfig.DEBUG;

    ITVView mFocused = null;

    static public String ROOTVIEW_TAG = "RootView";


    static final String TAG = "TVRootView";

    int mFloatFocusType = 1;


    static boolean enableFLoatFocus = false;

    View mFocusedView = null;

    boolean disableRootFocusSearch = true;

    boolean focusBlocked = false;

//    boolean isBlockKeyUp = false;
//    boolean isBlockKeyDown = false;
//    boolean isBlockKeyLeft = false;
//    boolean isBlockKeyRight = false;


    public void disableRootFocusSearch(boolean disableRootFocusSearch) {
        this.disableRootFocusSearch = disableRootFocusSearch;
    }

    public TVRootView(Context context) {
        super(context);
        setTag(ROOTVIEW_TAG);
        initFloatFocusManager();
        init();
    }

    public TVRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTag(ROOTVIEW_TAG);
        initFloatFocusManager();
    }

    void init(){


    }



    public TVRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTag(ROOTVIEW_TAG);
        initFloatFocusManager();
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TVRootView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTag(ROOTVIEW_TAG);
        initFloatFocusManager();
        init();
    }


    void initFloatFocusManager(){
        if(enableFLoatFocus) {
            mFloatFocusManager = new FloatFocusManagerDefaultImpl(this);
        }
//        Log.v(TAG,"initFloatFocusManager setFocusType mFloatFocusType is  "+mFloatFocusType);
//        setFocusType(mFloatFocusType);
    }




    @Override
    public void blockFocus() {
        focusBlocked = true;
        Log.e(TAG,"blockFocus called");
        super.blockFocus();
    }

    @Override
    public void unBlockFocus() {
        super.unBlockFocus();
        Log.e(TAG,"unBlockFocus called");
        focusBlocked = false;
    }

    void setFocusType(int value){
        Log.v(TAG,"setFocusType value is  "+value);
        if(mFloatFocusManager != null) {
            IFloatFocusManager.FloatFocusType type = IFloatFocusManager.FloatFocusType.None;
            switch (value) {
                case 0:
                    type = IFloatFocusManager.FloatFocusType.Default;
                    break;
                case 1:
                    type = IFloatFocusManager.FloatFocusType.None;
                    break;
                case 2:
                    type = IFloatFocusManager.FloatFocusType.Custom;
                    break;
            }
            mFloatFocusManager.mFocusType = type;
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        mFocusedView = focused;
        super.requestChildFocus(child, focused);
        if(DEBUG){
            Log.v(TAG,"====FRootView requestChildFocus child is "+child+" focused is "+focused +" mFocused is "+mFocused+" isEqual is "+(focused == mFocused));
        }
        if(focused != null) {
            removeRequestFocusInDescendantsTask();
        }

    }



    /**view初始化后调用
     * @param attrs
     */
    @Override
    protected void onInitializeFromAttributes(AttributeSet attrs){
        super.onInitializeFromAttributes(attrs);
        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TVRootView);
            final int floatFocusTypeValue  = typedArray.getInt(R.styleable.TVRootView_float_focus_type,1);
//            setFocusType(floatFocusTypeValue);
//            Log.v(TAG,"floatFocusTypeValue is "+floatFocusTypeValue+" Manager is "+mFloatFocusManager);
            mFloatFocusType = floatFocusTypeValue;
            initFloatFocusManager();
            setFocusType(floatFocusTypeValue);
            typedArray.recycle();
        }
    }



    @Override
    public void clearChildFocus(View child) {
        Log.e(TAG,"clearChildFocus child :"+child);
        super.clearChildFocus(child);
    }


    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        Log.e(TAG,"requestFocus direction :"+direction+" previouslyFocusedRect:"+previouslyFocusedRect);

        if(focusBlocked){
            Log.e(TAG,"focus blocked return ");
            return onRequestFocusInDescendants(direction,previouslyFocusedRect);
        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    public void clearMarkedFocus(){
        mFocusedView = null;
    }

    private Runnable onRequestFocusInDescendantsTask;




    private boolean superOnRequestFocusInDescendants(int direction, Rect previouslyFocusedRect){
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    private void removeRequestFocusInDescendantsTask(){
        if(onRequestFocusInDescendantsTask != null){
            removeCallbacks(onRequestFocusInDescendantsTask);
            Log.w(TAG,"onRequestFocusInDescendantsDelayed remove pre task");
        }
    }

    private boolean onRequestFocusInDescendantsDelayed(final int direction, final Rect previouslyFocusedRect){
        removeRequestFocusInDescendantsTask();
        onRequestFocusInDescendantsTask = new Runnable() {
            @Override
            public void run() {
                Log.w(TAG,"post superOnRequestFocusInDescendants");
                superOnRequestFocusInDescendants(direction,previouslyFocusedRect);
            }
        };
        postDelayed(onRequestFocusInDescendantsTask,300);
        return true;
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        final boolean isViewDescendantOfThis = TVViewUtil.isViewDescendantOf(mFocusedView,this);
        Log.e(TAG,"onRequestFocusInDescendants direction:"+direction+" is marked focus DescendantOf this:"+isViewDescendantOfThis);

        if(mFocusedView == null || !disableRootFocusSearch) {
           // return onRequestFocusInDescendantsDelayed(direction,previouslyFocusedRect);
            return superOnRequestFocusInDescendants(direction,previouslyFocusedRect);
        }else{
            if(!mFocusedView.isFocused() && isViewDescendantOfThis) {
                if(focusBlocked) {
                    super.unBlockFocus();
                }
                mFocusedView.requestFocus();
                if(focusBlocked) {
                    super.blockFocus();
                }
                Log.e(TAG, "onRequestFocusInDescendants give back focus to : " + mFocusedView);
            }else{
                Log.e(TAG, "onRequestFocusInDescendants interrupt!!! mFocusedView:"+mFocusedView);
            }
            return true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    @Override
    public boolean dispatchKeyEvent( KeyEvent event) {
        return super.dispatchKeyEvent( event);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupDefaultFloatFocusIfNeed();
    }

    void setupDefaultFloatFocusIfNeed(){
//        setFocusType(mFloatFocusType);
        if(enableFLoatFocus) {
            mFloatFocusManager.setupDefaultFloatFocus();
        }
    }


    @Override
    public void requestChildMoveFloatFocus(ITVView child, ITVView focused) {
        if(enableFLoatFocus) {
            mFloatFocusManager.requestChildMoveFloatFocus(child, focused);
        }
    }


    @Deprecated
    public @Nullable static TVRootView findRootView(View v) {
        if (enableFLoatFocus) {
            View androidRoot = v.getRootView();
            View root = androidRoot.findViewWithTag(ROOTVIEW_TAG);
            if (FConfig.DEBUG) {
                Log.v(TAG, "findRootView find RootWithTag is " + root + " androidRoot is " + androidRoot);
            }
            if (root instanceof TVRootView) {
                return (TVRootView) root;
            }
        }
        return null;
    }

    public @Nullable static TVRootView findRoot(View v) {
            View androidRoot = v.getRootView();
            View root = androidRoot.findViewWithTag(ROOTVIEW_TAG);
            if (FConfig.DEBUG) {
                Log.v(TAG, "findRoot find RootWithTag is " + root + " androidRoot is " + androidRoot);
            }
            if (root instanceof TVRootView) {
                return (TVRootView) root;
            }
            return null;
    }

    public @Nullable static TVRootView findRootBy(Context activityContext) {
        if(activityContext instanceof  Activity) {
            final View root = ((Activity) activityContext).getWindow().getDecorView();
            if(root != null) {
                final View result = root.findViewWithTag(ROOTVIEW_TAG);
                if (result instanceof TVRootView) {
                    return (TVRootView) result;
                }
            }
        }
        return null;
    }




    @Override
    public void addView(View child) {
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        if(enableFLoatFocus) {
            mFloatFocusManager.bringToFront();
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if(enableFLoatFocus) {
            mFloatFocusManager.bringToFront();
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        if(enableFLoatFocus) {
            mFloatFocusManager.bringToFront();
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
            if(enableFLoatFocus) {
                mFloatFocusManager.bringToFront();
            }
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        boolean b =  super.addViewInLayout(child, index, params);
        if(enableFLoatFocus) {
            mFloatFocusManager.bringToFront();
        }
        return b;
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        boolean b =  super.addViewInLayout(child, index, params, preventRequestLayout);
        if(enableFLoatFocus) {
            mFloatFocusManager.bringToFront();
        }
        return b;
    }


    @Override
    public IFloatFocusManager getFloatFocusManager() {
        return mFloatFocusManager;
    }
}
