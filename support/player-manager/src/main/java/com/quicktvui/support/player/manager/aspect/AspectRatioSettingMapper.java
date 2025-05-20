package com.quicktvui.support.player.manager.aspect;

/**
 * mapper
 */
public class AspectRatioSettingMapper {

    public static AspectRatio getAspectRatio(int value) {
        switch (value) {
            case 0:
                return AspectRatio.AR_ASPECT_FIT_PARENT;
            case 1:
                return AspectRatio.AR_ASPECT_FILL_PARENT;
            case 2:
                return AspectRatio.AR_ASPECT_WRAP_CONTENT;
            case 3:
                return AspectRatio.AR_16_9_FIT_PARENT;
            case 4:
                return AspectRatio.AR_4_3_FIT_PARENT;
        }
        //默认等比全屏
        return AspectRatio.AR_ASPECT_FILL_PARENT;
    }

    /**
     * 画面比例的值
     *
     * @param aspectRatioModel
     * @return
     */
    public static int getAspectRatioValue(AspectRatio aspectRatioModel) {
        return aspectRatioModel.getValue();
    }

    /**
     * 画面比例的名称
     *
     * @param aspectRatioModel
     * @return
     */
    public static String getAspectRatioName(AspectRatio aspectRatioModel) {
        return aspectRatioModel.getName();
    }
}
