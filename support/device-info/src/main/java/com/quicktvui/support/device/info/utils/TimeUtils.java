package com.quicktvui.support.device.info.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    @SuppressLint("SimpleDateFormat")
    public static String formatDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    private void test(){
        Calendar calendar = Calendar.getInstance();
    }

}
