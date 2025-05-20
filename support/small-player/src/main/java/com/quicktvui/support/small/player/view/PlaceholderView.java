package com.quicktvui.support.small.player.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.quicktvui.base.ui.FocusDispatchView;
import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.hippyext.views.fastlist.FastAdapter;
import com.quicktvui.hippyext.views.fastlist.FastItemView;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

import java.util.HashMap;

import com.quicktvui.support.player.manager.base.PlayerBaseView;
import com.quicktvui.support.player.manager.player.IPlayer;


public class PlaceholderView extends FastItemView implements View.OnClickListener {

    private String bindHolderID = null;
    private String followerName = null;
    private Object id;
    HashMap<String, View> followerMap;
    HashMap<Object, GlobalPlayerView> placeholderMap;
    public final static String TAG = "FloatItemLog";

    GlobalPlayerView mPlayerView;
    FastListView rootFastList;

    public static boolean LOG_ENABLE = LogUtils.isDebug();

    int offsetX, offsetY;
    int insetX = 0, insetY = 0;
    private Runnable changeVisibleTask;
    //    int changeLayoutDelay = 100;
    int changeVisibleDelay = 300;
    int changeFullScreenDelay = 300;

    RecyclerView.OnScrollListener onScrollListener;

    GlobalPlayerCommand mPlayerCommand;

    PlaceholderView mBindPlaceholder;//类型为播放器

//    private int tx, ty, tWidth, tHeight;

    public boolean isAttached = false;

    public Rect visibleArea;
    public Rect floatArea;

    //锁定
    private boolean lock = false;
    private boolean isFullScreen = false;
    private boolean isFullScreenClick = false;


    public boolean isPlaceholderFront;//是否在最前面

    //播放器是否可见
    public boolean isPlayerVisible = false;

    //播放器是否被暂停
    private boolean isPlayerPaused = true;
    //是否已经播放
    private boolean isPlayerStarted = false;

    private boolean isFloatState = false;

    private int windowState = 0;
    public static int WINDOW_STATE_SMALL = 0;
    public static int WINDOW_STATE_FULL = 1;
    public static int WINDOW_STATE_FLOAT = 2;

    private boolean disableLayout = false;

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public void refreshState() {
        this.requestChangePlayerVisible(true);
    }

    public void changeFullScreen(boolean b) {
        //this.postChangeFullScreen(0, b);
    }

    Object getBoundItem(){
        return bindItem;
    }

    public void setDisableLayout(boolean disableLayout) {
        this.disableLayout = disableLayout;
    }

    public PlaceholderView(Context context) {
        super(context);

        if (LOG_ENABLE) {
            Log.i(TAG, "new FloatItemView ,this:" + this );
        }

        int sw = getContext().getResources().getDisplayMetrics().widthPixels;
        int sh = getContext().getResources().getDisplayMetrics().heightPixels;
        this.visibleArea = new Rect(0,0,sw,sh);
//            this.floatArea = new Rect(50,50,960,480);

    }


    public void setFollowerName(String followerName) {
        this.followerName = followerName;
        if(LOG_ENABLE) {
            Log.i(TAG, "setFollowerName ,this:" + this + "followerName:" + followerName);
        }
    }

//    public void setPlaceholderID(String id) {
//        this.bindHolderID = id;
//        if(LOG_ENABLE) {
//            Log.i(TAG, "setPlaceholderName ,this:" + this + "name:" + id);
//        }
//        if(!TextUtils.isEmpty(id)){
//            View placeholder = findViewByIDFromRoot(this,id);
//            if(placeholder instanceof PlaceholderView) {
//                hookPlayer(this, (PlaceholderView) placeholder,true);
//            }
//            if(LOG_ENABLE) {
//                Log.e(TAG, "setPlaceholderName ,this:" + this + ",hookPlayer:" + placeholder + ",name:" + id);
//            }
//        }else{
//            mBindPlaceholder = null;
//        }
//    }

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


    public void setFollowerMap(HashMap<String, View> followerMap) {
        this.followerMap = followerMap;
    }

//    public void setPlaceholderMap(HashMap<Object, PlaceholderView> placeholderMap) {
//        this.placeholderMap = placeholderMap;
//    }

    protected GlobalPlayerView findFollowerView() {
        if (this.followerMap != null) {
            final View v = this.followerMap.get(followerName);
            if (v instanceof GlobalPlayerView) {
                return (GlobalPlayerView) v;
            }
        }
        return null;
    }


