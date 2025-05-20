package com.quicktvui.support.chart.chart;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.quicktvui.support.chart.R;
import com.quicktvui.support.chart.chart.utils.DensityUtil;


/**
 * 折线图基类
 */
public abstract class BaseLineChart extends View {

    protected int ScrWidth, ScrHeight;   //屏幕宽高
    protected RectF rectChart;          //图表矩形
    protected PointF centerPoint;       //chart中心点坐标

    protected Paint paint;
    protected Paint paintEffect;
    protected Paint paintLabel;



    protected int backColor = Color.WHITE;
    //辅助线宽度
    protected int lineWidth = DensityUtil.dip2px(getContext(), 0.5f);
    // 辅助线颜色（X轴&&YY轴&&虚线）
    protected int defColor = getResources().getColor(R.color.color_white);

    /**
     * 动画相关统一属性，也可以设置，需要写set方法
     */
    protected long animDuration = 1000;
    protected ValueAnimator anim;
    protected boolean startDraw = false;

    public BaseLineChart(Context context) {
        this(context, null);
    }

    public BaseLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        init(context, attrs, defStyle);
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ScrHeight = dm.heightPixels;
        ScrWidth = dm.widthPixels;

        paint = new Paint();
        paint.setAntiAlias(true);

        paintEffect = new Paint();
        paintEffect.setAntiAlias(true);
        paintEffect.setStyle(Paint.Style.FILL);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(Color.RED);

        paintLabel = new Paint();
        paintLabel.setAntiAlias(true);
    }

    public abstract void init(Context context, AttributeSet attrs, int defStyleAttr);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerPoint = new PointF(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        rectChart = new RectF(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(),
                getMeasuredHeight() - getPaddingBottom());
    }

    public void onDraw(Canvas canvas) {
        try {
            drawDefult(canvas);
            if (!startDraw) {
                startDraw = true;
                startAnimation(canvas);
            } else {
                drawChart(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制图表基本框架
     */
    public abstract void drawDefult(Canvas canvas);


    /**
     * 绘制图表
     */
    public abstract void drawChart(Canvas canvas);

    /**
     * 创建动画
     */
    protected abstract ValueAnimator initAnim();

    /**
     * 动画值变化之后计算数据
     */
    protected abstract void evaluatorData(ValueAnimator animation);

    public void setAnimDuration(long duration) {
        this.animDuration = duration;
    }

    protected void startAnimation(Canvas canvas) {
        if (anim != null) {
            anim.cancel();
        }
        anim = initAnim();
        if (anim == null) {
            drawChart(canvas);
        } else {
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    evaluatorData(animation);
                    invalidate();
                }
            });
            anim.setDuration(animDuration);
            anim.start();
        }
    }
}
