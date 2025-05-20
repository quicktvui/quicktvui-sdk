package com.quicktvui.sdk.core.mediasession.manager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.quicktvui.sdk.core.mediasession.manager.impl.MCommonServiceImpl;

/**
 * <br>
 *
 * <br>
 */
public class ServiceManagerFactory {

    @Nullable
    public static IServiceManager create(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new MCommonServiceImpl();
    }

}
