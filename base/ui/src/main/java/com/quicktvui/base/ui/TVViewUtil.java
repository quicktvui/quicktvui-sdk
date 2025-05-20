package com.quicktvui.base.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


/**
 * @author zhaopeng
 * @version 1.0
 * @title
 * @description
 * @company
 * @created 16/3/24
 * @changeRecord [修改记录] <br/>
 * 16/3/24 ：created
 */

public class TVViewUtil {


    public static ITVView.TVMovement getMovement(int direction, ITVView.TVOrientation orientation, boolean reverse) {

       ITVView.TVMovement movement = ITVView.TVMovement.INVALID;

        if (orientation == ITVView.TVOrientation.HORIZONTAL) {
            switch(direction) {
                case View.FOCUS_LEFT:
                    movement = (!reverse) ? ITVView.TVMovement.PREV_ITEM : ITVView.TVMovement.NEXT_ITEM;
                    break;
                case View.FOCUS_RIGHT:
                    movement = (!reverse) ? ITVView.TVMovement.NEXT_ITEM : ITVView.TVMovement.PREV_ITEM;
                    break;
                case View.FOCUS_UP:
                    movement = ITVView.TVMovement.PREV_ROW;
                    break;
                case View.FOCUS_DOWN:
                    movement = ITVView.TVMovement.NEXT_ROW;
                    break;
            }
        } else if (orientation == ITVView.TVOrientation.VERTICAL) {
            switch(direction) {
                case View.FOCUS_LEFT:
                    movement = (!reverse) ? ITVView.TVMovement.PREV_ROW : ITVView.TVMovement.NEXT_ROW;
                    break;
                case View.FOCUS_RIGHT:
                    movement = (!reverse) ? ITVView.TVMovement.NEXT_ROW : ITVView.TVMovement.PREV_ROW;
                    break;
                case View.FOCUS_UP:
                    movement = ITVView.TVMovement.PREV_ITEM;
                    break;
                case View.FOCUS_DOWN:
                    movement = ITVView.TVMovement.NEXT_ITEM;
                    break;
            }
        }

        return movement;
    }

    public static ITVView.TVMovement getMovement(KeyEvent event, ITVView.TVOrientation orientation, boolean reverse) {

        ITVView.TVMovement movement = ITVView.TVMovement.INVALID;

        if (orientation == ITVView.TVOrientation.HORIZONTAL) {
            switch(event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    movement = (!reverse) ? ITVView.TVMovement.PREV_ITEM : ITVView.TVMovement.NEXT_ITEM;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    movement = (!reverse) ? ITVView.TVMovement.NEXT_ITEM : ITVView.TVMovement.PREV_ITEM;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    movement = ITVView.TVMovement.PREV_ROW;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    movement = ITVView.TVMovement.NEXT_ROW;
                    break;
            }
        } else if (orientation == ITVView.TVOrientation.VERTICAL) {
            switch(event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    movement = (!reverse) ? ITVView.TVMovement.PREV_ROW : ITVView.TVMovement.NEXT_ROW;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    movement = (!reverse) ? ITVView.TVMovement.NEXT_ROW : ITVView.TVMovement.PREV_ROW;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    movement = ITVView.TVMovement.PREV_ITEM;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    movement = ITVView.TVMovement.NEXT_ITEM;
                    break;
            }
        }

        return movement;
    }

    public static ITVView.TVMovement getMovement(KeyEvent event, ITVView.TVOrientation orientation) {

        return getMovement(event,orientation,false);
    }

    public static ITVView.TVMovement getMovement(int direction) {

        return getMovement(direction, ITVView.TVOrientation.HORIZONTAL,false);
    }

    public static ITVView.TVMovement getMovement(int direction, ITVView.TVOrientation orientation) {

        return getMovement(direction,orientation,false);
    }


    /**将上下左右键转换成focus的方向,例如将KeyEvent.KEYCODE_DPAD_DOWN转换成View.FOCUS_DOWN
     * @param keycode 如果不是方向键则返回负IFView.FOCUS_INVALID
     * @return
     */
    public static int convertKeyCodeToDirection(int keycode){

        int direction = -1;

        switch (keycode){


            case KeyEvent.KEYCODE_DPAD_DOWN :
                direction = View.FOCUS_DOWN;
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                direction = View.FOCUS_UP;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                direction = View.FOCUS_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT :
                direction = View.FOCUS_LEFT;
                break;
        }

        return direction;

    }


