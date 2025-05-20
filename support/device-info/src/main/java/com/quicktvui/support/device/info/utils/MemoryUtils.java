package com.quicktvui.support.device.info.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;

import com.quicktvui.support.device.info.model.beans.StorageBean;

import java.io.BufferedReader;
import java.io.FileReader;

public class MemoryUtils {

    /**
     * 读取内存信息
     *
     * @return
     */
    public static void getMemoryInfo(Context context, StorageBean bean) {
        try {//long testValue = 758120448;
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            manager.getMemoryInfo(info);
            long gbValue = 1024 * 1024 * 1024;
            long totalMem = info.totalMem;
            long availMem = info.availMem;
            long usedMem = totalMem - availMem;
            String total = "";
            String usable = "";
            String free = "";
            total = Formatter.formatShortFileSize(context, totalMem);
            usable = Formatter.formatShortFileSize(context, usedMem);
            free = Formatter.formatShortFileSize(context, availMem);
            bean.setTotalMemory(total);
            bean.setFreeMemory(free);
            bean.setUsedMemory(usable);
            int ratio = (int) ((availMem / (double) totalMem) * 100);
            bean.setRatioMemory(ratio);
            double v = totalMem / 1024 / 1024 / 1024.0;
            String ram;
            if (v <= 0.5) {
                ram = "0.5 GB";
            } else if (v <= 1) {
                ram = "1 GB";
            } else if (v <= 1.5) {
                ram = "1.5 GB";
            } else if (v <= 2) {
                ram = "2 GB";
            } else if (v <= 2.5) {
                ram = "2.5 GB";
            } else if (v <= 3) {
                ram = "3 GB";
            } else if (v <= 3.5) {
                ram = "3.5 GB";
            } else if (v <= 4) {
                ram = "4 GB";
            } else if (v <= 6) {
                ram = "6 GB";
            } else if (v <= 8) {
                ram = "8 GB";
            } else if (v <= 12) {
                ram = "12 GB";
            } else {
                ram = "16 GB";
            }
            bean.setMemInfo(ram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过读取文件获取内存大小
     *
     * @param context
     * @return
     */
    private static String getMemoryTotal(Context context) {
        try {
            FileReader fileReader = new FileReader("/proc/meminfo");
            BufferedReader bufferedReader = new BufferedReader(fileReader, Constants.BUF_1024);
            String line = bufferedReader.readLine();
            String[] split = line.split("\\s+");
            long l = Long.parseLong(split[1]) * Constants.BUF_1024;
            bufferedReader.close();
            return Formatter.formatFileSize(context, l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constants.UNKNOWN;
    }

}