    void changeBindPlaceholder(PlaceholderView pv){
        this.mBindPlaceholder = pv;
        if (LOG_ENABLE) {
            Log.i(TAG, "changeBindPlaceholder pv:" + pv + ",item:" + bindItem);
        }
        if (pv.mPlayerCommand != null) {
            pv.mPlayerCommand.changeBindPlaceholder(getPlaceholderId());
        }
    }

    @Override
    public void onAttachToWindow(View parent, int position, Object item) {
        super.onAttachToWindow(parent, position, item);
        if (LOG_ENABLE) {
            Log.v(TAG, "onAttachToWindow ,this:" + this + ",position:" + position + ",parent:" + parent + ",onScrolling:" + this.onScrollListener);
        }
        if (parent instanceof FastListView) {
            if(this.onScrollListener != null) {
                ((FastListView) parent).addOnScrollListener(this.onScrollListener);
                ((FastListView) parent).setScrollToTopListener(this.onScrollListener);
            }
            this.rootFastList = (FastListView) parent;
            //((FastListView) parent).scrollToTop();
        }
//        if (item instanceof HippyMap) {
//            Object id = ((HippyMap) item).get("id");
//            this.id = id;
//            if (id != null && findFollowerView() != null) {
//                findFollowerView().registerPlaceholder(id,this);
//            }else{
//                if (LOG_ENABLE) {
//                    Log.e(TAG, "onAttachToWindow no id");
//                }
//            }
//        }else{
//            if (LOG_ENABLE) {
//                Log.e(TAG, "onAttachToWindow invalid item");
//            }
//        }

//        if (!this.isAttached) {
//            hookPlayer(this,false);
//        }
        this.isAttached = true;
        //requestStartPlayIfNeed();
        if (this.isFocusable()) {
            setOnClickListener(this);
        }
    }

//    void hookPlayer(PlaceholderView placeHolder, boolean forceBind){
//        final GlobalPlayerView pv = placeHolder.findFollowerView();
////        if(!forceBind && pv.bindHolderID != null && pv.mBindPlaceholder != null){
////            Log.e(TAG,"player has bindHolder :"+mBindPlaceholder);
////            return;
////        }
//        if (pv != null) {
//            pv.hookPlayer(placeHolder);
//        }
//    }


    @Override
    public void onUnBind(View parent, int position, Object item) {
        super.onUnBind(parent, position, item);
        if (LOG_ENABLE) {
            Log.i(TAG, "onUnBind ,this:" + this + "position:" + position);
        }
        if (parent instanceof FastListView && this.onScrollListener != null) {
            ((FastListView) parent).removeOnScrollListener(this.onScrollListener);
            ((FastListView) parent).setScrollToTopListener(null);
        }
        if (id != null && findFollowerView() != null) {
            findFollowerView().unregisterPlaceholder(id);
            id = null;
        }
    }

    @Override
    public void onDetachFromWindow(View parent, int position, Object item) {
        super.onDetachFromWindow(parent, position, item);
        if (LOG_ENABLE) {
            Log.v(TAG, "onDetachFromWindow ,this:" + this + "position:" + position);
        }
        this.mPlayerView = null;
        this.isAttached = false;


        if (this.isFocusable()) {
            setOnClickListener(null);
        }
        if (isFloatEnable() && isPlaceholderFront) {
            if (LOG_ENABLE) {
                Log.e(TAG, "onDetachFromWindow change visible ,this:" + this + "position:" + position);
            }
            requestChangePlayerVisible(false, 0, false);
            requestChangePlayerVisible(true, changeVisibleDelay, false);
        }
    }



    @Override
    public void onCreate(View parent) {
        super.onCreate(parent);
        if (LOG_ENABLE) {
            Log.e(TAG, "onCreate ,this:" + this);
        }
        requestChangePlayerVisible(false);
        this.onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (LOG_ENABLE) {
                    Log.v(TAG, "onScrollStateChanged ,this:" + this + ",newState:" + newState);
                }
                final GlobalPlayerView pv = findFollowerView();
                if (pv == null) {
                    return;
                }
                if(newState == RecyclerView.SCROLL_STATE_IDLE || isFloatEnable()) {
                    requestChangePlayerVisible(true, changeVisibleDelay, !isFloatEnable());
                    //pv.checkScrollStateDirty = false;
                }else{
                    if (LOG_ENABLE) {
                        Log.e(TAG, "onScrollStateChanged return on isPlayerVisible "+isPlayerVisible+",state:"+newState);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (LOG_ENABLE) {
                    Log.v(TAG, "onScrolled ,this:" + this + ",dx:" + dx + ",dy:" + dy);
                }
                if (Math.abs(dx) > 0 || Math.abs(dy) > 0) {
                    requestChangePlayerVisible(false, 0, !isFloatEnable());
//                    if(!isPlayerIntentVisible) {
//                        notifyPause();
//                    }
                } else if (dx == 0 && dy == 0) {
                    //滚动到顶部
                    requestChangePlayerVisible(false, 0, false);
                    requestChangePlayerVisible(true, changeVisibleDelay, !isFloatEnable());
                }
            }
        };
    }

