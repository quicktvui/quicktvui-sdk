package com.quicktvui.support.ui.legacy.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

import com.quicktvui.support.ui.legacy.animation.AnimationStore;
import com.quicktvui.support.ui.legacy.view.IObjectAdapterView;
import com.quicktvui.support.ui.legacy.view.IScrollView;
import com.quicktvui.support.ui.legacy.view.ITVFocusGroup;
import com.quicktvui.support.ui.legacy.view.ITVView;
import com.quicktvui.support.ui.leanback.ItemBridgeAdapter;
import com.quicktvui.support.ui.leanback.ObjectAdapter;
import com.quicktvui.support.ui.leanback.Presenter;
import com.quicktvui.support.ui.leanback.PresenterSelector;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.quicktvui.support.ui.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.quicktvui.support.ui.legacy.misc.TVSoundEffect;
import com.quicktvui.support.ui.legacy.R;
import com.quicktvui.support.ui.legacy.FConfig;
import com.quicktvui.support.ui.legacy.view.AttachInfo;
import com.quicktvui.support.ui.legacy.view.TVRootView;
import com.quicktvui.support.ui.legacy.view.TVViewUtil;
import com.quicktvui.support.ui.legacy.view.ITVViewGroup;
import com.quicktvui.support.ui.legacy.view.IFloatFocus;
import com.quicktvui.support.ui.legacy.view.IFloatFocusManager;
import com.quicktvui.support.ui.legacy.view.InnerViewGroupCode;

import java.util.ArrayList;

public class TVRecyclerView extends RecyclerView implements ITVViewGroup, IObjectAdapterView, IScrollView, ITVFocusGroup {

    protected int mFocusChildPosition = 0;

    static final String TAG = "TVRecycleView";

    private ObjectAdapter mObjectAdapter;

    protected PresenterSelector mPresenterSelector;
    private ItemBridgeAdapter mBridgeAdapter;

//    private int mSelectedPosition = -1;

    private ItemBridgeAdapter.Wrapper mShadowOverlayWrapper;

    protected boolean isDispatchDrawOrder = true;

    protected boolean isDispatchKeyEvent = true;

    protected boolean isFullScreen = false;

    OnAfterFocusSearchFailedListener mOnFocusSearchFailedListener;

    protected View mLastFocusedChild;
    protected View mSelectedChild;

    private TVRootView mFRootView;

    private AttachInfo mAttachInfo = new AttachInfo();

    protected View mFocusedView;

    protected boolean mShakeEndEnable ;


    protected Animator mShakeEndAnimator;

    protected boolean getDefaultShakeEndEnable(){
        return false;
    }

    public static boolean DEBUG = FConfig.DEBUG;

    Rect mClipMargin ;

    OnRecyclerViewFocusChangeListener mOnRecyclerViewFocusChangeListener;

    ViewTreeObserver.OnGlobalFocusChangeListener mOnGlobalFocusChangeListener;

    public static final int NO_CLIP_MARGIN = -10000;


    protected boolean forceListenGlobalFocusChange = true;

    public void setOnRecyclerViewFocusChangeListener(OnRecyclerViewFocusChangeListener listener) {
        this.mOnRecyclerViewFocusChangeListener = listener;

        listenGlobalFocusChangeIfNeed();

    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        super.onChildAttachedToWindow(child);

        int position = getChildAdapterPosition(child);

        child.setTag(position);
    }

    protected void onRecyclerViewFocusChanged(boolean hasFocus, View focused){
        //
        if(DEBUG) {
            Log.d(TAG, "onRecyclerViewFocusChanged hasFocus : " + hasFocus + " this :" + this);
        }


    }


