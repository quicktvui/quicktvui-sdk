package com.quicktvui.sdk.core.utils;

import java.io.File;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.internal.Constants;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-27 20:23
 */
public class PathHelper {

    public static File getAppPath(EsData data) {
        File baseDir = data.isCard() ? Constants.getEsCardDir() : Constants.getEsAppDir();
        return new File(baseDir, data.getEsPackage());
    }

}
