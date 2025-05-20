package com.quicktvui.support.chart;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.widget.FrameLayout;

import android.support.annotation.NonNull;

import com.quicktvui.sdk.base.component.IEsComponentView;

import java.util.List;

import com.quicktvui.support.chart.chart.LinesChart;

public class LineChartView extends FrameLayout implements IEsComponentView {

    private LinesChart chart;

    public LineChartView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        chart = new LinesChart(getContext());
        addView(chart, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }


    /**
     * 设置背景色
     *
     * @param color
     */
    public void setChartBackground(String color) {
        if (chart == null) {
            return;
        }
        chart.setBackgroundColor(Color.parseColor(color));
    }


    /**
     * 设置折线颜色
     *
     * @param color
     */
    public void setLineColor(String color) {
        if (chart == null) {
            return;
        }
        chart.setLineColor(Color.parseColor(color));
    }

    /**
     * 设置折线宽度
     *
     * @param size
     */
    public void setLineWidthSize(int size) {
        if (chart == null) {
            return;
        }
        chart.setLineSize(size);
    }


    public void setTextSize(int textSize) {
        if (chart == null) {
            return;
        }
        chart.setTextSize(textSize);
    }


    public void setTextColor(String textColor) {
        if (chart == null) {
            return;
        }
        chart.setTextColor(Color.parseColor(textColor));
    }

    public void setDefColor(String defColor) {
        if (chart == null) {
            return;
        }
        chart.setDefColor(Color.parseColor(defColor));
    }

    public void setDefLineWidth(int defLineWidth) {
        if (chart == null) {
            return;
        }
        chart.setDefLineWidth(defLineWidth);
    }


    public void setAnimationType(int type){
        if (chart == null) {
            return;
        }
        switch (type){
            case 0:
                chart.setAnimType(LinesChart.AnimType.NO_ANIMATION); //无动画
                break;
            case 1:
                chart.setAnimType(LinesChart.AnimType.LEFT_TO_RIGHT); //动画从左往右
                break;
            case 2:
                chart.setAnimType(LinesChart.AnimType.BOTTOM_TO_TOP); //动画从下往上升
                break;
            case 3:
                chart.setAnimType(LinesChart.AnimType.SLOW_DRAW); //动画缓慢绘制
                break;
        }
    }


    public void setYAxisNumLeftText(String strLeft){
        if (chart == null) {
            return;
        }
        chart.setYAxisNumLeftText(strLeft);
    }

    public void setYAxisNumRightText(String strRight){
        if (chart == null) {
            return;
        }
        chart.setYAxisNumRightText(strRight);
    }

    private Handler mHandler;

    public void setLineCharData(List<List<String>> dataList, List<String> xStrArr) {
        if (chart == null) {
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                chart.setData(dataList, xStrArr);//折线图设置数据
            }
        }, 100);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (chart != null) {
            chart = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        super.onDetachedFromWindow();
    }
}
