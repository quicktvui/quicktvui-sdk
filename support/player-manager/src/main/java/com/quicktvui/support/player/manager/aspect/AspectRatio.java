package com.quicktvui.support.player.manager.aspect;

public enum AspectRatio {
    AR_ASPECT_FIT_PARENT(0, "全屏"), //without clip全屏 自适应屏幕
    AR_ASPECT_FILL_PARENT(1, "等比全屏"),// may clip等比全屏 全屏截取中间
    AR_ASPECT_WRAP_CONTENT(2, "1:1"),//1:1 原始视频
    AR_MATCH_PARENT(3, "铺满屏幕"),//铺满屏幕
    AR_16_9_FIT_PARENT(4, "16:9"),//16:9
    AR_4_3_FIT_PARENT(5, "4:3"),//4:3
    AR_235_FIT_PARENT(6, "235");//宽屏


    private int value;
    private String name;

    AspectRatio(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AspectRatio getAspectRatio(int value) {
        AspectRatio[] AspectRatios = values();
        for (AspectRatio a : AspectRatios) {
            if (a.value == value) {
                return a;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     * 获得屏幕比例的显示名称
     *
     * @return
     */
    public String getName() {
        return name;
    }
}
