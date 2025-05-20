package com.quicktvui.sdk.core.utils;

import com.sunrain.toolkit.utils.log.L;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>
 *
 * <br>
 */
public class TimeCheckUtil {

    private static final Map<String, TimeCheckUtil> CACHE = new HashMap<>(1);

    private final String mTag;
    private final long mBeginTime = System.currentTimeMillis();
    private long mCurrentTime = mBeginTime;

    private TimeCheckUtil(String tag) {
        mTag = tag;
    }

    public static TimeCheckUtil getOrCreate(String tag) {
        if (CACHE.containsKey(tag)) {
            return CACHE.get(tag);
        }
        TimeCheckUtil check = new TimeCheckUtil(tag);
        CACHE.put(tag, check);
        return check;
    }

    public void printLog(String msg) {
        long time = System.currentTimeMillis();
        L.logIF(mTag + " " + msg + ": " + (time - mCurrentTime));
        mCurrentTime = time;
    }

    public void end() {
        L.logIF(mTag + " total: " + (mCurrentTime - mBeginTime));
        CACHE.remove(mTag);
    }

}
