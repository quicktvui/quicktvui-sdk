package com.quicktvui.support.ui.largelist;

import android.view.View;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.base.ui.TriggerTaskHost;
import com.quicktvui.hippyext.TriggerTaskManagerModule;


public class EventSender {

    private MyEvent notifyLoadPage;
    private MyEvent notifyClick;
    private MyEvent notifyItemFocus;
    private MyEvent notifyPositionEvent;
    final View view;


    public EventSender(View view) {
        this.view = view;
    }

    public void notifyLoadPageData(int page) {

        if (notifyLoadPage == null) {
            notifyLoadPage = new MyEvent("onLoadPageData");
        }
        notifyLoadPage.getMap().pushInt("page", page);
        notifyLoadPage.getMap().pushInt("tag", getTAG());
        notifyLoadPage.send(view);
    }

    public void notifyItemClick(int position, EsMap itemContent) {

        if (notifyClick == null) {
            notifyClick = new MyEvent("onItemClick");
        }
        notifyClick.getMap().pushInt("position", position);
        notifyClick.getMap().pushMap("data", itemContent);
        notifyClick.send(view);
    }

    public void notifyItemFocus(int position) {

        if (notifyItemFocus == null) {
            notifyItemFocus = new MyEvent("onItemFocused");
        }
        notifyItemFocus.getMap().pushInt("position", position);
        notifyItemFocus.send(view);
    }

    public void notifyGroupPositionEvent(int position) {

        if (notifyPositionEvent == null) {
            notifyPositionEvent = new MyEvent("onGroupItemFocused");
        }
        notifyPositionEvent.getMap().pushInt("position", position);
        notifyPositionEvent.send(view);
    }

    public void triggerFocusChange(TriggerTaskHost view, boolean hasFocus) {
        TriggerTaskManagerModule.dispatchTriggerTask(view, hasFocus ? "onFocusAcquired" : "onFocusLost");
    }

    public int getTAG() {
        return hashCode();
    }

    final static class MyEvent {
        final String name;
        EsMap map;

        MyEvent(String name) {
            this.name = name;
        }

        public EsMap getMap() {
            if (map == null) {
                map = new EsMap();
            }
            return map;
        }

        void send(View v) {
            EsProxy.get().sendUIEvent(v.getId(), name, map);
        }
    }
}
