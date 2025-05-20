package com.quicktvui.sdk.core.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

/**
 * 说明
 * <p>
 * Create by weipeng on 2022/08/26 09:54
 */
public class EsProgressLoadingView extends FrameLayout {

    private Animator mAnimator;

    public EsProgressLoadingView(@NonNull Context context) {
        super(context);
    }

    public EsProgressLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EsProgressLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EsProgressLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        setClipChildren(false);
//        post(this::startLoopAnimation);
//    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setClipChildren(false);
        post(this::startLoopAnimation);
    }

    private void startLoopAnimation() {
        View bgView = getChildAt(0);
        View thumbView = getChildAt(1);

        Interpolator interpolator = new AccelerateInterpolator();

        int offset = (int) (thumbView.getWidth() * 0.15F);
        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(thumbView, "x", -offset, bgView.getWidth() - thumbView.getWidth() + offset);
        moveAnimator.setDuration(2000);
        moveAnimator.setInterpolator(interpolator);
        moveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        moveAnimator.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(thumbView, "alpha", 0, 0, 0.5F, 1, 1, 1, 1, 0.5F, 0, 0);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setInterpolator(interpolator);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveAnimator, alphaAnimator);
        mAnimator = animatorSet;
        mAnimator.start();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == View.GONE) {
            if(mAnimator != null){
                mAnimator.cancel();
            }
            mAnimator = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimator != null){
            mAnimator.cancel();
        }
        mAnimator = null;
    }
}
