package com.quicktvui.support.player.ijk.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatTools {

    /**
     * 格式化网速
     * @param orSpeed   传入的网速，单位 byte
     * @return  单位 MBps
     */
    public static double formatSpeed(long orSpeed) {
        BigDecimal bg = new BigDecimal(Double.toString(orSpeed / (1024 * 1024.0)));
        return bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 格式化比特率
     * @param orBitRate   传入的码率，单位 bit
     * @return  单位 MBps
     */
    public static double formatBitRate(long orBitRate) {
        BigDecimal bg = new BigDecimal(Double.toString(orBitRate / (1024 * 1024.0 * 8)));
        return bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double formatNetSpeed(long totalByte, long milliSeconds) {
        BigDecimal bg = new BigDecimal(Double.toString((totalByte / (milliSeconds / 1000.0)) / (1024 * 1024.0)));
        return bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
