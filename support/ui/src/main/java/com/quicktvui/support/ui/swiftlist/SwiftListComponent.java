package com.quicktvui.support.ui.swiftlist;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.support.ui.largelist.MyTemplateHelper;
import com.quicktvui.support.ui.largelist.TemplateHelper;
import com.quicktvui.support.ui.largelist.TemplateItemPresenterSelector;

@ESKitAutoRegister
public class SwiftListComponent implements IEsComponent<SwiftListView> {
    @Override
    public SwiftListView createView(Context context, EsMap params) {
        boolean isVertical = false;
        if (params.containsKey("vertical")) {
            isVertical = true;
        }
        return new SwiftListView(context, isVertical);
    }

    @EsComponentAttribute
    public void initParam(final SwiftListView lv, EsMap map) {
        if (map.size() > 0) {
            final EsMap templateMap = map.getMap("template"); //number / leftRight / topDown / text

            final TemplateItemPresenterSelector ps = TemplateHelper.setupPresenters();
            MyTemplateHelper.initTemplate(ps, templateMap);
            lv.setSelector(ps);
            lv.initParams(map, templateMap);
        }
    }

    @EsComponentAttribute
    public void initFocusPosition(final SwiftListView lv, int pos) {
        lv.setFocusPosition(pos);
    }

    @EsComponentAttribute
    public void initPosition(final SwiftListView lv, int pos) {
        lv.setInitPosition(pos);
    }

    @EsComponentAttribute
    public void display(final SwiftListView lv, boolean display) {
        lv.setDisplay(display);
    }

    @Override
    public void dispatchFunction(SwiftListView view, String eventName, EsArray params, EsPromise promise) {
        switch (eventName) {
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                promise.resolve(map);
                break;
            case "setPageData":
                // 设置某一页的数据
                int page = params.getInt(0);
                EsArray array = params.getArray(1);
                view.setPageData(page, array);
                break;
            case "requestChildFocus":
                // 设置某一页的数据
                int pos = params.getInt(0);
                view.requestChildFocus(pos);
                break;
            case "setSelectChildPosition":
                // 设置某一页的数据
                view.setSelectChildPosition(params.getInt(0));
                break;
            case "notifyNoMoreData":
                // 设置某一页的数据
                view.notifyNoMoreData();
                break;
            case "scrollToPositionWithOffset":
                // 设置某一页的数据
                view.scrollToPosition(params.getInt(0), params.getInt(1), params.getBoolean(2));
                break;
            case "scrollToPosition":
                // 设置某一页的数据
                view.scrollToPosition(params.getInt(0));
                break;
            case "setFocusTargetChildPosition":
                // 设置某一页的数据
                view.setFocusTargetChildPosition(params.getInt(0));
                break;
            case "updateData":
                // 设置某一页的数据
                view.updateData(params.getInt(0), params.getMap(1));
                break;
        }
    }

    @Override
    public void destroy(SwiftListView view) {

    }
}
