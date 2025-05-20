package com.quicktvui.support.ui.item.view.shimmer

import java.util.ArrayList

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View


class ColorFocusShimmer
private constructor(override var parentView: View, builder: AbsFocusShimmer.Builder
) : AbsFocusShimmer(parentView,builder) {
    // 0为矩形
    private var mRoundRadius = 0f


    // 绘制矩形区域的动画
    private var mRoundRadiusAnimator: ValueAnimator? = null

    // 设置圆角，0为无圆角因此为矩形，为什么是矩形看抽象类AbsFocusBorder中定义了RectF类
    protected fun setRoundRadius(roundRadius: Float) {
        if (mRoundRadius != roundRadius) {
            mRoundRadius = roundRadius
            invalidateSelf()
        }
    }

    // 获取圆角
    override fun getRoundRadius(): Float {
        return mRoundRadius
    }

    // 获取动画执行的对象
    override fun getTogetherAnimators(
        newX: Float, newY: Float, newWidth: Int, newHeight: Int,
        options: Options
    ): List<Animator>? {
        if (options is Options) {
            val animators = ArrayList<Animator>()
            animators.add(getRoundRadiusAnimator(options.roundRadius)!!)
            return animators
        }
        return null
    }

    override fun getSequentiallyAnimators(
        newX: Float, newY: Float, newWidth: Int, newHeight: Int,
        options: Options
    ): List<Animator>? {
        return null
    }

    // 获取圆角动画的绘制对象
    private fun getRoundRadiusAnimator(roundRadius: Float): ValueAnimator? {
        if (null == mRoundRadiusAnimator) {
            mRoundRadiusAnimator =
                ValueAnimator.ofFloat( getRoundRadius(), roundRadius)
            mRoundRadiusAnimator?.addUpdateListener {
                mRoundRadius = it.animatedValue as Float
                invalidateSelf()
            }
        } else {
            mRoundRadiusAnimator!!.setFloatValues(getRoundRadius(), roundRadius)
        }
        return mRoundRadiusAnimator
    }


    class Builder : AbsFocusShimmer.Builder() {
        // 设置流光的主基色
        override fun shimmerColor(color: Int): AbsFocusShimmer.Builder {
            return super.shimmerColor(color)
        }

        override fun setOption(options: FocusShimmer.Options): AbsFocusShimmer.Builder {
            return super.setOption(options)
        }

        // 构造函数
        override fun build(parent: View): FocusShimmer {
            if (null == parent) {
                throw NullPointerException("The tvkit.item.view.shimmer.FocusShimmer parent cannot be null")
            }
            // 实例话ColorFocusBorder类对象
            val borderView = ColorFocusShimmer(
                parent, this
            )
            return borderView
        }
    }
}
