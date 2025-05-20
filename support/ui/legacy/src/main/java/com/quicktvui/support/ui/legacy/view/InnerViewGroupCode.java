package com.quicktvui.support.ui.legacy.view;

import android.util.Log;
import android.view.ViewGroup;


public class InnerViewGroupCode {
    /**
     * @param group
     * @param child
     * @param focused
     */
    public static void handleChildMoveFloatFocusForLayout(ITVViewGroup group, ITVView child, ITVView focused){

        final ViewGroup parent = (ViewGroup) group.getParent();
        if(TVRootView.DEBUG){
            Log.i(IFloatFocus.TAG,"InnerViewGroupCode handleChildMoveFloatFocusForLayout parent is "+parent+" group is "+group+" focused is "+focused);
        }


    }
}
