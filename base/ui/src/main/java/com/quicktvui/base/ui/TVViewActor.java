package com.quicktvui.base.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.CycleInterpolator;

import com.quicktvui.base.ui.graphic.BaseBorderDrawable;
import com.quicktvui.base.ui.graphic.BorderFrontDrawable;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为了各种View实现TV端特有功能（焦点、放大效果等）,此类嵌入到各个view中，实际执行代码逻辑，都在此类中。
 *
 * onFocusChanged
 * onDetachedFromWindow
 * onAttachedToWindow
 * addFocusables
 * onSizeChanged
 * drawFocusBorder
 * setFocusBorderType
 * focusSearch
 * //绘制
 * drawableStateChanged
 *
 */
public class TVViewActor implements ITVView {

    public static final String TAG = "TVViewActor";

    final View view;
    String name;
    int mDuration = TVFocusAnimHelper.DEFAULT_DURATION;
    float mFocusScaleX = TVFocusAnimHelper.DEFAULT_SCALE;
    float mFocusScaleY = TVFocusAnimHelper.DEFAULT_SCALE;
    private boolean mInReFocus = false;
    String nextFocusUp,nextFocusDown,nextFocusLeft,nextFocusRight;

    private final AttachInfo mAttachInfo = new AttachInfo();

    public TVViewActor(View view) {
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId(){
        return view.getId();
    }

    @Override
    public int getWidth() {
        return view.getWidth();
    }

    @Override
    public int getHeight() {
        return view.getHeight();
    }

    @Override
    public ViewParent getParent() {
        return view.getParent();
    }

    public boolean isFocusable(){
        return view.isFocusable();
    }

    /**
     * -------------------------------------------------------------------------------------
     **/

    @Override
    public void onHandleFocusScale(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (isFocusable() && mFocusScaleX != 1 || mFocusScaleY != 1) {
            handleFocusScale(gainFocus, direction, previouslyFocusedRect, mDuration);
        }

    }

    public void handleFocusScale(boolean gainFocus, int direction, Rect previouslyFocusedRect, int duration) {
        if (isFocusable() && (mFocusScaleX != 1 || mFocusScaleY != 1)) {
//      Log.e("zhaopeng","###handleFocusScale gainFocus:"+gainFocus+" duration:"+duration);
            TVFocusAnimHelper.handleOnFocusChange(view, gainFocus, mFocusScaleX, mFocusScaleY, duration);
        }
    }

    public void handleFocusScaleImmediately(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (isFocusable() && (mFocusScaleX != 1 || mFocusScaleY != 1)) {
//      Log.e("zhaopeng","!!!!!!handleFocusScaleImmediately gainFocus:"+gainFocus);
            TVFocusAnimHelper.handleOnFocusChange(view, gainFocus, mFocusScaleX, mFocusScaleY, 0);
        }
    }

    @Override
    public Rect getFloatFocusMarginRect() {
        return mAttachInfo.mFloatFocusMarginRect;
    }

    public void onFocusChanged(final boolean gainFocus, final int direction, final Rect previouslyFocusedRect) {
        if (isInReFocus()) {
            handleFocusScaleImmediately(gainFocus, direction, previouslyFocusedRect);
        } else {
            onHandleFocusScale(gainFocus, direction, previouslyFocusedRect);
        }
        //drawableStateChanged();
//        isBorderVisible = gainFocus;
      if (borderDrawable != null) {
        borderDrawable.setBorderVisible(gainFocus);
        borderDrawable.onFocusChanged(view, gainFocus);
      }
        view.postInvalidate();
    }


    /**
     * {@link ITVView#setFocusScale(float)}
     *
     * @param scale 缩放倍数
     */
    @Override
    public void setFocusScale(float scale) {
        this.mFocusScaleX = scale;
        this.mFocusScaleY = scale;
    }

  @Override
  public float getFocusScale() {
    return mFocusScaleX;
  }

  @Override
  public void setSkipRequestFocus(boolean b) {

  }

  @Override
  public void onSetSid(String sid) {

  }


  /**
     * 设置View获得焦点的放大倍数
     */
    @Override
    public void setFocusScaleX(float scale) {
        this.mFocusScaleX = scale;
    }

    /**
     * 设置View获得焦点的放大倍数
     */
    @Override
    public void setFocusScaleY(float scale) {
        this.mFocusScaleY = scale;
    }


    /**
     * {@link ITVView#setFocusScaleDuration(int)}
     *
     * @param duration 缩放动画时长 单位：毫秒
     */
    @Override
    public void setFocusScaleDuration(int duration) {
        this.mDuration = duration;
    }


    @Override
    public float getFocusScaleX() {
        return mFocusScaleX;
    }

    @Override
    public float getFocusScaleY() {
        return mFocusScaleY;
    }

  @Override
  public void setFillParent(boolean b) {

  }

  @Override
  public boolean isFillParent() {
    return false;
  }

  @Override
    public IFloatFocusManager getFloatFocusManager() {
        return null;
    }

    @Override
    public void setFloatFocusFocusedAlpha(float alpha) {
        mAttachInfo.setFloatFocusFocusedAlpha(alpha);
    }

  @Override
  public void notifyBringToFront(boolean b) {

  }

  @Override
    public AttachInfo getAttachInfo() {
        return mAttachInfo;
    }

  @Override
  public boolean isAutoFocus() {
    return false;
  }

  @Override
  public void setAutoFocus(boolean b) {

  }

  @Override
  public void setAutoFocus(boolean b, boolean requestFocusOnExit) {

  }

  @Override
    public View getView() {
        return view;
    }


    @Override
    public void notifyInReFocus(boolean isIn) {
        this.mInReFocus = isIn;
//    Log.d("zhaopeng"," notifyInReFocus :"+isIn+" this:"+this);
        if (!isFocusable() && view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                final View child = ((ViewGroup) view).getChildAt(i);
                if (child instanceof ITVView) {
                    ((ITVView) child).notifyInReFocus(isIn);
                }
            }
        }
    }

