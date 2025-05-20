package com.quicktvui.support.device.info.model.info;

import android.media.MediaCodecList;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MediaCodecInfo {

    public static List<Pair<String, String>> getCodeCInfo() {
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            android.media.MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo != null) {
                String[] types = codecInfo.getSupportedTypes();
                if (types != null) {
                    for (String type : types) {
                        list.add(new Pair<>(codecInfo.getName(), type));
                    }
                }
            }

        }
        return list;
    }

}
