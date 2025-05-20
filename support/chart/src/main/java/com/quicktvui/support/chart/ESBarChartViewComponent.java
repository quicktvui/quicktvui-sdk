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

import com.quicktvui.support.chart.chart.bean.BarBean;

@ESKitAutoRegister
public class ESBarChartViewComponent implements IEsComponent<BarChartView> {

    @Override
    public BarChartView createView(Context context, EsMap params) {
        return new BarChartView(context);
    }

    @Override
    public void dispatchFunction(BarChartView view, String eventName, EsArray params, EsPromise promise) {
        switch (eventName) {
            case "setChartData":
                setCharData(view, params);
                break;
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                promise.resolve(map);
                break;
        }
    }

    @EsComponentAttribute
    public void barItemSpace(BarChartView view, int space) {
        if (view != null) {
            view.setBarItemSpace(space);
        }
    }

    @EsComponentAttribute
    public void barWidth(BarChartView view, int width) {
        if (view != null) {
            view.setBarWidth(width);
        }
    }


    @EsComponentAttribute
    public void barColor(BarChartView view, String colorString) {
        if (view != null) {
            view.setBarColor(colorString);
        }
    }


    @EsComponentAttribute
    public void leftBackgroundColor(BarChartView view, String colorString) {
        if (view != null) {
            view.setLeftBackground(colorString);
        }
    }

    @EsComponentAttribute
    public void chartBackgroundColor(BarChartView view, String colorString) {
        if (view != null) {
            view.setChartBackground(colorString);
        }
    }


    @EsComponentAttribute
    public void topTextSize(BarChartView view, int size) {
        if (view != null) {
            view.setTopTextSize(size);
        }
    }

    @EsComponentAttribute
    public void topTextColor(BarChartView view, String colorString) {
        if (view != null) {
            view.setTopTextColor(colorString);
        }
    }


    @EsComponentAttribute
    public void bottomTextSize(BarChartView view, int size) {
        if (view != null) {
            view.setBottomTextSize(size);
        }
    }

    @EsComponentAttribute
    public void bottomTextColor(BarChartView view, String colorString) {
        if (view != null) {
            view.setBottomTextColor(colorString);
        }
    }


    @EsComponentAttribute
    public void leftTextSize(BarChartView view, int size) {
        if (view != null) {
            view.setLeftTextSize(size);
        }
    }

    @EsComponentAttribute
    public void leftTextColor(BarChartView view, String colorString) {
        if (view != null) {
            view.setLeftTextColor(colorString);
        }
    }

    @EsComponentAttribute
    public void defColor(BarChartView view,String defColor){
        if (view != null){
            view.setDefColor(defColor);
        }
    }

    public void setCharData(BarChartView view, EsArray params) {
        if (view != null) {
            try {
                EsArray array = params.getArray(0);
                EsArray array1 = params.getArray(1);
                //X轴
                List<String> strXList = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    strXList.add(array.get(i).toString());
                }

                //柱状图数据
                List<List<BarBean>> dataList = new ArrayList<>();
                for (int i = 0; i < array1.size(); i++) {
                    List<BarBean> list = new ArrayList<>();
                    list.add(new BarBean(Integer.parseInt(array1.get(i).toString())));
                    dataList.add(list);
                }
                view.setCharData(dataList, strXList);
            } catch (Exception e) {
                e.printStackTrace();
                if (L.DEBUG) {
                    L.logD("-----------BarChartView--数据错误---------->>>>>");
                }
            }
        }
    }

    @Override
    public void destroy(BarChartView view) {

    }
}
