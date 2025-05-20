package com.quicktvui.support.ui.legacy.view;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.quicktvui.support.ui.legacy.FConfig;

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

        int direction = ITVView.Companion.getFOCUS_INVALID();

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

        ViewParent realParent =  view.getParent();

        if(realParent == parent){
            return true;
        }

        if(realParent != null && realParent instanceof View){

            return isViewDescendantOf((View) realParent,parent);
        }

        return false;
    }

    public static void debugPerformance(View v ,  String text){
        if(FConfig.DEBUG) {
            Log.e("FuiPerformance", v.toString() +" : "+text);
        }
    }
}
