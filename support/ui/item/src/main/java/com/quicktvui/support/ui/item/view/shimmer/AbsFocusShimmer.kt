package com.quicktvui.support.ui.item.view.shimmer

import android.animation.*
import java.lang.ref.WeakReference
import java.util.ArrayList

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AccelerateInterpolator
import kotlin.math.abs


abstract class AbsFocusShimmer protected constructor(override var parentView : View, val builder: Builder
) : Drawable(), FocusShimmer {

    private var mFrameRectF = RectF(builder.offsetX,builder.offsetY,0f,0f)

    private var mPaddingRectF = RectF()

    private var mPaddingOffsetRectF = RectF()
    private var mTempRectF = RectF()
    private var mShimmerLinearGradient: LinearGradient? = null

    private lateinit var mShimmerGradientMatrix: Matrix

    private val mShimmerPaint: Paint
    private var visibility :Int = 0

    private var shimmerTranslate = 0f
        set(shimmerTranslate) {
//            Log.d(TAG,"onAnimation==$shimmerTranslate ==>mIsShimmerAnim=$mIsShimmerAnim ==> Translate this===${this.shimmerTranslate} shimmerTranslate=$shimmerTranslate")
            if (builder.mIsShimmerAnim && this.shimmerTranslate != shimmerTranslate) {
                field = shimmerTranslate
                //如果hostview是ViewGroup需要手动调取刷新才能触发子view的刷新
                parentView?.invalidate()
//                invalidateSelf()
            }
        }

    private var mShimmerAnimating = false



    private var mTranslationXAnimator: ValueAnimator? = null

    private var mTranslationYAnimator: ValueAnimator? = null

    private var mWidthAnimator: ValueAnimator? = null

    private var mHeightAnimator: ValueAnimator? = null

    private var mShimmerAnimator: ValueAnimator? = null

    private var mAnimatorSet: AnimatorSet? = null

    private var mOldFocusView: WeakReference<View>? = null



    private val shimmerAnimator: ValueAnimator
        get() {
            if (null == mShimmerAnimator) {
                mShimmerAnimator = ValueAnimator.ofFloat( -1f, 1f)
                mShimmerAnimator!!.addUpdateListener {

                    shimmerTranslate = it?.animatedValue as Float
                }
                mShimmerAnimator!!.interpolator = AccelerateInterpolator(2f)
                mShimmerAnimator!!.duration = builder.mShimmerDuration
                mShimmerAnimator!!.startDelay = 200
                mShimmerAnimator!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        setShimmerAnimating(true)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        setShimmerAnimating(false)
                    }
                })
            }else{
                mShimmerAnimator!!.cancel()
            }
            return mShimmerAnimator!!
        }



    init {
        this.builder.options = builder.options
        // 默认隐藏
        setVisible(false)
        mShimmerPaint = Paint()
        mShimmerGradientMatrix = Matrix()


    }

