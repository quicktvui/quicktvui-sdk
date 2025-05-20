package com.quicktvui.support.ui.largelist;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.util.Log;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.support.ui.ScreenAdapt;
import com.tencent.mtt.hippy.utils.LogUtils;

/**
 *
 */
@ESKitAutoRegister
public class LargeListComponent implements IEsComponent<LargeListViewGroup> {

    @Override
    public LargeListViewGroup createView(Context context, EsMap params) {
        ScreenAdapt.getInstance().init(context);
        return new LargeListViewGroup(context);
    }

    @EsComponentAttribute
    public void initParam(final LargeListViewGroup lv, EsMap map) {
        if(LogUtils.isDebug()){
            Log.i(LargeListViewGroup.TAG,"LargeListComponent initParam map:"+map+",view id:"+lv.getId());
        }
        if(map == null){
            Log.e("LargeListComponent","initParam error map is null");
            throw new IllegalArgumentException("initParam error map is null");
        }
        try {
            if (map.size() > 0) {
                final EsMap tm = map.getMap("template");
                final TemplateItemPresenterSelector ps = TemplateHelper.setupPresenters();
                MyTemplateHelper.initTemplate(ps, tm);
                lv.setSelector(ps);
                lv.initParams(map, tm);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void initFocusPosition(final LargeListViewGroup lv, int pos) {
        lv.setFocusPosition(pos);
    }

    @EsComponentAttribute
    public void triggerParam(final LargeListViewGroup lv, EsMap map) {

    }

    @EsComponentAttribute
    public void display(final LargeListViewGroup lv, boolean display) {
        lv.setDisplay(display);
    }


    @Override
    public void dispatchFunction(LargeListViewGroup view, String eventName, EsArray params, EsPromise promise) {
        //Log.i(LargeListViewGroup.TAG,"dispatchFunction eventName:"+eventName+",params:"+params);
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
    public void destroy(LargeListViewGroup view) {

    }
}
