package com.quicktvui.support.ui.selectseries;


import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.support.ui.ScreenAdapt;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.HippyGroupController;

// TODO vip等有时需要某些item隐藏的功能 能否实现？ 1、优化刷新逻辑 2、默认UI调整 3、display的影响(已解决)
@ESKitAutoRegister
@HippyController(name = ESSelectSeriesController.CLASS_NAME)
public class ESSelectSeriesController extends HippyGroupController<SelectSeriesViewGroup> {
    public static final String CLASS_NAME = "SelectSeries";

    @Override
    protected View createViewImpl(Context context) {
        ScreenAdapt.getInstance().init(context);
        return new SelectSeriesViewGroup(context);
    }

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        view.setVisibility(View.GONE);
        super.addView(parentView, view, index);
    }

    @HippyControllerProps(name = "customIndex", defaultType = HippyControllerProps.NUMBER)
    public void setCustomIndex(final SelectSeriesViewGroup lv, int index) {
        lv.setCustomIndex(index);
    }

    @HippyControllerProps(name = "groupParam", defaultType = HippyControllerProps.MAP)
    public void setGroupParam(final SelectSeriesViewGroup lv, HippyMap map) {
        if(map == null){
            Log.e("LargeListComponent","groupParam error map is null");
            throw new IllegalArgumentException("groupParam error map is null");
        }
        try {
            if (map.size() > 0) {
                lv.setGroupParam(map);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @HippyControllerProps(name = "scrollParam", defaultType = HippyControllerProps.MAP)
    public void setScrollParam(final SelectSeriesViewGroup lv, HippyMap map) {
        if(map == null){
            Log.e("LargeListComponent","scrollParam error map is null");
            throw new IllegalArgumentException("scrollParam error map is null");
        }
        try {
            if (map.size() > 0) {
                lv.setScrollParam(map);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @HippyControllerProps(name = "commonParam", defaultType = HippyControllerProps.MAP)
    public void setCommonData(final SelectSeriesViewGroup lv, HippyMap map) {
        if(map == null){
            Log.e("LargeListComponent","commonData error map is null");
            throw new IllegalArgumentException("commonData error map is null");
        }
        try {
            if (map.size() > 0) {
                lv.setCommonParam(map);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @HippyControllerProps(name = "initFocusPosition", defaultType = HippyControllerProps.NUMBER, defaultNumber = 0)
    public void initFocusPosition(final SelectSeriesViewGroup lv, int pos) {
        lv.setFocusPosition(pos);
    }

    @HippyControllerProps(name = "triggerParam", defaultType = HippyControllerProps.MAP)
    public void triggerParam(final SelectSeriesViewGroup lv, HippyMap map) {

    }

    @HippyControllerProps(name = "display", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = false)
    public void display(final SelectSeriesViewGroup lv, boolean display) {
        lv.setDisplay(display);
    }

    @Override
    public void dispatchFunction(SelectSeriesViewGroup view, String eventName, HippyArray params) {
        super.dispatchFunction(view, eventName, params);
        switch (eventName) {
            case "setInitData":
                view.setInitData(params.getInt(0), params.getInt(1));
                break;
            case "setDisplay":
                view.setDisplay(params.getBoolean(0));
                break;
            case "setPageData":
                // 设置某一页的数据
                int page = params.getInt(0);
                HippyArray array = params.getArray(1);
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
            case "scrollToPositionWithOffset":
                // 设置某一页的数据
                view.scrollToPosition(params.getInt(0), params.getInt(1), params.getBoolean(2));
                break;
            case "scrollToPosition":
                // 设置某一页的数据
                view.scrollToPosition(params.getInt(0));
                break;
            case "setup":
                // 设置某一页的数据
                view.setup();
                break;
            case "setGroupChildSelectByItemPosition":
                view.setGroupChildSelectByItemPosition(params.getInt(0));
                break;
            case "destroy":
                view.destroy();
                break;
        }
    }

    @Override
    public void dispatchFunction(SelectSeriesViewGroup view, String eventName, HippyArray params, Promise promise) {
        if (ES_OP_GET_ES_INFO.equals(eventName)) {
            HippyMap map = new HippyMap();
            promise.resolve(map);
        }
    }
}