    @Override
    public void clearChildFocus(View child) {
        super.clearChildFocus(child);
        Log.e(TAG,"clearChildFocus child"+child);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        Log.e(TAG,"onRequestFocusInDescendants direction"+direction);
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    boolean lastFocusState = false;
    private void notifyRecyclerViewFocusChanged(boolean hasFocus, boolean isOldFocusDescendantOf,View oldFocus,View focused){
        //
        if(lastFocusState != hasFocus) {
            onRecyclerViewFocusChanged(hasFocus, focused);
            if(mOnRecyclerViewFocusChangeListener != null){
                mOnRecyclerViewFocusChangeListener.onRecyclerViewFocusChanged(this,hasFocus,focused);
            }

            if(!hasFocus && isOldFocusDescendantOf){

                final View selectedChild = mSelectedChild;
                if(selectedChild != null){
                    callItemStateChangeIfNeed(selectedChild,StateListPresenter.STATE_SELECTED);
                }
            }

            lastFocusState = hasFocus;
        }
    }


    public @Nullable Presenter findChildPresenter(View child){

        if(child != null && child.getParent() == this){
            final ViewHolder h =   getChildViewHolder(child);
            if(h != null){
                if(h instanceof ItemBridgeAdapter.ViewHolder){
                    final Presenter p = ((ItemBridgeAdapter.ViewHolder) h).getPresenter();
                    return p;
                }

            }
        }
        return null;
    }


    public @Nullable Presenter findChildPresenter(int childAdapterPosition){
        final View v = getLayoutManager().findViewByPosition(childAdapterPosition);
        if(v != null){
            return findChildPresenter(v);
        }
        return null;
    }


    public @Nullable Presenter.ViewHolder findChildViewHolder(View child){
        final ItemBridgeAdapter.ViewHolder vh = findItemBridgeViewHolder(child);
        if(vh != null){
            return vh.getViewHolder();
        }
        return null;
    }

    public @Nullable Presenter.ViewHolder findChildViewHolder(int childAdapterPosition){
        final View v = getLayoutManager().findViewByPosition(childAdapterPosition);
        if(v != null){
            return findChildViewHolder(v);
        }
        return null;
    }


    public @Nullable Object findChildAttachItem(View child){
        final ItemBridgeAdapter.ViewHolder vh = findItemBridgeViewHolder(child);
        if(vh != null){
            return vh.getItem();
        }
        return null;
    }

    public @Nullable Object findChildAttachItem(int childAdapterPosition){
        final View v = getLayoutManager().findViewByPosition(childAdapterPosition);
        if(v != null){
            return findChildAttachItem(v);
        }
        return null;
    }

    public @Nullable ItemBridgeAdapter.ViewHolder findItemBridgeViewHolder(View child){

        if(child != null && child.getParent() == this){
            final ViewHolder h =   getChildViewHolder(child);
            if(h != null){
                if(h instanceof ItemBridgeAdapter.ViewHolder){
                    return (ItemBridgeAdapter.ViewHolder) h;
                }

            }
        }
        return null;
    }

    protected void callItemStateChangeIfNeed(View child, int state){

        final ItemBridgeAdapter.ViewHolder ivh = findItemBridgeViewHolder(child);

        final Drawable d = child.getBackground();
        if(d instanceof StateListDrawable){
            changeViewDrawableState(child,state, (StateListDrawable) d);
        }
        if(ivh != null){
            final Presenter itemPresenter = ivh.getPresenter();

            if(itemPresenter instanceof StateListPresenter){
                ((StateListPresenter) itemPresenter).onStateChanged(state,ivh.getViewHolder(),ivh.getItem());
                if(DEBUG) {
                    Log.d(TAG, "notify item state:" + state);
                }
            }
        }

    }

    private void changeViewDrawableState(View child,int state,StateListDrawable d){

        switch (state){
            case StateListPresenter.STATE_ACTIVATED:
                if(child.isFocused()){
                    d.setState(new int[]{
                            android.R.attr.state_activated,
                            android.R.attr.state_focused,
                    });
                }else{
                    d.setState(new int[]{
                            android.R.attr.state_activated,
                    });
                }

                break;
            case StateListPresenter.STATE_SELECTED :
                d.setState(new int[]{
                        android.R.attr.state_selected,
                });
                break;
                case StateListPresenter.STATE_FOCUSED :
                    d.setState(new int[]{
                        android.R.attr.state_focused
                    });
                default:
                    d.setState(new int[]{
                        android.R.attr.state_enabled
                    });
                break;
        }

    }


    @Override
    public void onChildDetachedFromWindow(@NonNull View child) {
        super.onChildDetachedFromWindow(child);
        if(child == mSelectedChild){
            mSelectedChild = null;
        }
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSelectedChild = null;
        stopListenGlobalFocusChange();
    }

    public interface OnAfterFocusSearchFailedListener{
        /***
         * 当焦点寻找失败时的逻辑处理
         * @param focused 当前焦点
         * @param direction 方向
         * @return 如果return null,则继续父类的逻辑
         */
        View onAfterFocusSearchFailed(View focused, int direction) ;
    }

    /**设置当焦点寻找失败时的逻辑处理
     * @param onFocusSearchFailedListener
     */
    public void setOnAfterFocusSearchFailedListener(OnAfterFocusSearchFailedListener onFocusSearchFailedListener) {
        mOnFocusSearchFailedListener = onFocusSearchFailedListener;
    }

    public TVRecyclerView(Context context) {
        super(context);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);

    }

