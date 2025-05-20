package com.quicktvui.base.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.HashMap;

public class FocusUtils {

  public static final String KEY_NEXT_FOCUS_UP = "nextFocusUp";
  public static final String KEY_NEXT_FOCUS_DOWN = "nextFocusDown";
  public static final String KEY_NEXT_FOCUS_LEFT = "nextFocusLeft";
  public static final String KEY_NEXT_FOCUS_RIGHT = "nextFocusRight";

  public static final String KEY_NEXT_FOCUS_UP_FRONT = "up";
  public static final String KEY_NEXT_FOCUS_DOWN_FRONT = "down";
  public static final String KEY_NEXT_FOCUS_LEFT_FRONT = "left";
  public static final String KEY_NEXT_FOCUS_RIGHT_FRONT = "right";

  public static final class FocusParams{
    public String specifiedTargetViewName;
    public String specifiedTargetSID;
    public boolean isPureSpecifiedTarget = false;

    @Override
    public String toString() {
      return "FocusParams{" +
        "specifiedTargetViewName='" + specifiedTargetViewName + '\'' +
        ", specifiedTargetSID='" + specifiedTargetSID + '\'' +
        ", isPureSpecifiedTarget='" + isPureSpecifiedTarget + '\'' +
        '}';
    }
  }

