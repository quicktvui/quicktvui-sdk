package com.quicktvui.sdk.core.display;

import com.tencent.mtt.hippy.utils.PixelUtil;

import com.quicktvui.sdk.base.display.IDisplayManager;

/**
 *
 */
public class ESDisplayManager implements IDisplayManager {

    @Override
    public float getDensity() {
        return PixelUtil.getDensity();
    }

    @Override
    public float dp2px(float size) {
        return PixelUtil.dp2px(size);
    }

    @Override
    public float dp2px(double size) {
        return PixelUtil.dp2px(size);
    }

    @Override
    public float px2dp(float size) {
        return PixelUtil.px2dp(size);
    }

    @Override
    public float sp2px(float size) {
        return PixelUtil.sp2px(size);
    }

    @Override
    public float px2sp(float size) {
        return PixelUtil.px2sp(size);
    }
}
