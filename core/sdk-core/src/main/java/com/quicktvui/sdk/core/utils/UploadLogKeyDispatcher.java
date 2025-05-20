package com.quicktvui.sdk.core.utils;

import android.view.KeyEvent;

import com.sunrain.toolkit.utils.log.L;

/**
 * <br>
 *
 * <br>
 */
public class UploadLogKeyDispatcher {

    // 上上下下左左右右
    private static final String KEY_PASS = "1919202021212222";

    private static final StringBuilder sTmpKey = new StringBuilder(KEY_PASS.length());

    public static boolean isTriggerKeyPass(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_DOWN) {
            if (sTmpKey.length() == KEY_PASS.length()) {
                sTmpKey.setLength(0);
            }
            sTmpKey.append(e.getKeyCode());
            L.iF("Cover", sTmpKey.toString());
            if (sTmpKey.indexOf(KEY_PASS) != -1) {
                sTmpKey.setLength(0);
                return true;
            }
        }
        return false;
    }
}
