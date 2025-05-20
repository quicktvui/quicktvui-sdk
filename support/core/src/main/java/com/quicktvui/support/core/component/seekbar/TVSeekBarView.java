package com.quicktvui.support.core.component.seekbar;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.SeekBar;

import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.component.IEsComponentView;

public class TVSeekBarView extends SeekBar
        implements IEsComponentView {

    public TVSeekBarView(Context context) {
        super(context);
    }

    boolean listenProgressEvent = true;
    boolean interceptKeyEvent = false;

    public void setInterceptKeyEvent(boolean interceptKeyEvent) {
        this.interceptKeyEvent = interceptKeyEvent;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        postInvalidateDelayed(16);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (L.DEBUG) {
            L.logD("#------dispatchKeyEvent------>>>interceptKeyEvent:" + interceptKeyEvent);
        }
        return (interceptKeyEvent && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT)
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)
                || super.dispatchKeyEvent(event);
    }

    public void setListenProgressEvent(boolean listen) {
        this.listenProgressEvent = listen;
    }

    public boolean isListenProgressEvent() {
        return listenProgressEvent;
    }
}
