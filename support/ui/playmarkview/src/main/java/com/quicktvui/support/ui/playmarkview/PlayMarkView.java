package com.quicktvui.support.ui.playmarkview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import android.support.v4.view.animation.FastOutSlowInInterpolator;

import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;


/**
 * 播放动态标识
 */
public class PlayMarkView extends View implements IEsComponentView {
    private static final String TAG = "PlayMarkViewLog";
    private static final int bottomWaveView = 0;
    private static final int centerWaveView = 1;
    int gap = -1;
    Paint mPaint;
    final static int BARS = 4;
    Bar[] bars;
    int maxHeight = 0;
    int heightCenter = 0;
    private boolean needCompute = true;
    int roundCorner = 3;
    int[] stateColor;

    private boolean attachted;
    private LinearGradient mLinearGradient;
    private int[] colors;
    private float[] floatArray;

    private int showType = 0;//默认0从底部向上波动 1中间波动
    private int isSupportGradient = 0;//是否支持渐变颜色  0不支持 1支持
    private String startColor = "#FF8534";
    private String endColor = "#FFCA37";

    public PlayMarkView(Context context) {
        super(context);
        init();
    }

    public void setShowType(int type) {
        this.showType = type;
        requestUpdate();
    }

    public void setGap(int gap) {
        this.gap = gap;
        requestUpdate();
    }

    public void setPlayColor(int color) {
        this.mPaint.setColor(color);
        isSupportGradient = 0;
        postInvalidateDelayed(16);
    }

    public void setPlayColorState(int[] stateColor) {
        if (stateColor != null && stateColor.length > 0) {
            setDuplicateParentStateEnabled(true);
        }
        this.stateColor = stateColor;
    }

    public void setRoundCorner(int roundCorner) {
        this.roundCorner = roundCorner;
        postInvalidateDelayed(16);
    }

    public void setArrayColor(String[] array) {
        this.startColor = array[0];
        this.endColor = array[1];
        isSupportGradient = 1;
    }

    public void setLinearGradientColors(String startColor, String endColor) {
        if (!startColor.isEmpty()) {
            this.startColor = startColor;
        }
        if (!endColor.isEmpty()) {
            this.endColor = endColor;
        }
        isSupportGradient = 1;
    }

