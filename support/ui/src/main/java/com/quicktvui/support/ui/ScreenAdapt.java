package com.quicktvui.support.ui;

import android.content.Context;

public class ScreenAdapt {

    private static volatile ScreenAdapt instance;

    private float scale = 1.0f;

    private ScreenAdapt() {
    }

    public static ScreenAdapt getInstance() {
        if (instance == null) {
            synchronized (ScreenAdapt.class) {
                if (instance == null) {
                    instance = new ScreenAdapt();
                }
            }
        }

        return instance;
    }

    public void init(Context context) {
        scale = ScreenUtils.getScreenWidth(context) / 1920.0f;
    }

    public int transform(int px) {
        return (int) (px * scale + 0.5f);
    }
}
