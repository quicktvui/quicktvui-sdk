package com.quicktvui.support.ui.viewpager.tabs;

import android.animation.Animator;

public class AnimatorLister implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {
        this.onAnimationStart(animation);
    }

    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {
        this.onAnimationEnd(animation);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
