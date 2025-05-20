package com.quicktvui.support.small.player.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.RecyclerView;

import com.quicktvui.base.ui.FocusDispatchView;
import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.hippyext.views.fastlist.FastAdapter;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;
import com.quicktvui.hippyext.views.fastlist.FastItemView;

import java.util.HashMap;

import com.quicktvui.support.player.manager.base.PlayerBaseView;
import com.quicktvui.support.player.manager.player.IPlayer;


public class GlobalPlayerView extends FastItemView {

    public String bindHolderID = null;
    private String followerName = null;
    HashMap<Object, PlaceholderView> placeholderMap;
    public final static String TAG = "FloatItemLog";

    boolean checkScrollStateDirty = true;

    public static boolean LOG_ENABLE = LogUtils.isDebug();

    int offsetX, offsetY;
    int insetX = 0, insetY = 0;
    //    int position = -1;
//    private Object item;
//    private Runnable changeLayoutTask;
    private Runnable changeVisibleTask;
    private Runnable changeVisibleTask2;
    private Runnable postShowTargetHolderTask;
    public boolean hasPlayerEnabled = false;
    //    int changeLayoutDelay = 100;
    int changeVisibleDelay = 300;
    int changeFullScreenDelay = 300;

    FastListView frontParentList;

    RecyclerView.OnScrollListener onScrollListener;

    GlobalPlayerCommand mPlayerCommand;

    PlaceholderView mBindPlaceholder;//类型为播放器

//    private int tx, ty, tWidth, tHeight;

    public Rect visibleArea;
    public Rect floatArea;

    //锁定
    private boolean lock = false;
    private boolean isFullScreen = false;

    protected boolean isPlayerShow;
    private boolean isPlaceholderFront;//是否在最前面

    //播放器是否可见
    private boolean isPlayerVisible = false;
//    private boolean isPlayerIntentVisible = false;

    //播放器是否被暂停
    private boolean isPlayerPaused = true;
    //是否已经播放
    private boolean isPlayerStarted = false;

    public boolean isFloatState = false;

    private int windowState = 0;
    public static int WINDOW_STATE_SMALL = 0;
    public static int WINDOW_STATE_FULL = 1;
    public static int WINDOW_STATE_FLOAT = 2;

    private boolean disableLayout = false;


    public GlobalPlayerView(Context context, String name) {
        super(context);
        if (LOG_ENABLE) {
            Log.i(TAG, "new FloatItemView ,this:" + this  + ",name:" + name);
        }
        int sw = getContext().getResources().getDisplayMetrics().widthPixels;
        int sh = getContext().getResources().getDisplayMetrics().heightPixels;
        this.visibleArea = new Rect(0,0,sw,sh);
    }

    public void registerPlaceholder(Object id, PlaceholderView placeholder){
        if (placeholderMap != null) {
            if (LOG_ENABLE) {
                Log.d(TAG, "registerPlaceholder ,id:" + id + "placeholder:" + placeholder);
            }
            placeholderMap.put(id,placeholder);
        }else{
            Log.e(TAG, "registerPlaceholder on placeholderMap null");
        }
    }

    public void unregisterPlaceholder(Object id){
        if (placeholderMap != null) {
            if (LOG_ENABLE) {
                Log.d(TAG, "unregisterPlaceholder ,id:" + id );
            }
            placeholderMap.remove(id);
        }
    }

    public void setFrontParentList(FastListView frontParentList) {
        this.frontParentList = frontParentList;
    }

    public void setPlaceholderID(String id) {
        this.bindHolderID = id;
        if(LOG_ENABLE) {
            Log.i(TAG, "setPlaceholderName ,this:" + this + "name:" + id);
        }
        if(!TextUtils.isEmpty(id)){
            View v = findViewByIDFromRoot(this,id);
            if (v instanceof PlaceholderView) {
                PlaceholderView ph = (PlaceholderView) v;
                ph.requestChangePlayerVisible(true);
            }
            if(LOG_ENABLE) {
                Log.e(TAG, "setPlaceholderName ,this:" + this + ",hookPlayer:" + v + ",name:" + id);
            }
        }else{
            mBindPlaceholder = null;
        }
    }