    @Override
    public void onBind(View parent, int position, Object item) {
        super.onBind(parent, position, item);
        if (LOG_ENABLE) {
            Log.v(TAG, "onBind ,this:" + this + ",position:" + position+",item:"+item);
        }
        final GlobalPlayerView pv = findFollowerView();
        if (pv != null) {
            pv.hookPlayer(this);
            if (item instanceof HippyMap) {
                Object id = ((HippyMap) item).get("id");
                this.id = id;
                if (id != null && findFollowerView() != null) {
                    findFollowerView().registerPlaceholder(id,this);
                }else{
                    if (LOG_ENABLE) {
                        Log.e(TAG, "onBind no id");
                    }
                }
            }else{
                if (LOG_ENABLE) {
                    Log.e(TAG, "onBind invalid item");
                }
            }
        }

    }


    int[] temp = new int[2];

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (mPlayerCommand != null) {
            mPlayerCommand.changeFocused(gainFocus);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    void requestChangeFullScreen(boolean isFull){

        if(mBindPlaceholder != null){//使用格子类型为播放器
            mBindPlaceholder.postChangeFullScreen(0,isFull);
        }
    }

    void notifyWindowStateChanged(String state){
        switch (state){
            case "small":
                this.windowState = WINDOW_STATE_SMALL;
                break;
            case "full":
                this.windowState = WINDOW_STATE_FULL;
                break;
            case "float":
                this.windowState = WINDOW_STATE_FLOAT;
                break;
        }
    }


    boolean checkPlaceHolderVisible(int x, int y, int width, int height) {
        boolean b = x > -1 && y > -1 && width > 0 && height > 0;
        final Rect area = visibleArea;

        b = this.isAttached;//如果没有attached，则直接返回false
        //检查placeHolder是否在可见范围内
        if (b && area != null) {
            //检查横向
            b = (y >= area.top && y + height <= area.bottom);
            //检查纵向
            b &= (x >= area.left && x + width <= area.right);
        }
//
//        int sw = getContext().getResources().getDisplayMetrics().widthPixels;
//        int sh = getContext().getResources().getDisplayMetrics().heightPixels;
        //return tx + tWidth <= sw && ty + tHeight <= sh;
        if (LOG_ENABLE) {
            Log.i(TAG, "checkPHVisible visible :" + b+",visibleArea:"+visibleArea+",this:"+this);
        }
        return b;
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




    boolean isFloatEnable(){
        return floatArea != null && !floatArea.isEmpty();
    }





    public void requestChangePlayerVisible(boolean b, int delay, boolean changePlay) {
        final GlobalPlayerView fv = findFollowerView();
        if (fv != null) {
            if (LOG_ENABLE) {
                Log.d(TAG, "requestChangePlayerVisible this:" + this + ",b:" + b);
            }
            if (b) {
                fv.postShowTargetHolder(delay,changePlay,getRootFastList());
            }else {
//                fv.checkScrollStateDirty = true;
                fv.removePostShowTargetHolder();
                fv.postChangeVisible(false, delay,this, changePlay);
            }
        }
    }

    public void requestChangePlayerVisible(boolean b) {
        this.requestChangePlayerVisible(b, changeVisibleDelay, false);
        if (mPlayerCommand != null) {
            mPlayerCommand.changeOnShow(b);
        }
    }

    @Override
    public FastListView getParentListView() {
        return super.getParentListView();
    }

    public FastListView getRootFastList(){
        return rootFastList;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    public void notifyPauseTask() {
        super.notifyPauseTask();
        if (LOG_ENABLE) {
            Log.e(TAG, "----notifyPauseTask this:" + this);
        }
    }

    @Override
    public void notifyResumeTask() {
        super.notifyResumeTask();
        if (LOG_ENABLE) {
            Log.e(TAG, "+++++notifyResumeTask this:" + this);
        }
        if (getParentListView() != null) {
            GlobalPlayerView fv = findFollowerView();
            if (fv != null) {
                fv.setFrontParentList(getRootFastList());
            }
        }

    }

    @Override
    public void notifyBringToFront(boolean front) {
        super.notifyBringToFront(front);
        if (LOG_ENABLE) {
            Log.e(TAG, "notifyBringToFront ,this:" + this + ",front:" + front + ",isAttached:" + isAttached);
        }
        isPlaceholderFront = front;
        if (front) {
//            if(isAttached){
//                hookPlayer(this,true);
//            }
            //requestStartPlayIfNeed();
        } else {
            notifyStop();
            if (mPlayerView != null) {
//                mPlayerView.bindHolderID = null;
                mPlayerView.mBindPlaceholder = null;
            }
        }
        if (front) {
            requestChangePlayerVisible(false, 0, false);
            requestChangePlayerVisible(true, changeVisibleDelay * 2, true);
        }else {
            requestChangePlayerVisible(false, 0, false);
        }
    }

    void requestStartPlayIfNeed(GlobalPlayerView player){
        if (LOG_ENABLE) {
            Log.d(TAG, "requestStartPlayIfNeed ,this:" + this + ",isAttached:" + isAttached);
        }
        if (isAttached) {
            if (player != null) {
                notifyStart();
            }else{
                Log.e(TAG, "requestStartPlayIfNeed return on placeholder unVisible");
            }
        }
    }


    void notifyPause() {
        if(!isPlayerPaused) {
            isPlayerPaused = true;
            if (mPlayerCommand != null) {
                mPlayerCommand.pause();
            }
        }
    }

    void notifyResume() {
        if(isPlayerPaused){
            isPlayerPaused = false;
            if (mPlayerCommand != null) {
                mPlayerCommand.resume();
            }
        }
    }

    void notifyStart() {
//        Log.i(TAG,"+++notifyStart called this:"+this);
        //requestChangePlayerVisible(true);
        if (mPlayerCommand != null && !isPlayerStarted) {
            isPlayerStarted = true;
            mPlayerCommand.start(getBoundItem(),true);
        }
    }

    void notifyStop() {
//        Log.i(TAG,"+++notifyStop called this:"+this);
        //updateFloatLayoutIfNeed();
        requestChangePlayerVisible(false);
        if (mPlayerCommand != null && isPlayerStarted) {
            isPlayerStarted = false;
            mPlayerCommand.stop(getBoundItem());
        }
    }

    @Override
    public void onClick(View v) {
        if (LOG_ENABLE) {
            Log.i(TAG, "onClick isFullScreen:" + isFullScreen);
        }
        //this.postChangeFullScreen(0, true);
        if (mPlayerCommand != null) {
            if(mPlayerView != null){
                mPlayerView.isFloatState = false;
            }
            mPlayerCommand.changeFullScreen(true);
        }
    }


    public void postChangeFullScreen(int delay, boolean isFullScreen) {
        if (LOG_ENABLE) {
            Log.i(TAG, "postChangeFullScreen ,this:" + this + ",delay:" + delay + ",isFullScreen:" + isFullScreen);
        }
        GlobalPlayerView target = findFollowerView();
        if (isFullScreen) {

            if (mPlayerCommand == null) {
                mPlayerCommand = new GlobalPlayerCommand(target,this);
            }

            if (target != null) {
                target.removeVisibleTask();
                this.isFullScreen = true;
                int sw = getContext().getResources().getDisplayMetrics().widthPixels;
                int sh = getContext().getResources().getDisplayMetrics().heightPixels;
                if (mPlayerCommand != null) {
                    mPlayerCommand.layout(0, 0, sw, sh);
                }
                target.layoutFullScreen(0, 0, sw, sh);

            }
        }else{
            //exeChangeVisible(false);
            if (target != null) {
                target.removeVisibleTask();
            }
            this.isFullScreen = false;
            requestChangePlayerVisible(true, 0, false);
            if(isFocusable()) {
                requestFocus();
            }
        }

    }

    @Override
    public String toString() {
        return "Placeholder:"+hashCode()+",isFront"+isPlaceholderFront+",isAttached"+isAttached;
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

    public void requestPHFocus() {
        requestFocus();
    }

    public void syncLayout(boolean requestFocus) {
        if (LOG_ENABLE) {
            Log.e(TAG, "syncLayout requestFocus:" + requestFocus);
        }
        if (mPlayerView != null) {
            mPlayerView.removeVisibleTask();
        }
        if(!isFloatEnable()) {
            requestChangePlayerVisible(true, 0, false);
        }else{
            requestChangePlayerVisible(false,0,false);
            requestChangePlayerVisible(true, this.changeFullScreenDelay * 2, false);
        }
        if(isFocusable() && requestFocus) {
            requestFocus();
        }
    }

    public String getPlaceholderId() {
        if (bindItem instanceof HippyMap) {
            return ((HippyMap) bindItem).getString("id");
        }
        return null;
    }
}
