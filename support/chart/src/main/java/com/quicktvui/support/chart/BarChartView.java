package com.quicktvui.support.chart;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.support.chart.chart.BarChart;

import java.util.List;

import com.quicktvui.support.chart.chart.bean.BarBean;
import com.quicktvui.support.chart.chart.utils.DensityUtil;

public class BarChartView extends FrameLayout implements IEsComponentView {

    protected BarChart chart;

    public BarChartView(Context context) {
        super(context);
        init();
    }

    private void init() {
        chart = new BarChart(getContext());
        addView(chart, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }


    /**
     * 设置柱间距
     *
     * @param space
     */
    public void setBarItemSpace(int space) {
        if (chart == null) {
            return;
        }
        chart.setBarItemSpace(DensityUtil.dip2px(getContext(), space));
    }

    /**
     * 设置柱宽度
     */
    public void setBarWidth(int width) {
        if (chart == null) {
            return;
        }
        chart.setBarWidth(DensityUtil.dip2px(getContext(), width));
    }

    /**
     * 设置柱颜色
     *
     * @param colorString
     */
    public void setBarColor(String colorString) {
        if (chart == null) {
            return;
        }
        chart.setBarColor(new int[]{Color.parseColor(colorString)});
    }


    /**
     * 是否显示左侧Y轴轴线
     *
     * @param isShow
     */
    public void setLeftLine(boolean isShow) {
        if (chart == null) {
            return;
        }
        chart.setLeftLine(isShow);
    }


    /**
     * 设置左侧Y轴背景色
     *
     * @param color
     */
    public void setLeftBackground(String color) {
        if (chart == null) {
            return;
        }
        chart.setBackColor(Color.parseColor(color));
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


    /******************顶部**************************/
    public void setTopTextSize(int topSize) {
        if (chart == null) {
            return;
        }
        chart.setTextSizeTag(DensityUtil.dip2px(getContext(),topSize));
        invalidate();
    }

    public void setTopTextColor(String color) {
        if (chart == null) {
            return;
        }
        chart.setTextColorTag(Color.parseColor(color));
    }


    /******************底部**************************/
    public void setBottomTextSize(int topSize) {
        if (chart == null) {
            return;
        }
        chart.setTextSizeCoordinate(DensityUtil.dip2px(getContext(),topSize));
        invalidate();
    }

    public void setBottomTextColor(String color) {
        if (chart == null) {
            return;
        }
        chart.setTextColorCoordinate(Color.parseColor(color));
    }


    /******************左边--Y轴**************************/
    public void setLeftTextSize(int topSize) {
        if (chart == null) {
            return;
        }
        chart.setLeftTextSizeCoordinate(DensityUtil.dip2px(getContext(),topSize));
        invalidate();
    }

    public void setLeftTextColor(String color) {
        if (chart == null) {
            return;
        }
        chart.setLeftTextColorCoordinate(Color.parseColor(color));
    }

    /**
     * 设置x、y轴线颜色
     * @param defColor
     */
    public void setDefColor(String defColor){
        if(chart == null){
            return;
        }
        chart.setDefColor(Color.parseColor(defColor));
    }

    public void setCharData(List<List<BarBean>> dataList, List<String> strXList) {
        if (chart == null) {
            return;
        }
        chart.setData(dataList, strXList);
    }
}
