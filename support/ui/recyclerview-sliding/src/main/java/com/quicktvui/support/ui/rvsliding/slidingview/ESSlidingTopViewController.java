package com.quicktvui.support.ui.rvsliding.slidingview;

import android.content.Context;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyGroupController;

/**
 * @Author xxd
 * @Date 2022/11/8
 * @Description : SlidingTopView
 */
@ESKitAutoRegister
@HippyController(name = ESSlidingTopViewController.CLASS_NAME)
public class ESSlidingTopViewController extends HippyGroupController {
    public static final String CLASS_NAME = "SlidingTopView";

    public ESSlidingTopViewController() {

    }

    @Override
    protected View createViewImpl(Context context) {
        return null;
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        SlidingTopView slidingTopView = new SlidingTopView(context);
        if (iniProps != null) {
            HippyMap map = (HippyMap) iniProps.get("style");
            if (map != null) {
                int height = map.getInt("height");
                if (height > 0) {
                    slidingTopView.setViewHeight(height);
                }
            }
            int scrollBottomHeight = iniProps.getInt("scrollBottomHeight");
            int scrollTopHeight = iniProps.getInt("scrollTopHeight");
            int duration = iniProps.getInt("duration");
            boolean enableSliding = iniProps.getBoolean("enableSliding");
            slidingTopView.setEnableSliding(enableSliding);
            if (duration > 0) {
                slidingTopView.setDuration(duration);
            }
            if (scrollBottomHeight > 0) {
                slidingTopView.setScrollBottomHeight(scrollBottomHeight);
            }
            if (scrollTopHeight > 0) {
                slidingTopView.setScrollTopHeight(scrollTopHeight);
            }
        }
        slidingTopView.setVisibility(View.GONE);
        return slidingTopView;
    }
}