    @Override
    public boolean isInReFocus() {
        return mInReFocus;
    }

    public void onDetachedFromWindow(){
        if (isFocusable()) {
            TVFocusAnimHelper.changeFocusScaleDirectly(view, 1, 1);
        }
        if (view.isSelected()) {
            view.setSelected(false);
        }
        stopListenGlobalFocusChange();
    }

    public void onAttachedToWindow() {
        listenGlobalFocusChangeIfNeed();
    }


    public void stopListenGlobalFocusChange() {
        if (mOnGlobalFocusChangeListener != null) {
            view.getViewTreeObserver().removeOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);
            mOnGlobalFocusChangeListener = null;
        }
    }

    public void setListenGlobalFocusChange(boolean listenGlobalFocusChange) {
        isListenGlobalFocusChange = listenGlobalFocusChange;
    }

    public boolean hasFocus(){
        return view.hasFocus();
    }

    public void listenGlobalFocusChangeIfNeed() {
        stopListenGlobalFocusChange();
        if (isListenGlobalFocusChange && view instanceof ViewGroup) {
            mOnGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
                @Override
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//                    if (LogUtils.isDebug()) {
//                        Log.d(TAG, "onGlobalFocusChanged hasFocus : " + hasFocus() + " this :" + this);
//                    }
                    if (hasFocus()) {
                        if (oldFocus == null) {
                            //首次获得焦点
                            notifyViewFocusChanged(true, false, null, newFocus, false);
                        } else {
                            //焦点在内部，但上一个view不属于内部
                            final boolean isOldFocusDescendantOf = TVViewUtil.isViewDescendantOf(oldFocus, (ViewGroup) view);
                            if (!isOldFocusDescendantOf) {
                                notifyViewFocusChanged(true, false, oldFocus, newFocus, false);
                            }
                        }
                    } else {
                        final boolean isNewFocusDescendantOf = TVViewUtil.isViewDescendantOf(newFocus, (ViewGroup) view);
//                        if (LogUtils.isDebug()) {
//                            Log.d(TAG, "onGlobalFocusChanged  hasFocus : " + hasFocus() + " isNewFocusDescendantOf : " + isNewFocusDescendantOf);
//                        }
                        if (!isNewFocusDescendantOf) {
                            //焦点丢失
                            final boolean isOldFocusDescendantOf = TVViewUtil.isViewDescendantOf(oldFocus, (ViewGroup) view);

                            if (isOldFocusDescendantOf) {
                                notifyViewFocusChanged(false, true, oldFocus, newFocus, true);
                            }
                        }
                    }
                }
            };
            view.getViewTreeObserver().addOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener);
        }
    }

    private boolean isListenGlobalFocusChange = false;
    private ViewTreeObserver.OnGlobalFocusChangeListener mOnGlobalFocusChangeListener;

    boolean lastFocusState = false;

    private void notifyViewFocusChanged(boolean hasFocus, boolean isOldFocusDescendantOf, View oldFocus, View focused, boolean loseFocus) {
        //
        Log.d(TAG, "notifyViewGroupFocusChanged lastFocusState != hasFocus:" + (lastFocusState != hasFocus) + ",loseFocus:" + loseFocus + ",isOldFocusDescendantOf:" + isOldFocusDescendantOf);
        if (lastFocusState != hasFocus) {
            onViewFocusChanged(hasFocus, focused);
            lastFocusState = hasFocus;
        }
    }

    protected void onViewFocusChanged(boolean hasFocus, View focused){

    }

    public void setClipChildren(boolean b){
        if(view instanceof ViewGroup){
            ((ViewGroup) view).setClipChildren(b);
        }
    }