    public TVRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitializeFromAttributes(attrs);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);


    }

    public TVRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInitializeFromAttributes(attrs);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);

    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        onHandleFocusScale(gainFocus, direction, previouslyFocusedRect);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(Companion.getDEBUG()){
            Log.v(TAG,TAG+" onFocusChanged gainFocus is "+previouslyFocusedRect+" previouslyFocusedRect"+" this is "+this+" hasFocus is "+hasFocus()+" isFocused is "+isFocused());
        }
        if(isFocused()){
            dispatchFocusToChild(direction, previouslyFocusedRect);
        }
    }


    /**在RecycleView获得焦点后，将焦点给予子view
     * @param direction
     * @param previouslyFocusedRect
     */
    protected boolean dispatchFocusToChild(int direction,Rect previouslyFocusedRect){
        if(Companion.getDEBUG()){
            Log.v(TAG, "dispatchFocusToChild direction  is " + TVViewUtil.getMovement(direction) + " previouslyFocusedRect is " + previouslyFocusedRect);
        }
        return false;
    }


    private void stopListenGlobalFocusChange(){
        if (mOnGlobalFocusChangeListener != null) {
            getViewTreeObserver().removeOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);
        }
    }
    private void listenGlobalFocusChangeIfNeed(){
        stopListenGlobalFocusChange();
        if(forceListenGlobalFocusChange || mOnRecyclerViewFocusChangeListener != null) {
            mOnGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
                @Override
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                        if (DEBUG) {
                            Log.d(TAG, "onGlobalFocusChanged hasFocus : " + hasFocus() + " this :" + this);
                        }
                        if (hasFocus()) {
                            if (oldFocus == null) {
                                //首次获得焦点
                                notifyRecyclerViewFocusChanged(true,false,null, newFocus);
                            } else {
                                //焦点在内部，但上一个view不属于内部
                                final boolean isOldFocusDescendantOf = TVViewUtil.isViewDescendantOf(oldFocus, TVRecyclerView.this);
                                if (!isOldFocusDescendantOf) {
                                    notifyRecyclerViewFocusChanged(true, false,oldFocus,newFocus);
                                }
                            }
                        } else {
                            final boolean isNewFocusDescendantOf = TVViewUtil.isViewDescendantOf(newFocus, TVRecyclerView.this);
                            if (DEBUG) {
                                Log.d(TAG, "onGlobalFocusChanged  hasFocus : " + hasFocus() + " isNewFocusDescendantOf : " + isNewFocusDescendantOf);
                            }
                            if (!isNewFocusDescendantOf) {
                                //焦点丢失
                                final boolean isOldFocusDescendantOf = TVViewUtil.isViewDescendantOf(oldFocus, TVRecyclerView.this);

                                if (isOldFocusDescendantOf) {
                                    notifyRecyclerViewFocusChanged(false, true,oldFocus,newFocus);
                                }
                            }
                        }
                    }
            };
            getViewTreeObserver().addOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);
        }
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFRootView = TVRootView.findRootView(this);

        mAttachInfo = new AttachInfo();
        if(Companion.getDEBUG()) {
            Log.v("performance", "FRecycleView onAttachedToWindow");
        }
        listenGlobalFocusChangeIfNeed();

    }




    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(Companion.getDEBUG()) {
            TVViewUtil.debugPerformance(this,"onLayout");
        }
        super.onLayout(changed, l, t, r, b);

        resetClipBounds();
    }

    @Override

    protected void onMeasure(int widthSpec, int heightSpec) {
        if(Companion.getDEBUG()) {
            TVViewUtil.debugPerformance(this,"onMeasure width is "+getWidth()+" height is "+getHeight());
        }
        super.onMeasure(widthSpec, heightSpec);


    }





    private void resetClipBounds(){
        if(DEBUG) {
            Log.e(TAG, "resetClipBounds width is  "+getWidth()+" height is "+getHeight());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if(getWidth() > 0 && getHeight() > 0) {
                final Rect clipMargin = getClipMarginRect();
                if (clipMargin.left != NO_CLIP_MARGIN || clipMargin.right != NO_CLIP_MARGIN || clipMargin.top != NO_CLIP_MARGIN || clipMargin.bottom != NO_CLIP_MARGIN) {
                        Rect b = new Rect(clipMargin.left,clipMargin.top,clipMargin.right + getWidth() , clipMargin.bottom + getHeight());
                        setClipBounds(b);
                    }
                }
        }
    }




    /**view初始化后调用
     * @param attrs
     */
    protected void onInitializeFromAttributes( AttributeSet attrs){
        if(attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TVRecyclerView);

            setDispatchDrawOrder(typedArray.getBoolean(R.styleable.TVRecyclerView_enable_dispatch_draw, true));

            setDispatchKeyEvent(typedArray.getBoolean(R.styleable.TVRecyclerView_enable_dispatch_keyevent,true));

            setShakeEndEnable(typedArray.getBoolean(R.styleable.TVRecyclerView_enable_shake_list_end,getDefaultShakeEndEnable()));

            int clipMargin = typedArray.getDimensionPixelSize(R.styleable.TVRecyclerView_clipMargin,0);

            if(clipMargin != 0){
                getClipMarginRect().set(clipMargin * -1,clipMargin * -1,clipMargin,clipMargin);
            }

            int clipMarginHorizontal = typedArray.getDimensionPixelSize(R.styleable.TVRecyclerView_clipMarginHorizontal,0);

            if(clipMarginHorizontal != 0){
                getClipMarginRect().left = clipMarginHorizontal * -1;
                getClipMarginRect().right = clipMarginHorizontal;
            }

            int clipMarginVertical = typedArray.getDimensionPixelSize(R.styleable.TVRecyclerView_clipMarginVertical,0);

            if(clipMarginVertical != 0){
                getClipMarginRect().top = clipMarginVertical * -1;
                getClipMarginRect().bottom = clipMarginVertical;
            }

            if(DEBUG){
                Log.d(TAG,"onInitializeFromAttributes clipMargin is "+clipMargin +" clip H is "+clipMarginHorizontal+" clipMargin Vertical is "+clipMarginVertical);
            }

            typedArray.recycle();

        }

    }


    public void setClipMargin(int left,int top,int right,int bottom){

        setClipMargin(new Rect(left * -1,top * -1,right ,bottom ));
        resetClipBounds();
    }

    void setClipMargin(final Rect rect){
        getClipMarginRect().set(rect);
    }


    public Rect getClipMarginRect(){
        if(null == mClipMargin) {
            mClipMargin = new Rect(NO_CLIP_MARGIN,NO_CLIP_MARGIN,NO_CLIP_MARGIN,NO_CLIP_MARGIN);
        }
        return mClipMargin;
    }


    @Override
    public void requestChildFocus(View child, View focused) {
        if(Companion.getDEBUG()){
            Log.d(IFloatFocus.TAG,"-----FReycycleView requestChildFocus focused is "+focused+" this is "+this);
        }
        //20160606 这里只要有焦点变化就刷新，因为如果不刷新界面，会导致子view放大时会被遮盖的问题
        postInvalidateDelayed(16);
        final View lastFocus = mSelectedChild;

        if(mSelectedChild != null){
            callItemStateChangeIfNeed(lastFocus,StateListPresenter.STATE_NORMAL);
        }
        mFocusedView = focused;
        mSelectedChild = child;
        if(isDispatchDrawOrder && getFocusedChild() != mLastFocusedChild){
            if(Companion.getDEBUG()){
                Log.v(IFloatFocus.TAG,"requestChildFocus 焦点改变，刷新 this is "+this+" focused is "+focused);
            }
//            invalidate();
            mLastFocusedChild = getFocusedChild();
            if(child instanceof ITVView && focused instanceof ITVView){
                requestChildMoveFloatFocus((ITVView) child,(ITVView) focused);
            }
        }
        mFocusChildPosition = getChildAdapterPosition(child);
        try {
            super.requestChildFocus(child, focused);
        }catch (Throwable t){
            Log.e(TAG,"requestChildFocus error :"+t.getMessage()+" focused:"+focused);
            t.printStackTrace();
        }

        callItemStateChangeIfNeed(child,StateListPresenter.STATE_FOCUSED);

        if(mOnRecyclerViewFocusChangeListener != null){
            mOnRecyclerViewFocusChangeListener.onRequestChildFocus(this,child,mFocusChildPosition,focused);
        }

    }

    public int getSelectPosition(){
        return mFocusChildPosition;
    }

    public int getFocusChildPosition(){
        return mFocusChildPosition;
    }

    //FIXME 20170829 这里因为recycleView 25里将这个方法弃用，这里方法无效。会导致浮动焦点失效。
