package com.quicktvui.sdk.core.adapter;

import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.adapter.soloader.HippySoLoaderAdapter;

import com.quicktvui.sdk.base.core.EsProxy;

/**
 * 自定义so加载路径
 */
public class EsSoLoaderAdapter implements HippySoLoaderAdapter {

    public static final String HP_SO_PKG = "eskit.so.hp.v1";

    @Override
    public String loadSoPath(String soName) {
        String shortName = soName.substring(3, soName.length() - 3);
        L.logIF("load hp so: " + shortName);
        EsProxy.get().getSoManager().loadLibrary(HP_SO_PKG, shortName);
        return null;
    }
}
