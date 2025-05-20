package com.quicktvui.support.small.player.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.quicktvui.hippyext.views.fastlist.FastItemView;
import com.quicktvui.hippyext.views.fastlist.FastItemViewController;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.utils.PixelUtil;

import java.util.HashMap;


@HippyController(name = "GlobalPlayerFrame")
public class GlobalPlayerController extends FastItemViewController {

    HashMap<String,View> followerMap;
    HashMap<Object, PlaceholderView> placeholderMap;
    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        Log.i(PlaceholderView.TAG,"createViewImpl iniProps:"+iniProps);
        final boolean isPlaceholder = iniProps.containsKey("placeholder");

        final String name = iniProps.getString("name");
        if(followerMap == null){
            followerMap = new HashMap<>();
        }
        if (placeholderMap == null) {
            placeholderMap = new HashMap<>();
        }
        View fv;
        if (isPlaceholder) {
            fv = new PlaceholderView(context);
            ((PlaceholderView) fv).setFollowerMap(followerMap);
        }else{
            fv = new GlobalPlayerView(context,name);
            ((GlobalPlayerView) fv).setPlaceholderMap(placeholderMap);
        }
        if(!isPlaceholder && name != null){
            followerMap.put(name,fv);
        }
        return fv;
    }


    @Override
    protected View createViewImpl(Context context) {
        Log.i(PlaceholderView.TAG,"createViewImpl ");
        return null;
    }

    @HippyControllerProps(name = "player", defaultType = HippyControllerProps.STRING)
    public void setFollower(View view, String name) {
        if(view instanceof PlaceholderView){
            ((PlaceholderView) view).setFollowerName(name);
        }
    }