    public static View findRootView(View view){
        View rootView = HippyViewGroup.findPageRootView(view);
        if (rootView == null) {
            rootView = FocusDispatchView.findRootView(view);
        }
        if(LOG_ENABLE) {
            Log.i(TAG,"findRootView :"+rootView);
        }
        return rootView;
    }

    public static View findViewByIDFromRoot(View view, String name){
        View root = findRootView(view);
        View result = null;
        if (root != null) {
            result = FastAdapter.findTVItemViewById(root,name);
        }
        if(LOG_ENABLE) {
            Log.i(TAG, "findViewByNameFromRoot root:" + root + ",result:" + result + ",name:" + name);
        }
        return result;
    }

    public void setPlaceholderMap(HashMap<Object, PlaceholderView> placeholderMap) {
        this.placeholderMap = placeholderMap;
    }

    void changeBindPlaceholder(PlaceholderView pv){
        this.mBindPlaceholder = pv;
        if (pv.mPlayerCommand == null) {
            pv.mPlayerCommand = new GlobalPlayerCommand(this,pv);
        }
        if(!hasPlayerEnabled){
            pv.mPlayerCommand.changeEnable(true);
            hasPlayerEnabled = true;
        }

        if (LOG_ENABLE) {
            Log.i(TAG, "changeBindPlaceholder pv:" + pv + ",item:" + pv.getBoundItem());
        }
        if (pv.mPlayerCommand != null) {
            pv.mPlayerCommand.changeBindPlaceholder(pv.getPlaceholderId());
        }
    }


    void hookPlayer(PlaceholderView placeHolder){
            placeHolder.mPlayerView = this;
            if (floatArea != null) {
                placeHolder.floatArea = floatArea;
            }
            if(visibleArea != null){
                placeHolder.visibleArea = visibleArea;
            }
    }


    int[] temp = new int[2];


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    void layoutFullScreen(int x, int y, int w, int h) {
        removeVisibleTask();
        exeChangeVisible(false);
        IPlayer ip = findIPlayer(this);
        if (ip != null && ip.getPlayerView() != null) {
            ip.getPlayerView().setVisibility(View.INVISIBLE);
        }
        this.changeVisibleTask = () -> {
            layoutFloatItem(x, y, w, h);
            //layoutFullScreen(GlobalPlayerFrameView.this,x,y,w,h);
            if (ip != null && ip.getPlayerView() != null) {
                ip.getPlayerView().setVisibility(View.VISIBLE);
            }
            exeChangeVisible(true);
        };
        postDelayed(this.changeVisibleTask, changeFullScreenDelay);
    }

    void requestChangeFullScreen(boolean isFull){

        if(mBindPlaceholder != null){//使用格子类型为播放器
            mBindPlaceholder.postChangeFullScreen(0,isFull);
        }
    }


