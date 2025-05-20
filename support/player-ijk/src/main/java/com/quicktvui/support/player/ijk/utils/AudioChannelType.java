package com.quicktvui.support.player.ijk.utils;

import java.util.HashMap;
import java.util.Map;

public class AudioChannelType {

    private static final Map<Integer, String> channelMap = new HashMap<>();
    static {
        channelMap.put(1, "单声道");
        channelMap.put(2, "立体声");
        channelMap.put(3, "2.1立体声");
        channelMap.put(4, "4.0环绕声");
        channelMap.put(5, "5.0环绕声");
        channelMap.put(6, "5.1环绕声");
        channelMap.put(7, "6.1环绕声");
        channelMap.put(8, "7.1环绕声");
    }

    public static String getChannelName(int num) {
        String name =  channelMap.get(num);
        return name == null ? "" : name;
    }
}
