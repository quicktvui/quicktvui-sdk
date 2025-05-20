package com.quicktvui.support.ui.viewpager.tabs;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.quicktvui.hippyext.AutoFocusManager;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

public class AutoFocusUtils {
    public static final String TAG = "AutoFocusUtils";

    private static @Nullable AutoFocusManager findAutoFocusManager(View view){
        if(view instanceof TabsView){
            return ((TabsView) view).mAutoFocusManager;
        }
        final View rootView = HippyViewGroup.findPageRootView(view);
        if(rootView instanceof HippyViewGroup){
            return ((HippyViewGroup) rootView).getAutoFocusManager();
        }else{
            //final View rootView = HippyViewGroup.findPageRootView(view);
            Log.e(AutoFocusManager.TAG,"setAutoFocus called error rootView is :"+rootView);
        }
        return null;
    }

    public static void setAppearFocusTag(View view,String autoFocusID,int delay){
//        final AutoFocusManager afm = findAutoFocusManager(view);
//        if (afm != null) {
//            afm.setGlobalAutofocusSID(autoFocusID,delay);
//        }
    }

//    public static void checkAndRequestAutoFocus(View container,Object autoFocusID){
//        if(container == null){
//            Log.e(TAG,"checkAndRequestAutoFocus error container is null,autoFocusID : "+autoFocusID);
//            return;
//        }
////        Log.d(TAG,"checkAndRequestAutoFocus  container  : "+container+",autoFocusID:"+autoFocusID);
//        final AutoFocusManager afm = findAutoFocusManager(container);
//        if (afm != null) {
//            final View target = AutoFocusManager.findViewByTagID(container,autoFocusID);
//            if(target != null) {
//                afm.checkAndRequestAutoFocus(target,autoFocusID);
//            }else{
//                Log.d(TAG,"checkAndRequestAutoFocus target is null,"+",autoFocusID:"+autoFocusID);
//            }
//        }else{
//            Log.e(TAG,"checkAndRequestAutoFocus error AutoFocusManager is null,autoFocusID : "+autoFocusID);
//        }
//    }
}
