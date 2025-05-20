package com.quicktvui.support.player.ijk.utils;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.support.player.ijk.player.IjkMediaOption;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

    public static IjkMediaOption getIjkMediaOption(EsMap map) {
        if (map != null) {
            IjkMediaOption ijkMediaOption = new IjkMediaOption();
            int type = map.getInt("type");
            int category = map.getInt("category");
            if (category < 1 || category > 4) { // 可以认为非ijk，是Apollo
                type = 1;
            } else {
                ijkMediaOption.setType(type);
                ijkMediaOption.setCategory(category);
            }
            String name = map.getString("name");
            ijkMediaOption.setName(name);
            switch (type) {
                case 0:
                    long longValue = map.getLong("value");
                    ijkMediaOption.setLongValue(longValue);
                    break;
                case 1:
                    String stringValue = map.getString("value");
                    ijkMediaOption.setStringValue(stringValue);
                    break;
            }
            return ijkMediaOption;
        }
        return null;
    }

    public static List<IjkMediaOption> getIjkMediaOptions(EsArray array) {
        if (array != null && array.size() > 0) {
            List<IjkMediaOption> options = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
                EsMap map = array.getMap(i);
                if (map != null) {
                    options.add(getIjkMediaOption(map));
                }
            }
            return options;
        }
        return null;
    }
}
