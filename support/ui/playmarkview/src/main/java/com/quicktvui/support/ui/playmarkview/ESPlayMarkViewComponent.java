package com.quicktvui.support.ui.playmarkview;


import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.graphics.Color;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;


@ESKitAutoRegister
public class ESPlayMarkViewComponent implements IEsComponent<PlayMarkView> {

    @Override
    public PlayMarkView createView(Context context, EsMap params) {
        return new PlayMarkView(context);
    }

    @EsComponentAttribute
    public void markColor(PlayMarkView playMarkView, String markColor) {
        try {
            playMarkView.setPlayColor(Color.parseColor(markColor));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void makeLinearGradientColors(PlayMarkView playMarkView, EsArray markColor) {
        try {
            if (markColor != null) {
                if (markColor.size() > 1) {
                    String[] array = new String[]{markColor.getString(0), markColor.getString(1)};
                    playMarkView.setArrayColor(array);
                } else {
                    playMarkView.setLinearGradientColors(markColor.getString(0), "");
                }
            } else {
                playMarkView.setLinearGradientColors("", "");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void showType(PlayMarkView playMarkView, int type) {
        try {
            playMarkView.setShowType(type);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void gap(PlayMarkView playMarkView, int gap) {
        try {
            playMarkView.setGap(gap);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void roundCorner(PlayMarkView playMarkView, int round) {
        try {
            playMarkView.setRoundCorner(round);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void stateColor(PlayMarkView playMarkView, EsArray markColor) {
        try {
            int[] array = new int[]{
                    Color.parseColor(markColor.getString(0)),
                    Color.parseColor(markColor.getString(1))
            };
            playMarkView.setPlayColorState(array);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatchFunction(PlayMarkView view, String functionName, EsArray params, EsPromise promise) {
        if (ES_OP_GET_ES_INFO.equals(functionName)) {
            EsMap map = new EsMap();
            promise.resolve(map);
        }
    }

    @Override
    public void destroy(PlayMarkView view) {

    }
}
