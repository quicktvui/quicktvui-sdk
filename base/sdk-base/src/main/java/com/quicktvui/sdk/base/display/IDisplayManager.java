package com.quicktvui.sdk.base.display;

/**
 *
 */
public interface IDisplayManager {

    public float getDensity();

    public float dp2px(float size);

    public float dp2px(double size);

    public float px2dp(float size);

    public float sp2px(float size);

    public float px2sp(float size);
}
