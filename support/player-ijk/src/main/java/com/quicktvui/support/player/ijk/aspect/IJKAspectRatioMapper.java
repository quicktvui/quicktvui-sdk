package com.quicktvui.support.player.ijk.aspect;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.player.ijk.player.IRenderView;
import com.quicktvui.support.player.manager.aspect.AspectRatio;

public class IJKAspectRatioMapper {

    /**
     * 获取所有的屏幕比例
     *
     * @return
     */
    public static List<AspectRatio> generateAllAspectRatio() {

        List<AspectRatio> aspectRatioList = new ArrayList<>();
        aspectRatioList.add(generateAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT));
        aspectRatioList.add(generateAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT));
        aspectRatioList.add(generateAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT));
        aspectRatioList.add(generateAspectRatio(IRenderView.AR_16_9_FIT_PARENT));
        aspectRatioList.add(generateAspectRatio(IRenderView.AR_4_3_FIT_PARENT));
        aspectRatioList.add(generateAspectRatio(IRenderView.AR_MATCH_PARENT));
        return aspectRatioList;
    }

    public static AspectRatio generateAspectRatio(int aspectRatio) {
        switch (aspectRatio) {
            case IRenderView.AR_ASPECT_FIT_PARENT:
                return AspectRatio.AR_ASPECT_FIT_PARENT;

            case IRenderView.AR_ASPECT_FILL_PARENT:
                return AspectRatio.AR_ASPECT_FILL_PARENT;

            case IRenderView.AR_ASPECT_WRAP_CONTENT:
                return AspectRatio.AR_ASPECT_WRAP_CONTENT;

            case IRenderView.AR_16_9_FIT_PARENT:
                return AspectRatio.AR_16_9_FIT_PARENT;

            case IRenderView.AR_4_3_FIT_PARENT:
                return AspectRatio.AR_4_3_FIT_PARENT;

            case IRenderView.AR_MATCH_PARENT:
                return AspectRatio.AR_MATCH_PARENT;
            default:
                return null;
        }
    }

    public static int generateAspectRatio(AspectRatio aspectRatio) {
        if (aspectRatio == null) {
            return -1;
        }
        switch (aspectRatio) {
            case AR_ASPECT_FIT_PARENT:
                return IRenderView.AR_ASPECT_FIT_PARENT;

            case AR_ASPECT_FILL_PARENT:
                return IRenderView.AR_ASPECT_FILL_PARENT;

            case AR_ASPECT_WRAP_CONTENT:
                return IRenderView.AR_ASPECT_WRAP_CONTENT;

            case AR_16_9_FIT_PARENT:
                return IRenderView.AR_16_9_FIT_PARENT;

            case AR_4_3_FIT_PARENT:
                return IRenderView.AR_4_3_FIT_PARENT;

            case AR_MATCH_PARENT:
                return IRenderView.AR_MATCH_PARENT;
            default:
                return -1;
        }
    }
}
