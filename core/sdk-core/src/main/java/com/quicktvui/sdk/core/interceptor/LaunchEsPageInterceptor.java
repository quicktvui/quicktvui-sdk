package com.quicktvui.sdk.core.interceptor;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * <br>
 *
 * <br>
 */
public interface LaunchEsPageInterceptor {
    String NAME = "LaunchEsPageInterceptor";

    boolean launchEsPage(EsData last, EsMap params);
}