    public static boolean isViewDescendantOf(View view, ViewGroup parent){
        if(view == null){
            return false;
        }

        if(view  == parent){
          return true;
        }

        ViewParent realParent =  view.getParent();

        if(realParent == parent){
            return true;
        }

        if(realParent != null && realParent instanceof View){

            return isViewDescendantOf((View) realParent,parent);
        }

        return false;
    }

    public static void debugPerformance(View v , String text){

    }

    public static int getScreenWidth(Context context){
      if(context != null){
        return context.getResources().getDisplayMetrics().widthPixels;
      }else{
        return -1;
      }
    }

  public static  int getVectorByDirection(int direction,int orientation) {
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

  /**
   * 获取view的
   * @param view
   * @return
   */
  public static String getViewSID(@Nullable View view){
    if (view == null) {
      return null;
    }
    final ExtendTag et = ExtendTag.getExtendTag(view);
    if (et != null && !TextUtils.isEmpty(et.sid)) {
      return et.sid;
    }
    final Object o = view.getTag(R.id.tag_item_id);
    if (o instanceof String) {
      return (String) o;
    }
    return null;
  }

  public static String getViewName(@Nullable View view){
    if (view == null) {
      return null;
    }
    final ExtendTag et = ExtendTag.getExtendTag(view);
    if (et != null && !TextUtils.isEmpty(et.name)) {
      return et.name;
    }
    return null;
  }

  public static String debugView(View view){
    if (view == null) {
      return "null";
    }
    return "view:["+ view.getId()
      + ","+ getViewSID(view)
      +","+getViewName(view)+"]"
      +":super:"+ view;
  }

  public static String debugFocusInfo(View view){
    ExtendTag tag = ExtendTag.obtainExtendTag(view);
    return "FocusInfo" + "{" + Integer.toHexString(view.hashCode())
      + " " + debugViewLite(view)
      + "nextFocusSID: [" + tag.nextFocusLeftSID + "," + tag.nextFocusUpSID + "," + tag.nextFocusRightSID + "," + tag.nextFocusDownSID + "]"
      + "nextFocusID: [" + view.getNextFocusLeftId() + "," + view.getNextFocusUpId() + "," + view.getNextFocusRightId() + "," +view.getNextFocusDownId() + "]"
      + " [" + view.getVisibility() + "," + view.isFocusable() + "," + view.isFocused() + "]"
      + "}";
  }


  public static String debugViewLite(View view){
    if (view == null) {
      return "null";
    }
    String className = view.getClass().getSimpleName();
    switch (className){
      case "HippyViewGroup":
        className = "Div";
        break;
      case "HippyListView":
        className = "HippyList";
        break;
      case "HippyImageView":
        className = "HippyImage";
        break;
      case "HippyTextView":
        className = "HippyText";
        break;
      case "TVTextView":
        className = "TVText";
        break;
      case "FastListView":
        className = "FastList";
        break;
      default:
        break;
    }
//    if (view.getClass() == HippyViewGroup.class) {
//      className = "Div";
//    }else if(view.getClass() == FastListView.class){
//      className = "FastList";
//    }else if(view.getClass() == HippyListView.class){
//      className = "UL";
//    }else if(view.getClass() == HippyImageView.class){
//      className = "Image";
//    }else if(view.getClass() == HippyTextView.class){
//      className = "Text";
//    }else if(view.getClass() == TVTextView.class){
//      className = "TextView";
//    }else if("ItemRootView".equals(className)) {
////      className = "Placeholder(";
//      className = "ItemView";
//    }
    if(className.startsWith("Hippy")){
      className = className.substring(5);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      return className+"{"+Integer.toHexString(view.hashCode())
        +" "+view.getId()
        +" "+getViewSID(view)
        +" "+getViewName(view)
        +" ["+view.getLeft()+","+view.getTop()+","+view.getRight()+","+view.getBottom()+"]"
        +" ["+view.getVisibility()+","+view.isFocusable()+","+view.isFocused()+","+ view.isAttachedToWindow()+"]"
        + "}";
    }else{
      return className+"{"+Integer.toHexString(view.hashCode())
        +" "+view.getId()
        +" "+getViewSID(view)
        +" "+getViewName(view)
        +" ["+view.getLeft()+","+view.getTop()+","+view.getRight()+","+view.getBottom()+"]"
        +" ["+view.getVisibility()+","+view.isFocusable()+","+view.isFocused()+"]"
        + "}";
    }
  }

  /**
   * 获取view的
   * @param view
   * @return
   */
  public static void putViewSID(View view,String sid){
    if(view != null) {
      ExtendTag.obtainExtendTag(view).sid = sid;
      view.setTag(R.id.tag_item_id,sid);
//      Log.d("configID4Item","putViewSID sid:"+sid+",view:"+ExtendUtil.debugView(view));
//      if("bg-player".equals(sid)){
//        //logParent(ReplaceChildView.TAG,view);
//        ExtendUtil.logView(ReplaceChildView.TAG,view);
//      }
    }
  }

  static void logParent(String TAG,View view){
    if (view != null) {
      Log.i(TAG,"log view:"+debugView(view));
      if (view.getParent() instanceof View) {
        logParent(TAG, (View) view.getParent());
      }
    }
  }

  public static @Nullable String findName(View v){
    final ExtendTag et = ExtendTag.getExtendTag(v);
    if (et != null) {
      return et.name;
    }
    return null;
  }

  public static View findViewBySID(String id, View view,boolean checkValid) {
    if (view == null) {
      return null;
    }
//    Log.v("DebugExtend",">findViewBySID id :"+id+",view:"+ExtendUtil.debugViewLite(view));
    if (TextUtils.isEmpty(id)) {
      return null;
    }
    if (id.equals(getViewSID(view))) {
      Log.i("DebugExtend","findViewBySID id :"+id+",view:"+debugViewLite(view));
      return view;
    }
    if(checkValid && view instanceof ExtendViewGroup){
      ExtendViewGroup hv = (ExtendViewGroup)view;
      if(hv.isPageHidden()){
        Log.i("DebugExtend","findViewBySID return on view is hidden :"+debugView(view));
        return null;
      }
    }
    if (view instanceof ViewGroup) {
//      Log.v("DebugExtend","--findViewBySID childCount :"+((ViewGroup) view).getChildCount()+",view:"+ExtendUtil.debugViewLite(view));
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View child = ((ViewGroup) view).getChildAt(i);
        View r = findViewBySID(id, child,checkValid);
        if (r != null) {
          return r;
        }
      }
    }
    return null;
  }