  public static boolean testFocusable(View view) {
    if (view == null) {
      return false;
    }
    if (view.isFocusable()) {
      return true;
    }
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        if (testFocusable(((ViewGroup) view).getChildAt(i))) {
          return true;
        }
      }
    }
    return false;
  }

  public static @Nullable ViewParent findFocusBlockedParent(ViewParent view){
    if(view == null){
      return null;
    }
    if(view instanceof ViewGroup && ((ViewGroup) view).getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS){
      return view;
    }
    return findFocusBlockedParent(view.getParent());
  }


  public static void setNextFocusDownSID(View view,String nextSID){
    ExtendTag.obtainExtendTag(view).nextFocusDownSID = nextSID;
  }
  public static void setNextFocusUpSID(View view,String nextSID){

    ExtendTag.obtainExtendTag(view).nextFocusUpSID = nextSID;
  }

  public static void setNextFocusLeftSID(View view,String nextSID){
    ExtendTag.obtainExtendTag(view).nextFocusLeftSID = nextSID;
  }

  public static void setNextFocusRightSID(View view,String nextSID){
    ExtendTag.obtainExtendTag(view).nextFocusRightSID = nextSID;
  }


  public static void setNextFocusName(View view, String nextLeft, String nextRight, String nextUP, String nextDown){
    if (view != null) {
      final ExtendTag et = ExtendTag.obtainExtendTag(view);
      if(et.nextFocusFocusName == null) {
        et.nextFocusFocusName = new HashMap();
      }
      if(nextLeft != null) {
        et.nextFocusFocusName.put(KEY_NEXT_FOCUS_LEFT, nextLeft);
      }
      if(nextRight != null) {
        et.nextFocusFocusName.put(KEY_NEXT_FOCUS_RIGHT, nextRight);
      }
      if(nextUP != null) {
        et.nextFocusFocusName.put(KEY_NEXT_FOCUS_UP, nextUP);
      }
      if(nextDown != null) {
        et.nextFocusFocusName.put(KEY_NEXT_FOCUS_DOWN, nextDown);
      }
    }
  }

  public static int getVectorByDirection(int direction,int orientation) {
    int vector = 0;
    boolean vertical = orientation == RecyclerView.VERTICAL;
    if (vertical) {
      if (direction == View.FOCUS_UP) {
        vector = -1;
      } else if (direction == View.FOCUS_DOWN) {
        vector = 1;
      }
    } else {
      if (direction == View.FOCUS_LEFT) {
        vector = -1;
      } else if (direction == View.FOCUS_RIGHT) {
        vector = 1;
      }
    }
    return vector;
  }

  public static int getDirectionFromDpadKey(KeyEvent e){
    int direction = -1;
    switch (e.getKeyCode()) {
      case KeyEvent.KEYCODE_DPAD_UP:
        direction = View.FOCUS_UP;
        break;
      case KeyEvent.KEYCODE_DPAD_DOWN:
        direction = View.FOCUS_DOWN;
        break;
      case KeyEvent.KEYCODE_DPAD_LEFT:
        direction = View.FOCUS_LEFT;
        break;
      case KeyEvent.KEYCODE_DPAD_RIGHT:
        direction = View.FOCUS_RIGHT;
        break;
      default:
        break;
    }
    return direction;
  }


  public static String getDirectionName(int direction) {
    String key = null;
    switch (direction) {
      case View.FOCUS_DOWN:
        key = "down";
        break;
      case View.FOCUS_UP:
        key = "up";
        break;
      case View.FOCUS_LEFT:
        key = "left";
        break;
      case View.FOCUS_RIGHT:
        key = "right";
        break;
      case View.FOCUS_FORWARD:
        key = "forward";
        break;
      case View.FOCUS_BACKWARD:
        key = "backward";
        break;
    }
    return key;
  }

  public static boolean isPureFocusView(@NonNull View view) {
    //获取view是一个直接可以获取焦点的View,还是一个viewGroup
    if(view.isFocusable()){
      return true;
    }
    return false;
  }

  public static View executeFindNextFocus(ViewGroup group, View focused, int direction) {
    //注意： 此方法会调用this.addFocusables()
    //由于 focused可能不是group的子孙view,可能导致崩溃，所以这里try catch
    View result;
    try {
      result = FocusFinder.getInstance().findNextFocus(group, focused, direction);
    } catch (Throwable t) {
      result = null;
    }
    return result;
  }

  public static boolean sameDescend(View parent,View focused, String userSpecifiedId) {
    View target = focused;
    String targetName = TVViewUtil.findName(focused);
    while (target != parent && target instanceof View) {

      if (userSpecifiedId.equals(targetName)) {
        return true;
      }

      target = target.getParent() instanceof View ? (View) target.getParent() : null;
    }
    return false;
  }


  public static boolean sameDescend(View parent,View focused, String userSpecifiedId,String userSpecialSID) {
    View target = focused;
    String targetName = TVViewUtil.findName(focused);
    String targetSID = TVViewUtil.getViewSID(focused);
    if (userSpecifiedId == null) {
      return false;
    }
    while (target != parent && target != null) {

      if (userSpecifiedId.equals(targetName)) {
        return true;
      }
      if(!TextUtils.isEmpty(userSpecialSID) && userSpecialSID.equals(targetSID)){
        return true;
      }

      target = target.getParent() instanceof View ? (View) target.getParent() : null;
    }
    return false;
  }

  public static FocusParams findUserPureSpecifiedNextFocus(View focused, int direction){
    FocusParams f = new FocusParams();
    String specifiedTargetViewName = null;
    String specifiedTargetSID = null;
    /**
     * 寻找当前view指定的下一焦点id
     *
     */
    specifiedTargetViewName = findSpecifiedNextFocusName(focused, direction);
    if (specifiedTargetViewName != null) {
      f.specifiedTargetViewName = specifiedTargetViewName;
      return f;
    }
    specifiedTargetSID = findSpecifiedNextFocusSID(focused,direction);
    if(!TextUtils.isEmpty(specifiedTargetSID)){
      f.specifiedTargetSID = specifiedTargetSID;
      return f;
    }
    return f;
  }


  public static FocusParams findUserSpecifiedNextFocusViewIdTraverse(ViewGroup parent, View focused, int direction,@Nullable View root) {
    FocusParams f = new FocusParams();
    String specifiedTargetViewName = null;
    String specifiedTargetSID = null;
    /**
     * 寻找当前view以及其父view指定的下一焦点id
     *
     */
    View target = focused;
    while (target != null && target != root) {

      specifiedTargetViewName = findSpecifiedNextFocusName(target, direction);

//                  if(LogUtils.isDebug()){
//                      Log.d(FocusDispatchView., " findSpecifiedNextFocusId target is "+target+" find specifiedTargetViewID is "+specifiedTargetViewID);
//                  }

      if (specifiedTargetViewName != null) {
        f.specifiedTargetViewName = specifiedTargetViewName;
        if(focused == target){
          f.isPureSpecifiedTarget = true;
        }
        //return specifiedTargetViewName;
        return f;
      }

      specifiedTargetSID = findSpecifiedNextFocusSID(target,direction);
//      if(LogUtils.isDebug()){
//        Log.v(FocusDispatchView.TAG,"---findSpecifiedNextFocusSID specifiedTargetSID :"+specifiedTargetSID+",target:"+ExtendUtil.debugView(target));
//      }
      if(!TextUtils.isEmpty(specifiedTargetSID)){
        f.specifiedTargetSID = specifiedTargetSID;
        if(focused == target){
          f.isPureSpecifiedTarget = true;
        }
        return f;
      }
      target = target.getParent() instanceof View ? (View) target.getParent() : null;
    }
    return f;
  }

  /***
   * 寻找当前view指定的下一焦点id
   * @param
   */
  public static String findSpecifiedNextFocusName(View sourceView, int direction) {
    String specifiedTargetViewName = null;

    if (sourceView != null) {
      ExtendTag et = ExtendTag.obtainExtendTag (sourceView);
      if (et.nextFocusFocusName != null) {
        switch (direction) {
          case View.FOCUS_UP:
            specifiedTargetViewName = et.nextFocusFocusName.get(KEY_NEXT_FOCUS_UP);
            break;
          case View.FOCUS_DOWN:
            specifiedTargetViewName = et.nextFocusFocusName.get(KEY_NEXT_FOCUS_DOWN);
            break;

          case View.FOCUS_LEFT:
            specifiedTargetViewName = et.nextFocusFocusName.get(KEY_NEXT_FOCUS_LEFT);
            break;

          case View.FOCUS_RIGHT:
            specifiedTargetViewName = et.nextFocusFocusName.get(KEY_NEXT_FOCUS_RIGHT);
            break;
        }
      }
    }
    return specifiedTargetViewName;
  }

  /***
   * 寻找当前view指定的下一焦点id
   * @param
   */
  public static String findSpecifiedNextFocusSID(View sourceView, int direction) {
    String specifiedTargetViewSID = null;
    if (sourceView != null) {
      ExtendTag et = ExtendTag.obtainExtendTag (sourceView);
      switch (direction) {
        case View.FOCUS_UP:
          specifiedTargetViewSID = et.nextFocusUpSID;
          break;
        case View.FOCUS_DOWN:
          specifiedTargetViewSID = et.nextFocusDownSID;
          break;

        case View.FOCUS_LEFT:
          specifiedTargetViewSID = et.nextFocusLeftSID;
          break;

        case View.FOCUS_RIGHT:
          specifiedTargetViewSID = et.nextFocusRightSID;
          break;
      }
    }
    return specifiedTargetViewSID;
  }

  private final static int[] STATE_SET_SELECTED_FOCUS = new int[]{android.R.attr.state_selected, android.R.attr.state_focused};
  private final static int[] STATE_SET_SELECTED = new int[]{android.R.attr.state_selected};
  private final static int[] STATE_SET_FOCUSED = new int[]{android.R.attr.state_focused};

  public static boolean handleShowOnState(View view, int[] states, int showOnState[]){
    // [-1,focused,selected]
    // [-1,focused]
    if(showOnState != null && showOnState.length > 0){
      if(stateContainsAttribute(showOnState,-1)){//showOnState中包含-1
        //[normal]
        //[normal,selected]
        //[normal,focused]
        final boolean isNormalState = !stateContainsAttribute(states,findOppositeState(showOnState));
        view.setVisibility ( isNormalState ?  View.VISIBLE: View.INVISIBLE);
//        if(LogUtils.isDebug()) {
//          LogUtils.v("HippyDrawable", "drawableStateChanged 1 ,this:" +view. hashCode() + ",isNormalState:" + isNormalState + ",hasFocus:" + view.hasFocus()+",showOnState size:"+showOnState.length);
//        }
      }else {
        //[focused,selected] [focused],[selected]
        boolean isShow = false;//最终是不是要显示
        boolean takeOver = false;//是否要接管
        if(showOnState.length == 1){ //
          boolean containFocusAndSelect = stateContainsAttribute(showOnState,STATE_SET_SELECTED_FOCUS);
          if(containFocusAndSelect){
            if(showOnState[0] == StateView.STATE_FOCUS_AND_SELECT){
              //此时只有一种可能，状态是focused&selected
              isShow = true;
              takeOver = true;
//              "selected&focused":
//              "focused&selected":
            }
          }else{
            if(showOnState[0] == StateView.STATE_SELECT_NO_FOCUS){
              //showOnState = ["selected&!focused"]
              takeOver = true;
              if(stateContainsAttribute(states,STATE_SET_SELECTED)
                && !stateContainsAttribute(states,STATE_SET_FOCUSED)){
                //只有selected状态才显示
                isShow = true;
              }
            }
            if(showOnState[0] == StateView.STATE_FOCUS_NO_SELECT){
              //showOnState = ["focused&!selected"]
              takeOver = true;
              if(stateContainsAttribute(states,STATE_SET_FOCUSED) &&
                ! stateContainsAttribute(states,STATE_SET_SELECTED)){
                //只有focused状态才显示
                isShow = true;
              }
            }
          }
        }
//        Log.i("CustomState","-----isShow:"+isShow+",takeOver："+takeOver+",showOnState:"+ Arrays.toString(showOnState)+",states:"+Arrays.toString(states));
        if(takeOver){
          view.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        }else{
          final boolean contain = FocusUtils.stateContainsAttribute(states, showOnState);
          view.setVisibility(contain ? View.VISIBLE : View.INVISIBLE);
//          if(LogUtils.isDebug()) {
//            LogUtils.v("HippyDrawable", "drawableStateChanged 2 ,this:" + view.hashCode() + ",contain:" + contain + ",hasFocus:" +view. hasFocus() + ",isDuplicateParentStateEnabled:" +view. isDuplicateParentStateEnabled()+",showOnState size:"+showOnState.length);
//          }
        }
      }
      return true;
    }
    return false;
  }

  public static boolean checkVisibleOnCustomState(ArrayMap<String,Boolean> states, String[] showOnState){
    //eg:
    //showOnState:['s1','s2','s3']
    //state:{s1:false,s2:true,s3:false}
    boolean visible = false;
    for(String state:showOnState){//['s1','s2','s3']
      for(ArrayMap.Entry<String,Boolean> entry:states.entrySet()){
        //{s1:false}
        //{s2:true}
        //{s3:false}
        if(state.equals(entry.getKey())){
          visible |= entry.getValue();
        }
      }
    }
    return visible;
  }

  public static boolean handleCustomShowOnState(View view, ArrayMap<String,Boolean> states, String[] showOnState){
    // [-1,focused,selected]
    // [-1,focused]
    if(states != null && showOnState != null && showOnState.length > 0){
      final boolean visible = checkVisibleOnCustomState(states, showOnState);
      if (view != null) {
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
      }
      return true;
    }
    return false;
  }

  //[-1,focused] -> [focused]
  //[-1] -> [focused,selected]
  public static int[] findOppositeState(int[] showOnState) {
    int[] result;
    if(showOnState.length == 2){
      //[-1，selected]
      //[-1,focused]
      result = new int[1];
      for (int state : showOnState) {
        if (state != -1) {
          //因为只有这俩种可能性，所以简单的变换一下
          //focused->selected
          //selected->focused
          if(state == android.R.attr.state_focused){
            result[0] = android.R.attr.state_selected;
          }else{
            result[0] = android.R.attr.state_focused;
          }
        }
      }
    }else{
      //[-1]
      //[-1，selected,focused]
      result = STATE_SET_SELECTED_FOCUS;
    }
    return result;
  }

  public static void dumpArray(String tag,int[] stateSpecs,String prefix) {
    if (stateSpecs != null && stateSpecs.length > 0) {
      for (int specAttr : stateSpecs) {
        Log.d(tag,prefix+"->dumpArray item:"+specAttr);
      }
    }else{
      Log.d(tag,prefix+"->dumpArray array is empty or null");
    }
  }

  public static boolean stateContainsAttribute(int[] stateSpecs, int state[]) {
    if (stateSpecs != null && state != null) {
      for (int specAttr : stateSpecs) {
        for(int toState : state){
          if (specAttr == toState || -specAttr == toState) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static boolean stateContainsAttribute(int[] stateSpecs, int attr) {
    if (stateSpecs != null) {
      for (int specAttr : stateSpecs) {
        if (specAttr == attr || -specAttr == attr) {
          return true;
        }
      }
    }
    return false;
  }

  public static String getAutofocusTypeString(int type){
    switch (type){
      case ExtendViewGroup.AUTOFOCUS_TYPE_VISIBILITY:
        return "AUTOFOCUS_TYPE_VISIBILITY";
      case ExtendViewGroup.AUTOFOCUS_TYPE_SIZE_VALID:
        return "AUTOFOCUS_TYPE_SIZE_VALID";
      case ExtendViewGroup.AUTOFOCUS_TYPE_ATTACH:
        return "AUTOFOCUS_TYPE_ATTACH";
      case ExtendViewGroup.AUTOFOCUS_TYPE_FORCE:
        return "AUTOFOCUS_TYPE_FORCE";
      default:
        return "UNKNOWN";
    }
  }
}
