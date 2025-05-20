package com.quicktvui.sdk.core.utils;

import android.app.Instrumentation;

import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.thread.Executors;

import java.lang.ref.SoftReference;

/**
 * 按键事件工具类
 * <p>
 * Create by weipeng on 2022/08/22 16:53
 */
public class KeyEventUtil {

    private static volatile SoftReference<Instrumentation> mInstrumentationRef;
//    private static final int[] sFilter = {
//            // 上
//            KeyEvent.KEYCODE_DPAD_UP,
//            // 下
//            KeyEvent.KEYCODE_DPAD_DOWN,
//            // 左
//            KeyEvent.KEYCODE_DPAD_LEFT,
//            // 右
//            KeyEvent.KEYCODE_DPAD_RIGHT,
//            // 确定
//            KeyEvent.KEYCODE_DPAD_CENTER,
//            KeyEvent.KEYCODE_ENTER,
//            // 音量
//            KeyEvent.KEYCODE_VOLUME_UP,
//            KeyEvent.KEYCODE_VOLUME_DOWN,
//            KeyEvent.KEYCODE_VOLUME_MUTE,
//            // 菜单
//            KeyEvent.KEYCODE_MENU,
//
//    };

    /**
     * 发送按键事件
     **/
    public static void sendKeyEvent(final int keyCode) {
        sendKeyEvent(keyCode, 0);
    }

    /**
     * 延时发送按键事件
     **/
    public static void sendKeyEvent(final int keyCode, final int delay) {
        Executors.get().execute(() -> {
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (Exception ignored) {
                }
            }
            try {
                Instrumentation instrumentation;
                if (mInstrumentationRef == null || (instrumentation = mInstrumentationRef.get()) == null) {
                    instrumentation = new Instrumentation();
                    mInstrumentationRef = new SoftReference<>(instrumentation);
                }
                instrumentation.sendKeyDownUpSync(keyCode);
            } catch (SecurityException e) {
                L.logW("send key permission", e);
            } catch (Exception e) {
                L.logW("send key", e);
            }
        });
    }
}
