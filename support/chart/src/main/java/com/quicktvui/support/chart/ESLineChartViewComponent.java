package com.quicktvui.support.chart;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.List;


@ESKitAutoRegister
public class ESLineChartViewComponent implements IEsComponent<LineChartView> {
    @Override
    public LineChartView createView(Context context, EsMap params) {
        return new LineChartView(context);
    }

    @Override
    public void dispatchFunction(LineChartView view, String eventName, EsArray params, EsPromise promise) {
        switch (eventName) {
            case "setChartData":
                setLineCharData(view, params);
                break;
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                promise.resolve(map);
                break;
        }
    }


    /**
     * 文字颜色
     *
     * @param view
     * @param colorString
     */
    @EsComponentAttribute
    public void textColor(LineChartView view, String colorString) {
        if (view != null) {
            view.setTextColor(colorString);
        }
    }

    /**
     * 文字大小
     *
     * @param view
     * @param textSize
     */
    @EsComponentAttribute
    public void textSize(LineChartView view, int textSize) {
        if (view != null) {
            view.setTextSize(textSize);
        }
    }

    /**
     * 整体背景色
     *
     * @param view
     * @param backgroundColor
     */
    @EsComponentAttribute
    public void chartBackground(LineChartView view, String backgroundColor) {
        if (view != null) {
            view.setChartBackground(backgroundColor);
        }
    }

    /**
     * 折线颜色
     *
     * @param view
     * @param colorString
     */
    @EsComponentAttribute
    public void lineColor(LineChartView view, String colorString) {
        if (view != null) {
            view.setLineColor(colorString);
        }
    }


    /**
     * 折线宽度
     *
     * @param view
     * @param size
     */
    @EsComponentAttribute
    public void lineWidthSize(LineChartView view, int size) {
        if (view != null) {
            view.setLineWidthSize(size);
        }
    }

    /**
     * 辅助线（X轴、Y轴、虚线）颜色
     *
     * @param view
     * @param colorString
     */
    @EsComponentAttribute
    public void defColor(LineChartView view, String colorString) {
        if (view != null) {
            view.setDefColor(colorString);
        }
    }

    /**
     * 辅助线（X轴、Y轴、虚线）宽度
     *
     * @param view
     * @param defLineWidth
     */
    @EsComponentAttribute
    public void defLineWidth(LineChartView view, int defLineWidth) {
        if (view != null) {
            view.setDefLineWidth(defLineWidth);
        }
    }


    /**
     * 设置绘制折线动画类型
     * @param view
     * @param animationType
     */
    @EsComponentAttribute
    public void animationType(LineChartView view, int animationType) {
        if (view != null) {
            view.setAnimationType(animationType);
        }
    }


    /**
     * Y轴 数字 左侧文字
     * @param view
     * @param strLeft
     */
    @EsComponentAttribute
    public void leftText(LineChartView view, String strLeft) {
        if (view != null) {
            view.setYAxisNumLeftText(strLeft);
        }
    }

    /**
     * Y轴 数字 右侧文字
     * @param view
     * @param strRight
     */
    @EsComponentAttribute
    public void rightText(LineChartView view, String strRight) {
        if (view != null) {
            view.setYAxisNumRightText(strRight);
        }
    }


    /**
     * 设置数据
     *
     * @param view
     * @param params
     */
    public void setLineCharData(LineChartView view, EsArray params) {
        if (view != null) {
            try {
                EsArray array = params.getArray(0);
                EsArray array1 = params.getArray(1);

                List<String> xStrArr = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    xStrArr.add(array.get(i) + "");
                }

                List<List<String>> dataList = new ArrayList<>();
                for (int i = 0; i < array1.size(); i++) {
                    List<String> list = new ArrayList<>();
                    list.add(0 + "");
                    list.add(array1.get(i) + "");
                    dataList.add(list);
                }
                view.setLineCharData(dataList, xStrArr);
            } catch (Exception e) {
                e.printStackTrace();
                if (L.DEBUG) {
                    L.logD("-----------LineChartView--数据错误---------->>>>>");
                }
            }
        }
    }

    @Override
    public void destroy(LineChartView view) {

    }
}