    public PlayMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (isDuplicateParentStateEnabled() && stateColor != null && stateColor.length == 2) {
            final int[] state = getDrawableState();
            final boolean focused = EsProxy.get().stateContainsAttribute(state, android.R.attr.state_focused);
            if (focused) {
                setPlayColor(stateColor[1]);
            } else {
                setPlayColor(stateColor[0]);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setupBars();
        if (bars != null && attachted) {
            for (Bar bar : bars) {
                bar.draw(canvas, mPaint);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachted = true;
        requestUpdate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attachted = false;
        stopAnim();
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (View.VISIBLE == visibility) {
            requestUpdate();
        } else {
            stopAnim();
        }
    }

    void requestUpdate() {
        this.needCompute = true;
        postInvalidateDelayed(16);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        requestUpdate();
    }

    void stopAnim() {
        if (bars != null) {
            for (Bar b : bars) {
                b.stopAnim();
            }
        }
    }

    void setupBars() {
        if (View.VISIBLE == getVisibility() && getAlpha() > 0) {
            if (needCompute) {
                if (bars == null) {
                    bars = new Bar[BARS];
                } else {
                    for (Bar b : bars) {
                        b.stopAnim();
                    }
                }
                needCompute = false;
                maxHeight = getHeight() - getPaddingTop() - getPaddingBottom();
                heightCenter = maxHeight / 2;
                int gap = this.gap > 0 ? this.gap : (int) (getWidth() * 0.1f);
                int barWidth = (int) ((float) (getWidth() - (BARS - 1) * gap) / BARS);

                Log.d(TAG, "doComputeInit maxHeight:" + maxHeight + ",barWidth:" + barWidth + ",getWidth:" + getWidth() + ",heightCenter:" + heightCenter);

                int startX = 0;
                float[] barStartRate = new float[]{0.8f, 0.9f, 1f, 0.5f};//开始抖动频率
                float[] barEndRate = new float[]{0.2f, 0.4f, 0.2f, 0.1f};//结束抖动频率
                int[] duration = new int[]{200, 190, 180, 180};//动画执行时间
                Interpolator[] interpolators = new Interpolator[]{
                        null, null, null, new FastOutSlowInInterpolator()
                };
                int[] delays = new int[]{0, 0, 0, 20};

                for (int i = 0; i < BARS; i++) {
                    final int startY = (int) (maxHeight * barStartRate[i]);
                    final int endY = (int) (maxHeight * barEndRate[i]);
                    //时间间隔、条宽、起始y、结束y、起始x、当前view、延迟时间、差值器、圆角
                    Bar bar = new Bar((int) (duration[i] * 2f), barWidth, startY, endY, startX, this, delays[i], interpolators[i], roundCorner, heightCenter, showType);
                    startX += (barWidth + gap);
                    bar.startAnim();
                    bars[i] = bar;
                    //isSupportGradient 为1支持渐变 设置渐变颜色
                    if (isSupportGradient == 1) {
                        colors = new int[]{Color.parseColor(startColor), Color.parseColor(endColor)};
                        floatArray = new float[]{0f, 0.8f};
                        mLinearGradient = new LinearGradient(0, 0, startX, maxHeight, colors, floatArray, Shader.TileMode.CLAMP);
                        mPaint.setShader(mLinearGradient);//设置渐变颜色
                    }
                }
                invalidate();
            }
        }

    }

    void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        isSupportGradient = 0;
    }

    private static final class Bar implements ValueAnimator.AnimatorUpdateListener {
        final int duration; //一次变动的时长
        final int stoke; // 单条宽度
        final int startY;
        Animator anim;
        int round;
        RectF rect;//波纹数据
        final int x;//x偏移 累加
        final View view;
        final int delay;
        final int endY;
        Interpolator interpolator;
        int heightCenter;
        int type;

        public Bar(int duration, int stoke, int startY, int endY, int x, View view, int delay, Interpolator interpolator, int round, int heightCenter, int type) {
            this.duration = duration;
            this.stoke = stoke;
            this.startY = startY;
            this.x = x;
            this.view = view;
            this.delay = delay;
            this.endY = endY;
            this.interpolator = interpolator;
            this.round = round;
            this.heightCenter = heightCenter;
            this.type = type;
        }

        void startAnim() {
            if (anim != null) {
                anim.removeAllListeners();
                anim.cancel();
                anim = null;
            }
            ValueAnimator down = ValueAnimator.ofFloat(startY, endY);
            down.setFloatValues();
            down.setupStartValues();
            down.addUpdateListener(this);
            down.setStartDelay(delay);
            down.setRepeatMode(ValueAnimator.REVERSE);
            down.setRepeatCount(ValueAnimator.INFINITE);
            down.setDuration(duration);
            if (interpolator != null) {
                down.setInterpolator(interpolator);
            }
            down.start();
            this.anim = down;
        }

        void draw(Canvas canvas, Paint paint) {
            if (rect != null) {
                canvas.drawRoundRect(rect, round, round, paint);
            }
        }

        public void stopAnim() {
            if (anim != null) {
                anim.removeAllListeners();
                anim.cancel();
                anim = null;
            }
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float value = (float) animation.getAnimatedValue();
            if (type == bottomWaveView) {
                if (rect == null) {
                    rect = new RectF(x, view.getHeight() - value, x + stoke, view.getHeight() + round);
                } else {
                    rect.top = view.getHeight() - value;
                }
            } else if (type == centerWaveView) {
                if (rect == null) {
                    rect = new RectF(x, heightCenter - value, x + stoke, heightCenter + round);
                } else {
                    rect.top = heightCenter - value / 2;
                    rect.bottom = heightCenter + value / 2;
                }
            }
            view.postInvalidateDelayed(16);
        }
    }
}