    void updatePlayerView(View view, int x, int y, int w, int h) {
        if (view != null) {
            RenderUtil.reLayoutView(view, x, y, w, h);
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                final View v = vg.getChildAt(i);
                updatePlayerView(v, x, y, w, h);
            }
        }
    }


    IPlayer findIPlayer(View view) {
        if(view instanceof PlayerBaseView){
            return ((PlayerBaseView) view).getPlayer();
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                final View v = vg.getChildAt(i);
                IPlayer player = findIPlayer(v);
                if (player != null) {
                    return player;
                }
            }
        }
        return null;
    }


    void layoutFloatItem(int x, int y, int w, int h) {
        if (LOG_ENABLE) {
            Log.d(TAG, "layoutFloatItem x :" + x + ",y:" + y + ",width:" + w + ",height:" + h + ",this:" + this);
        }
        if(!disableLayout) {
            RenderUtil.reLayoutView(this, x, y, w, h);
        }
    }


    void removeVisibleTask() {
        if (this.changeVisibleTask != null) {
            removeCallbacks(this.changeVisibleTask);
        }
        if (this.changeVisibleTask2 != null) {
            removeCallbacks(this.changeVisibleTask2);
        }
    }

    void exeChangeVisible(boolean b) {
//        boolean isPlaceholderFront = false;
//        if (b && mBindPlaceholder != null) {
//            //b = mBindPlaceholder.isPlaceholderFront;
//            isPlaceholderFront =  mBindPlaceholder.isPlaceholderFront;
//        }
        if(mBindPlaceholder != null){
            if(b != mBindPlaceholder.isPlayerVisible) {
                setVisibility(b ? View.VISIBLE : View.INVISIBLE);
            }
            mBindPlaceholder.isPlayerVisible = b;
        }else{
            setVisibility(View.INVISIBLE);
        }
        if (LOG_ENABLE) {
            Log.e(TAG, "exeChangeVisible :" + b  + ",bindPlaceHolder:" + mBindPlaceholder);
        }
    }

    boolean isFloatEnable(){
        return floatArea != null && !floatArea.isEmpty();
    }

    void callChangeFloatWindow(boolean b){
        if(mBindPlaceholder != null){
            if (LOG_ENABLE) {
                Log.e(TAG, "callChangeFloatWindow " + b);
            }
            mBindPlaceholder.mPlayerCommand.changeFloatWindow(b);
        }
    }

    boolean checkPlaceholderVisible(final PlaceholderView placeholder){
        placeholder.getLocationOnScreen(temp);
        final int x = temp[0] + offsetX + insetX;
        final int y = temp[1] + offsetY + insetY;
        int width = placeholder.getWidth() - insetX * 2;
        int height = placeholder.getHeight() - insetY * 2;
        return placeholder.checkPlaceHolderVisible(x,y,width,height);
    }


    void postChangeVisible(final boolean b, int delay, final PlaceholderView placeholder, final boolean changePlayState) {
        removeVisibleTask();
        if (!lock) {
            placeholder.getLocationOnScreen(temp);
            final int x = temp[0] + offsetX + insetX;
            final int y = temp[1] + offsetY + insetY;
            int width = placeholder.getWidth() - insetX * 2;
            int height = placeholder.getHeight() - insetY * 2;
            final boolean placeHolderVisible = placeholder.checkPlaceHolderVisible(x,y,width,height);
            if (!b) {
                if (LOG_ENABLE) {
                    Log.d(TAG, "postChangeVisible false----");
                }
                if (!placeHolderVisible && changePlayState && !placeholder.isFloatEnable()) {
                    placeholder.notifyPause();
                }
                if (placeholder.isFloatEnable()) {
                    if(placeHolderVisible) {
                        //变化成float状态时，不需要再隐藏，否则会出现滚动过程中，小窗显示/非显示频繁切换
                        exeChangeVisible(false);
                    }
                }else{
                    exeChangeVisible(false);
                }
            } else {
                this.changeVisibleTask = () -> {
                    if (LOG_ENABLE) {
                        Log.d(TAG, "postChangeVisible true+++++ placeholder："+placeholder);
                    }
                    if (placeHolderVisible) {
                        if (LOG_ENABLE) {
                            Log.v(TAG, "updateFloatLayoutIfNeed x :" + x + ",y:" + y + ",width:" + width + ",height:" + height);
                        }
                        if (placeholder.mPlayerCommand != null) {
                            placeholder.mPlayerCommand.layout(x, y, width, height);
                        }
                        layoutFloatItem(x,y,width,height);
                        if(!isFloatEnable()) {
                            exeChangeVisible(true);
                        }else{
                            Runnable runnable = () -> exeChangeVisible(true);
                            this.changeVisibleTask2 = runnable;
                            postDelayed(runnable,400);
//                            exeChangeVisible(true);
                            if (placeholder.mPlayerCommand != null && isFloatState) {
                                    //防止快速点击进入全屏时，误切换成了浮动模式
                                    isFloatState = false;
                                    callChangeFloatWindow(false);
                            }
                        }
                        if (changePlayState && placeholder.mPlayerCommand != null) {
                                placeholder.notifyResume();
                        }
                    }else{
                        if (placeholder.isFloatEnable()) {
                            final Rect fr = placeholder.floatArea;
                            //如果启动悬浮功能，则将位置定位到悬浮位置
                            if (placeholder.mPlayerCommand != null) {
                                placeholder.mPlayerCommand.layout(fr.left, fr.top, fr.width(), fr.height());
                                if (!isFloatState) {
                                    isFloatState = true;

                                    callChangeFloatWindow(true);
                                }
                            }
                            layoutFloatItem(fr.left, fr.top, fr.width(), fr.height());
                            exeChangeVisible(true);
                        }
                    }
                };
                if (delay < 1) {
                    this.changeVisibleTask.run();
                } else {
                    postDelayed(this.changeVisibleTask, delay);
                }
            }
        }
    }

    public void removePostShowTargetHolder(){
        if (postShowTargetHolderTask != null) {
            removeCallbacks(postShowTargetHolderTask);
            postShowTargetHolderTask = null;
        }
    }

    public void postShowTargetHolder(int delay,boolean changePlay,FastListView parent){
        removePostShowTargetHolder();
        postShowTargetHolderTask = () -> exeShowTargetHolder(delay,changePlay,parent);
        postDelayed(this.postShowTargetHolderTask,100);
    }
    public void exeShowTargetHolder(int delay,boolean changePlay,FastListView parent){
        if (placeholderMap == null) {
            Log.e(TAG,"postShowTargetHolder error on placeholderMap == null");
            return;
        }
        for(PlaceholderView ph : placeholderMap.values()){
            boolean placeholderVisible = checkPlaceholderVisible(ph);
            if (placeholderVisible && ph.isAttached) {
                boolean isFront = parent != null && parent == ph.getRootFastList();
                if (LOG_ENABLE) {
                    Log.i(TAG, "postShowTargetHolder isFront :"+isFront+",ph:"+ph);
                    Log.i(TAG, "postShowTargetHolder parent :"+parent+",ph parent:"+ph.getRootFastList());
                }
                if(isFront){
                    //hookPlayer(ph);
                    changeBindPlaceholder(ph);
                    ph.requestStartPlayIfNeed(this);
                    Log.e(TAG,"postShowTargetHolder find visible holder:"+ph);
                    postChangeVisible(true,delay,ph,changePlay);
                    break;
                }else{
                    Log.v(TAG,"postShowTargetHolder return on isPlaceholderFront false :"+ph);
                }
            }else{
                Log.v(TAG,"postShowTargetHolder unVisible holder:"+ph);
            }
        }
    }



    @Override
    public String toString() {
        return "Player:"+hashCode();
    }

    public void setLayout(int l, int t, int w, int h,boolean syncPlayer) {
        RenderUtil.reLayoutView(this,l,t,w,h);
        if (mBindPlaceholder != null && mBindPlaceholder.mPlayerCommand != null) {
            mBindPlaceholder.mPlayerCommand.layout(l,t,w,h);
        }
        if (syncPlayer) {
            IPlayer ip = findIPlayer(this);
            if (ip != null) {
                ip.setPlayerSize(w,h);
            }
        }
    }


    private String getPlaceholderId() {
        if (bindItem instanceof HippyMap) {
            return ((HippyMap) bindItem).getString("id");
        }
        return null;
    }

    public void setDisableLayout(Boolean disableLayout) {
        this.disableLayout = disableLayout;
    }

    public void setLock(Boolean lock) {
        this.lock = lock;
    }
}
