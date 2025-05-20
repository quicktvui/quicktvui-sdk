package com.quicktvui.sdk.core.utils;

import android.content.Intent;

import java.net.URISyntaxException;

/**
 * <br>
 *
 * <br>
 */
public class EsIntent extends Intent {

    public static final byte FLAG_FOREGROUND_SERVICE = 1;

    private byte mFlags;

    public EsIntent() {
    }

    public EsIntent(String action) {
        super(action);
    }

    public EsIntent(Intent o) {
        super(o);
    }

    public static EsIntent parseUri(String uri, int flags) throws URISyntaxException {
        return new EsIntent(Intent.parseUri(uri, flags));
    }

    public void addCustomFlag(byte flag) {
        mFlags |= flag;
    }

    public boolean hasCustomFlag(byte flag) {
        return (mFlags & flag) == flag;
    }
}
