package com.quicktvui.support.chart.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.quicktvui.support.chart.R;
import com.quicktvui.support.chart.chart.anim.AngleEvaluator;
import com.quicktvui.support.chart.chart.bean.DataPoint;

import java.util.ArrayList;
import java.util.List;


import com.quicktvui.support.chart.chart.utils.DensityUtil;
import com.quicktvui.support.chart.chart.utils.FontUtil;

/**
 * 折线图
 */
public class LinesChart extends BaseLineChart {

    //设置数据
    private List<List<String>> dataList;
    private List<String> lableXList;
    private int dataNumCount = 0;
    //计算后的数据
    private List<DataPoint> lableXPointList;
    private List<ArrayList<DataPoint>> linePointList;

    /**
     * 可以设置的属性
     */
    //设置Y轴刻度数量
    private int YMARK_NUM = 5;
    //设置线条颜色
    private int lineColor = getResources().getColor(R.color.color_white);
    //设置曲线粗细
    private int lineSize = DensityUtil.dip2px(getContext(), 3f);
    //曲线数量
    private int LINE_NUM = 1;
    //设置坐标文字大小
    private int textSize = (int) getResources().getDimension(R.dimen.text_size_26);
    //设置坐标文字颜色
    private int textColor = getResources().getColor(R.color.color_white);
    //设置X坐标字体与横轴的距离
    private int textSpaceX = DensityUtil.dip2px(getContext(), 15);
    //设置Y坐标字体与横轴的距离
    private int textSpaceY = DensityUtil.dip2px(getContext(), 10);
    //设置动画类型
    private AnimType animType = AnimType.NO_ANIMATION;

    public enum AnimType {
        LEFT_TO_RIGHT,   //动画从左往右
        BOTTOM_TO_TOP,   //动画从下往上升
        SLOW_DRAW,        //动画缓慢绘制
        NO_ANIMATION     //无动画
    }

    /**
     * 需要计算相关值
     */
    private float lableLead, lableHeight;
    /*字体绘制相关*/
    private float YMARK = 1;    //Y轴刻度间隔
    private float YMARK_MAX = Float.MIN_VALUE;    //Y轴刻度最大值
    private float YMARK_MIN = Float.MAX_VALUE;    //Y轴刻度最小值

    private String yNumLeftText = "";
    private String yNumRightText = "";


    //整体图表距左
    private int paddingLeftChart =  DensityUtil.dip2px(getContext(), 60);
    private int paddingYLable = DensityUtil.dip2px(getContext(), 15);


    public LinesChart(Context context) {
        this(context, null);
    }