//    @Override
//    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
//        if(DEBUG){
//            Log.d(IFloatFocus.TAG,"++++FReycycleView requestRecycleViewChildRectangleOnScreen is "+child+" rect is "+rect);
//        }
//        if(getLayoutManager() instanceof ITVLayoutManager){
//            ITVLayoutManager layoutManager = (ITVLayoutManager) getLayoutManager();
//            Point p = layoutManager.handleRequestChildRectangleOnScreen(this,child,rect,immediate);
//            onCaculateFloatFocusMoveLocationOffsetWhenRequestChildFocus(child,rect,immediate,p,mFocusedView);
//            return p.x != 0 || p.y != 0;
//        }else{
//            return super.requestChildRectangleOnScreen(child, rect, immediate);
//        }
//    }

    public void onCaculateFloatFocusMoveLocationOffsetWhenRequestChildFocus(View child, Rect rect, boolean immediate, Point scrollOffset, final View focused){
//        if(focused instanceof ITVView && focused == child){
        if(focused instanceof ITVView) {
            AttachInfo attachInfo = ((ITVView) focused).getAttachInfo();
            attachInfo.offsetFloatFocusOffset(-scrollOffset.x, -scrollOffset.y);
            onFLoatFocusMoveOffsetCaculated(child,focused,attachInfo.mFloatFocusOffset);
            if (Companion.getDEBUG()) {
                Log.i(IFloatFocus.TAG, " RecycleView onCaculateFloatFocusMoveLocationOffsetWhenRequestChildFocus child isFocused is " + isFocused() + " focused is " + focused);
                Log.i(IFloatFocus.TAG, "$$$$$$FRecycleView focused attachInfo is " + attachInfo+" this is "+this);
            }
//        }
        }
    }

    /**完成计算浮动焦点移动偏移量,复写此方法，可以最后一次机会调整浮动窗口移动的位置
     * @param focused
     * @param offsetResult
     */
    public void onFLoatFocusMoveOffsetCaculated(View child,final View focused,final Point offsetResult){
        //do nothing
    }


    @Override
    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        if(Companion.getDEBUG()){
            Log.v(IFloatFocus.TAG,TAG + " requestRectangleOnScreen rectangle is "+rectangle+" immediate is "+immediate);
        }
        return super.requestRectangleOnScreen(rectangle, immediate);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle) {
        return super.requestRectangleOnScreen(rectangle);
    }

    void letLayoutManagerTakeFloatFocusMove(ITVView child, ITVView focused){
        if(Companion.getDEBUG()){
            Log.v(IFloatFocus.TAG,"letLayoutManagerTakeFloatFocusMove this is "+this+" getLayoutManager is "+getLayoutManager());
        }
        if(getLayoutManager() instanceof ITVLayoutManager){
            ITVLayoutManager fLayoutManager = (ITVLayoutManager) getLayoutManager();
                fLayoutManager.requestMoveFloatFocus(this,child,focused);
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        Log.e(TAG,"requestFocus direction  is "+ TVViewUtil.getMovement(direction) +" previouslyFocusedRect is "+previouslyFocusedRect +" hasFocus is "+hasFocus()+" isFocused is "+isFocused());
        if(isFocusable()){
            boolean handle = dispatchFocusToChild(direction,previouslyFocusedRect);
            if(handle) {
                return true;
            }
        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }


    @Override
    public void draw(Canvas c) {
        super.draw(c);
    }

    public boolean isDispatchDrawOrder() {
        return isDispatchDrawOrder;
    }

    /**设置是否允许FrecycleView自己处理draw order.如果不处理，有可能出现子view放大时，被遮挡的问题
     * @param isDispatchDrawOrder
     */
    public void setDispatchDrawOrder(boolean isDispatchDrawOrder) {
        this.isDispatchDrawOrder = isDispatchDrawOrder;
        invalidate();
    }

    public boolean isDispatchKeyEvent() {
        return isDispatchKeyEvent;
    }

    /**设置是否允许FrecycleView自己处理key事件.
     * @param isDispatchKeyEvent
     */
    public void setDispatchKeyEvent(boolean isDispatchKeyEvent) {
        this.isDispatchKeyEvent = isDispatchKeyEvent;
    }

//    /**FrecycleView处理事件分发的逻辑。如果有处返回false，则会执行默认的dispatchKeyEvent();
//     * @param event
//     * @return
//     */
//    protected  boolean onDispatchKeyEvent(KeyEvent event){
//        if(!isDispatchKeyEvent || isLayoutFrozen())
//            return false;
//        boolean b = false;
//
//        if(event.getAction() == KeyEvent.ACTION_DOWN && this.hasFocus()){
//            if(DEBUG){
//                Log.d(TAG, "FRecycleView onDispatchKeyEvent return "+b+" hasFocus is "+this.hasFocus()+" findFocus is "+this.findFocus() +" this is "+this.getClass().getSimpleName());
//            }
//            View focused = this.findFocus();
//            if(focused != null) {
//                final int direction = TVViewUtil.convertKeyCodeToDirection(event.getKeyCode());
//                if(direction != ITVView.FOCUS_INVALID) {
//                    View v = this.focusSearch(focused, direction);
//                    if (v != null && v != focused && v.getLayoutParams() instanceof RecyclerView.LayoutParams) {
//                        v.requestFocus();
//                        if(DEBUG){
//                            Log.d(TAG, "FRecycleView onDispatchKeyEvent let "+v+" requestFocus ");
//                        }
//                        //如果发现了，选中的位置改变，则需要改变将选中的view在最后渲染。所以这里需要刷新界面以调用dispatchDraw
//                        invalidate();
//                        if(focused != this)
//                            onItemSelectChanged(focused,v);
//                        b = true;
//                    }else{
//                        if(DEBUG){
//                            Log.d(TAG, "FRecycleView onDispatchKeyEvent focusSearch null "+" this is "+this.getClass().getSimpleName());
//                        }
//                    }
//                    if (b) {
//                        playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
//                    }
//                }
//            }else{
//                if(DEBUG){
//                    Log.d(TAG,"onDispatchKeyEvent focused is null ");
//                }
//            }
//        }
//        if(DEBUG){
//            View focused = findFocus();
//            Log.i(TAG, "FRecycleView onDispatchKeyEvent final return " + b+" focused is "+focused + focused == null ? " tag is "+focused.getTag() :"");
//        }
//
//        return b;
//    }

    protected void onItemSelectChanged(View focused,View next){
    }



    //    public int getSelectedPosition() {
//        return mSelectedPosition;
//    }


    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//       return onDispatchKeyEvent(event) || super.dispatchKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }


    @Override
    public View focusSearch(int direction) {
        View v = super.focusSearch(direction);
        if(Companion.getDEBUG()){
            Log.v(TAG, "FRecycleView focusSearch result: " + v+" this is "+this.getClass().getSimpleName());
        }
        return v;
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View v;
        try {
            v = super.focusSearch(focused, direction);
        }catch (Throwable t){
            t.printStackTrace();
            Log.e(TAG,"focusSearch error : t"+t.getMessage());
            v = null;
        }

        if(v  == null && mOnFocusSearchFailedListener != null){
            return mOnFocusSearchFailedListener.onAfterFocusSearchFailed(focused,direction);
        }
        if(Companion.getDEBUG()){
            Log.v(TAG, "FRecycleView focusSearch with focused result: " + v+" this is "+this.getClass().getSimpleName());
        }


        if(isShakeWhenReachListEnd(focused,v,direction) && isFullScreen){
            shakeRecycleView();
        }else if(Companion.getDEBUG()){
            Log.v(TAG,"no shake mShakeEndEnable is "+mShakeEndEnable+" findIFLayoutManager() is "+findIFLayoutManager()+" isFullScreen is "+isFullScreen);
        }

        return v;
    }

    public void setShakeEndEnable(boolean shakeEndEnable) {
        mShakeEndEnable = shakeEndEnable;
    }



    public ShakeEndCallback findIFLayoutManager(){
        final LayoutManager layoutManager = getLayoutManager();
        if(layoutManager instanceof ShakeEndCallback)
            return (ShakeEndCallback) layoutManager;
        return null;
    }

    protected TVOrientation getShakeRecycleViewOrientation(ShakeEndCallback layoutManager){
        if(layoutManager != null)
            return layoutManager.getFOrientation();
        return TVOrientation.VERTICAL;
    }

    public void shakeRecycleView(){

        final ShakeEndCallback layoutManager = findIFLayoutManager();

        final TVOrientation orientation = getShakeRecycleViewOrientation(layoutManager);

        if(Companion.getDEBUG()){
            Log.v(TAG,"shakeRecycleView orientation is "+orientation+" layoutManager is "+layoutManager);
        }

        if(mShakeEndAnimator == null){
            final Animator  shake = AnimationStore.defaultShakeEndAnimator(this,orientation);
            mShakeEndAnimator = shake;
            if(null != shakeEndListenner){
                shakeEndListenner.onShake(orientation);
            }
            shake.start();
//            setLayoutFrozen(true);
            shake.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
//                    setLayoutFrozen(false);
                    mShakeEndAnimator = null;
                }
            });

            TVSoundEffect.playCantMove();
        }


    }



    protected boolean isShakeWhenReachListEnd(View focused,View focusSearched,int direction){

        ShakeEndCallback ifLayoutManager = findIFLayoutManager();

        return  mShakeEndEnable && ifLayoutManager != null && ifLayoutManager.isReachListEnd(this,focused,focusSearched, mFocusChildPosition,direction);

    }




    @Override
    public void addFocusables(ArrayList<View> views, int direction) {
        super.addFocusables(views, direction);
    }

    /**为了使用{@link #setObjectAdapter(ObjectAdapter)}可以选择设置PresenterSelector
     * @param presenterSelector
     */
    public void setPresenterSelector(PresenterSelector presenterSelector) {
        mPresenterSelector = presenterSelector;
    }

    /**设置一个无关位置（position）的ObjectAdapter
     * @param adapter
     */
    public void setObjectAdapter(ObjectAdapter adapter){
        this.mObjectAdapter = adapter;
        updateAdapter();
    }




    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(Companion.getDEBUG() && isDispatchDrawOrder){
            Log.v(TAG,"dispatchDraw focused is "+getFocusedChild());
        }
        super.dispatchDraw(canvas);
        if(isDispatchDrawOrder && getFocusedChild()  != null)
            super.drawChild(canvas,getFocusedChild(),getDrawingTime());
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if(isDispatchDrawOrder && child == getFocusedChild())
            return true;
        return super.drawChild(canvas, child, drawingTime);
    }

    public void notifyObjectAdapterChanged(ObjectAdapter adapter){
        if(mBridgeAdapter != null){
            mBridgeAdapter.setAdapter(adapter);
        }
    }


    protected ItemBridgeAdapter onCreateBridgeAdapter(){
        return new ItemBridgeAdapter(mObjectAdapter, mPresenterSelector == null ? mObjectAdapter.getPresenterSelector() : mPresenterSelector) {
            @Override
            protected void onCreate(ViewHolder viewHolder) {
                super.onCreate(viewHolder);
                if (Companion.getDEBUG())
                    Log.v(TAG, "ItemBridgeAdapter onCreate viewHolder is " + viewHolder + " this is " + TVRecyclerView.this);
                onBridgeAdapterCreateViewHolder(viewHolder);
            }



            @Override
            protected void onBind(ViewHolder viewHolder) {
                super.onBind(viewHolder);
                onBridgeAdapterBindViewHolder(viewHolder);
            }

            @Override
            public int getItemViewType(int position) {
                final int type = super.getItemViewType(position);
                if (Companion.getDEBUG())
                    Log.v(TAG, "ItemBridgeAdapter getItemViewType is " + type + " position is " + position + " this is " + TVRecyclerView.this);
                return type;
            }

        };
    }

    protected void onBridgeAdapterCreateViewHolder(ItemBridgeAdapter.ViewHolder viewHolder){

    }

    protected void onBridgeAdapterBindViewHolder(ItemBridgeAdapter.ViewHolder viewHolder){

    }

    public ObjectAdapter getObjectAdapter() {
        return mObjectAdapter;
    }

    void updateAdapter() {

        if (mBridgeAdapter != null) {
            // detach observer from ObjectAdapter
            mBridgeAdapter.clear();
            mBridgeAdapter = null;
//                mBridgeAdapter.setAdapter(mObjectAdapter);
        }
        // If presenter selector is null, adapter ps will be used
        if(mObjectAdapter != null) {
            mBridgeAdapter = onCreateBridgeAdapter();

            PresenterSelector selector = mPresenterSelector == null ? mObjectAdapter.getPresenterSelector() : mPresenterSelector;

            if(selector != null) {
                Presenter[] presenters = selector.getPresenters();
                if(presenters != null) {
                    ArrayList arrayList = new ArrayList();
                    for (Presenter p : presenters) {
                        if(FConfig.DEBUG){
                            Log.d(TAG,"updateAdapter presenters it:"+p);
                        }
                        arrayList.add(p);
                    }
                    mBridgeAdapter.setPresenterMapper(arrayList);
                }
            }
        }

        mSelectedChild = null;
        mFocusedView = null;
        mFocusChildPosition = -1;
        mLastFocusedChild = null;

        this.setAdapter(mBridgeAdapter);
    }

    @Override
    public float getFocusScaleX() {
        return 0;
    }

    @Override
    public float getFocusScaleY() {
        return 0;
    }

    @Override
    public View getView() {
        return this;
    }

    /** see {@link ITVView#setFocusScale(float)}
     * @param scale 放大倍数
     */
    @Override
    public void setFocusScale(float scale) {
    }

    /**
     * 设置View获得焦点的整体放大倍数
     *
     * @param scale
     * @return
     */
    @Override
    public void setFocusScaleX(float scale) {

    }

    /**
     * 设置View获得焦点的放大倍数
     *
     * @param scale
     * @return
     */
    @Override
    public void setFocusScaleY(float scale) {

    }

    @Override
    public void setFocusScaleDuration(int duration) {
    }


    private class InnerItemBridgeAdapter extends ItemBridgeAdapter{
        @Override
        protected void onCreate(ViewHolder viewHolder) {
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        isFullScreen = false;
    }

    @Override
    public void onHandleFocusScale(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    }

    @Override
    public Rect getFloatFocusMarginRect() {
        return mAttachInfo.mFloatFocusMarginRect;
    }

    @Override
    public TVRootView getFRootView() {
        return mFRootView;
    }

    @Override
    public AttachInfo getAttachInfo() {
        return mAttachInfo;
    }

    @Override
    public ITVView getFloatFocusFocusableView() {
        return null;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


//    /**
//     *为了recycleView正确处理key事件，而写的helper类，用此类方法处理keyevent,可以实现reyclceView在item寻找焦点找不到时候，根据
//     * <p/>真实逻辑，不会将事件的控制权释放，除非item之后再无数据。
//     */
//    public static class FDispatchKeyEventHelperForRecycleView {
//
//        /**代为处理key事件
//         * @param recyclerView
//         * @param event
//         * @return
//         */
//        public static boolean handleKeyEvent(RecyclerView recyclerView,KeyEvent event){
//
//                boolean b = false;
//                if(event.getAction() == KeyEvent.ACTION_DOWN && recyclerView.hasFocus()){
//                    if(DEBUG){
//                        Log.d(TAG, "FRecycleView dispatchKeyEvent return "+b+" hasFocus is "+recyclerView.hasFocus()+" findFocus is "+recyclerView.findFocus());
//                    }
//                    View focused = recyclerView.findFocus();
//                    if(focused != null) {
//                        final int direction = TVViewUtil.convertKeyCodeToDirection(event.getKeyCode());
//                        if(direction != ITVView.FOCUS_INVALID) {
//                            View v = recyclerView.focusSearch(focused, direction);
//                            if (v != null && v != focused) {
//                                v.requestFocus();
//                            }
//
//                            b = v != null;
//                            if (b) {
//                                recyclerView.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
//                            }
//                        }
//                    }
//                }
//                if(DEBUG){
//                    Log.d(TAG, "FRecycleView dispatchKeyEvent return " + b);
//                }
//                return b;
//            }
//        }


    /**
     * 请求移动浮动焦点到对应view
     *
     * @param child
     * @param focused
     */
    @Override
    public void requestChildMoveFloatFocus(ITVView child, ITVView focused) {
        InnerViewGroupCode.handleChildMoveFloatFocusForLayout(this,child,focused);
        //TODO
        if(Companion.getDEBUG()){
            Log.d(IFloatFocus.TAG,"FRecyecleView requestChildMoveFloatFocus this is "+this);
        }
        letLayoutManagerTakeFloatFocusMove(child,focused);
    }


    @Override
    public void scrollBy(int x, int y) {

        super.scrollBy(x, y);

        if(Companion.getDEBUG()){
            Log.d(IFloatFocus.TAG,TAG+" scrollBy x is "+x+" scrollBy y is "+y+" LayoutManager is "+getLayoutManager()+" this is "+this);
        }


    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
        if(Companion.getDEBUG()){
            Log.d(IFloatFocus.TAG,TAG+" smoothScrollToPosition  position is "+position+" this is "+this);
        }
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {

        super.smoothScrollBy(dx, dy);

        if(Companion.getDEBUG()){
            Log.d(IFloatFocus.TAG,TAG+" smoothScrollBy dx is "+dx+" smoothScrollBy dy is "+dy+" LayoutManager is "+getLayoutManager()+" this is "+this);
        }


    }


//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//
//        if(DEBUG){
//            Log.i(TAG,"onScrollChanged l is "+l+" t is "+t+" oldL is "+oldl+" oltT is "+oldt);
//        }
//        super.onScrollChanged(l, t, oldl, oldt);
//    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        // 这里进行满屏判断，假如 recyclerview 容器可以发生滚动，证明当前 child 超过一屏
        if (dx != 0 || dy != 0) {
            isFullScreen = true;
        }
        if(Companion.getDEBUG()){
            Log.d(IFloatFocus.TAG,TAG+" *******onScrolled dx is "+dx+" dy is "+dy + " this is "+this);
        }
//        if(hasPendingFloatFocusMoveTask()){
//            mFocusMoveTask.offset.offset(dx,dy);
//        }
//        if(getFRootView() != null){
//            getFRootView().offsetFloatFocus(new Point(-dx,-dy));
//        }
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if(Companion.getDEBUG()){
            Log.i(IFloatFocus.TAG,TAG+" onScrollStateChanged state is "+state+" scroll X is "+getScrollX() +" this is "+this);
        }
        if(state == SCROLL_STATE_SETTLING) {

        }else if(state == SCROLL_STATE_IDLE){
        }
    }

    @Override
    public IFloatFocusManager getFloatFocusManager() {
        return getFRootView().getFloatFocusManager();
    }

    @Override
    public void setFloatFocusFocusedAlpha(float alpha) {
        mAttachInfo.setFloatFocusFocusedAlpha(alpha);
    }

    /**
     * 监听尾部抖动的监听
     */
    public interface OnShakeEndListenner{

        /**
         * 尾部抖动回调执行方法
         * 这里传出一个方向，用户可以自己处理（定义动画），一般可以使用#AnimationStore.defaultShakeEndAnimator()创建和尾部一样的动画
         * @param orientation
         */
        public void onShake(ITVView.TVOrientation orientation);
    }

    private OnShakeEndListenner shakeEndListenner;

    /**
     * 设置尾部抖动监听
     * @param shakeEndListenner
     */
    public void setOnShakeEndListenner(OnShakeEndListenner shakeEndListenner) {
        this.shakeEndListenner = shakeEndListenner;
    }




    public int nextSpecifiedFocusUpIndex = -1;
    public int nextSpecifiedFocusDownIndex = -1;
    public int nextSpecifiedFocusLeftIndex = -1;
    public int nextSpecifiedFocusRightIndex = -1;

    public int nextSpecifiedFocusUpId = -1;
    public int nextSpecifiedFocusDownId = -1;
    public int nextSpecifiedFocusLeftId = -1;
    public int nextSpecifiedFocusRightId = -1;

    @Override
    public void setNextSpecifiedFocusIndex(int index) {
        this.nextSpecifiedFocusDownIndex = index;
        this.nextSpecifiedFocusLeftIndex = index;
        this.nextSpecifiedFocusRightIndex = index;
        this.nextSpecifiedFocusUpIndex = index;
    }

    @Override
    public void setNextSpecifiedFocusIndex(int direction, int index){
        switch (direction){
            case FOCUS_DOWN:
                this.nextSpecifiedFocusDownIndex = index;
                break;
            case FOCUS_UP :
                this.nextSpecifiedFocusUpIndex = index;
                break;
            case FOCUS_LEFT :
                this.nextSpecifiedFocusLeftIndex = index;
                break;

            case FOCUS_RIGHT :
                this.nextSpecifiedFocusRightIndex = index;
                break;
        }
    }


    private final ArrayList<View> tempFocusList = new ArrayList();

    @Override
    public View getNextSpecifiedFocus(View focused, int direction) {
        int specifiedIndex = -1;
        int nextSpecifiedId = -1;
        switch (direction){
            case FOCUS_DOWN:
                specifiedIndex = this.nextSpecifiedFocusDownIndex;
                nextSpecifiedId = this.nextSpecifiedFocusDownId;
                break;
            case FOCUS_UP :
                specifiedIndex = this.nextSpecifiedFocusUpIndex;
                nextSpecifiedId = this.nextSpecifiedFocusUpId;
                break;
            case FOCUS_LEFT :
                specifiedIndex =  this.nextSpecifiedFocusLeftIndex ;
                nextSpecifiedId = this.nextSpecifiedFocusLeftId;
                break;

            case FOCUS_RIGHT :
                specifiedIndex = this.nextSpecifiedFocusRightIndex;
                nextSpecifiedId = this.nextSpecifiedFocusRightId;
                break;
        }
        if(specifiedIndex >= 0 && nextSpecifiedId < 0){
            try {
                if (specifiedIndex >= 0 && specifiedIndex < getChildCount()) {
                    View target = getChildAt(specifiedIndex);
                    if(target.isFocusable()){
                        return target;
                    }

                    target.addFocusables(tempFocusList,direction);

                    for(View v: tempFocusList){
                        Log.d(TAG,"getNextSpecifiedFocus tempFocusList :  "+v);
                    }
                    
                    if(tempFocusList.size() > 0){
                        return tempFocusList.get(0);
                    }
                }
            }finally {
                tempFocusList.clear();
            }
        }

        if(nextSpecifiedId > 0){
            return findViewById(nextSpecifiedId);
        }

        return null;
    }


    @Override
    public int getNextSpecifiedFocusUpId() {
        return nextSpecifiedFocusUpId;
    }

    @Override
    public void setNextSpecifiedFocusUpId(int i) {
        this.nextSpecifiedFocusUpId = i;
    }

    @Override
    public int getNextSpecifiedFocusDownId() {
        return nextSpecifiedFocusDownId;
    }

    @Override
    public void setNextSpecifiedFocusDownId(int i) {
        this.nextSpecifiedFocusDownId = i;
    }

    @Override
    public int getNextSpecifiedFocusLeftId() {
        return nextSpecifiedFocusLeftId;
    }

    @Override
    public void setNextSpecifiedFocusLeftId(int i) {
        this.nextSpecifiedFocusLeftId = i;
    }

    @Override
    public int getNextSpecifiedFocusRightId() {
        return this.nextSpecifiedFocusRightId;
    }

    @Override
    public void setNextSpecifiedFocusRightId(int i) {
        this.nextSpecifiedFocusRightId = i;
    }

    public interface ShakeEndCallback{
        boolean isReachListEnd(RecyclerView parent,View select, View focusSearched,int selection, int direction);
        ITVView.TVOrientation getFOrientation();
    }





    @Override
    public void requestLayout() {
        if(DEBUG){
            TVViewUtil.debugPerformance(this,"requestLayout");
        }
        super.requestLayout();

    }

    @Override
    public String toString() {
        if(DEBUG) {
            if (getTag() != null) {
                return super.toString() +" view tag is "+getTag();
            }
        }
        return super.toString();
    }

}
