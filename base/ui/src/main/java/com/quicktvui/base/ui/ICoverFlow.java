package com.quicktvui.base.ui;

/**
 * Create by sorosunrain on 2021/05/14 18:22
 */
public interface ICoverFlow {
    void setAutoScrollInterval(int interval);

    void setZoomInValue(float value);

    int getCurrentIndex();

    void scrollToIndex(int index, int duration);
}