//    override fun isInEditMode(): Boolean {
//        return true
//    }

    /**
     * 绘制闪光
     *
     * @param canvas
     */
    protected fun onDrawShimmer(canvas: Canvas) {
        if (mShimmerAnimating) {
            canvas.save()
            mTempRectF.set(mFrameRectF)
            mTempRectF.inset(2f, 2f)
            val shimmerTranslateX = mTempRectF.width() * shimmerTranslate
            val shimmerTranslateY = mTempRectF.height() * shimmerTranslate

            mShimmerGradientMatrix.setTranslate(shimmerTranslateX+builder.offsetX, shimmerTranslateY+builder.offsetY)
            mShimmerLinearGradient!!.setLocalMatrix(mShimmerGradientMatrix)
            canvas.drawRoundRect(mTempRectF, getRoundRadius(), getRoundRadius(), mShimmerPaint)
            canvas.restore()
        }
    }



    // 默认调用绘制流光效果
    override fun draw(canvas: Canvas) {
        setBounds(parentView!!.left+builder.offsetX.toInt(),parentView!!.top+builder.offsetY.toInt(),parentView!!.right+builder.offsetX.toInt(),parentView!!.bottom+builder.offsetY.toInt())
        onDrawShimmer(canvas)
//        super.draw(canvas)
    }


    // 设置动画执行的参数，流光参数具体含义可参考LinearGradient的定义，参考博客http://blog.csdn.net/u012702547/article/details/50821044
    private fun setShimmerAnimating(shimmerAnimating: Boolean) {
        mShimmerAnimating = shimmerAnimating
        if (mShimmerAnimating) {
            mShimmerLinearGradient = LinearGradient(
                builder.offsetX, builder.offsetY, mFrameRectF.width(), mFrameRectF.height(),
                intArrayOf(0x00FFFFFF, 0x1AFFFFFF, builder.mShimmerColor, 0x1AFFFFFF, 0x00FFFFFF),
                floatArrayOf(0f, 0.2f, 0.5f, 0.8f, 1f), Shader.TileMode.CLAMP
            )
            mShimmerPaint.shader = mShimmerLinearGradient
        }else{
            mShimmerAnimator!!.cancel()
        }
    }

    override fun setAlpha(alpha: Int) {
        mShimmerPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mShimmerPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }


    override fun setVisible(visible: Boolean) {
        if (isVisible != visible) {
            visibility = if (visible) View.VISIBLE else View.INVISIBLE
            setVisible(visible,false)
            if (!visible ) {
                mShimmerAnimator?.cancel()
            }
        }
    }



    //根据view以及缩放大小计算出左上角点作为位移终点
    private fun findLocationWithView(): Rect {
        var rect = Rect()
        rect.offset(parentView!!.left- parentView!!.scrollX+builder.offsetX.toInt(), parentView!!.top - parentView!!.scrollY+builder.offsetY.toInt())
        return rect
//        return findOffsetDescendantRectToMyCords(view)
    }


    // 核心调用
    override fun onFocus(options: FocusShimmer.Options?) {
        if (null != mOldFocusView && null != mOldFocusView!!.get()) {
            mOldFocusView!!.clear()
        }

        if (options != null&&options is Options) {
            // 执行动画1
            runFocusAnimation(options)
        }else{
            if (this.builder.options!=null&&this.builder.options is Options){
                runFocusAnimation(this.builder.options as Options)
            }
        }
    }


    // 执行动画2
    private fun runFocusAnimation( options: Options) {
        isVisible = true
        // 移动边框的动画
        runBorderAnimation( options)
    }


    // 执行动画3 最终执行了
    protected fun runBorderAnimation( options: Options) {
        if (null != mAnimatorSet) {
            mAnimatorSet!!.cancel()
        }
        createBorderAnimation(options)
        // 开始执行
        mAnimatorSet!!.start()
    }

    // 组织动画效果，子类中默认调用执行
    protected fun createBorderAnimation(options: Options) {
        val paddingWidth =
            mPaddingRectF.left + mPaddingRectF.right + mPaddingOffsetRectF.left + mPaddingOffsetRectF.right
        val paddingHeight =
            mPaddingRectF.top + mPaddingRectF.bottom + mPaddingOffsetRectF.top + mPaddingOffsetRectF.bottom
        val newWidth = if(builder.width>0){builder.width}else(parentView!!.measuredWidth * options.scaleX + paddingWidth).toInt()
        val newHeight = if(builder.height>0){builder.height}else(parentView!!.measuredHeight * options.scaleY + paddingHeight).toInt()
        //以画布左上角作为起始点
        val fromRect = Rect(builder.offsetX.toInt(),builder.offsetY.toInt(),0,0)
        val toRect = findLocationWithView()
        val x = toRect.left - fromRect.left
        val y = toRect.top - fromRect.top
        val newX = x - abs(parentView!!.measuredWidth - newWidth) / 2f
        val newY = y - abs(parentView!!.measuredHeight - newHeight) / 2f

        val together = ArrayList<Animator>()
        val appendTogether = getTogetherAnimators(newX, newY, newWidth, newHeight, options)
        together.add(getWidthAnimator(newWidth))
        together.add(getHeightAnimator(newHeight))
        if (null != appendTogether && appendTogether.isNotEmpty()) {
            together.addAll(appendTogether)
        }

        val sequentially = ArrayList<Animator>()
        val appendSequentially = getSequentiallyAnimators(newX, newY, newWidth, newHeight, options)

        if (builder.mIsShimmerAnim) {
            sequentially.add(shimmerAnimator)
        }
        if (null != appendSequentially && !appendSequentially.isEmpty()) {
            sequentially.addAll(appendSequentially)
        }

        mAnimatorSet = AnimatorSet()
        //mAnimatorSet!!.interpolator = DecelerateInterpolator(1f)
        mAnimatorSet!!.playTogether(together)
        mAnimatorSet!!.playSequentially(sequentially)
    }


    private fun getHeightAnimator(height: Int): ValueAnimator {
        if (null == mHeightAnimator) {
            mHeightAnimator = ValueAnimator.ofInt( bounds.height(), height)
                .setDuration(builder.mAnimDuration)
            mHeightAnimator?.addUpdateListener{
                mFrameRectF.bottom = mFrameRectF.top+it?.animatedValue as Int
                this.invalidateSelf()
            }
        } else {
            mHeightAnimator!!.setIntValues(bounds.height(), height)
        }
        return mHeightAnimator!!
    }

    private fun getWidthAnimator(width: Int): ValueAnimator {
        if (null == mWidthAnimator) {
            mWidthAnimator =
                ValueAnimator.ofInt(width).setDuration(builder.mAnimDuration)
            mWidthAnimator?.addUpdateListener{
                mFrameRectF.right = mFrameRectF.left+it?.animatedValue as Int
                this.invalidateSelf() }
        }else {
            mWidthAnimator!!.setIntValues(bounds.width(), width)
        }
        return mWidthAnimator!!
    }

    abstract fun getRoundRadius(): Float

    abstract fun getTogetherAnimators(
        newX: Float,
        newY: Float,
        newWidth: Int,
        newHeight: Int,
        options: Options
    ): List<Animator>?

    abstract fun getSequentiallyAnimators(
        newX: Float, newY: Float, newWidth: Int, newHeight: Int,
        options: Options
    ): List<Animator>?

    open class Options internal constructor() : FocusShimmer.Options() {
        var scaleX = 1f
        var scaleY = 1f
        // 默认圆角角度为0，则为矩形
        var roundRadius = 0f

        val isScale: Boolean
            get() = scaleX != 1f || scaleY != 1f

        object OptionsHolder {
            val INSTANCE = Options()
        }

        companion object {

            operator fun get(scaleX: Float, scaleY: Float): Options {
                OptionsHolder.INSTANCE.scaleX = scaleX
                OptionsHolder.INSTANCE.scaleY = scaleY
                return OptionsHolder.INSTANCE
            }

            // 接受圆角角度
            operator fun get(roundRadius: Float): Options {
                OptionsHolder.INSTANCE.roundRadius = roundRadius
                return OptionsHolder.INSTANCE
            }
        }
    }

    // 这里都是些抽象的默认变量，子类中可直接调用
    abstract class Builder {
         var mShimmerColor = -0x70000001

        // 默认流光特效开启
         var mIsShimmerAnim = true

         var mAnimDuration = DEFAULT_ANIM_DURATION_TIME

         var mShimmerDuration = DEFAULT_SHIMMER_DURATION_TIME

         var mPaddingOffsetRectF = RectF()

         var options: FocusShimmer.Options = FocusShimmer[0f]
        //流光动画x的偏移量
         var offsetX = 0f
        //流光动画y偏移量
        var offsetY = 0f
        //流光动画的大小
        var width:Int = -1
        var height:Int = -1
        open fun setOffset(offsetX:Float,offsetY: Float): Builder {
            this.offsetX = offsetX
            this.offsetY = offsetY
            return this
        }
        open fun setSize(width: Int,height: Int): Builder {
            this.width = width
            this.height = height
            return this
        }
        open fun shimmerColor(color: Int): Builder {
            this.mShimmerColor = color
            return this
        }

        open fun setOption(options: FocusShimmer.Options): Builder {
            this.options = options
            return this
        }
        fun shimmerDuration(duration: Long): Builder {
            this.mShimmerDuration = duration
            return this
        }

        fun noShimmer(): Builder {
            this.mIsShimmerAnim = false
            return this
        }


        fun pandding(left: Float, top: Float, right: Float, bottom: Float): Builder {
            this.mPaddingOffsetRectF.left = left
            this.mPaddingOffsetRectF.top = top
            this.mPaddingOffsetRectF.right = right
            this.mPaddingOffsetRectF.bottom = bottom
            return this
        }

        abstract fun build(parent: View): FocusShimmer
    }

    companion object {
        // 默认动画播放时间 ms
        private val DEFAULT_ANIM_DURATION_TIME: Long = 100

        // 默认流光时间 ms
        private val DEFAULT_SHIMMER_DURATION_TIME: Long = 1000
    }
}