    public LinesChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinesChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
    }

    public void setYMARK_NUM(int YMARK_NUM) {
        this.YMARK_NUM = YMARK_NUM;
    }


    /***********************************设置属性set方法**********************************/
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setAnimType(AnimType animType) {
        this.animType = animType;
    }

    public void setLINE_NUM(int LINE_NUM) {
        this.LINE_NUM = LINE_NUM;
    }


    public void setDefColor(int defColor){
        this.defColor = defColor;
    }

    public void setDefLineWidth(int defLineWidth){
        this.lineWidth = defLineWidth;
    }


    public void setYAxisNumLeftText(String leftText){
        this.yNumLeftText = leftText;
        if (!TextUtils.isEmpty(yNumLeftText)){
            int lfetTextLength = (int) FontUtil.getFontlength(paintLabel, yNumLeftText);
            paddingLeftChart  = paddingLeftChart+ lfetTextLength;
        }
    }

    public void setYAxisNumRightText(String rightText){
        this.yNumRightText = rightText;
        if (!TextUtils.isEmpty(yNumRightText)){
            int rightTextLength = (int) FontUtil.getFontlength(paintLabel, yNumRightText);
            paddingLeftChart  = paddingLeftChart+  rightTextLength;
        }
    }

    /***********************************设置属性set方法over**********************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        evaluatorByData();
        invalidate();
    }


    /***************************************************/

    /***************************************************/

    /**
     * 设置数据
     *
     * @param dataList   折线数据
     * @param lableXList X轴显示的刻度，默认是9:30-15:00（传null）
     *                   如果需要显示其他的数据，需要重新设置
     */
    public void setData(List<List<String>> dataList, List<String> lableXList) {
        if (null == dataList){
            return;
        }
        this.dataList.clear();
        this.dataList.addAll(dataList);
        if (null != lableXList && lableXList.size() > 0)
            this.lableXList = lableXList;

        if (getMeasuredWidth() > 0) {
            evaluatorByData();
            startDraw = false;
            invalidate();
        }
    }

    /**
     * 设置数据后，计算相关值
     */
    private void evaluatorByData() {
        if (dataList.size() <= 0){
            return;
        }
        dataNumCount = dataList.size();
        /**①、计算字体相关以及图表原点坐标*/
        paintLabel.setTextSize(textSize);
        lableHeight = FontUtil.getFontHeight(paintLabel);
        lableLead = FontUtil.getFontLeading(paintLabel);
        //图表主体矩形
        rectChart = new RectF(getPaddingLeft() + paddingLeftChart, getPaddingTop() + lableHeight + textSpaceY,
                getMeasuredWidth() - getPaddingRight(),
                getMeasuredHeight() - getPaddingBottom() - lableHeight - textSpaceX);

        /**②、计算X标签绘制坐标*/
        float lableXSpace = 0;
        for (String lableX : lableXList) {
            lableXSpace += FontUtil.getFontlength(paintLabel, lableX);
        }
        lableXSpace = (rectChart.right - rectChart.left - lableXSpace) / (lableXList.size() - 1);
        lableXPointList = new ArrayList<>();
        if (lableXSpace > 0) {
            float left = rectChart.left;
            for (int i = 0; i < lableXList.size(); i++) {
                String lableX = lableXList.get(i);
                lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                        left, rectChart.bottom + textSpaceX + lableLead)));
                left += (FontUtil.getFontlength(paintLabel, lableXList.get(i)) + lableXSpace);
            }
        } else {
            //如果X轴标签字体过长的情况需要特殊处理
            float oneWidth = (rectChart.right - rectChart.left) / lableXList.size();
            for (int i = 0; i < lableXList.size(); i++) {
                String lableX = lableXList.get(i);
                float xLen = FontUtil.getFontlength(paintLabel, lableX);
                lableXPointList.add(new DataPoint(lableX, 0, new PointF(
                        rectChart.left + i * oneWidth + (oneWidth - xLen) / 2, rectChart.bottom + textSpaceX + lableLead)));
            }
        }

        /**③、计算Y刻度最大值和最小值以及幅度*/
        int lineNum = LINE_NUM == 0 ? (dataList.get(0).size() - 1) : LINE_NUM;
        YMARK_MAX = Float.MIN_VALUE;    //Y轴刻度最大值
        YMARK_MIN = Float.MAX_VALUE;    //Y轴刻度最小值
        for (List<String> list : dataList) {
            for (int i = 1; i < lineNum + 1; i++) {
                String str = list.get(i);
                try {
                    YMARK = Float.parseFloat(str);

                    if (YMARK > YMARK_MAX)
                        YMARK_MAX = YMARK;
                    if (YMARK < YMARK_MIN)
                        YMARK_MIN = YMARK;
                } catch (Exception e) {
                }
            }

        }
        if (YMARK_MAX > 0){
            YMARK_MAX *= 1.1f;
        } else{
            YMARK_MAX /= 1.1f;
        }

        if (YMARK_MIN > 0){
            YMARK_MIN /= 1.1f;
        }else {
            YMARK_MIN *= 1.1f;
        }

        YMARK = (YMARK_MAX - YMARK_MIN) / (YMARK_NUM - 1);

        YMARK = (int) YMARK + 1;
        YMARK_MIN = (int) YMARK_MIN;
        YMARK_MAX = YMARK_MIN + YMARK * (YMARK_NUM - 1);

        /**④、计算点的坐标，如果有动画的情况下，边绘制边计算会耗费性能，所以先计算*/
        //创建集合，用于存放每条线上每个点的坐标数据
        List<String> group = dataList.get(0);
        linePointList = new ArrayList<>();
        for (int i = 0; i < group.size() - 1; i++)
            linePointList.add(new ArrayList<DataPoint>());
        float oneSpace = (rectChart.right - rectChart.left) / (dataNumCount - 1);
        for (int i = 0; i < dataList.size(); i++) {
            //一组数据，包含每条线的一个item数据
            group = dataList.get(i);
            for (int j = 1; j < group.size(); j++) {
                try {
                    float valueY = Float.parseFloat(group.get(j));
                    PointF point = new PointF();
                    //只有需要绘制的线才计算坐标
                    if (j < lineNum + 1) {
                        point.x = rectChart.left + i * oneSpace;
                        //根据最高价和最低价，计算当前数据在图表上Y轴的坐标
                        point.y = rectChart.bottom -
                                (rectChart.bottom - rectChart.top) / (YMARK_MAX - YMARK_MIN) * (valueY - YMARK_MIN);
                    }
                    linePointList.get(j - 1).add(new DataPoint(group.get(0), valueY, point));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 绘制图表基本框架
     */
    @Override
    public void drawDefult(Canvas canvas) {
        if (null == linePointList || linePointList.size() <= 0)
            return;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        drawGrid(canvas);
        drawXLable(canvas);
        drawYLable(canvas);
    }


    /**
     * 绘制图表
     */
    @Override
    public void drawChart(Canvas canvas) {
        if (null == linePointList || linePointList.size() <= 0)
            return;
        drawDataPath(canvas);
    }

    /**
     * 绘制X轴方向辅助网格
     */
    private void drawGrid(Canvas canvas) {
        float yMarkSpace = (rectChart.bottom - rectChart.top) / (YMARK_NUM - 1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(defColor);
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(lineWidth);
        paintEffect.setColor(defColor);
        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        for (int i = 0; i < YMARK_NUM; i++) {
            if (i == 0) {
                //实线
                canvas.drawLine(rectChart.left, rectChart.bottom - yMarkSpace * i,
                        rectChart.right, rectChart.bottom - yMarkSpace * i, paint);
            } else {
                //虚线
                Path path = new Path();
                path.moveTo(rectChart.left, rectChart.bottom - yMarkSpace * i);
                path.lineTo(rectChart.right, rectChart.bottom - yMarkSpace * i);
                PathEffect effects = new DashPathEffect(new float[]{4, 4}, 0);
                paintEffect.setPathEffect(effects);
                canvas.drawPath(path, paintEffect);
            }
        }
    }

    /**
     * 绘制X轴刻度
     */
    private void drawXLable(Canvas canvas) {
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        paintLabel.setTextAlign(Paint.Align.LEFT);
        for (DataPoint lable : lableXPointList) {
            canvas.drawText(lable.getValueX(), lable.getPoint().x, lable.getPoint().y, paintLabel);
        }
    }

    /**
     * 绘制Y轴刻度
     */
    private void drawYLable(Canvas canvas) {
        float yMarkSpace = (rectChart.bottom - rectChart.top) / (YMARK_NUM - 1);
        paintLabel.setTextSize(textSize);
        paintLabel.setColor(textColor);
        paintLabel.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < YMARK_NUM; i++) {
            canvas.drawText(yNumLeftText+(int) (YMARK_MIN + i * YMARK) + yNumRightText, rectChart.left - paddingYLable,
                    rectChart.bottom - yMarkSpace * i - lableHeight - textSpaceY + lableLead + paddingYLable, paintLabel);
        }
    }

    /**
     * 绘制曲线
     */
    private void drawDataPath(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(lineSize);
        int lineNum = LINE_NUM == 0 ? (dataList.get(0).size() - 1) : LINE_NUM;
        for (int j = 0; j < lineNum; j++) {
            List<DataPoint> lineList = linePointList.get(j);
            paint.setColor(lineColor);
            //一条一条的绘制
            Path path = new Path();
            PointF lastPoint = null;
            for (int i = 0; i < lineList.size(); i++) {
                DataPoint dataPoint = lineList.get(i);
                if (i == 0) {
                    if (animType == AnimType.LEFT_TO_RIGHT) {
                        path.moveTo(rectChart.left + (dataPoint.getPoint().x - rectChart.left) * animPro, dataPoint.getPoint().y);
                    } else if (animType == AnimType.BOTTOM_TO_TOP) {
                        path.moveTo(dataPoint.getPoint().x, rectChart.bottom - (rectChart.bottom - dataPoint.getPoint().y) * animPro);
                    } else {
                        path.moveTo(dataPoint.getPoint().x, dataPoint.getPoint().y);
                    }
                } else {
                    //quadTo：二阶贝塞尔曲线连接前后两点，这样使得曲线更加平滑
                    if (animType == AnimType.LEFT_TO_RIGHT) {
                        path.quadTo(rectChart.left + (lastPoint.x - rectChart.left) * animPro, lastPoint.y,
                                rectChart.left + (dataPoint.getPoint().x - rectChart.left) * animPro, dataPoint.getPoint().y);
                    } else if (animType == AnimType.BOTTOM_TO_TOP) {
                        path.quadTo(lastPoint.x, rectChart.bottom - (rectChart.bottom - lastPoint.y) * animPro,
                                dataPoint.getPoint().x, rectChart.bottom - (rectChart.bottom - dataPoint.getPoint().y) * animPro);
                    } else if (animType == AnimType.SLOW_DRAW) {
                        if (i > lineList.size() * animPro)
                            break;
                        path.quadTo(lastPoint.x, lastPoint.y, dataPoint.getPoint().x, dataPoint.getPoint().y);
                    } else {
                        path.quadTo(lastPoint.x, lastPoint.y, dataPoint.getPoint().x, dataPoint.getPoint().y);
                    }
                }
                lastPoint = dataPoint.getPoint();
            }
            canvas.drawPath(path, paint);
        }
    }

    private float animPro;//动画计算的占比数量

    /**
     * 创建动画
     */
    @Override
    protected ValueAnimator initAnim() {
        if (dataList.size() > 0) {
            ValueAnimator anim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, 1f);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            return anim;
        }
        return null;
    }

    /**
     * 动画值变化之后计算数据
     */
    @Override
    protected void evaluatorData(ValueAnimator animation) {
        animPro = (float) animation.getAnimatedValue();
    }

}
