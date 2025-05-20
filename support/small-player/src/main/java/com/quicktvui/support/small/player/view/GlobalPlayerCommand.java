package com.quicktvui.support.small.player.view;

import android.view.View;

import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.hippy.utils.LogUtils;


/**
 * @auth: njb
 * @date: 2022/12/8 23:26
 * @desc: 播放器
 */
public class GlobalPlayerCommand {
    private HippyViewEvent cmdEvent;
    private final GlobalPlayerView globalPlayer;
    private final PlaceholderView placeholder;
    public final static String TAG = "FloatItemPlayerLog";

    GlobalPlayerCommand(GlobalPlayerView player, PlaceholderView placeholder) {
        this.globalPlayer = player;
        this.placeholder = placeholder;
    }

    protected HippyViewEvent getEvent() {
        if (this.cmdEvent == null) {
            this.cmdEvent = new HippyViewEvent("onCall");
        }
        return this.cmdEvent;
    }

    protected HippyMap generateItemEventMap(String eventName) {
        HippyMap hm = new HippyMap();
        hm.pushString("name", eventName);
        return hm;
    }

    public void stop(Object item){
        LogUtils.i(TAG,">>>>>stop");
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("stop");
            HippyMap startMap = new HippyMap();
            startMap.pushObject("item",item);
            map.pushObject("param",startMap);
            getEvent().send(view,map);
        }
    }

    public void pause(){
        LogUtils.i(TAG,">>>>>pause");
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("pause");
            getEvent().send(view,map);
        }
    }

    public void resume(){
        LogUtils.i(TAG,">>>>>resume");
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("resume");
            getEvent().send(view,map);
        }
    }

    public void start(Object bindItem, Boolean b){
        LogUtils.i(TAG,">>>>>start");
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("start");
            HippyMap startMap = new HippyMap();
            startMap.pushBoolean("isPlayerStart",b);
            startMap.pushObject("item",bindItem);
            map.pushObject("param",startMap);
            getEvent().send(view,map);
        }
    }

    public void changeFullScreen(boolean b){
        LogUtils.i(TAG,">>>>>changeFullScreen:"+b);
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("changeFull");
            map.pushObject("param",b);
            getEvent().send(view,map);
        }
    }

    public void changeFloatWindow(boolean b){
        LogUtils.i(TAG,">>>>>changeFloatWindow:"+b);
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("changeFloat");
            map.pushObject("param",b);
            getEvent().send(view,map);
        }
    }

    public void layout(int x,int y, int width,int height){
        LogUtils.i(TAG,">>>>>layout:x"+x+",y:"+y+",width:"+width+",height:"+height);
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("layout");
            HippyMap layout = new HippyMap();
            layout.pushInt("x",x);
            layout.pushInt("y",y);
            layout.pushInt("width",width);
            layout.pushInt("height",height);
            map.pushObject("param",layout);
            getEvent().send(view,map);
        }
    }

    public void changeOnShow(boolean b){
        LogUtils.i(TAG,">>>>>changeOnShow:"+b);
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("show");
            map.pushBoolean("param",b);
            map.pushObject("item",placeholder.getBoundItem());
            getEvent().send(view,map);
            this.globalPlayer.isPlayerShow = b;
        }
    }

    public void changeEnable(boolean b){
        LogUtils.i(TAG,">>>>>changeEnable:"+b);
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("enable");
            map.pushBoolean("param",b);
            getEvent().send(view,map);
        }
    }

    public void changeFocused(boolean isFocused){
        LogUtils.i(TAG,">>>>>changeFocused:"+ isFocused);
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("focusChange");
            map.pushBoolean("param",isFocused);
            getEvent().send(view,map);
        }
    }

    public void changeBindPlaceholder(String bindId){
        View view = this.globalPlayer;
        if (view != null) {
            HippyMap map = generateItemEventMap("bindChange");
            map.pushString("bindId", bindId);
            getEvent().send(view,map);
        }
    }
}
