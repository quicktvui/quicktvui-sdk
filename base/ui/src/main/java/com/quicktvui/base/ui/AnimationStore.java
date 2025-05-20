package com.quicktvui.base.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.quicktvui.base.ui.anim.FastOutLinearInInterpolator;
import com.quicktvui.base.ui.anim.LinearOutSlowInInterpolator;


/**
 * @author zhaopeng
 * @version 1.0
 * @title
 * @description
 * @company
 * @created 16/9/1
 * @changeRecord [修改记录] <br/>
 * 16/9/1 ：created
 */

public class AnimationStore {


    public static final String NAME_TRANSLATION_X = "translationX";

    public static final String NAME_TRANSLATION_Y = "translationY";


    public static final int RISE_DURATION = 400;

    /**一般减速曲线
     * @return
     */
    public static Interpolator defaultDecelerationInterpolator(){

        return new LinearOutSlowInInterpolator();
    }


    /**一般标准曲线
     * @return
     */
    public static Interpolator defaultStandInterpolator(){

        return new AccelerateDecelerateInterpolator();
    }

    /**一般加速曲线
     * @return
     */
    public static Interpolator defaultAccelerationInterpolator(){

        return new FastOutLinearInInterpolator();
    }

    public static Interpolator defaultBounceInterpolator(){


        return new OvershootInterpolator();
    }


    public static Animator defaultShakeEndAnimator(View v, ITVView.TVOrientation orientation){

        return orientation == ITVView.TVOrientation.VERTICAL ? defaultShakeEndVerticalAnimator(v) : defaultShakeEndHorizontalAnimator(v);
    }

  public static Animator defaultShakeEndAnimator(View v, int orientation){

    return orientation == RecyclerView.VERTICAL ? defaultShakeEndVerticalAnimator(v) : defaultShakeEndHorizontalAnimator(v);
  }


//    defaultShakeEndAnimator

    /**纵向抖动动画
     * @param v
     * @return
     */
    public static Animator defaultShakeEndVerticalAnimator(View v){

        AnimatorSet set = new AnimatorSet();

        final String name =  NAME_TRANSLATION_Y;
        Animator toLeft = ObjectAnimator.ofFloat(v,name,0,-50);
        toLeft.setDuration(200);
        toLeft.setInterpolator(defaultDecelerationInterpolator());


        Animator toRight = ObjectAnimator.ofFloat(v,name,0);
        toRight.setInterpolator(defaultStandInterpolator());
        toRight.setDuration(170);

        set.playSequentially(toLeft,toRight);

        return set;
    }

    /**横向抖动动画
     * @param v
     * @return
     */
    public static Animator defaultShakeEndHorizontalAnimator(View v){

        AnimatorSet set = new AnimatorSet();

        final String name =  NAME_TRANSLATION_X;
//
        Animator toLeft = ObjectAnimator.ofFloat(v,name,v.getTranslationX(),-50);
        toLeft.setDuration(200);
        toLeft.setInterpolator(defaultDecelerationInterpolator());


        Animator toRight = ObjectAnimator.ofFloat(v,name,0);
        toRight.setInterpolator(defaultStandInterpolator());
        toRight.setDuration(170);

        set.playSequentially(toLeft,toRight);

        return set;

    }

    public static Animator generateCoverBounceScaleAnimator(View view, float scaleXValue, float scaleYValue, boolean isShake, int duration) {

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleX, scaleY;

        scaleX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), scaleXValue);
        scaleX.setDuration(duration + 200);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            scaleX.setAutoCancel(true);
        }

        scaleY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), scaleYValue);
        scaleY.setDuration(duration + 200);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            scaleY.setAutoCancel(true);
        }

        scaleX.setInterpolator(AnimationStore.defaultDecelerationInterpolator());
        scaleY.setInterpolator(AnimationStore.defaultDecelerationInterpolator());

        if (isShake) {
            set.setInterpolator(FocusBounceInterpolator.getScaleInterpolator());
        }

        set.playTogether(scaleX, scaleY);
        return set;
    }


    public static class FocusBounceInterpolator {

        private static class ScaleInterpolator implements Interpolator {
            float factor = 0.6f;

            public ScaleInterpolator() {
            }

            public ScaleInterpolator(float factor) {
                this.factor = factor;
            }

            @Override
            //返回为float值 也就是实时的值
            public float getInterpolation(float input) {

                return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
            }

            float bounce(float t) {
                return t * t * 8;
            }
        }

        public static ScaleInterpolator getScaleInterpolator() {
            return new ScaleInterpolator();
        }
    }

}
