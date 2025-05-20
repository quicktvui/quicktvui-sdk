package com.quicktvui.support.swiper;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.component.IEsComponentView;

/**
 *
 */
public class SwiperView extends FrameLayout implements IEsComponentView {

    private final AnimationSet transformSet = new AnimationSet(true);

    public SwiperView(Context context) {
        super(context);
    }

    public void doTransform(float fromX, float toX, float fromY, float toY, float fromAlpha, float toAlpha, long duration) {
        if (transformSet.getAnimations().size() > 0) {
            transformSet.reset();
            transformSet.cancel();
            transformSet.getAnimations().clear();
        }
        AlphaAnimation transformAlpha = new AlphaAnimation(fromAlpha, toAlpha);
        TranslateAnimation transformMove = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromX,
                Animation.RELATIVE_TO_SELF, toX,
                Animation.RELATIVE_TO_SELF, fromY,
                Animation.RELATIVE_TO_SELF, toY
        );
        transformMove.setInterpolator(new DecelerateInterpolator(2));
        transformSet.addAnimation(transformAlpha);
        transformSet.addAnimation(transformMove);
        transformSet.setInterpolator(new DecelerateInterpolator(2));
        transformSet.setDuration(duration);
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            child.startAnimation(transformSet);
        }
    }
}
