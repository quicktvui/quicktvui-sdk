package com.quicktvui.base.ui;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Create by WeiPeng on 2020/12/15 12:32
 */
public class FocusDispatchView extends FrameLayout {

  public static final String TAG = "DebugFocus";
  public static final boolean DEBUG = false;
//    private static final boolean DEBUG = DEBUG;

  private View mFocused;
  private View mSpecialFocusSearchRequest;
  private ArrayList<View> mTempFocusList = new ArrayList<>();
  private int lastKeyCode = 0;
  private HashMap<String, Object> viewTag;

  public void putViewTag(String key, Object object) {
    if (viewTag == null) {
      viewTag = new HashMap<>();
    }
    viewTag.put(key, object);
  }

  public Object getViewTag(String key) {
    return viewTag == null ? null : viewTag.get(key);
  }

  public FocusDispatchView(@NonNull Context context) {
    super(context);
  }

  public FocusDispatchView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FocusDispatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public int getLastKeyCode() {
    return lastKeyCode;
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  public FocusDispatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public void requestChildFocus(View child, View focused) {
    if (DEBUG) {
      int focusedId = -1;
      int nextID = -1;
      if (mFocused != null) {
        focusedId = mFocused.getId();
      }
      if (focused != null) {
        nextID = focused.getId();
      }
      Log.d(TAG, "FocusChange : From: " + focusedId + " To:" + nextID + ",ToView:" + focused);
    }
    mFocused = focused;
    super.requestChildFocus(child, focused);
  }

  /**
   * TODO
   * 1，线性布局内部处理焦点寻找逻辑
   * 2，nextXXid 指定为自己时的处理
   * 3，添加可以设置焦点在被动选择时，默认选择第几个child的接口
   */

  @Override
  public void addFocusables(ArrayList<View> views, int direction) {
    if (DEBUG) Log.d(TAG, "+addFocusables views : $mTempFocusList");
    if (views != null) views.addAll(mTempFocusList);
  }

  private void markSpecifiedFocusSearch(View specialTarget) {
//        Log.d(TAG, "+mark SpecifiedFocusSearch  target : "+specialTarget)
    mSpecialFocusSearchRequest = specialTarget;
  }

  private void consumeSpecifiedFocusSearchRequest() {
    if (mSpecialFocusSearchRequest != null) {
//            Log.d(TAG, "-consume SpecifiedFocusSearchRequest")
      mSpecialFocusSearchRequest = null;
    }
  }

  private boolean isSpecifiedFocusSearch() {
    return mSpecialFocusSearchRequest != null;
  }


  @Override
  public boolean dispatchKeyEventPreIme(KeyEvent event) {
    lastKeyCode = event.getKeyCode();
    return super.dispatchKeyEventPreIme(event);
  }

  public void blockFocus() {
    if (DEBUG) {
      Log.e(TAG, "blockFocus called");
    }
    setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
  }

  public void unBlockFocus() {
    if (DEBUG) {
      Log.e(TAG, "releaseBlockFocus called");
    }
    setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
  }

  private View findFocusFromGroup(ViewGroup group, int direction, View focused, boolean clearSearchList) {
    if (group == null)
      return null;
    if (clearSearchList) {
      mTempFocusList.clear();
    }
    group.addFocusables(mTempFocusList, direction);

    return FocusUtils.executeFindNextFocus(group, focused, direction);
  }

  View findViewByName(String name, View view) {
    if (TextUtils.isEmpty(name)) {
      return null;
    }
    String viewName = TVViewUtil.findName(view);

    if (name.equals(viewName)) {
      return view;
    }

    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View v = ((ViewGroup) view).getChildAt(i);
        View r = findViewByName(name, v);
        if (r != null) {
          return r;
        }
      }
    }
    return null;
  }

  @Override
  public View focusSearch(int direction) {
    if (DEBUG) Log.d(TAG, "focusSearch  without focused " + direction + "direction");
    return super.focusSearch(direction);
  }

  @Override
  public View focusSearch(View focused, int direction) {
    if (DEBUG) {
      Log.d(TAG, "-----------------begin : focusSearch with focused : $focused direction : $direction -------------");
    }
    //return super.focusSearch(focused, direction)

    //寻找结果
    View found = null;
    //用户自定义的目标view
    View userSpecifiedTarget = null;
    //用户自定义的目标viewID
    String userSpecifiedTargetName = null;
    //当前焦点的parent
    ViewGroup focusedParent = null;

    try {

      //1, find user specified
      if (focused != null) {
        //发现用户指定targetView，则直接调用此view的addFocusables方法
        userSpecifiedTargetName = findUserSpecifiedNextFocusViewIdTraverse(focused, direction);
        userSpecifiedTarget = findViewByName(userSpecifiedTargetName, this);
        if (DEBUG) {
          Log.d(TAG, "1 : find specifiedTargetViewName is $userSpecifiedTargetName specifiedView is $userSpecifiedTarget");
        }
        if (focused.getParent() instanceof ViewGroup)
          focusedParent = (ViewGroup) focused.getParent();
      }

      //2 , find focus
      if (userSpecifiedTarget != null && userSpecifiedTarget.getVisibility() == View.VISIBLE) {
        markSpecifiedFocusSearch(userSpecifiedTarget);
        // it.addFocusables(views,direction,focusableMode)
        //1,优先在当前parent中寻找，以保证优先搜索比较近的view
        if (found == null) {
          if (focusedParent != userSpecifiedTarget && focusedParent != userSpecifiedTarget.getParent()) { //确保俩个parent不是同一个
            if (focusedParent != null)
              focusedParent.addFocusables(mTempFocusList, direction);
            //zhaopeng 20190327 由于这里focused不是focusedParent的子view时，会发生崩溃，所以这里try catch
            found = FocusUtils.executeFindNextFocus(focusedParent, focused, direction);
            if (DEBUG) {
              Log.d(TAG, "2-1: find from focusedParent found :  $found");
            }
          }
        }
        //2， 从IFocusGroup中寻找

//                    if(it is ITVFocusGroup){
//                        val specifiedFocused = it.getNextSpecifiedFocus(focused,direction)
//
//                        if(specifiedFocused != null ){
//                                found = specifiedFocused
//                                if(FConfig.DEBUG) {
//                                    Log.d(TAG, "2-2: find from  ITVFocusGroup next is :  " + found)
//                                }
//
//                        }else{
//                            if(FConfig.DEBUG) {
//                                Log.d(TAG, "2-2: find from  ITVFocusGroup specifiedFocused == null  ")
//                            }
//                        }
//                    }
        //3， userSpecifiedTarget中寻找
        if (found == null) {
          mTempFocusList.clear();
          userSpecifiedTarget.addFocusables(mTempFocusList, direction);
          found = FocusUtils.executeFindNextFocus(this, focused, direction);
          if (DEBUG) {
            Log.d(TAG, "2-3 :  find from  userSpecifiedTarget  $found");
          }
        }


        //4 这里处理用户是否拦截了焦点
        if (found == null) {
          if (focused != null) {
            // if(getParent() instanceof ViewGroup && getParent().id == userSpecifiedTargetId){
            if (sameDescend(focused, userSpecifiedTargetName)) {
              // 这种情况下，用户将nextXXID 设置成本身，所以将focused返回
              if (DEBUG) {
                Log.d(TAG, "2-4 : find from : sameDescend return focused "+TVViewUtil.debugViewLite(focused));
              }
              found = focused;
            }
          }
        }

        //5 这里只有一种情况，用户设置的焦点不太符合物理逻辑，所以不再考虑focused位置的情况下再次搜索
        if (found == null) {
          mTempFocusList.clear();
          if (userSpecifiedTarget != null)
            userSpecifiedTarget.addFocusables(mTempFocusList, direction);
          found = FocusUtils.executeFindNextFocus(this, null, direction);
          if (DEBUG) {
            Log.d(TAG, "2-5 : find without focused found is $found");
          }
        }

      } else {
        if (DEBUG) {
          Log.d(TAG, "2 : ** userSpecifiedTarget is NULL find from Root");
        }
        //2 为空时用户没有设定，从root中寻找
        super.addFocusables(mTempFocusList, direction);

        //3 find nextFocus from root
        // 注意： 此方法会调用this.addFocusables()
        found = FocusUtils.executeFindNextFocus(this, focused, direction);
        if (DEBUG) {
          Log.d(TAG, "3 :  FocusFinder search from Root result is  $found");
        }
      }
    } finally {
      mTempFocusList.clear();
    }


    consumeSpecifiedFocusSearchRequest();
    if (DEBUG) {
      Log.d(TAG, "-----------------end : focusSearch searched : $found-----------------");
    }

    return found != null ? found : super.focusSearch(focused, direction);
  }


  private boolean sameDescend(View focused, String userSpecifiedId) {
    View target = focused;
    String targetName = TVViewUtil.findName(focused);
    while (target != this && target instanceof View) {

      if (userSpecifiedId.equals(targetName)) {
        return true;
      }

      target = target.getParent() instanceof View ? (View) target.getParent() : null;
    }
    return false;
  }


  private String findUserSpecifiedNextFocusViewIdTraverse(View focused, int direction) {

    String specifiedTargetViewName = null;
    /**
     * 寻找当前view以及其父view指定的下一焦点id
     *
     */
    View target = focused;
    while (target != this && target != null) {

      specifiedTargetViewName = FocusUtils.findSpecifiedNextFocusName(target, direction);

//            if(DEBUG){
//                Log.d(TAG, " findSpecifiedNextFocusId target is "+target+" find specifiedTargetViewID is "+specifiedTargetViewID)
//            }

      if (specifiedTargetViewName != null) {
        return specifiedTargetViewName;
      }
      target = target.getParent() instanceof View ? (View) target.getParent() : null;
    }
    return specifiedTargetViewName;
  }


  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (viewTag != null) {
      viewTag.clear();
    }
    viewTag = null;
    if (DEBUG) {
      Log.e(TAG, "DebugPool getCacheWorker RootView detached release tags" + this);
    }
  }

  void blockFocusForTime(long duration) {
    blockFocus();
    postDelayed(new Runnable() {
      @Override
      public void run() {
        unBlockFocus();
      }
    }, duration);
  }

  public static void blockFocus(View view) {
    if (DEBUG) {
      Log.e(TAG, "blockFocus called");
    }
    if (view instanceof ViewGroup && ((ViewGroup) view).getDescendantFocusability() != ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
      ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }
  }

  public static void unBlockFocus(View view) {
    if (DEBUG) {
      Log.e(TAG, "releaseBlockFocus called");
    }
    if (view instanceof ViewGroup && ((ViewGroup) view).getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
      ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
    }
  }

  public static FocusDispatchView findRootView(View v) {
    if (v instanceof FocusDispatchView) {
      return (FocusDispatchView) v;
    }
    if (v != null && v.getParent() instanceof View) {
      return findRootView((View) v.getParent());
    } else {
      return null;
    }
  }


}
