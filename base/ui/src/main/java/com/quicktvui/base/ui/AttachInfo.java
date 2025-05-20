package com.quicktvui.base.ui;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * @author zhaopeng
 * @version 1.0
 * @title
 * @description
 * @company
 * @created 16/5/9
 * @changeRecord [修改记录] <br/>
 * 16/5/9 ：created
 */
public class AttachInfo {

    public Point mFloatFocusOffset = new Point();

    boolean mCancelMove = false;

    public float mFloatFocusAlpha = 1;

    public Rect mFloatFocusMarginRect = new Rect();

    public void offsetFloatFocusOffset(int dx,int dy){
        mFloatFocusOffset.offset(dx,dy);
    }


    public int nextDownFocusID = -1;
    public int nextUpFocusID = -1;
    public int nextLeftFocusID = -1;
    public int nextRightFocusID = -1;


//    public void setFloatFocusOffset(int x,int y){
//        mFloatFocusOffset.set(x,y);
//    }

    public void resetFloatFocusOffset(){
        mFloatFocusOffset.set(0,0);
        mCancelMove = false;
    }


    @Override
    public String toString() {
        return "AttachInfo{" +
                "mFloatFocusOffset=" + mFloatFocusOffset +
                '}';
    }

    public void setFloatFocusFocusedAlpha(float floatFocusAlpha) {
        mFloatFocusAlpha = floatFocusAlpha;
    }
}
