package com.quicktvui.support.ui.legacy.view;

import android.util.Log;
import android.view.View;


public class InnerViewCode {

    /**
     * @param view
     */
    public static void handleFloatFocusMove(View view){
        if(TVRootView.DEBUG){
            Log.v(IFloatFocus.TAG,"InnerViewCode handleFloatFocusMove view is "+view);
        }
        if(view instanceof ITVView){
            AttachInfo at = ((ITVView) view).getAttachInfo();
            at.resetFloatFocusOffset();
        }
    }


}
