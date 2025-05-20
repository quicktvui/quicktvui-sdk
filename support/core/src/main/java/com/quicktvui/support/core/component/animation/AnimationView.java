package com.quicktvui.support.core.component.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;

import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

import com.sunrain.toolkit.utils.log.L;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;

public class AnimationView extends FrameLayout implements IEsComponentView {

    private static final String TAG = "AndroidAnimationView";

    protected static final String EVENT_PROP_ANIMATION_ID = "animationId";
    protected static final String EVENT_PROP_IS_REVERSE = "isReverse";
    protected static final String EVENT_PROP_ANIMATED_VALUE = "animatedValue";


    protected Map<String, Animator> animatorMap =
            Collections.synchronizedMap(new HashMap<>());

    protected Map<String, AnimatorSet.Builder> animatorBuilderMap =
            Collections.synchronizedMap(new HashMap<>());

    public enum Events {
        EVENT_ON_ANIMATION_CANCEL("onAnimationCancel"),
        EVENT_ON_ANIMATION_END("onAnimationEnd"),
        EVENT_ON_ANIMATION_REPEAT("onAnimationRepeat"),
        EVENT_ON_ANIMATION_START("onAnimationStart"),
        EVENT_ON_ANIMATION_PAUSE("onAnimationPause"),
        EVENT_ON_ANIMATION_RESUME("onAnimationResume"),
        EVENT_ON_ANIMATION_UPDATE("onAnimationUpdate");
        //
        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }


    public AnimationView(Context context) {
        super(context);
        this.init();
    }

    private void init() {
    }


    public void resetAnimators() {
        if (L.DEBUG) {
            L.logD("#--------resetAnimators------->>");
        }
        try {
            if (animatorBuilderMap != null) {
                animatorBuilderMap.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (animatorMap != null) {
                for (String key : animatorMap.keySet()) {
                    try {
                        Animator animator = animatorMap.get(key);
                        if(animator != null) {
                            animator.cancel();
                            animator.removeAllListeners();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                animatorMap.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------
    public void startAnimator(String animatorId) {
        Animator animator = getAnimatorById(animatorId);
        if (L.DEBUG) {
            L.logD(animatorId + "#--------startAnimator------->>" + animator);
        }
        if (animator != null) {
            animator.start();
        }
    }

    public void startAnimatorDelay(String animatorId, long delay) {
        Animator animator = getAnimatorById(animatorId);
        if (L.DEBUG) {
            L.logD(animatorId + "#--------startAnimatorDelay------->>" + animator + "----->>>" + delay);
        }
        if (animator != null) {
            animator.setStartDelay(delay);
            animator.start();
        }
    }

    public void reverseAnimator(String animatorId) {
        Animator animator = getAnimatorById(animatorId);
        if (L.DEBUG) {
            L.logD(animatorId + "#--------reverseAnimator------->>" + animator);
        }
        //
        if (animator instanceof ObjectAnimator) {
            ((ObjectAnimator) animator).reverse();
        }
        //
        else if (animator instanceof AnimatorSet) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((AnimatorSet) animator).reverse();
            }
        }
    }

    public void cancelAnimator(String animatorId) {
        Animator animator = getAnimatorById(animatorId);
        if (L.DEBUG) {
            L.logD(animatorId + "#--------cancelAnimator------->>" + animator);
        }
        if (animator != null) {
            animator.cancel();
            animator.removeAllListeners();
            removeAnimatorById(animatorId);
        }
    }

    public void resumeAnimator(String animatorId) {
        Animator animator = getAnimatorById(animatorId);
        if (L.DEBUG) {
            L.logD(animatorId + "#--------resumeAnimator------->>" + animator);
        }
        if (animator != null
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.resume();
        }
    }

    public void pauseAnimator(String animatorId) {
        Animator animator = getAnimatorById(animatorId);
        if (L.DEBUG) {
            L.logD(animatorId + "#--------pauseAnimator------->>" + animator);
        }
        if (animator != null
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.pause();
        }
    }


    //------------------------AnimatorSet--------------------------
    public void animatorSet(String animatorId, int duration, boolean listenAnimator) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (duration >= 0) {
            animatorSet.setDuration(duration);
        }
        if (listenAnimator) {
            animatorSet.addListener(new AnimationViewListener(animatorId));
        }
        putAnimatorById(animatorId, animatorSet);
    }

    public void play(String animatorSetId, String animatorId) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator = getAnimatorById(animatorId);
        if (animatorSet instanceof AnimatorSet) {
            AnimatorSet.Builder builder = ((AnimatorSet) animatorSet).play(animator);
            //
            putAnimatorBuilderById(animatorSetId, builder);
        }
    }

    public void with(String animatorSetId, String animatorId) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator = getAnimatorById(animatorId);
        if (animatorSet instanceof AnimatorSet
                && animator != null) {
            AnimatorSet.Builder builder = getAnimatorBuilderById(animatorSetId);
            if (builder != null) {
                builder.with(animator);
            }
        }
    }

    public void before(String animatorSetId, String animatorId) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator = getAnimatorById(animatorId);
        if (animatorSet instanceof AnimatorSet
                && animator != null) {
            AnimatorSet.Builder builder = getAnimatorBuilderById(animatorSetId);
            if (builder != null) {
                builder.before(animator);
            }
        }
    }

    public void after(String animatorSetId, String animatorId) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator = getAnimatorById(animatorId);
        if (animatorSet instanceof AnimatorSet
                && animator != null) {
            AnimatorSet.Builder builder = getAnimatorBuilderById(animatorSetId);
            if (builder != null) {
                builder.after(animator);
            }
        }
    }

