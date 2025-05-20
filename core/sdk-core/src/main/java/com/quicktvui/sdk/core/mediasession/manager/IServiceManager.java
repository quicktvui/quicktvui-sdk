package com.quicktvui.sdk.core.mediasession.manager;

import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;

/**
 * <br>
 *
 * <br>
 */
public interface IServiceManager {

    void attachToSession(MediaSessionCompat session);

    void handleActionFromVueToService(MediaSessionCompat session, String event, Bundle extras);
}
