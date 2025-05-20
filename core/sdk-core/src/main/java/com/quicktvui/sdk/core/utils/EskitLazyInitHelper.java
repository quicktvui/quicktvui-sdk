package com.quicktvui.sdk.core.utils;

import com.extscreen.runtime.api.ability.ESAbilityProvider;
import com.quicktvui.sdk.core.internal.EsContext;

/**
 * <br>
 *
 * <br>
 */
public class EskitLazyInitHelper {

    /** SDK被动初始化 **/
    public static void initIfNeed() {
        if (EsContext.get().getContext() != null) return;
        Runnable runnable = ESAbilityProvider.get().getAbility("eskit_lazy_init");
        if (runnable == null) return;
        runnable.run();
    }
}