    public void afterDelay(String animatorSetId, long delay) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        if (animatorSet instanceof AnimatorSet) {
            AnimatorSet.Builder builder = getAnimatorBuilderById(animatorSetId);
            if (builder != null) {
                builder.after(delay);
            }
        }
    }

    //---------------------------------playSequentially--------------------------------------
    public void playSequentially(String animatorSetId,
                                 String animatorId1) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null) {
            ((AnimatorSet) animatorSet).playSequentially(
                    animator1
            );
        }
    }

    public void playSequentially(String animatorSetId,
                                 String animatorId1,
                                 String animatorId2) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null) {
            ((AnimatorSet) animatorSet).playSequentially(
                    animator1,
                    animator2
            );
        }
    }

    public void playSequentially(String animatorSetId,
                                 String animatorId1,
                                 String animatorId2,
                                 String animatorId3) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        Animator animator3 = getAnimatorById(animatorId3);

        if (L.DEBUG) {
            L.logD(animatorSetId + "#--------playSequentially------->>"
                    + animatorSet + "----->>>" + animatorSetId + "\n"
                    + animator1 + "----->>>" + animatorId1 + "\n"
                    + animator2 + "----->>>" + animatorId2 + "\n"
                    + animator3 + "----->>>" + animatorId3 + "\n"
            );
        }

        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null
                && animator3 != null) {
            ((AnimatorSet) animatorSet).playSequentially(
                    animator1,
                    animator2,
                    animator3
            );
        }
    }

    public void playSequentially(String animatorSetId,
                                 String animatorId1,
                                 String animatorId2,
                                 String animatorId3,
                                 String animatorId4) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        Animator animator3 = getAnimatorById(animatorId3);
        Animator animator4 = getAnimatorById(animatorId4);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null
                && animator3 != null
                && animator4 != null) {
            ((AnimatorSet) animatorSet).playSequentially(
                    animator1,
                    animator2,
                    animator3,
                    animator4
            );
        }
    }

    public void playSequentially(String animatorSetId,
                                 String animatorId1,
                                 String animatorId2,
                                 String animatorId3,
                                 String animatorId4,
                                 String animatorId5) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        Animator animator3 = getAnimatorById(animatorId3);
        Animator animator4 = getAnimatorById(animatorId4);
        Animator animator5 = getAnimatorById(animatorId5);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null
                && animator3 != null
                && animator4 != null
                && animator5 != null) {
            ((AnimatorSet) animatorSet).playSequentially(
                    animator1,
                    animator2,
                    animator3,
                    animator4,
                    animator5
            );
        }
    }

    //---------------------------------------------------
    public void playTogether(String animatorSetId,
                             String animatorId1) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null) {
            ((AnimatorSet) animatorSet).playTogether(
                    animator1
            );
        }
    }

    public void playTogether(String animatorSetId,
                             String animatorId1,
                             String animatorId2) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null) {
            ((AnimatorSet) animatorSet).playTogether(
                    animator1,
                    animator2
            );
        }
    }

    public void playTogether(String animatorSetId,
                             String animatorId1,
                             String animatorId2,
                             String animatorId3) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        Animator animator3 = getAnimatorById(animatorId3);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null
                && animator3 != null) {
            ((AnimatorSet) animatorSet).playTogether(
                    animator1,
                    animator2,
                    animator3
            );
        }
    }

    public void playTogether(String animatorSetId,
                             String animatorId1,
                             String animatorId2,
                             String animatorId3,
                             String animatorId4) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        Animator animator3 = getAnimatorById(animatorId3);
        Animator animator4 = getAnimatorById(animatorId4);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null
                && animator3 != null
                && animator4 != null) {
            ((AnimatorSet) animatorSet).playTogether(
                    animator1,
                    animator2,
                    animator3,
                    animator4
            );
        }
    }

    public void playTogether(String animatorSetId,
                             String animatorId1,
                             String animatorId2,
                             String animatorId3,
                             String animatorId4,
                             String animatorId5) {
        Animator animatorSet = getAnimatorById(animatorSetId);
        Animator animator1 = getAnimatorById(animatorId1);
        Animator animator2 = getAnimatorById(animatorId2);
        Animator animator3 = getAnimatorById(animatorId3);
        Animator animator4 = getAnimatorById(animatorId4);
        Animator animator5 = getAnimatorById(animatorId5);
        if (animatorSet instanceof AnimatorSet
                && animator1 != null
                && animator2 != null
                && animator3 != null
                && animator4 != null
                && animator5 != null) {
            ((AnimatorSet) animatorSet).playTogether(
                    animator1,
                    animator2,
                    animator3,
                    animator4,
                    animator5
            );
        }
    }

    //------------------------ofFloat--------------------------
    public void ofFloat(String id, String propertyName, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                        EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        objectAnimator.setDuration(duration);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat1(String id, String propertyName, float value1, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }

        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat2(String id, String propertyName, float value1, float value2, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat3(String id, String propertyName, float value1, float value2, float value3, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat4(String id, String propertyName, float value1, float value2, float value3, float value4, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3, value4);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat5(String id, String propertyName, float value1, float value2, float value3, float value4, float value5, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3, value4, value5);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat6(String id, String propertyName, float value1, float value2, float value3, float value4, float value5, float value6, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3, value4, value5, value6);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat7(String id, String propertyName, float value1, float value2, float value3, float value4, float value5, float value6, float value7, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3, value4, value5, value6, value7);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat8(String id, String propertyName, float value1, float value2, float value3, float value4, float value5, float value6, float value7, float value8, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3, value4, value5, value6, value7, value8);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat9(String id, String propertyName, float value1, float value2, float value3, float value4, float value5, float value6, float value7, float value8, float value9, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                         EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3, value4, value5, value6, value7, value8, value9);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofFloat10(String id, String propertyName, float value1, float value2, float value3, float value4, float value5, float value6, float value7, float value8, float value9, float value10, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                          EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, value1, value2, value3, value4, value5, value6, value7, value8, value9, value10);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }


    //-----------------------ofInt---------------------------
    public void ofInt(String id, String propertyName, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                      EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt1(String id, String propertyName, int value1, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt2(String id, String propertyName, int value1, int value2, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt3(String id, String propertyName, int value1, int value2, int value3, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt4(String id, String propertyName, int value1, int value2, int value3, int value4, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3, value4);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt5(String id, String propertyName, int value1, int value2, int value3, int value4, int value5, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3, value4, value5);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt6(String id, String propertyName, int value1, int value2, int value3, int value4, int value5, int value6, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3, value4, value5, value6);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt7(String id, String propertyName, int value1, int value2, int value3, int value4, int value5, int value6, int value7, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3, value4, value5, value6, value7);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt8(String id, String propertyName, int value1, int value2, int value3, int value4, int value5, int value6, int value7, int value8, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3, value4, value5, value6, value7, value8);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt9(String id, String propertyName, int value1, int value2, int value3, int value4, int value5, int value6, int value7, int value8, int value9, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                       EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3, value4, value5, value6, value7, value8, value9);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public void ofInt10(String id, String propertyName, int value1, int value2, int value3, int value4, int value5, int value6, int value7, int value8, int value9, int value10, int duration, int repeatMode, int repeatCount, boolean listenAnimator, boolean listenAnimatorValue,
                        EsMap interpolatorMap) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, propertyName, value1, value2, value3, value4, value5, value6, value7, value8, value9, value10);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatMode(repeatMode);
        objectAnimator.setRepeatCount(repeatCount);
        if (listenAnimator) {
            objectAnimator.addListener(new AnimationViewListener(id));
        }
        if (listenAnimatorValue) {
            objectAnimator.addUpdateListener(new AnimatorViewUpdateListener(id));
        }
        try {
            TimeInterpolator interpolator = getTimeInterpolatorByType(interpolatorMap);
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        putAnimatorById(id, objectAnimator);
    }

    public Animator getAnimatorById(String id) {
        if (TextUtils.isEmpty(id) || animatorMap == null
                || animatorMap.isEmpty()) {
            if (L.DEBUG) {
                L.logD("#---getAnimatorById---空--->>>" +
                        "id:" + id + "----->>>" +
                        "animatorMap:" + animatorMap
                );
            }
            return null;
        }
        Animator animator = animatorMap.get(id);
        if (L.DEBUG) {
            L.logD("#---getAnimatorById------>>>" +
                    "id:" + id + "----->>>" +
                    "animator:" + animator
            );
        }
        return animator;
    }

    public boolean putAnimatorById(String id, Animator animator) {
        if (animator == null || animatorMap == null) {
            return false;
        }

        if (L.DEBUG) {
            L.logD("#---putAnimatorById------>>>" +
                    "id:" + id + "----->>>" +
                    "animator:" + animator
            );
        }

        animatorMap.put(id, animator);
        return true;
    }

    public boolean removeAnimatorById(String id) {
        if (TextUtils.isEmpty(id) || animatorMap == null
                || animatorMap.isEmpty()) {
            return false;
        }
        animatorMap.remove(id);
        return true;
    }

    public AnimatorSet.Builder getAnimatorBuilderById(String id) {
        if (TextUtils.isEmpty(id) || animatorBuilderMap == null
                || animatorBuilderMap.isEmpty()) {
            return null;
        }
        AnimatorSet.Builder animatorBuilder = animatorBuilderMap.get(id);
        return animatorBuilder;
    }

    public boolean putAnimatorBuilderById(String id, AnimatorSet.Builder animatorBuilder) {
        if (animatorBuilder == null || animatorBuilderMap == null) {
            return false;
        }
        animatorBuilderMap.put(id, animatorBuilder);
        return true;
    }

    public boolean removeAnimatorBuilderById(String id) {
        if (TextUtils.isEmpty(id) || animatorBuilderMap == null
                || animatorBuilderMap.isEmpty()) {
            return false;
        }
        animatorBuilderMap.remove(id);
        return true;
    }

    class AnimatorViewUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private String animationId;

        AnimatorViewUpdateListener(String id) {
            this.animationId = id;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            Object value = valueAnimator.getAnimatedValue();

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationUpdate------->>" + value);
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            eventMap.pushObject(EVENT_PROP_ANIMATED_VALUE, value);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_UPDATE.toString(), eventMap);
        }
    }


    class AnimationViewListener extends AnimatorListenerAdapter {

        private String animationId;

        AnimationViewListener(String id) {
            this.animationId = id;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationCancel------->>");
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_CANCEL.toString(), eventMap);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationEnd------->>");
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_END.toString(), eventMap);
        }

        @Override
        public void onAnimationEnd(Animator animation, boolean isReverse) {
            super.onAnimationEnd(animation, isReverse);

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationEnd------->>" + isReverse);
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            eventMap.pushBoolean(EVENT_PROP_IS_REVERSE, isReverse);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_END.toString(), eventMap);
        }

        @Override
        public void onAnimationPause(Animator animation) {
            super.onAnimationPause(animation);

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationPause------->>");
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_PAUSE.toString(), eventMap);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            super.onAnimationRepeat(animation);

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationRepeat------->>");
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_REPEAT.toString(), eventMap);
        }

        @Override
        public void onAnimationResume(Animator animation) {
            super.onAnimationResume(animation);

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationResume------->>");
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_RESUME.toString(), eventMap);
        }

        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationStart------->>");
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_START.toString(), eventMap);
        }

        @Override
        public void onAnimationStart(Animator animation, boolean isReverse) {

            if (L.DEBUG) {
                L.logD(this.animationId + "#--------onAnimationStart------->>" + isReverse);
            }

            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_ANIMATION_ID, animationId);
            eventMap.pushBoolean(EVENT_PROP_IS_REVERSE, isReverse);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_ANIMATION_START.toString(), eventMap);
        }
    }

    private TimeInterpolator getTimeInterpolatorByType(EsMap esMap) {
        try {
            if (esMap == null) {
                return null;
            }
            int type = esMap.getInt("type");
            EsArray paramsArray = esMap.getArray("params");
            switch (type) {
                case 1:
                    return new AccelerateDecelerateInterpolator();
                //-------------------------------------------------
                case 2:
                    if (paramsArray != null && paramsArray.size() > 0) {
                        double factor = paramsArray.getDouble(0);
                        return new AccelerateInterpolator((float) factor);
                    } else {
                        return new AccelerateInterpolator();
                    }
                    //-------------------------------------------------
                case 3:
                    if (paramsArray != null && paramsArray.size() > 0) {
                        double tension = paramsArray.getDouble(0);
                        return new AnticipateInterpolator((float) tension);
                    } else {
                        return new AnticipateInterpolator();
                    }
                case 4:
                    if (paramsArray != null && paramsArray.size() >= 2) {
                        double tension = paramsArray.getDouble(0);
                        double extraTension = paramsArray.getDouble(1);
                        return new AnticipateOvershootInterpolator((float) tension, (float) extraTension);
                    }
                    //
                    else if (paramsArray != null && paramsArray.size() >= 1) {
                        double tension = paramsArray.getDouble(0);
                        return new AnticipateOvershootInterpolator((float) tension);
                    }
                    //
                    else {
                        return new AnticipateOvershootInterpolator();
                    }
                    //-------------------------------------------------
                case 5:
                    return new BounceInterpolator();
                //-------------------------------------------------
                case 6:
                    double value = paramsArray.getDouble(0);
                    return new CycleInterpolator((float) value);
                //-------------------------------------------------
                case 7:
                    if (paramsArray != null && paramsArray.size() > 0) {
                        double value12 = paramsArray.getDouble(0);
                        return new DecelerateInterpolator((float) value12);
                    } else {
                        return new DecelerateInterpolator();
                    }
                    //-------------------------------------------------
                case 8:
                    return new LinearInterpolator();
                //-------------------------------------------------
                case 9:
                    if (paramsArray != null && paramsArray.size() > 0) {
                        double tension = paramsArray.getDouble(0);
                        return new OvershootInterpolator((float) tension);
                    } else {
                        return new OvershootInterpolator();
                    }
                    //-------------------------------------------------
                case 10:
                    return new FastOutLinearInInterpolator();
                case 11:
                    return new FastOutSlowInInterpolator();
                case 12:
                    if (paramsArray != null && paramsArray.size() >= 4) {
                        double value20 = paramsArray.getDouble(0);
                        double value21 = paramsArray.getDouble(1);
                        double value22 = paramsArray.getDouble(2);
                        double value23 = paramsArray.getDouble(3);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            return new PathInterpolator(
                                    (float) value20,
                                    (float) value21,
                                    (float) value22,
                                    (float) value23);
                        }

                    } else if (paramsArray != null && paramsArray.size() >= 2) {
                        double value18 = paramsArray.getDouble(0);
                        double value19 = paramsArray.getDouble(1);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            return new PathInterpolator((float) value18, (float) value19);
                        }
                    }
                default:
                    return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (L.DEBUG) {
            L.logD("#--------onDetachedFromWindow----resetAnimators--->>");
        }
        resetAnimators();
    }
}