  public static View findViewBySID(String id, View view) {
    return findViewBySID(id,view,false);
  }

  @Deprecated
  public static View findViewByItemID(String id, View view) {
    return findViewBySID(id,view);
  }



  public static View findViewByName(String name, View view) {
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

  public static View findViewByNameOrSID(View focused,FocusUtils.FocusParams fp, View view) {
//    Log.i(FocusDispatchView.TAG,"------findViewByNameOrSID view:"+ExtendUtil.debugView(view)+",fp:"+fp);
    if (TextUtils.isEmpty(fp.specifiedTargetSID) && TextUtils.isEmpty(fp.specifiedTargetViewName)) {
      return null;
    }
    String viewName = TVViewUtil.findName(view);
    if(view.getVisibility() != View.VISIBLE){
      //2025 03 25 这里注掉alpha判断，因为有些view的alpha是0，但是是可见的,导致了播放器菜单向上时不可用
//    if(view.getVisibility() != View.VISIBLE || view.getAlpha() == 0){
      return null;
    }
    if (fp.specifiedTargetViewName != null &&  fp.specifiedTargetViewName.equals(viewName)) {
      if(focused != view){
        return view;
      }
    }
    final String viewSID = ExtendTag.obtainExtendTag(view).sid;
    if(fp.specifiedTargetSID != null && fp.specifiedTargetSID.equals(viewSID)){
      return view;
    }

    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View v = ((ViewGroup) view).getChildAt(i);
        View r = findViewByNameOrSID(focused,fp, v);
        if (r != null) {
          return r;
        }
      }
    }
    return null;
  }


}
