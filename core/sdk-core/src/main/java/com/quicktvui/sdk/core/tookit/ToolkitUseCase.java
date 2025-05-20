package com.quicktvui.sdk.core.tookit;

import android.support.annotation.Keep;

/**
 * <br>
 *
 * <br>
 */
@Keep
public enum ToolkitUseCase {

    /** 调试焦点 **/
    DEBUG_FOCUS(false);

    /** 默认值 **/
    public Object defaultValue;

    ToolkitUseCase(Object value) {
        this.defaultValue = value;
    }
}