//    @HippyControllerProps(name = "placeholderID", defaultType = HippyControllerProps.STRING)
//    public void setPlaceholderID(View view, String id) {
//        if(view instanceof GlobalPlayerFrameView){
//            ((GlobalPlayerFrameView) view).setID(id);
//        }
//    }



    @HippyControllerProps(name = "bindHolderID", defaultType = HippyControllerProps.STRING)
    public void setPlaceholderID(View view, String id) {
        if(view instanceof GlobalPlayerView){
            ((GlobalPlayerView) view).setPlaceholderID(id);
        }

    }


    @HippyControllerProps(name = "visibleDelay", defaultType = HippyControllerProps.NUMBER)
    public void setVisibleDelay(View view, int delay) {
        if(view instanceof PlaceholderView){
            ((PlaceholderView) view).changeVisibleDelay = delay;
        }
    }

    @HippyControllerProps(name = "fullScreenDelay", defaultType = HippyControllerProps.NUMBER)
    public void setFullScreenDelay(View view, int delay) {
        if(view instanceof PlaceholderView){
            ((PlaceholderView) view).changeFullScreenDelay = delay;
        }
    }

    @HippyControllerProps(name = "floatArea", defaultType = HippyControllerProps.ARRAY)
    public void setFloatArea(View view, HippyArray array) {
        if(view instanceof PlaceholderView){
            if (array == null || array.size() < 4) {
                ((PlaceholderView) view).floatArea = null;
            }else{
                final int l = (int) PixelUtil.dp2px(array.getInt(0));
                final int t = (int) PixelUtil.dp2px(array.getInt(1));
                final int r = (int) PixelUtil.dp2px(array.getInt(2));
                final int b = (int) PixelUtil.dp2px(array.getInt(3));
                ((PlaceholderView) view).floatArea = new Rect(l,t,r,b);
            }
        }
        if(view instanceof GlobalPlayerView){
            if (array == null || array.size() < 4) {
                ((GlobalPlayerView) view).floatArea = null;
            }else{
                final int l = (int) PixelUtil.dp2px(array.getInt(0));
                final int t = (int) PixelUtil.dp2px(array.getInt(1));
                final int r = (int) PixelUtil.dp2px(array.getInt(2));
                final int b = (int) PixelUtil.dp2px(array.getInt(3));
                ((GlobalPlayerView) view).floatArea = new Rect(l,t,r,b);
            }
        }
    }

    @HippyControllerProps(name = "visibleArea", defaultType = HippyControllerProps.ARRAY)
    public void setVisibleArea(View view, HippyArray array) {
        if(view instanceof PlaceholderView){
            if (array == null || array.size() < 4) {
                ((PlaceholderView) view).visibleArea = null;
            }else{
                final int l = (int) PixelUtil.dp2px(array.getInt(0));
                final int t = (int) PixelUtil.dp2px(array.getInt(1));
                final int r = (int) PixelUtil.dp2px(array.getInt(2));
                final int b = (int) PixelUtil.dp2px(array.getInt(3));
                ((PlaceholderView) view).visibleArea = new Rect(l,t,r,b);
            }
        }
        if(view instanceof GlobalPlayerView){
            if (array == null || array.size() < 4) {
                ((GlobalPlayerView) view).visibleArea = null;
            }else{
                final int l = (int) PixelUtil.dp2px(array.getInt(0));
                final int t = (int) PixelUtil.dp2px(array.getInt(1));
                final int r = (int) PixelUtil.dp2px(array.getInt(2));
                final int b = (int) PixelUtil.dp2px(array.getInt(3));
                ((GlobalPlayerView) view).visibleArea = new Rect(l,t,r,b);
            }
        }
    }


    @HippyControllerProps(name = "inset", defaultType = HippyControllerProps.ARRAY)
    public void setInset(View view, HippyArray array) {
        if(view instanceof PlaceholderView){
            ((PlaceholderView) view).insetX = (int) PixelUtil.dp2px(array.getInt(0));
            ((PlaceholderView) view).insetX = (int) PixelUtil.dp2px(array.getInt(1));
        }
        if(view instanceof GlobalPlayerView){
            ((GlobalPlayerView) view).insetX = (int) PixelUtil.dp2px(array.getInt(0));
            ((GlobalPlayerView) view).insetX = (int) PixelUtil.dp2px(array.getInt(1));
        }
    }


    @HippyControllerProps(name = "offset", defaultType = HippyControllerProps.ARRAY)
    public void setOffset(View view, HippyArray array) {
        if(view instanceof PlaceholderView){
            ((PlaceholderView) view).offsetX = (int) PixelUtil.dp2px(array.getInt(0));
            ((PlaceholderView) view).offsetY = (int) PixelUtil.dp2px(array.getInt(1));
        }
        if(view instanceof GlobalPlayerView){
            ((GlobalPlayerView) view).offsetX = (int) PixelUtil.dp2px(array.getInt(0));
            ((GlobalPlayerView) view).offsetY = (int) PixelUtil.dp2px(array.getInt(1));
        }
    }

    @HippyControllerProps(name = "fullScreen", defaultType = HippyControllerProps.BOOLEAN)
    public void changeFullScreen(View view, Boolean isFull) {
        if(view instanceof PlaceholderView){
            ((PlaceholderView) view).changeFullScreen(isFull);
        }
    }

    @HippyControllerProps(name = "disableLayout", defaultType = HippyControllerProps.BOOLEAN)
    public void disableLayout(View view, Boolean disableLayout) {
        if(view instanceof GlobalPlayerView){
            ((GlobalPlayerView) view).setDisableLayout(disableLayout);
        }
    }

    @HippyControllerProps(name = "lock", defaultType = HippyControllerProps.BOOLEAN)
    public void setLock(View view, Boolean lock) {
        if(view instanceof GlobalPlayerView){
            ((GlobalPlayerView) view).setLock(lock);
        }
    }

    @Override
    public void dispatchFunction(FastItemView view, String functionName, HippyArray var) {
        super.dispatchFunction(view, functionName, var);
        switch (functionName){
            case "setLock":
                this.setLock(view,var.getBoolean(0));
                break;
            case "setBindHolderID":
                this.setPlaceholderID(view,var.getString(0));
                break;
            case "invokePlayerFunction":
                if (view instanceof PlaceholderView) {
                    final PlaceholderView gv = (PlaceholderView) view;
                    final GlobalPlayerView pv = gv.findFollowerView();
                    if(pv != null) {
                        dispatchFunction(pv, var.getString(0), var.getArray(1));
                    }
                }
                break;
            case "exitFullScreen":
                if (view instanceof PlaceholderView) {
                    final PlaceholderView gv = (PlaceholderView) view;
                    gv.postChangeFullScreen(0,false);
                }
                if (view instanceof GlobalPlayerView) {
                    final PlaceholderView gv = (PlaceholderView) view;
                    gv.requestChangeFullScreen(false);
                }
                break;
            case "postChangeFull":
                if(view instanceof PlaceholderView){
                    final PlaceholderView gv = (PlaceholderView) view;
                    gv.postChangeFullScreen(0,true);
                }
                if(view instanceof GlobalPlayerView){
                    final GlobalPlayerView gv = (GlobalPlayerView) view;
                    gv.requestChangeFullScreen(true);
                }
                break;
            case "requestPHFocus":
                if(view instanceof PlaceholderView){
                    final PlaceholderView gv = (PlaceholderView) view;
                    gv.requestPHFocus();
                }
                if(view instanceof GlobalPlayerView){
                    final GlobalPlayerView gv = (GlobalPlayerView) view;
                    if (gv.mBindPlaceholder != null) {
                        gv.mBindPlaceholder.requestPHFocus();
                    }
                }
                break;
            case "syncLayout":
                if(view instanceof PlaceholderView){
                    boolean requestFocus = var.getBoolean(0);
                    final PlaceholderView gv = (PlaceholderView) view;
                    gv.syncLayout(requestFocus);
                }
                if(view instanceof GlobalPlayerView){
                    boolean requestFocus = var.getBoolean(0);
                    final GlobalPlayerView gv = (GlobalPlayerView) view;
                    if (gv.mBindPlaceholder != null) {
                        gv.mBindPlaceholder.syncLayout(requestFocus);
                    }
                }
                break;
            case "layout":
                if(view instanceof PlaceholderView){
                    final int l = (int) PixelUtil.dp2px(var.getInt(0));
                    final int t = (int) PixelUtil.dp2px(var.getInt(1));
                    final int w = (int) PixelUtil.dp2px(var.getInt(2));
                    final int h = (int) PixelUtil.dp2px(var.getInt(3));
                    final boolean syncPlayer = var.getBoolean(4);
                    final PlaceholderView gv = (PlaceholderView) view;
                    GlobalPlayerView fv = gv.findFollowerView();
                    if (fv != null) {
                        fv.setLayout(l,t,w,h,syncPlayer);
                    }
                }
                if(view instanceof GlobalPlayerView){
                    final int l = (int) PixelUtil.dp2px(var.getInt(0));
                    final int t = (int) PixelUtil.dp2px(var.getInt(1));
                    final int w = (int) PixelUtil.dp2px(var.getInt(2));
                    final int h = (int) PixelUtil.dp2px(var.getInt(3));
                    final boolean syncPlayer = var.getBoolean(4);
                    final GlobalPlayerView gv = (GlobalPlayerView) view;
                    gv.setLayout(l,t,w,h,syncPlayer);
                }
                break;
            case "changeFullScreen":
                boolean isFull = var.getBoolean(0);
                if(view instanceof PlaceholderView){
                    final PlaceholderView gv = (PlaceholderView) view;
                    gv.postChangeFullScreen(0,isFull);
                }
                if(view instanceof GlobalPlayerView){
                    final GlobalPlayerView gv = (GlobalPlayerView) view;
                    gv.requestChangeFullScreen(isFull);
                }
                break;
            case "notifyWindowState":
               //TODO
                break;
        }
    }
}
