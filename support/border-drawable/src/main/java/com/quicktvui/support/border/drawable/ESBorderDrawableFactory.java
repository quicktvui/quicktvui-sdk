package com.quicktvui.support.border.drawable;

import com.quicktvui.base.ui.graphic.BaseBorderDrawable;
import com.quicktvui.base.ui.graphic.BaseBorderDrawableProvider;
import com.quicktvui.base.ui.graphic.BorderFrontDrawable;

import java.util.concurrent.ConcurrentHashMap;

public class ESBorderDrawableFactory implements BaseBorderDrawableProvider<ConcurrentHashMap<Integer, BaseBorderDrawable>> {

    public ESBorderDrawableFactory() {
    }

    @Override
    public ConcurrentHashMap<Integer, BaseBorderDrawable> create() {
        ConcurrentHashMap<Integer, BaseBorderDrawable> hashMap = new ConcurrentHashMap<>();
        hashMap.put(0, new BorderFrontDrawable());
        hashMap.put(1, new BlinkFluidBorderDrawable());
        hashMap.put(2, new BlinkBorderDrawable());
        return hashMap;
    }
}
