package com.quicktvui.base.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


/**
 * @author zhaopeng
 * @version 1.0
 * @title TVFocusAnimHelper
 * @description 焦点放大缩小等行为的实施类。包括放大倍数，放大的时长，放大动画。
 *
 * @company
 * @changeRecord [修改记录] <br/>
 */
public class TVFocusAnimHelper implements View.OnFocusChangeListener {

    public final static String TAG = "TVFocusAnimHelper";

    public static int clampMaxWidth = -1;

    /**
     * 所有view放大和缩小的时长
     * **/
    public  static int DEFAULT_DURATION = 400;
    public  static boolean DISABLE_SCALE = false;
    /**
     * 所有view放大的默认倍数
     * **/
    public static float DEFAULT_SCALE = 1f;
    public static float DEFUALT_FRACTOR = 1.5f;

    public  int duration = DEFAULT_DURATION;
    public  float scaleX = DEFAULT_SCALE;
    public  float scaleY = DEFAULT_SCALE;

    View.OnFocusChangeListener mOnFocusChangeListener;

    public TVFocusAnimHelper(View.OnFocusChangeListener onFocusChangeListener, int duration, float scaleX, float scaleY) {
        mOnFocusChangeListener = onFocusChangeListener;
        this.duration = duration;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public TVFocusAnimHelper(View.OnFocusChangeListener onFocusChangeListener, int duration, float scale) {
        this(onFocusChangeListener,duration,scale,scale);
    }

    public TVFocusAnimHelper(){

    }

    public TVFocusAnimHelper(View.OnFocusChangeListener listener){
        this.mOnFocusChangeListener = listener;
    }

    public TVFocusAnimHelper(float scale){
        this.scaleX = scale;
        this.scaleY = scale;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        handleOnFocusChange(v,hasFocus,scaleX,scaleY,duration);
        if(mOnFocusChangeListener != null){
            mOnFocusChangeListener.onFocusChange(v,hasFocus);
        }
    }

    public static void changeFocusScaleDirectly(View v, float scaleX, float scaleY){
      cancelPrevAnimator(v);
      v.setScaleX(scaleX);
      v.setScaleY(scaleY);
    }



    public static void handleOnFocusChange(View v, boolean hasFocus, float scaleX, float scaleY, int duration){

        if(DISABLE_SCALE){
          return;
        }
        if(duration <= 0){
          cancelPrevAnimator(v);
          if(hasFocus) {
            v.setScaleX(clampScaleValue(v,scaleX));
            v.setScaleY(clampScaleValue(v,scaleY));
          }else{
            v.setScaleX(1);
            v.setScaleY(1);
          }
          v.invalidate();
          return;
        }

        if (hasFocus) {
            cancelPrevAnimator(v);
            final Animator a = bounceScaleTo(v, clampScaleValue(v,scaleX), clampScaleValue(v,scaleY), duration);
            markAnimTag(v,a);
        } else {
            cancelPrevAnimator(v);
            final Animator a = scaleTo(v, 1f, 1f, (int) (duration * 0.6f));
            markAnimTag(v,a);
        }
    }

    private static float clampScaleValue(View v,float scale){
      if(v.getWidth() > 0 && clampMaxWidth > 0 && v.getWidth() * scale > clampMaxWidth){
        return (float)clampMaxWidth / v.getWidth();
      }else{
        return scale;
      }
    }

  public static void handleOnFocusChange(View v, boolean hasFocus, float scaleX, float scaleY, int duration,int tagID){

    if (hasFocus) {
      cancelPrevAnimator(v,tagID);
      final Animator a = bounceScaleTo(v, scaleX, scaleY, duration);
      markAnimTag(v,a,tagID);
    } else {
      cancelPrevAnimator(v,tagID);
      final Animator a = scaleTo(v, 1f, 1f, (int) (duration * 0.6f));
      markAnimTag(v,a,tagID);
    }
  }


    private static void cancelPrevAnimator(View v){
        final Object tag = v.getTag(R.id.tag_focus_scale_animation);
        if(tag instanceof Animator){
            final Animator oldAnim = (Animator) tag;
            oldAnim.cancel();
        }
    }

    private static void markAnimTag(final View v, Animator a){
        if(v != null && a != null) {
            v.setTag(R.id.tag_focus_scale_animation, a);
            a.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    v.setTag(R.id.tag_focus_scale_animation, null);
                }
            });
        }

    }

  private static void cancelPrevAnimator(View v,int tagID){
    final Object tag = v.getTag(tagID);
    if(tag instanceof Animator){
      final Animator oldAnim = (Animator) tag;
      oldAnim.cancel();
      v.postInvalidateDelayed(16);
    }
  }

  private static void markAnimTag(final View v, Animator a, final int tagID){
    if(v != null && a != null) {
      v.setTag(tagID, a);
      a.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          v.setTag(tagID, null);
          v.postInvalidateDelayed(16);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
          super.onAnimationCancel(animation);
          v.setTag(tagID, null);
        }
      });
    }

  }

    public void setFocusScale(float scale){
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public void setFocusScaleX(float sx){
        this.scaleX = sx;
    }

    public void setFocusScaleY(float sy){
        this.scaleY = sy;
    }


    /**让一个view执行缩放动画
     * @param v 执行的view
     * @param scaleX
     * @param scaleY
     * @param duration 缩放进行的时长,单位ms
     */
    public static Animator scaleTo(View v , float scaleX, float scaleY, int duration){

        final float fractor = DEFUALT_FRACTOR;

        ObjectAnimator sx = ObjectAnimator.ofFloat(v,"scaleX",v.getScaleX(),scaleX);
        sx.setInterpolator(new DecelerateInterpolator(fractor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sx.setAutoCancel(true);
        }
        sx.setDuration(duration);

        ObjectAnimator sy = ObjectAnimator.ofFloat(v,"scaleY",v.getScaleY(),scaleY);
        sy.setInterpolator(new DecelerateInterpolator(fractor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sy.setAutoCancel(true);
        }
        sy.setDuration(duration);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(sx,sy);

        set.start();


        return set;
    }

    /**让一个view执行缩放动画
     * @param v 执行的view
     * @param scaleX
     * @param scaleY
     * @param duration 缩放进行的时长,单位ms
     */
    public static Animator bounceScaleTo(View v , float scaleX, float scaleY, int duration){

        Animator animator = AnimationStore.generateCoverBounceScaleAnimator(v,scaleX,scaleY,scaleX != 1 || scaleY != 1,duration);

        animator.start();

        return animator;
    }

    /**让一个view执行缩放动画
     * @param v 执行的view
     * @param duration 缩放进行的时长,单位ms
     */
    public static void scaleTo(View v , float scale, int duration){
        scaleTo(v,scale,scale,duration);

    }


    public void setScaleDuration(int duration) {
        this.duration = duration;
    }

}