//    public static @Nullable TVViewActor findTVActor(@Nullable View view){
//        if (view != null) {
//            if(view instanceof TVViewActorHost){
//                return ((TVViewActorHost) view).getTVActor();
//            }
//            final Object o = view.getTag(R.id.tag_tv_actor);
//            if(o instanceof TVViewActor){
//                return (TVViewActor) o;
//            }
//        }
//        return null;
//    }
//
//
//    static void putTVActorTag(@Nullable View view, @Nullable TVViewActor actor){
//        if (view != null) {
//            view.setTag(R.id.tag_tv_actor,actor);
//        }
//    }

//    static @Nullable String findViewName(@Nullable View view){
//        final TVViewActor actor = findTVActor(view);
//        if (actor != null) {
//            return actor.name;
//        }
//        return null;
//    }

    public ViewGroup getAsViewGroup(){
        return (ViewGroup) view;
    }


    /**
     * begin:寻焦
     * -------------------------------------------------------------------------------------------
     */

    private boolean useAdvancedFocusSearch = false;
    private View mSpecialFocusSearchRequest;
    //在tvList焦点滚动时是否以此为目标
    private boolean isFocusScrollTarget = false;

    public View focusSearch(View focused, int direction) {
        if(useAdvancedFocusSearch){
            return advanceFocusSearch(focused, direction);
        }
        return null;
    }

    void markSpecifiedFocusSearch(View specialTarget) {
//        Log.d(TAG, "+mark SpecifiedFocusSearch  target : "+specialTarget)
        mSpecialFocusSearchRequest = specialTarget;
    }

    void consumeSpecifiedFocusSearchRequest() {
        if (mSpecialFocusSearchRequest != null) {
//            Log.d(TAG, "-consume SpecifiedFocusSearchRequest")
            mSpecialFocusSearchRequest = null;
        }
    }

    private boolean isSpecifiedFocusSearch() {
        return mSpecialFocusSearchRequest != null;
    }

    ArrayList<View> mTempFocusList = new ArrayList<>();

    protected View advanceFocusSearch(View focused, int direction) {
//        if(view instanceof ViewGroup) {
//            return FocusSystemUtil.advanceFocusSearch((ViewGroup) view, focused, direction, false);
//        }
        return null;
    }


//    private String firstFocusTargetName = null;
//
//    public void setFirstFocusTargetName(String name) {
//        this.firstFocusTargetName = name;
//    }
//
//    public View findFocusTargetName(int direction) {
//        final String target = FocusSystemUtil.findSpecifiedNextFocusName(this,direction);
//        return TVViewActorUtil.findViewByName(target, view);
//    }
//
//    public View findNextSpecialFocusView(View focused, int direction) {
//        return view instanceof ViewGroup? FocusSystemUtil.advanceFocusSearch((ViewGroup) view, focused, direction, true)
//                : null;
//    }

//    public boolean addFocusables(ArrayList<View> views, int direction, int focusableMode) {
//        if (!hasFocus()) {
//            final View view = findFocusTargetName(direction);
//            if (view != null) {
//                if (LogUtils.isDebug()) {
//                    LogUtils.d(TAG, "div: +addFocusables by focusSearchTarget : " + direction + ",view :" + view.getId());
//                }
//                view.addFocusables(views, direction, focusableMode);
//                return true;
//            } else {
//                if (LogUtils.isDebug()) {
//                    LogUtils.d(TAG, "div: +addFocusables by focusSearchTarget : " + direction + ",view :" + null);
//                }
//            }
//        }
//        if (!hasFocus() && firstFocusTargetName != null) {
//            final View view = TVViewActorUtil.findViewByName(firstFocusTargetName,getView());
//            if (view != null) {
//                LogUtils.d(TAG, "div: +addFocusables by firstTargetName : " + firstFocusTargetName + ",view :" + view.getId());
//                view.addFocusables(views, direction, focusableMode);
//                return true;
//            }
//        }
//        if (useAdvancedFocusSearch && mTempFocusList != null && mTempFocusList.size() > 0) {
//            if (LogUtils.isDebug()) Log.d(TAG, "+addFocusables views : $mTempFocusList");
//            if (views != null) views.addAll(mTempFocusList);
//        }
//        return false;
//    }

    public void invalidate(){
        view.invalidate();
    }

    /**
     * end:寻焦
     * -------------------------------------------------------------------------------------------
     */

    /**
     * begin:焦点状态背景、边框
     * -------------------------------------------------------------------------------------------
     */

    //zhaopeng add

    private boolean mHasSetTempBackgroundColor = false;
    private boolean mUserHasSetBackgroudnColor = false;
    private int mUserSetBackgroundColor = Color.TRANSPARENT;

    //zhaopeng add
