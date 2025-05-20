package com.quicktvui.support.network.speed.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatTools {
    /**
     * 格式化网速
     *
     * @param totalByte 传入的网速，单位 byte
     * @return 单位 MBps
     */
    public static double formatNetSpeedKB(long totalByte, long milliSeconds) {
        BigDecimal bg = new BigDecimal(Double.toString((totalByte / (milliSeconds / 1000.0)) / (1024.0)));//KB
        return bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 格式化网速
     *
     * @param totalByte 传入的网速，单位 byte
     * @return 单位 MBps
     */
    public static double formatNetSpeedMB(long totalByte, long milliSeconds) {
        BigDecimal bg = new BigDecimal(Double.toString((totalByte / (milliSeconds / 1000.0)) / (1024 * 1024.0)));//MB
        return bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
