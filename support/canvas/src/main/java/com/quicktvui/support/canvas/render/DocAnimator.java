/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.render;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class DocAnimator {
    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_PAGE_OPEN_ENTER = 1;
    public static final int TYPE_PAGE_OPEN_EXIT = 2;
    public static final int TYPE_PAGE_CLOSE_ENTER = 3;
    public static final int TYPE_PAGE_CLOSE_EXIT = 4;
    private static final String TAG = "DocAnimator";
    private static final int ANIM_DURATION = 300;

    /**
     * android.view.RenderNodeAnimator
     */
    private static final int TRANSLATION_X = 0;

    private static final int ALPHA = 11;

    private int mRootViewWidth;
    private int mAnimType;

    private Animator.AnimatorListener mListener;
    private View mTargetView;

    private ArrayList<PropertyValueHolder> mPropHolderList = new ArrayList<>();

    public DocAnimator(Context context, View targetView, int animType) {
        ViewGroup parent = (ViewGroup) targetView.getParent();
        mRootViewWidth = parent.getWidth();
        mTargetView = targetView;
        mAnimType = animType;
        initAnimatorProperty();
    }

    private void initAnimatorProperty() {
        PropertyValueHolder translationXHolder = null;
        PropertyValueHolder alphaHolder = null;
        switch (mAnimType) {
            case TYPE_PAGE_OPEN_ENTER:
            case TYPE_PAGE_CLOSE_ENTER:
                translationXHolder = new PropertyValueHolder(TRANSLATION_X, 0);
                alphaHolder = new PropertyValueHolder(ALPHA, 1);
                break;
            case TYPE_PAGE_OPEN_EXIT:
                translationXHolder =
                        new PropertyValueHolder(TRANSLATION_X, -mRootViewWidth * 0.25f);
                alphaHolder = new PropertyValueHolder(ALPHA, 0.6f);
                break;
            case TYPE_PAGE_CLOSE_EXIT:
                translationXHolder = new PropertyValueHolder(TRANSLATION_X, mRootViewWidth);
                alphaHolder = new PropertyValueHolder(ALPHA, 0.6f);
                break;
            default:
                break;
        }
        mPropHolderList.add(alphaHolder);
        mPropHolderList.add(translationXHolder);

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                //when OS version behind Android O, should set View's mTransformationInfo field
                // with a new instance
                Field mTransformationInfo = View.class.getDeclaredField("mTransformationInfo");
                mTransformationInfo.setAccessible(true);
                Class<?> transClz = Class.forName("android.view.View$TransformationInfo");
                Constructor<?> constructor = transClz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object transObj = constructor.newInstance();
                mTransformationInfo.set(mTargetView, transObj);
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "renderNodeAnimator not find method", e);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "renderNodeAnimator invoke field failed", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "TransformationInfo class not found", e);
        } catch (InstantiationException e) {
            Log.e(TAG, "TransformationInfo invoke failed", e);
        } catch (Exception e) {//IllegalAccessException | InvocationTargetException 安卓sdk>19才有 用exception捕获异常
            Log.e(TAG, "TransformationInfo invoke failed", e);
        }
        if (mAnimType == TYPE_PAGE_OPEN_ENTER) {
            mTargetView.setTranslationX(mRootViewWidth);
        } else if (mAnimType == TYPE_PAGE_CLOSE_ENTER) {
            mTargetView.setTranslationX(-mRootViewWidth * 0.25f);
            mTargetView.setAlpha(0.6f);
        }
    }

    public void start() {
        for (int i = 0; i < mPropHolderList.size(); i++) {
            PropertyValueHolder holder = mPropHolderList.get(i);
            Animator animator = createAnimator(holder.property, holder.value);
            if (animator != null) {
                setAnimatorTarget(animator);
                if (i == 0) {
                    // Only need to be added once
                    animator.addListener(mListener);
                }
                animator.setDuration(ANIM_DURATION);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            }
        }
        mPropHolderList.clear();
    }

    private Animator createAnimator(int renderProperty, float finalValue) {
        try {
            Class<?> clz = Class.forName("android.view.RenderNodeAnimator");
            Constructor<?> constructor = clz.getConstructor(int.class, float.class);
            return (Animator) constructor.newInstance(renderProperty, finalValue);
        } catch (Exception e) {
            Log.e(TAG, "reflect renderNodeAnimator failed", e);
        }
        return null;
    }

    private void setAnimatorTarget(Animator animator) {
        try {
            Class<?> clz = animator.getClass();
            Method setTargetMethod = clz.getDeclaredMethod("setTarget", View.class);
            setTargetMethod.invoke(animator, mTargetView);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "renderNodeAnimator not find method", e);
        } catch (Exception e) {//IllegalAccessException | InvocationTargetException 安卓sdk>19才有 用exception捕获异常
            Log.e(TAG, "TransformationInfo invoke failed", e);
        }
    }

    public void setListener(Animator.AnimatorListener listener) {
        mListener = listener;
    }

    public void setAnimType(int animType) {
        mAnimType = animType;
    }

    private static class PropertyValueHolder {
        int property;
        float value;

        PropertyValueHolder(int property, float value) {
            this.property = property;
            this.value = value;
        }
    }
}