//  private final BorderFrontDrawable focusFrontDrawable;

    //border相关配置本地变量
    private boolean isBorderVisible = true;
    private boolean isBlackRectEnable;
    private int borderColor = Color.WHITE;
    private float borderCorner;
    private int borderWidth;
    //添加自定义borderDrawable
    protected BaseBorderDrawable borderDrawable;
    //自定义边框type 默认type = 0
    protected int borderType;


    //设置focusDrawable样式
    public void setFocusBorderType(int type) {
        this.borderType = type;
        setBorderDrawable(getBorderDrawableProvider());
        initBorderDrawable();
        invalidate();
    }
    public ConcurrentHashMap<Integer, BaseBorderDrawable> getBorderDrawableProvider(){
      ConcurrentHashMap<Integer, BaseBorderDrawable> map = new ConcurrentHashMap<>();
      map.put(0,new BorderFrontDrawable());
      return map;
    }

    public void initBorderDrawable() {
        if (borderDrawable != null) {
            borderDrawable.setBorderVisible(this.isBorderVisible);
            borderDrawable.setCallback(view);
            borderDrawable.setVisible(false, false);
             //fixme 这里暂未实现，正式发布前需要实现
            //borderDrawable.setBorderColor(this.borderColor);
//          if (view.getContext() instanceof HippyInstanceContext) {
//            FocusManagerModule.GlobalFocusConfig config = FocusManagerModule.getGlobalFocusConfig(((HippyInstanceContext) view.getContext()).getEngineContext());
//            if (config.defaultFocusBorderWidth != -1) {
//              borderDrawable.setBorderWidth(PixelUtil.dp2pxInt(config.defaultFocusBorderWidth));
//            }
//            borderDrawable.setBlackRectEnable(config.defaultFocusBorderInnerRectEnable);
////            Log.i(TAG,"initBorderDrawable config:"+config+",this.borderCorner:"+this.borderCorner);
//            if (this.borderCorner <= 0) {
//              borderDrawable.setBorderCorner(PixelUtil.dp2px(config.defaultFocusBorderRadius));
//            }else{
//              borderDrawable.setBorderCorner(this.borderCorner);
//            }
//            borderDrawable.setBorderColor(config.defaultFocusBorderColor);
//          }
        }
    }

    public BaseBorderDrawable getBorderDrawable() {
        return borderDrawable;
    }

    //设置默认borderDrawable
    public void setBorderDrawable(ConcurrentHashMap<Integer, BaseBorderDrawable> borderMap) {
        if (borderMap != null && borderMap.size() > 0) {
            for (Integer i : borderMap.keySet()) {
                if (i == borderType) {
                    this.borderDrawable = borderMap.get(i);
                }
            }
            if (borderType == 0 && this.borderDrawable == null) {
                this.borderDrawable = new BorderFrontDrawable();
            }
        } else {
            this.borderDrawable = new BorderFrontDrawable();
        }
    }

    /**
     * 设置边框颜色
     *
     * @param color
     */
    public void setFocusBorderColor(@ColorInt int color) {
        if (borderDrawable != null) {
            this.borderColor = color;
            borderDrawable.setBorderColor(color);
            invalidate();
        }
    }

    /**
     * 设置边框弧度
     *
     * @param
     */
    public void setFocusBorderCorner(float radius) {
        if (borderDrawable != null) {
            this.borderCorner = radius;
            borderDrawable.setBorderCorner(radius);
            invalidate();
        }
    }

    /**
     * 设置边框宽度
     *
     * @param
     */
    public void setFocusBorderWidth(int radius) {
        if (borderDrawable != null) {
            this.borderWidth = radius;
            borderDrawable.setBorderWidth(radius);
            invalidate();
        }
    }

    public void setFocusBorderEnable(boolean enable) {
        if (borderDrawable != null) {
            this.isBorderVisible = enable;
            borderDrawable.setBorderVisible(enable);
            invalidate();
        }
    }

    public void setBlackRectEnable(boolean enable) {
        if (borderDrawable != null) {
            this.isBlackRectEnable = enable;
            borderDrawable.setBlackRectEnable(enable);
            invalidate();
        }
    }

    /**
     * 绘制焦点边框
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (borderDrawable != null) {
            borderDrawable.onDraw(canvas);
        }
    }


    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (borderDrawable != null) {
            borderDrawable.onSizeChanged(w, h, oldw, oldh);
        }
    }

  private int mUserSetFocusBackgroundColor = 0;
  private int mUserSetSelectBackgroundColor = 0;
  private boolean relayoutAfterAttach = false;


  /***
   *
   * @param color
   */
  public void setFocusBackGroundColor(int color) {
    this.mUserSetFocusBackgroundColor = color;
//    invalidate();
    drawableStateChanged();
  }

  /***
   *
   * @param color
   */
  public void setSelectBackGroundColor(int color) {
    this.mUserSetSelectBackgroundColor = color;
//    invalidate();
    drawableStateChanged();
  }

  private void postSetBGColor(final int bg) {

    view.setBackgroundColor(bg);
    view.postInvalidateDelayed(16);
//    invalidate();
  }

  public void drawableStateChanged() {
    int[] states = view.getDrawableState();
    if (!view.isFocused() && view.isDuplicateParentStateEnabled()) {
      final boolean focused = TVViewUtil.stateContainsAttribute(states, android.R.attr.state_focused);
      if(isBorderVisible){
        if (borderDrawable != null) {
          borderDrawable.onDrawableStateChanged(view, focused);
        }
      }
      if (focusScaleOnDuplicateParentState && (mFocusScaleX != 1 || mFocusScaleY != 1)) {
        handleFocusScale(focused,-1,null,mDuration);
      }
    }

    FocusUtils.handleShowOnState(view, states, showOnState);
    if (isFocusable() || view.isDuplicateParentStateEnabled()) {
      if (mUserSetFocusBackgroundColor != 0) {
        final boolean focused = FocusUtils.stateContainsAttribute(states, android.R.attr.state_focused);
        if (focused || view.isFocused()) {
          postSetBGColor(mUserSetFocusBackgroundColor);
          return;
        }
      }
      final boolean select = FocusUtils.stateContainsAttribute(states, android.R.attr.state_selected);
      if (mUserSetSelectBackgroundColor != 0) {
        if (select || view.isSelected()) {
          postSetBGColor(mUserSetSelectBackgroundColor);
          return;
        }
      }
//      if (mUrlFetchState != IMAGE_LOADED || onBindNew || getId() != -1) {
//        //zhaopeng 因为图片已经加载成功，直接展示图片，不需要再展示背景
//        //getId != -1的判断是由于在fastList里会走onBindNew，而在普通的img标签上却没有机会执行，所以这里做一下修正
//        postSetBGColor(getUserSetBackgroundColor());
//      } else {
//        postInvalidateDelayed(16);
//      }
    }
  }

  private boolean focusScaleOnDuplicateParentState = false;


  public void setFocusScaleOnDuplicateParentState(boolean focusScaleOnDuplicateParentState) {
    this.focusScaleOnDuplicateParentState = focusScaleOnDuplicateParentState;
  }



  private int[] showOnState;


  public void setShowOnState(int[] showOnState) {
    this.showOnState = showOnState;
  }

    //zhaopeng add

    /**
     * end:drawable
     * -------------------------------------------------------------------------------------------
     */
    private boolean animRunning = false;

    protected void shakeSelf(View view, int direction) {
        if (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP) {
            if (!animRunning) {
                animRunning = true;
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y.getName(), 0, 5f);//抖动幅度0到5
                objectAnimator.setDuration(250);//持续时间
                objectAnimator.setInterpolator(new CycleInterpolator(2));//抖动次数
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        animRunning = false;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animRunning = false;
                    }
                });
                objectAnimator.start();//开始动画
            }
        } else {
            if (!animRunning) {
                animRunning = true;
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X.getName(), 0, 5f);//抖动幅度0到5
                objectAnimator.setDuration(250);//持续时间
                objectAnimator.setInterpolator(new CycleInterpolator(2));//抖动次数
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        animRunning = false;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animRunning = false;
                    }
                });
                objectAnimator.start();//开始动画
            }
        }
    }




}
