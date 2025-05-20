//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.quicktvui.support.ui.viewpager.utils;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class TimeInterpolatorUtils {
    public TimeInterpolatorUtils() {
    }

    public static TimeInterpolator getTimeInterpolatorByType(int type) {
        try {
            switch (type) {
                case 2:
                    return new AccelerateInterpolator();//加速
                case 3:
                    return new DecelerateInterpolator();//减速
                case 4:
                    return new AccelerateDecelerateInterpolator();//先加速 后减速
                case 5:
                    return new AnticipateInterpolator();//先反向变化 在正向快速变化
                case 6:
                    return new OvershootInterpolator();//快速变化到超出结束值再缓慢反向变化
                case 7:
                    return new BounceInterpolator();//不断回弹地变化
                default:
                    return new LinearInterpolator();
            }
        } catch (Throwable var2) {
            var2.printStackTrace();
            return new LinearInterpolator();
        }
    }
}
